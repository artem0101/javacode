package ru.example.javacode.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "wallet", name = "wallet")
public class Wallet {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq")
    @SequenceGenerator(name = "wallet_id_seq", sequenceName = "wallet.wallet_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "wallet_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID walletId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

}
