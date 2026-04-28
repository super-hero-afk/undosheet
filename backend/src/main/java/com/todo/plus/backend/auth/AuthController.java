package com.todo.plus.backend.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
    Map<String, Object> user = Map.of(
        "id", "u_" + System.currentTimeMillis(),
        "username", request.username().trim());
    return ResponseEntity.ok(Map.of("user", user));
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout() {
    return ResponseEntity.ok(Map.of("message", "ok"));
  }

  public record LoginRequest(
      @NotBlank(message = "用户名不能为空") String username,
      @NotBlank(message = "密码不能为空") String password) {
  }
}
