package com.haidv.lab.ecommerce.controller;

import com.haidv.lab.ecommerce.dto.AuthenticationRequestDto;
import com.haidv.lab.ecommerce.dto.PasswordResetRequestDto;
import com.haidv.lab.ecommerce.dto.user.UserResponseDto;
import com.haidv.lab.ecommerce.exception.ApiRequestException;
import com.haidv.lab.ecommerce.exception.PasswordConfirmationException;
import com.haidv.lab.ecommerce.exception.PasswordException;
import com.haidv.lab.ecommerce.mapper.UserMapper;
import com.haidv.lab.ecommerce.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final ControllerUtils controllerUtils;
    private static final Logger logger = LogManager.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthenticationRequestDto request) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            return ResponseEntity.ok(userMapper.login(request.getEmail()));
        } catch (AuthenticationException e) {
            logger.error(request.getEmail());
            throw new ApiRequestException("Incorrect password or email", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequestDto passwordReset) {
        boolean forgotPassword = userMapper.sendPasswordResetCode(passwordReset.getEmail());
        if (!forgotPassword) {
            throw new ApiRequestException("Email not found", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Reset password code is send to your E-mail");
    }

    @GetMapping("/reset/{code}")
    public ResponseEntity<UserResponseDto> getPasswordResetCode(@PathVariable String code) {
        UserResponseDto user = userMapper.findByPasswordResetCode(code);
        if (user == null) {
            throw new ApiRequestException("Password reset code is invalid!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> passwordReset(@RequestBody PasswordResetRequestDto passwordReset) {
        if (controllerUtils.isPasswordConfirmEmpty(passwordReset.getPassword2())) {
            throw new PasswordConfirmationException("Password confirmation cannot be empty.");
        }
        if (controllerUtils.isPasswordDifferent(passwordReset.getPassword(), passwordReset.getPassword2())) {
            throw new PasswordException("Passwords do not match.");
        }
        return ResponseEntity.ok(userMapper.passwordReset(passwordReset.getEmail(), passwordReset.getPassword()));
    }
}
