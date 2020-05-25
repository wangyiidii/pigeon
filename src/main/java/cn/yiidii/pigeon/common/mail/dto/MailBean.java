package cn.yiidii.pigeon.common.mail.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MailBean implements Serializable {

    private static final long serialVersionUID = -8712913059042908866L;

    String subject;//邮件主题
    String content;//邮件内容
    String[] toWho;//收件人
    String[] ccPeoples;//抄送人
    String[] bccPeoples;//抄送人
    String[] attachments;//附件

    Integer type;//邮件类型 1：text邮件；2：html邮件；3：模板邮件

    String templateName;//邮件模板名称
    Object model;//模板对应的对象


}
