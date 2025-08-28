package com.bluesky.player.dto.account;

import com.bluesky.player.database.entity.account.Account;
import com.bluesky.player.database.entity.audio.Playlist;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public Account toEntity(CreateAccountRequest createAccountRequest) {
        Account account = new Account();
        account.setEmail(createAccountRequest.email());
        account.setPassword(createAccountRequest.password());
        return account;
    }
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getEmail(), account.getPassword());
    }
}
