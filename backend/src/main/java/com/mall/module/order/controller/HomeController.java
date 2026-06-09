package com.mall.module.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/cart.html";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/cart.html";
    }
}
