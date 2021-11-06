package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("hello")
    public String Hello(Model model) {
        model.addAttribute("data", "Hello");

        return "hello";
    }

    @GetMapping(value = "/test")
    public String test2() {
        System.out.println("RestController.test2");
        return "OK";
    }
}
