package pe.chalk.takoyaki;

import pe.chalk.takoyaki.data.Member;

import javax.mail.Message;
import javax.mail.MessagingException;
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
    public static String USERNAME = "mcpekorea.takoyaki@gmail.com";
    public static String PASSWORD = null;

    public static final String FORMAT_HTML =
            "<table align=\"center\" width=\"696\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                "<tbody>" +
                    "<tr>" +
                        "<td align=\"left\">" +
                            "<img src=\"http://i.imgur.com/M9peKSu.png\">" +
                            "<br>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 4px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 3px; border-top-width: 3px; border-top-style: solid; border-top-color: rgb(85, 79, 76); font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td>" +
                            "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                                "<tbody>" +
                                    "<tr>" +
                                        "<td style=\"width: 515px;\">" +
                                            "<img src=\"http://i.imgur.com/4R1O3ou.png\">" +
                                            "<br>" +
                                        "</td>" +
                                        "<td>" +
                                            "<img src=\"http://i.imgur.com/uGKJ5mx.png\">" +
                                            "<br>" +
                                        "</td>" +
                                    "</tr>" +
                                "</tbody>" +
                            "</table>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 3px; font-size: 0px;\">" +
                            "<img src=\"http://i.imgur.com/Y2n9zpJ.png\">" +
                            "<br>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 25px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"font-size: 16px; font-weight: bold;\">" +
                            "<img src=\"http://i.imgur.com/1NS81yU.png\">" +
                            "<br>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 22px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"border: 1px solid rgb(233, 233, 233); background: rgb(249, 249, 249);\">" +
                            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                                "<tbody>" +
                                    "<tr>" +
                                        "<td style=\"line-height: 0; width: 29px; height: 22px; font-size: 0px;\">" +
                                            "&nbsp;" +
                                        "</td>" +
                                        "<td></td>" +
                                    "</tr>" +
                                    "<tr>" +
                                        "<td>" +
                                            "&nbsp;" +
                                        "</td>" +
                                        "<td>" +
                                            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                                                "<tbody>" +
                                                    "<tr>" +
                                                        "<td>" +
                                                            "<span style=\"color: rgb(102, 102, 102); font-weight: bold; line-height: 19px;\">" +
                                                                "%s" +
                                                            "</span>" +
                                                        "</td>" +
                                                    "</tr>" +
                                                "</tbody>" +
                                            "</table>" +
                                        "</td>" +
                                    "</tr>" +
                                    "<tr>" +
                                        "<td style=\"line-height: 0; height: 20px; font-size: 0px;\">" +
                                            "&nbsp;" +
                                        "</td>" +
                                        "<td></td>" +
                                    "</tr>" +
                                "</tbody>" +
                            "</table>" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 25px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 19px; font-size: 12px; color: rgb(102, 102, 102);\">" +
                            "해당 메일을 확인하시고 별도로 주의 및 경고 처리 해 주세요!" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 24px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 19px; font-size: 12px; color: rgb(102, 102, 102);\">" +
                            "%s" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 24px; font-size: 0px;\">" +
                                "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td align=\"center\" style=\"font-size: 16px; font-weight: bold; color: rgb(102, 102, 102)\">" +
                            "© 2014-2015 ChalkPE. All rights reserved." +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 28px; border-top-width: 2px; border-top-style: solid; border-top-color: rgb(85, 79, 76); font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";


    public static void send(String subject, String body, List<Member> recipients){
        new Thread(() -> {
            if(Mailer.PASSWORD == null){
                throw new IllegalStateException("Mailer.PASSWORD must not be null");
            }

            Properties props = new Properties();
            props.put("mail.smtps.auth", "true");

            Session session = Session.getDefaultInstance(props);
            MimeMessage msg = new MimeMessage(session);

            try{
                msg.setSubject(subject);
                msg.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
                msg.setContent(body, "text/html; charset=utf-8");
                msg.setFrom(new InternetAddress(Mailer.USERNAME));

                if(recipients == null){
                    throw new IllegalArgumentException("recipients must not be null");
                }

                recipients.forEach(recipient -> {
                    try{
                        msg.addRecipient(Message.RecipientType.TO, recipient.getInternetAddress());
                    }catch(MessagingException e){
                        Takoyaki.getInstance().getLogger().error(e.getMessage());
                    }
                });

                // 발송 처리
                Transport transport = session.getTransport("smtps");
                transport.connect("smtp.gmail.com", Mailer.USERNAME, Mailer.PASSWORD);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();

                Takoyaki.getInstance().getLogger().debug("메일이 성공적으로 발송되었습니다!");
            }catch(Exception e){
                Takoyaki.getInstance().getLogger().error(e.getMessage());
            }
        }).start();
    }
}