package com.barbearia.financial.application.listener;

import com.barbearia.scheduling.domain.event.AppointmentCompletedEvent;
import com.barbearia.financial.application.service.FinancialApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AppointmentCompletedListener {
    private final FinancialApplicationService service;

    @TransactionalEventListener
    public void on(AppointmentCompletedEvent event) {
        service.registerCompletedAppointment(event);
    }
}
