package com.zendaimoney.coreaccount.email;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SimpleMailService {

    private MailSender mailSender;
    private SimpleMailMessage simpleMailMessage;
    @Value("${cc}")
    private String cc;

    /**
     * @方法名: sendMail
     * @参数名：@param subject 邮件主题
     * @参数名：@param content 邮件主题内容
     * @参数名：@param to 收件人Email地址
     * @描述语: 发送邮件
     */
    public void sendMail(String subject, String to, String content) {

        if (StringUtils.isNotBlank(subject)) {
            simpleMailMessage.setSubject(subject);// 设置邮件主题
        }
        if (StringUtils.isNotBlank(to)) {
            simpleMailMessage.setTo(to); // 设定收件人
        }
        if (StringUtils.isNotBlank(cc)) {
            String[] array = cc.split(";");
            simpleMailMessage.setCc(array);
        }
        simpleMailMessage.setText(content); // 设置邮件主题内容

        mailSender.send(simpleMailMessage); // 发送邮件
    }

    // Spring 依赖注入
    public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
        this.simpleMailMessage = simpleMailMessage;
    }

    // Spring 依赖注入
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
}
