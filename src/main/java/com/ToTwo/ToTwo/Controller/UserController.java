package com.ToTwo.ToTwo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ToTwo.ToTwo.model.User;
import com.ToTwo.ToTwo.Repo.UserRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin // allows frontend requests (React/Angular etc.)
public class UserController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ✅ Create User
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // ✅ Get All Users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Get User by ID
 // ✅ Get User by ID (with avatar fallback)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Generate default avatar if missing
        if (user.getAvatarUrl() == null) {
            user.setAvatarUrl(
            		"https://api.dicebear.com/7.x/thumbs/svg?seed=" 
            				+ user.getId()
            				+ "&backgroundColor=b39ddb,d1c4e9,ede7f6,ce93d8,ba68c8"

            );
        }

        return user;
    }

	// ✅ UPDATE: Get CURRENT logged-in user using JWT
    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(null); // never expose password

        return user;
    }


   // ✅ Update User
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setBio(userDetails.getBio()); 
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    } 
   

    // ✅ Delete User
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted with id: " + id;
    }
    
    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("Not allowed");
        }

        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = "avatar_" + id + "_" + System.currentTimeMillis()
                + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir + filename);
        Files.write(filePath, file.getBytes());

        String avatarUrl = "https://localhost:8443/uploads/" + filename;

        currentUser.setAvatarUrl(avatarUrl);
        userRepository.save(currentUser);

        return ResponseEntity.ok(avatarUrl);
    }
    
}
