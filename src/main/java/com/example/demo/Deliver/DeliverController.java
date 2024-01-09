package com.example.demo.Deliver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/deliver")
public class DeliverController {

    @GetMapping("/list")
    public String listPage(@RequestParam("p")Integer page){
        return "Deliver/DeliverList";
    }

}
