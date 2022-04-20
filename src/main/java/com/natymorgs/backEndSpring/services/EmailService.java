package com.natymorgs.backEndSpring.services;

import org.springframework.mail.SimpleMailMessage;

import com.natymorgs.backEndSpring.domain.Cliente;
import com.natymorgs.backEndSpring.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);

	void sendEmail(SimpleMailMessage msg);

	void sendNewPasswordEmail(Cliente cliente, String newPass);
}
