package ru.example.javacode.service.impl;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.example.javacode.models.Wallet;
import ru.example.javacode.repository.WalletRepository;
import ru.example.javacode.service.WalletService;
import ru.example.javacode.service.convertor.WalletMapper;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.WalletDto;
import ru.example.javacode.service.dto.enums.OperationType;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional(readOnly = true)
    public WalletDto findByWalletId(UUID uuid) {
        return walletRepository.findWalletByWalletId(uuid)
                .map(walletMapper::toWalletDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public WalletDto createOrUpdateWallet(CreateOrUpdateWalletDto dto) {
        var result = walletRepository.findWalletByWalletId(dto.getWalletId())
                .map(wallet -> walletRepository.save(
                        Optional.of(wallet)
                                .map(w -> {
                                    w.setAmount(dto.getOperationType() == OperationType.WITHDRAW
                                            ? w.getAmount().subtract(dto.getAmount())
                                            : w.getAmount().add(dto.getAmount()));
                                    return w;
                                })
                                .orElse(wallet)))
                .orElseGet(() -> {
                    var newWallet = new Wallet();
                    newWallet.setWalletId(dto.getWalletId());
                    newWallet.setAmount(dto.getOperationType() == OperationType.DEPOSIT
                            ? dto.getAmount()
                            : dto.getAmount().negate());
                    return walletRepository.save(newWallet);
                });

        return walletMapper.toWalletDto(result);
    }

}
