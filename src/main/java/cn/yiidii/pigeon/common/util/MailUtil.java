package cn.yiidii.pigeon.common.util;


import cn.yiidii.pigeon.common.mail.dto.MailBean;
import cn.yiidii.pigeon.common.mail.service.impl.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MailUtil {

    @Autowired
    private MailService mailService;
    private static MailService mailServiceStatic;

    @PostConstruct
    public void init() {
        mailServiceStatic = mailService;
    }

    public static void sendMail(MailBean mail) {
        Integer type = mail.getType();
        switch (type) {
            case 1:
                mailServiceStatic.sendSimpleTextMailActual(mail.getSubject(), mail.getContent(), mail.getToWho(), mail.getCcPeoples(), mail.getBccPeoples(), mail.getAttachments());
                break;
            case 2:
                mailServiceStatic.sendHtmlMail(mail.getSubject(), mail.getContent(), mail.getToWho());
                break;
            case 3:
                mailServiceStatic.sendTemplateHtmlMail(mail.getSubject(), mail.getContent(), mail.getToWho(), mail.getTemplateName(), mail.getModel());
                break;
            default:
                log.warn("The mail whose subject is '{}' is unsupported.", mail.getSubject());
                break;
        }
    }
}
