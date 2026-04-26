package com.ToTwo.ToTwo.Controller;

import com.ToTwo.ToTwo.model.User;
import com.ToTwo.ToTwo.Repo.UserRepo;
import com.ToTwo.ToTwo.Security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTER WITH SIGNUP COUNTER (1–100)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // ❌ email already exists
    	if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("EMAIL_EXISTS");
        }

        // ✅ count current users
        long currentCount = userRepo.count();

        // ❌ limit reached
        if (currentCount >= 100) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("SIGNUP_LIMIT_REACHED");
        }

        // ✅ save user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        // ✅ calculate position
        long signupNumber = currentCount + 1;

        // ✅ response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "SIGNUP_SUCCESS");
        response.put("signupNumber", signupNumber);

        return ResponseEntity.ok(response);
    }

    // ✅ LOGIN (UNCHANGED)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

    	User user = userRepo.findByEmail(loginRequest.getEmail())
    	        .orElseThrow(() -> new RuntimeException("Invalid email or password"));


        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
