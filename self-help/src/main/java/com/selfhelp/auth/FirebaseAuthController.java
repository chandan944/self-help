package com.selfhelp.auth;

import com.selfhelp.common.ApiResponse;
import com.selfhelp.user.Role;
import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class FirebaseAuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @PostMapping("/firebase")
    public ResponseEntity<?> authenticateWithFirebase(@RequestBody FirebaseAuthRequest request) {
        System.out.println("📥 Auth request from: " + request.getEmail());

        try {
            // Step 1: Verify Google token
            String accessToken = request.getFirebaseIdToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> googleResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v2/userinfo",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            String email = (String) googleResponse.getBody().get("email");
            System.out.println("✅ Google token verified: " + email);

            // Step 2: Find or create user
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        System.out.println("👤 Creating new user");
                        User newUser = User.builder()
                                .email(email)
                                .name(request.getName())
                                .imageUrl(request.getImageUrl())
                                .role(Role.USER)
                                .build();
                        return userRepository.save(newUser);
                    });

            // Step 3: Generate backend JWT
            String backendJwt = jwtService.generateToken(user);
            System.out.println("✅ JWT generated for: " + user.getEmail());

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

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Auth error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Authentication failed", null));
        }
    }
}