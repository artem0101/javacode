package ru.example.javacode.service;

import java.util.Optional;
import java.util.UUID;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.WalletDto;

public interface WalletService {

    WalletDto findByWalletId(UUID uuid);

    WalletDto createOrUpdateWallet(CreateOrUpdateWalletDto dto);

}
