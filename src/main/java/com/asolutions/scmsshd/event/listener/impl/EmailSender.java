package com.asolutions.scmsshd.event.listener.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Oleg Ilyenko
 */
public class EmailSender implements InitializingBean, DisposableBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private boolean disable = false;

    private String host;

    private Integer port;

    private String protocol = "smtp";

    private boolean auth = false;

    private String user;

    private String password;

    private String from;

    private String replayTo;

    private String forceEmail;

    private Session mailSession;

    private BlockingQueue<EmailMessage> mailsQueue;

    private Thread senderThread;

    private SenderRunnable senderRunnable;

    private boolean disabledReported = false;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplayTo() {
        return replayTo;
    }

    public void setReplayTo(String replayTo) {
        this.replayTo = replayTo;
    }

    public String getForceEmail() {
        return forceEmail;
    }

    public void setForceEmail(String forceEmail) {
        this.forceEmail = forceEmail;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean withAuth = auth || (user != null && !user.trim().equals(""));

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", protocol);

        props.setProperty("mail." + protocol + ".host", host);

        if (port != null) {
            props.setProperty("mail." + protocol + ".port", port.toString());
        }

        props.setProperty("mail." + protocol + ".auth", "" + withAuth);

        if (user != null) {
            props.setProperty("mail.user", user);
        }

        if (password != null) {
            props.setProperty("mail.password", password);
        }

        if (withAuth) {
            Authenticator auth = new javax.mail.Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            };

            mailSession = Session.getInstance(props, auth);
        } else {
            mailSession = Session.getInstance(props, null);
        }

        mailsQueue = new ArrayBlockingQueue<EmailMessage>(200);

        senderRunnable = new SenderRunnable(mailSession, mailsQueue);
        senderThread = new Thread(senderRunnable);
        senderThread.setDaemon(true);
        senderThread.setName("email-sender");
        senderThread.start();
    }

    @Override
    public void destroy() throws Exception {
        if (senderRunnable != null) {
            senderRunnable.stop();
        }
    }

    public void sendMessage(List<String> emails, String subject, String body) {
        if (isDisabled()) {
            return;
        }

        for (String email : emails) {
            mailsQueue.add(new EmailMessage(subject, body, from, replayTo, email, forceEmail));
        }
    }

    private boolean isDisabled() {
        String p = System.getProperty("disable.email");

        if (p != null && Boolean.valueOf(p)) {
            if (!disabledReported) {
                log.warn("Email sending disabled with system property. No emails would be sent!");
                disabledReported = true;
            }

            return true;
        } if (disable) {
            if (!disabledReported) {
                log.warn("Email sending disabled with bean config. No emails would be sent!");
                disabledReported = true;
            }

            return true;
        } else {
            return false;
        }
    }

    private static class EmailMessage {
        private final String subject;
        private final String body;
        private final String from;
        private final String replyTo;
        private final String email;
        private final String forceEmail;

        private EmailMessage(String subject, String body, String from, String replyTo, String email, String forceEmail) {
            this.subject = subject;
            this.body = body;
            this.from = from;
            this.replyTo = replyTo;
            this.email = email;
            this.forceEmail = forceEmail;
        }

        public String getFrom() {
            return from;
        }

        public String getReplyTo() {
            return replyTo != null && !replyTo.trim().equals("") ? replyTo : from;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public String getEmail() {
            return email;
        }

        public String getForceEmail() {
            return forceEmail;
        }
    }

    private static class SenderRunnable implements Runnable {

        protected final Logger log = LoggerFactory.getLogger(getClass());

        private Session mailSession;

        private Transport transport;

        private BlockingQueue<EmailMessage> mailQueue;

        private volatile boolean stop;

        private SenderRunnable(Session mailSession, BlockingQueue<EmailMessage> mailQueue) {
            this.mailSession = mailSession;
            this.mailQueue = mailQueue;
        }

        public void stop() {
            stop = true;
        }

        public void run() {
            try {
                log.info("Starting mails sending thread");
                while (!stop) {
                    EmailMessage m = mailQueue.poll(2, TimeUnit.SECONDS);

                    if (m != null) {
                        processMessage(m);
                    }
                }
            } catch (InterruptedException e) {
                log.warn("Mail sender thread interrupted!", e);
            } finally {
                log.info("Mails sending thread stopped");
                closeTransport();
            }
        }

        private void processMessage(EmailMessage m) {
            try {
                MimeMessage message = new MimeMessage(mailSession);

                message.setContent(m.getBody(), "text/plain");
                message.setFrom(new InternetAddress(m.getFrom()));
                message.setReplyTo(new Address[]{new InternetAddress(m.getReplyTo())});

                if (m.getForceEmail() != null && !m.getForceEmail().trim().equals("")) {
                    log.warn("Emails was forced to " + m.getForceEmail() + " (original email is " + m.getEmail() + ")");
                    message.setSubject(m.getSubject() + " (original recipient " + m.getEmail() + ")");
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(m.getForceEmail()));
                } else {
                    message.setSubject(m.getSubject());
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(m.getEmail()));
                }

                log.debug("Sending emails message to " + message.getRecipients(Message.RecipientType.TO)[0] + " with subject: " + message.getSubject());
                getTransport().sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            } catch (MessagingException e) {
                log.warn("Error during sending email.", e);
            }
        }

        private Transport getTransport() {
            if (transport != null && transport.isConnected()) {
                return transport;
            }

            if (transport != null) {
                closeTransport();
            }

            try {
                transport = mailSession.getTransport();
                transport.connect();
            } catch (Exception e) {
                log.warn("Unable to connect to mail server.", e);
                throw new RuntimeException(e);
            }

            return transport;
        }

        private void closeTransport() {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    log.warn("Ooooh.... error during closing transport :(", e);
                }
            }
        }
    }
}
