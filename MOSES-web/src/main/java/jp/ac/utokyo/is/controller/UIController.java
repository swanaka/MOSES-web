package jp.ac.utokyo.is.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class UIController {
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("value", "Hello World!");
        return "index";
    }
}
