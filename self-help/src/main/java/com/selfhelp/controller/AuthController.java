package com.selfhelp.controller;

import com.selfhelp.auth.GoogleAuthRequest;
import com.selfhelp.auth.JwtService;
import com.selfhelp.user.Role;
import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${google.client.id}")
    private String googleClientId;

    // ✅ Define admin emails here (or move to application.properties)
    private static final List<String> ADMIN_EMAILS = Arrays.asList(
            "kanchanparajapati4@gmail.com" // Your admin email
                          // Add more admin emails here
    );

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "Self Help Backend",
                "version", "1.0.0"
        ));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            System.out.println("📥 Received Google auth request");
            System.out.println("📧 Email: " + request.getEmail());
            System.out.println("👤 Name: " + request.getName());

            // Verify Google ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());

            if (idToken == null) {
                System.err.println("❌ Invalid Google ID token");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid Google token"
                ));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            if (!email.equals(request.getEmail())) {
                System.err.println("❌ Email mismatch");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Email verification failed"
                ));
            }

            System.out.println("✅ Google token verified for: " + email);

            // ✅ Determine role based on email whitelist
            Role userRole = isAdminEmail(email) ? Role.ADMIN : Role.USER;
            System.out.println("🔐 Assigned role: " + userRole + " for email: " + email);

            // Find or create user
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        System.out.println("🆕 Creating new user: " + email);
                        return userRepository.save(
                                User.builder()
                                        .email(email)
                                        .name(request.getName())
                                        .imageUrl(request.getImageUrl())
                                        .role(userRole)  // ✅ Use determined role
                                        .build()
                        );
                    });

            // ✅ Update existing user's role if it changed (optional)
            if (!user.getRole().equals(userRole)) {
                System.out.println("🔄 Updating user role from " + user.getRole() + " to " + userRole);
                user.setRole(userRole);
                userRepository.save(user);
            }

            // Generate JWT token
            String jwtToken = jwtService.generateToken(user);
            System.out.println("🎫 JWT generated for user ID: " + user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", jwtToken,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "name", user.getName(),
                            "imageUrl", user.getImageUrl() != null ? user.getImageUrl() : "",
                            "role", user.getRole().name()
                    )
            ));

        } catch (Exception e) {
            System.err.println("❌ Authentication error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Authentication failed: " + e.getMessage()
            ));
        }
    }

    // ✅ Helper method to check if email is admin
    private boolean isAdminEmail(String email) {
        return ADMIN_EMAILS.stream()
                .anyMatch(adminEmail -> adminEmail.equalsIgnoreCase(email));
    }
}