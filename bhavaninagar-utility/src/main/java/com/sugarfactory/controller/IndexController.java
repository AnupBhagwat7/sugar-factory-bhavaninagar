package com.sugarfactory.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({ "/aaa", "/index1111" })
public class IndexController {

    @GetMapping
    public String main(Model model) {
        return "index";
    }

    @GetMapping("{tabaaaa}")
    public String tab(@PathVariable String tab) {
        if (Arrays.asList("searchUpdateDistance", "distanceInfo")
                .contains(tab)) {
            return tab;
        }

        return "empty";
    }
}