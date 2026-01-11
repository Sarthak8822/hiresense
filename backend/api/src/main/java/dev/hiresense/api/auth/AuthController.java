package dev.hiresense.api.auth;

import dev.hiresense.api.security.JwtService;
import dev.hiresense.api.user.User;
import dev.hiresense.api.user.UserRepo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record SignupReq(@Email String email, @NotBlank String password) {}
record LoginReq(@Email String email, @NotBlank String password) {}
record TokenRes(String token) {}
record MeRes(String email) {}

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepo repo;
    private final PasswordEncoder enc;
    private final JwtService jwt;

    public AuthController(UserRepo repo, PasswordEncoder enc, JwtService jwt) {
        this.repo = repo; this.enc = enc; this.jwt = jwt;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReq r) {
        if (r.password() == null || r.password().length() < 6) return ResponseEntity.badRequest().build();
        if (repo.findByEmail(r.email()).isPresent()) return ResponseEntity.status(409).body("Email exists");
        var u = repo.save(new User(r.email(), enc.encode(r.password())));
        return ResponseEntity.ok(new TokenRes(jwt.issue(u.getEmail())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq r) {
        var u = repo.findByEmail(r.email()).orElse(null);
        if (u == null || !enc.matches(r.password(), u.getPasswordHash()))
            return ResponseEntity.status(401).body("Invalid credentials");
        return ResponseEntity.ok(new TokenRes(jwt.issue(u.getEmail())));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value="Authorization", required=false) String h) {
        if (h == null || !h.startsWith("Bearer ")) return ResponseEntity.status(401).build();
        String sub = jwt.parseSubject(h.substring(7).trim());
        if (sub == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new MeRes(sub));
    }
}
