package com.geektry.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChatController {

    @GetMapping("")
    public ModelAndView view() {
        return new ModelAndView("index");
    }
}
