package com.bluesky.player.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 25) String password
) {}
