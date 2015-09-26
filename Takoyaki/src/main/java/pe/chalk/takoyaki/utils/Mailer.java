/*
 * Copyright 2014-2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.chalk.takoyaki.utils;

import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.data.Article;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.data.Member;
import pe.chalk.takoyaki.data.Violation;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-19
 */
public class Mailer {
    public static String USERNAME = null;
    public static String PASSWORD = null;
    public static String HOOK_URL = null;

    public static final String FORMAT_HTML =
            "<link href='http://fonts.googleapis.com/css?family=Inconsolata' rel='stylesheet' type='text/css' />" +
            "<link href='http://fonts.googleapis.com/earlyaccess/nanumgothic.css' rel='stylesheet' type='text/css' />" +
            "<table align=\"center\" width=\"696\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding: 20px; background-color: white;\">" +
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
                        "<td style=\"border: 5px solid #555555; background: #2B2B2B;\">" +
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
                                                            "<span style=\"color: #BBBBBB; font-family: 'Inconsolata', 'Nanum Gothic', 'NanumGothic'; line-height: 19px;\">" +
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
                        "<td style=\"line-height: 0; height: 20px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 19px; font-size: 12px; color: rgb(102, 102, 102);\">" +
                            "해당 메일을 확인하시고 별도로 주의 및 경고 처리 해 주세요!" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 20px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 19px; font-size: 12px; color: rgb(102, 102, 102);\">" +
                            "%s" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 20px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td align=\"center\" style=\"font-size: 16px; font-weight: bold; color: rgb(102, 102, 102)\">" +
                            "© 2014-2015 ChalkPE. All rights reserved." +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 4px; font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td style=\"line-height: 0; height: 0px; border-top-width: 2px; border-top-style: solid; border-top-color: rgb(85, 79, 76); font-size: 0px;\">" +
                            "&nbsp;" +
                        "</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";

    private Mailer(){}

    private static void __send(String subject, String body, Object[] recipients){
        try{
            if(Mailer.USERNAME == null || Mailer.PASSWORD == null || recipients == null){
                return;
            }

            Properties properties = new Properties();
            properties.put("mail.smtps.auth", "true");

            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);

            message.setSubject(subject);
            message.setHeader("Content-Type", "text/html; charset=UTF-8");
            message.setContent(TextFormat.replaceTo(TextFormat.Type.HTML, body), "text/html; charset=UTF-8");
            message.setFrom(new InternetAddress(Mailer.USERNAME));

            for(Object recipient : recipients){
                if(recipient instanceof Address){
                    message.addRecipient(Message.RecipientType.TO, (Address) recipient);
                }else if(recipient instanceof Member){
                    message.addRecipient(Message.RecipientType.TO, ((Member) recipient).getInternetAddress());
                }else if(recipient instanceof String){
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress((String) recipient));
                }
            }

            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", Mailer.USERNAME, Mailer.PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            Takoyaki.getInstance().getLogger().info(String.format("메일이 발송되었습니다: %s -> %s", subject, Stream.of(message.getAllRecipients()).map(Address::toString).collect(Collectors.joining(", "))));
        }catch(MessagingException e){
            Takoyaki.getInstance().getLogger().error(e.getMessage());
        }
    }

    public static void send(String subject, String body, Object[] recipients){
        send(subject, body, recipients, true);
    }

    public static void send(String subject, String body, Object[] recipients, boolean async){
        if(async){
            new Thread(() -> __send(subject, body, recipients)).start();
        }else{
            __send(subject, body, recipients);
        }
    }

    public static void sendMail(String prefix, String subject, String body, Object[] recipients){
        sendMail(prefix, subject, body, recipients, true);
    }

    public static void sendMail(String prefix, String subject, String body, Object[] recipients, boolean async){
        send(String.format("[%s] [%s] %s", Takoyaki.getInstance().getPrefix(), prefix, subject), String.format(Mailer.FORMAT_HTML, body, Mailer.getFooter()), recipients, async);
    }
    public static void sendViolation(Violation violation, Object[] recipients){
        sendViolation(violation, recipients, true);
    }

    public static void sendViolation(Violation violation, Object[] recipients, boolean async){
        String body = String.format("[%s] %s\n\n%s\n\n작성자: %s", violation.getLevel(), violation.getName(), Stream.of(violation.getViolations()).map(data -> {
            String str = data.toString();

            if(Mailer.HOOK_URL != null && data instanceof Article){
                str = TextFormat.replaceTo(TextFormat.Type.HTML, str)
                        + "  <a href=\"http://cafe.naver.com/" + violation.getTarget().getAddress() + "/" + ((Article) data).getId()
                        + "\"><img src=\"" + Mailer.HOOK_URL + "/ArticleDoctor.php?clubid=" + violation.getTargetId() + "&articleid=" + ((Article) data).getId()
                        + "\" style=\"vertical-align: middle\" width=\"15px\" height=\"15px\"></a>";
            }

            return str;
        }).collect(Collectors.joining("\n")), violation.getViolator());

        violation.getTarget().getLogger().warning(String.format("[%s] %s - 작성자: %s\n%s", violation.getLevel(), violation.getName(), violation.getViolator(), Stream.of(violation.getViolations()).map(Data::toString).collect(Collectors.joining("\n"))));
        sendMail(violation.getPrefix(), violation.getName(), body, recipients, async);
    }

    public static String getFooter(){
        return String.format("발신 시각: %s\n서버 정보: %s - %s %s\n자바 버전: %s\n타코야키 버전: %s",
                TextFormat.DATE_FORMAT.format(new Date()), System.getProperty("user.name"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("java.version"), Takoyaki.VERSION
        );
    }
}