package ru.example.javacode.service.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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
//        return Optional.ofNullable(walletMapper.toWalletDto(walletRepository.findWalletByWalletId(uuid)
//                                                                            .get()));

        var result = walletRepository.findWalletByWalletId(uuid).orElse(null);
        return walletMapper.toWalletDto(result);
    }

    @Override
    @Transactional
    public WalletDto createOrUpdateWallet(CreateOrUpdateWalletDto dto) {
        var resultWallet = new AtomicReference<Wallet>();
        walletRepository.findWalletByWalletId(dto.getWalletId())
                        .ifPresentOrElse(wallet -> {
                            if (dto.getOperationType() == OperationType.WITHDRAW) {
                                wallet.setAmount(wallet.getAmount()
                                                       .subtract(dto.getAmount()));
                            } else if (dto.getOperationType() == OperationType.DEPOSIT) {
                                wallet.setAmount(wallet.getAmount()
                                                       .add(dto.getAmount()));
                            }

                            resultWallet.set(walletRepository.save(wallet));
                        }, () -> {
                            var newAmount = dto.getOperationType()
                                               .equals(OperationType.DEPOSIT) ? dto.getAmount() : dto.getAmount()
                                                                                                     .negate();

                            var newWallet = new Wallet();
                            newWallet.setWalletId(dto.getWalletId());
                            newWallet.setAmount(newAmount);

                            resultWallet.set(walletRepository.save(newWallet));
                        });

        return walletMapper.toWalletDto(resultWallet.get());
    }

}
