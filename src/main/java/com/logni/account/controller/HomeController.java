package com.logni.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/")
    public @ResponseBody  String home(){
        return "Hello LoogNi Core Wallet/Account";
    }


    @GetMapping("/docs")
    public String apiDoc(){
        return  "redirect:/v3/api-docs";
    }
}
