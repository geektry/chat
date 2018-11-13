package com.geektry.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class ChatController {

    @GetMapping("")
    public ModelAndView view() {
        return new ModelAndView("redirect:/room/" + UUID.randomUUID().toString());
    }

    @GetMapping("/room/{roomId}")
    public ModelAndView viewRoom(@PathVariable("roomId") String roomId) {
        return new ModelAndView("room").addObject("roomId", roomId);
    }
}
