package com.genhub.service.blog;

import java.util.Map;


public interface MailService {
    void config();
    void sendTemplateEmail(String to, String title, String template, Map<String, Object> content);
}
