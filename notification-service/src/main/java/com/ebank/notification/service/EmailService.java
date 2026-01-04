package com.ebank.notification.service;

import com.ebank.notification.kafka.event.*;
import com.ebank.notification.model.email.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:MolChkaraApp}")
    private String appName;

   public void sendAuthEmail(AuthEmailInfo info) {
    Context context = new Context();
    context.setVariable("userName", info.getUserName());
    context.setVariable("code", info.getCode());
    context.setVariable("validity", info.getValidity());
    context.setVariable("appName", appName);

    // On utilise dynamiquement le nom fourni par le Consumer
    processAndSend(info.getTo(), info.getSubject(), info.getTemplateName(), context);
}
    public void sendNotificationEmail(NotificationEmailInfo info) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("message", info.getMessage());
            
            // Injecte automatiquement tout le contenu de metadata (amount, currency, etc.)
            if (info.getMetadata() != null) {
                info.getMetadata().forEach(context::setVariable);
            }

            String htmlContent = templateEngine.process(info.getTemplateName(), context);

            helper.setTo(info.getTo());
            helper.setSubject(info.getSubject());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("✅ Email '{}' envoyé à {}", info.getTemplateName(), info.getTo());
        } catch (Exception e) {
            log.error("❌ Erreur envoi email: {}", e.getMessage());
        }
    }


   
   
    private void processAndSend(String to, String subject, String templateName, Context context) {
        try {
            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(to, subject + " - " + appName, htmlContent);
            log.info("✅ Email '{}' envoyé avec succès à {}", templateName, to);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email '{}' à {}", templateName, to, e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}