package com.example.blog.controllers.web;

import com.example.blog.domain.Authors;
import com.example.blog.service.manager.AuthorsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller("registerwebcontroller")
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private AuthorsManager authorsManager;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;


    @PostMapping
    public String registrationAuthor(@Valid @ModelAttribute("user") Authors author, BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        authorsManager.save(author);

        //mail sender
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        Map<String, Object> doubleBraceMap  = new HashMap<String, Object>() {{
            put("user", author);
        }};
        context.setVariables(doubleBraceMap);
        helper.setTo(author.getEmail());
        helper.setSubject("Successful registration\n");
        String html = springTemplateEngine.process("email", context);
        helper.setText(html, true);
        javaMailSender.send(message);

        return "redirect:/register?success";
    }

    @GetMapping
    public String showForm() {
        return "register";
    }

    @ModelAttribute("user")
    public Authors userRegistration() {
        return new Authors();
    }

}
