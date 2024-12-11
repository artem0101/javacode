package ru.example.javacode.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import ru.example.javacode.service.WalletService;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.WalletDto;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("wallets/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<WalletDto> findWallet(@PathVariable UUID uuid) {
        var result = walletService.findByWalletId(uuid);
        if (Objects.isNull(result)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("wallet")
    public ResponseEntity<WalletDto> createWallet(@Valid @RequestBody CreateOrUpdateWalletDto dto) throws URISyntaxException {
        var result = walletService.createOrUpdateWallet(dto);

        return ResponseEntity.created(new URI("/api/v1/wallet" + result.getWalletId()))
                             .body(result);
    }

}
