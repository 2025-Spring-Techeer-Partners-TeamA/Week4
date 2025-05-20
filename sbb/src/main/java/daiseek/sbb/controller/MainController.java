package daiseek.sbb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/sbb")
    @ResponseBody // 애노테이션이 없으면, 템플릿에서 index.html 파일을 찾음
    public String index() {
        System.out.println("index");
        return "index";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }}
