package com.bluesky.player.database.service;

import com.bluesky.player.database.entity.account.Account;
import com.bluesky.player.database.repository.AccountRepository;
import com.bluesky.player.dto.account.AccountMapper;
import com.bluesky.player.dto.account.AccountResponse;
import com.bluesky.player.dto.account.CreateAccountRequest;
import com.bluesky.player.exception.account.AccountNotFoundException;
import com.bluesky.player.exception.account.EmailAlreadyExistsException;
import com.bluesky.player.util.password.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.channels.AcceptPendingException;

@Service
@AllArgsConstructor
public class AccountService {
    public final AccountRepository accountRepository;
    public final PasswordUtil passwordUtil;
    public final AccountMapper accountMapper;

    public AccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.existsByEmail(createAccountRequest.email()))
            throw new EmailAlreadyExistsException("Email address already busy");
        Account account = accountMapper.toEntity(createAccountRequest);
        account.setPassword(passwordUtil.hashPassword(account.getPassword()));
        return accountMapper.toResponse(accountRepository.save(account));
    }
    public AccountResponse getAccountByEmail(String email) {
        Account account  = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for " + email));
        return accountMapper.toResponse(account);
    }

}
