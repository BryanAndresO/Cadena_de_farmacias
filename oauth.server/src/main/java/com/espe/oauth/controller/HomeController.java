package com.espe.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Redirect to Gateway by default if someone lands here
        // This handles cases where users log in directly at port 9000 without a saved
        // request
        return "redirect:http://localhost:8080/";
    }

    @GetMapping("/status")
    @ResponseBody
    public String status() {
        return "<h1>Auth Server is Running 🚀</h1>";
    }
}
