package ru.example.javacode.service.impl;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.javacode.repository.WalletRepository;
import ru.example.javacode.service.convertor.WalletMapper;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.enums.OperationType;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WalletServiceImpl.class)
class WalletServiceImplTest {

    @Autowired
    private WalletServiceImpl walletService;

    @MockitoBean
    private WalletRepository walletRepository;

    @MockitoBean
    private WalletMapper walletMapper;

    @Test
    void findByWalletIdTest() {
        walletService.findByWalletId(UUID.randomUUID());
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setWalletId(UUID.randomUUID());
        walletOperation.setAmount(BigDecimal.TEN);
        walletOperation.setOperationType(OperationType.DEPOSIT);

        when(walletRepository.findWalletByWalletId(Mockito.any())).thenReturn(null);

        verify(walletRepository, times(1)).findWalletByWalletId(Mockito.any());
    }

    @Test
    void createWalletTest() {
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setWalletId(UUID.randomUUID());
        walletOperation.setAmount(BigDecimal.TEN);
        walletOperation.setOperationType(OperationType.DEPOSIT);

        walletService.createOrUpdateWallet(walletOperation);

        verify(walletRepository, times(1)).save(Mockito.any());
        verify(walletMapper, times(1)).toWalletDto(Mockito.any());
    }

}
