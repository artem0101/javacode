package ru.example.javacode.service.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.example.javacode.service.dto.enums.OperationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateWalletDto {

    @NonNull
    private UUID walletId;

    @NonNull
    private OperationType operationType;

    @NonNull
    @DecimalMin(value = "1")
    private BigDecimal amount;

}
