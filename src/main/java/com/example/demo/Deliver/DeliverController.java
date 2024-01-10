package com.example.demo.Deliver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deliver")
@RequiredArgsConstructor
public class DeliverController {

    @GetMapping("/{id}/cus")
    public String detailPage( @PathVariable Long id){
        return "Deliver/DetailPage";
    }
    @GetMapping("/{id}/admin")
    public String AdminDetailPage( @PathVariable Long id){
        return "/Admin/Deliver/DetailPage";
    }

}
