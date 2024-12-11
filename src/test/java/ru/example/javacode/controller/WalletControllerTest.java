package ru.example.javacode.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.enums.OperationType;
import ru.example.javacode.service.dto.WalletDto;
import ru.example.javacode.service.impl.WalletServiceImpl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WalletController.class)
class WalletControllerTest {

    private static final String GET_URL = "/api/v1/wallets/";
    private static final String POST_URL = "/api/v1/wallet";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletServiceImpl walletService;

    @Test
    void findNonExistedWalletTest() throws Exception {
        var uuid = UUID.randomUUID();

        when(walletService.findByWalletId(uuid)).thenReturn(null);

        mockMvc.perform(get(GET_URL.concat(uuid.toString())))
               .andExpect(status().isNotFound());
    }

    @Test
    void findExistedWalletTest() throws Exception {
        var uuid = UUID.randomUUID();

        var wallet = Mockito.mock(WalletDto.class);

        when(walletService.findByWalletId(uuid)).thenReturn(wallet);

        mockMvc.perform(get(GET_URL.concat(uuid.toString())))
               .andExpect(status().isOk());
    }

    @Test
    void createWalletWithInsufficientNumberOfFieldsTest() throws Exception {
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setAmount(BigDecimal.TEN);

        var requestJson = getJsonWalletObject(walletOperation);

        mockMvc.perform(post(POST_URL)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(requestJson)
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    void createWalletWithNegativeAmountTest() throws Exception {
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setWalletId(UUID.randomUUID());
        walletOperation.setAmount(new BigDecimal("-14.51"));
        walletOperation.setOperationType(OperationType.DEPOSIT);

        var requestJson = getJsonWalletObject(walletOperation);

        mockMvc.perform(post(POST_URL)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(requestJson)
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    void createWalletTest() throws Exception {
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setWalletId(UUID.randomUUID());
        walletOperation.setAmount(BigDecimal.TEN);
        walletOperation.setOperationType(OperationType.DEPOSIT);

        var requestJson = getJsonWalletObject(walletOperation);

        when(walletService.createOrUpdateWallet(walletOperation)).thenReturn(Mockito.mock(WalletDto.class));

        mockMvc.perform(post(POST_URL)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(requestJson)
               )
               .andDo(print())
               .andExpect(status().isCreated());
    }

    private String getJsonWalletObject(CreateOrUpdateWalletDto dto) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        var ow = mapper.writer()
                       .withDefaultPrettyPrinter();
        return ow.writeValueAsString(dto);
    }

}
