package roomescape.controller.rest;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.rest.request.LoginRequest;
import roomescape.controller.rest.response.LoginCheckResponse;
import roomescape.service.AuthService;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest body, HttpServletResponse response) {
        Cookie cookie = authService.createToken(body);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> check(@CookieValue String token) {
        LoginCheckResponse loginCheckResponse = authService.check(token);
        return ResponseEntity.ok().body(loginCheckResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}