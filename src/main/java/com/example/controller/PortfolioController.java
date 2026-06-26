package com.example.controller;

import com.example.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PortfolioController {

    private final EmailService emailService;

    public PortfolioController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/projects")
    public String projects() {
        return "projects";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/contact")
    public String sendMessage(@RequestParam("name") String name,
                              @RequestParam("email") String email,
                              @RequestParam("message") String message,
                              Model model) {
        if (!emailService.isConfigured()) {
            model.addAttribute("error", true);
            model.addAttribute("errorMsg", "Email is not configured yet. Please set MAIL_USERNAME and MAIL_PASSWORD.");
            return "contact";
        }
        try {
            emailService.sendContactEmail(name, email, message);
            model.addAttribute("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", true);
            model.addAttribute("errorMsg", "Failed to send message. Please try again later.");
        }
        return "contact";
    }
}
