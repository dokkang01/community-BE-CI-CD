package kakaotechbootcamp.communityservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;


@Controller
public class LegalController {
    @GetMapping("/api/terms")
    public String terms(Model model) {
        model.addAttribute("lastUpdated", LocalDate.of(2025, 10, 23)); // 필요시 변경
        model.addAttribute("title", "커뮤니티 이용약관");
        return "termsOfUse";
    }

    @GetMapping("/api/privacy")
    public String privacy(Model model) {
        model.addAttribute("lastUpdated", LocalDate.of(2025, 10, 1)); // 필요시 변경
        model.addAttribute("title", "개인정보 처리방침");
        return "privacyPolicy";
    }
}
