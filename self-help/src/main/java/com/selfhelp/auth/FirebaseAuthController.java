package com.selfhelp.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.selfhelp.common.ApiResponse;
import com.selfhelp.user.Role;
import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class FirebaseAuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/firebase")
    public ResponseEntity<?> authenticateWithFirebase(@RequestBody FirebaseAuthRequest request) {
        System.out.println("📥 Received Firebase auth request");
        System.out.println("📧 Email: " + request.getEmail());
        System.out.println("👤 Name: " + request.getName());
        System.out.println("🎫 Token: " + request.getFirebaseIdToken().substring(0, 20) + "...");

        try {
            // Step 1: Verify Firebase ID token
            System.out.println("🔐 Verifying Firebase token...");
            FirebaseToken decodedToken = FirebaseAuth.getInstance()
                    .verifyIdToken(request.getFirebaseIdToken());

            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            System.out.println("✅ Firebase token verified");
            System.out.println("🆔 Firebase UID: " + firebaseUid);
            System.out.println("📧 Verified email: " + email);

            // Step 2: Find or create user in database
            System.out.println("🔍 Looking for user in database...");
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        System.out.println("👤 Creating new user...");
                        User newUser = User.builder()
                                .email(email)
                                .name(request.getName())
                                .imageUrl(request.getImageUrl())
                                .role(Role.USER)
                                .build();
                        User savedUser = userRepository.save(newUser);
                        System.out.println("✅ New user created with ID: " + savedUser.getId());
                        return savedUser;
                    });

            System.out.println("✅ User found/created: " + user.getEmail());

            // Step 3: Generate your backend JWT token
            System.out.println("🎫 Generating backend JWT token...");
            String backendJwt = jwtService.generateToken(user);
            System.out.println("✅ Backend JWT generated: " + backendJwt.substring(0, 20) + "...");

            // Step 4: Return response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", backendJwt);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().name()
            ));

            System.out.println("✅ Sending success response to client");
            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            System.err.println("❌ Firebase token verification failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid Firebase token", null));

        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Authentication failed", null));
        }
    }
}
