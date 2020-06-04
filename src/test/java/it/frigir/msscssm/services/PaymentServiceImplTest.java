package it.frigir.msscssm.services;

import it.frigir.msscssm.domain.Payment;
import it.frigir.msscssm.domain.PaymentState;
import it.frigir.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.preAuth(savedPayment.getId());

        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println(preAuthPayment);
    }

    @Transactional
    @Test
    @RepeatedTest(10)
    void newToAuth() {
        Payment savedPayment = paymentService.newPayment(payment);

        if (paymentService.preAuth(savedPayment.getId()).getState().getId() == PaymentState.PRE_AUTH) {
            paymentService.authorizePayment(savedPayment.getId());
        }

        Payment authPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println(authPayment);
    }
}