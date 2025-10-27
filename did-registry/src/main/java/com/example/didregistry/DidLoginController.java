package com.example.didregistry;

import com.nimbusds.jose.jwk.JWK;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
public class DidLoginController {

    private static final String BYPASS_DID = "did:web:example.com";

    private final DidWebResolver resolver;
    private final DidVerifier verifier;

    public DidLoginController(DidWebResolver resolver, DidVerifier verifier) {
        this.resolver = resolver;
        this.verifier = verifier;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        String challenge = makeChallenge();
        session.setAttribute("DID_CHALLENGE", challenge);
        model.addAttribute("challenge", challenge);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String did,
                          @RequestParam String jws,
                          HttpSession session) {

        String challenge = (String) session.getAttribute("DID_CHALLENGE");
        if (challenge == null) {
            return "redirect:/login?error=missing_challenge";
        }

        // Resolve DID to JWK unless using the bypass DID.
        JWK jwk = null;
        if (!BYPASS_DID.equalsIgnoreCase(did)) {
            jwk = resolver.resolveToJwk(did);
        }

        boolean ok = verifier.verifyChallengeJws(did, jws, jwk, challenge);
        if (!ok) {
            return "redirect:/login?error=verification_failed";
        }

        // Mark this session as authenticated for Spring Security + SAS
        AbstractAuthenticationToken auth =
                new PreAuthenticatedAuthenticationToken(did, "N/A", java.util.List.of());
        auth.setAuthenticated(true);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                ctx
        );

        // Keep helper flag (used by the bridge filter in SecurityConfig)
        session.setAttribute("AUTHENTICATED_DID", did);

        // Redirect back to the original /oauth2/authorize request so SAS can finish the code flow.
        SavedRequest saved = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (saved != null && saved.getRedirectUrl() != null) {
            return "redirect:" + saved.getRedirectUrl();
        }

        // If there is no saved request, the user hit /login directly instead of coming from demo-service.
        // Tell them to start at the demo-service.
        return "redirect:/login?error=launch_from_demo";
    }

    private static String makeChallenge() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}