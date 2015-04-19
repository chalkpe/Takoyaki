package pe.chalk.takoyaki.logger;

import pe.chalk.takoyaki.Takoyaki;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */
public class Mailer {
    public static void send(String username, String password, String subject, String body, List<InternetAddress> recipients){
        new Thread(() -> {
            //properties 설정
            Properties props = new Properties();
            props.put("mail.smtps.auth", "true");

            //메일 세션
            Session session = Session.getDefaultInstance(props);
            MimeMessage msg = new MimeMessage(session);

            //메일 관련
            try{
                msg.setSubject(subject);
                msg.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
                msg.setContent(body, "text/html; charset=utf-8");
                msg.setFrom(new InternetAddress(username));

                if(recipients == null){
                    throw new IllegalArgumentException("메일 전송 대상이 없습니다");
                }

                recipients.forEach(recipient -> {
                    try{
                        msg.addRecipient(Message.RecipientType.TO, recipient);
                    }catch(Exception e){
                        Takoyaki.getInstance().getLogger().error(e.getMessage());
                    }
                });

                // 발송 처리
                Transport transport = session.getTransport("smtps");
                transport.connect("smtp.gmail.com", username, password);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();

                Takoyaki.getInstance().getLogger().debug("메일이 성공적으로 발송되었습니다!");
            }catch(Exception e){
                Takoyaki.getInstance().getLogger().error(e.getMessage());
            }
        }).start();
    }
}