package com.example.springdebuggingdemo.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class DebugController {

    @GetMapping("/greet/{name}")
    public String greet(String name) {
        return String.format("Hello %s!", name);
    }
}
