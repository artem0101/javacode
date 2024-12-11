package ru.example.javacode.service.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {

    @NonNull
    private UUID walletId;

    @NonNull
    @DecimalMin(value = "1")
    private BigDecimal amount;

}
