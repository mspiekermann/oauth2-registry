package com.example.demoservice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) return "redirect:/";
        String username = oidcUser.getPreferredUsername() != null
                ? oidcUser.getPreferredUsername()
                : oidcUser.getSubject();
        String endpoint = (String) oidcUser.getClaims().getOrDefault("connector_endpoint", "N/A");
        model.addAttribute("username", username);
        model.addAttribute("endpoint", endpoint);
        model.addAttribute("allClaims", oidcUser.getClaims());
        return "profile";
    }
}