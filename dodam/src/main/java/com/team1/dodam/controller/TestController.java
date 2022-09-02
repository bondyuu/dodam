package com.team1.dodam.controller;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test completed";
    }
}