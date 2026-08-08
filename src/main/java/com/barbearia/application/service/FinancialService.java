package com.barbearia.application.service;

import com.barbearia.application.dto.request.PayCommissionRequestDTO;
import com.barbearia.application.dto.request.TransactionRequestDTO;
import com.barbearia.application.dto.response.BarberCommissionResponseDTO;
import com.barbearia.application.dto.response.FinancialSummaryDTO;
import com.barbearia.application.dto.response.TransactionResponseDTO;
import com.barbearia.domain.entities.Appointment;
import com.barbearia.domain.entities.Barber;
import com.barbearia.domain.entities.BarberCommission;
import com.barbearia.domain.entities.Financial;
import com.barbearia.domain.enums.CommissionStatus;
import com.barbearia.domain.enums.PaymentMethod;
import com.barbearia.domain.enums.TransactionCategory;
import com.barbearia.domain.enums.TransactionType;
import com.barbearia.infrastructure.persistence.AppointmentRepository;
import com.barbearia.infrastructure.persistence.BarberCommissionRepository;
import com.barbearia.infrastructure.persistence.BarberRepository;
import com.barbearia.infrastructure.persistence.FinancialRepository;
import com.barbearia.shared.exceptions.EntityAlreadyExistsException;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final FinancialRepository financialRepository;
    private final BarberCommissionRepository barberCommissionRepository;
    private final BarberRepository barberRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<BarberCommissionResponseDTO> getCommissionsByBarber(UUID barberId, CommissionStatus status) {
        return barberCommissionRepository.findByBarberIdAndStatus(barberId, status)
                .stream()
                .map(BarberCommissionResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinancialSummaryDTO getFinancialSummary() {

       List<Financial> transactions = financialRepository.findAll();

        BigDecimal totalIncomes = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Financial::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncomes.subtract(totalExpenses);

        List<BarberCommission> pendingCommissionsList = barberCommissionRepository.findAll().stream()
                .filter(c -> c.getStatus() == CommissionStatus.PENDING)
                .toList();

        BigDecimal pendingCommissions = pendingCommissionsList.stream()
                .map(BarberCommission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalServiceRendered = transactions.stream()
                .filter(t -> t.getCategory() == TransactionCategory.SALE_SERVICE)
                .count();

        return new FinancialSummaryDTO(
                totalIncomes,
                totalExpenses,
                netBalance,
                pendingCommissions,
                totalServiceRendered
        );
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {

        Barber barber = null;
        if (dto.barberId() != null) {
            barber = barberRepository.findById(dto.barberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        }

        Appointment appointment = null;
        if (dto.appointmentId() != null) {
            appointment = appointmentRepository.findById(dto.appointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        }

        Financial financial = Financial.builder()
                .description(dto.description())
                .amount(dto.amount())
                .type(dto.type())
                .category(dto.category())
                .paymentMethod(dto.paymentMethod())
                .barber(barber)
                .appointment(appointment)
                .build();

        return new TransactionResponseDTO(financialRepository.save(financial));
    }

    @Transactional
    public List<BarberCommissionResponseDTO> payCommissions(PayCommissionRequestDTO dto) {

        List<BarberCommission> commissions = barberCommissionRepository.findAllById(dto.commissionId());

        if (commissions.isEmpty()) {
            throw new ResourceNotFoundException("Commissions not found");
        }

        BigDecimal totalCommissions = BigDecimal.ZERO;
        Barber barber = commissions.getFirst().getBarber();

        for (BarberCommission commission : commissions) {
            if (commission.getStatus() == CommissionStatus.PAID) {
                throw new IllegalArgumentException("One or more commissions are already paid");
            }
            commission.setStatus(CommissionStatus.PAID);
            commission.setPaidAt(LocalDateTime.now());
            totalCommissions = totalCommissions.add(commission.getCommissionAmount());
        }

        barberCommissionRepository.saveAll(commissions);

        Financial financial = Financial.builder()
                .description("Payment for commissions - Barber: " + barber.getName())
                .amount(totalCommissions)
                .type(TransactionType.EXPENSE)
                .category(TransactionCategory.BARBER_COMMISSION)
                .paymentMethod(dto.paymentMethod())
                .barber(barber)
                .build();

        financialRepository.save(financial);

        return commissions.stream()
                .map(BarberCommissionResponseDTO::new)
                .toList();
    }

    @Transactional
    public void processFinancialByAppointment(Appointment appointment, PaymentMethod paymentMethod) {

        financialRepository.findByAppointmentId(appointment.getId()).ifPresent(t->{
            throw new EntityAlreadyExistsException("Financial transaction already exists for this appointment");
        });

        BigDecimal totalAmount = appointment.getTotalPrice();
        Barber barber = appointment.getBarber();

        Financial incomeTransaction = Financial.builder()
                .description("Income from appointment #" + appointment.getId())
                .amount(totalAmount)
                .type(TransactionType.INCOME)
                .category(TransactionCategory.SALE_SERVICE)
                .paymentMethod(paymentMethod)
                .appointment(appointment)
                .barber(barber)
                .build();

        financialRepository.save(incomeTransaction);
    }

    @Transactional
    public void processCommissionByAppointment(Appointment appointment) {

        if (appointment.getBarber().getCommissionRate() == null || appointment.getBarber().getCommissionRate().compareTo(BigDecimal.ZERO) <= 0) {
            appointment.getBarber().setCommissionRate(new BigDecimal("50"));
        }
        BigDecimal commissionAmount = appointment.getTotalPrice()
                .multiply(appointment.getBarber().getCommissionRate())
                .divide(new BigDecimal("100"),2, RoundingMode.HALF_UP);

        BarberCommission commission = BarberCommission.builder()
                .barber(appointment.getBarber())
                .appointment(appointment)
                .serviceAmount(appointment.getTotalPrice())
                .commissionRate(appointment.getBarber().getCommissionRate())
                .commissionAmount(commissionAmount)
                .status(CommissionStatus.PENDING)
                .build();

        barberCommissionRepository.save(commission);
    }
}
