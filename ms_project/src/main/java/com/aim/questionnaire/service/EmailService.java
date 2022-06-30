package com.aim.questionnaire.service;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {

    public void sendMail(String address, String subject, String htmlMsg, Boolean isSSL) throws EmailException {

        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(htmlMsg)) {
            throw new EmailException();
        }

        try {
            HtmlEmail email = new HtmlEmail();
            List<String> list = new ArrayList<String>();
            list.add(address);
            String[] tos = list.toArray(new String[list.size()]);

            // 这里是SMTP发送服务器的名字：163的如下："smtp.163.com"
            email.setHostName("smtp.163.com");
            if (isSSL) {
                email.setSSLOnConnect(true);
                email.setSmtpPort(465);
            }
            // 字符编码集的设置
            email.setCharset("UTF-8");
            // 收件人的邮箱
            email.addTo(tos);
            // 发送人的邮箱以及发件人名称
            email.setFrom("m13002428027@163.com", "单炟崴");
            // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
            email.setAuthentication("m13002428027@163.com", "Kiritodevil123.");
            // 要发送的邮件主题
            email.setSubject(subject);
            // 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签
            email.setHtmlMsg(htmlMsg);

            String result1 = email.send();

        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailException();
        }
    }
}