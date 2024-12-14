package ru.example.javacode.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import ru.example.javacode.service.dto.CreateOrUpdateWalletDto;
import ru.example.javacode.service.dto.enums.OperationType;
import ru.example.javacode.service.dto.WalletDto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15://testcontainers/wallet"
})
class WalletIntegrationTest {

    @LocalServerPort
    private Integer port;

    private static final String V_1_WALLETS = "/api/v1/wallets/";
    private RequestSpecification requestSpecification;

    @BeforeEach
    public void setUpAbstractIntegrationTest() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpecification = new RequestSpecBuilder()
                .setPort(port)
                .addHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Test
    void findNonExistedWalletTest() {
        given(requestSpecification)
                .when()
                .get(V_1_WALLETS + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

    @Test
    void findCreatedWalletsTest() {
        var uuid = UUID.randomUUID();

        given(requestSpecification)
                .when()
                .get(V_1_WALLETS + uuid)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log()
                .ifValidationFails(LogDetail.ALL);

        var firstWalletOperation = new CreateOrUpdateWalletDto();
        firstWalletOperation.setWalletId(uuid);
        firstWalletOperation.setAmount(BigDecimal.TEN);
        firstWalletOperation.setOperationType(OperationType.DEPOSIT);

        var secondWalletOperation = new CreateOrUpdateWalletDto();
        secondWalletOperation.setWalletId(uuid);
        secondWalletOperation.setAmount(BigDecimal.ONE);
        secondWalletOperation.setOperationType(OperationType.WITHDRAW);

        createOperationWallet(firstWalletOperation);

        getWalletInfo(uuid, secondWalletOperation, firstWalletOperation.getAmount());

        var expectedAmount = firstWalletOperation.getAmount().subtract(secondWalletOperation.getAmount());

        createOperationWallet(secondWalletOperation, expectedAmount);

        getWalletInfo(uuid, secondWalletOperation, expectedAmount);
    }

    private static void assertionWalletInfo(CreateOrUpdateWalletDto secondWalletOperation, BigDecimal expectedAmount, WalletDto secondGetResult) {
        Assertions.assertEquals(secondGetResult.getWalletId(), secondWalletOperation.getWalletId());
        Assertions.assertEquals(0, secondGetResult.getAmount().compareTo(expectedAmount));
    }

    private void getWalletInfo(UUID uuid, CreateOrUpdateWalletDto secondWalletOperation, BigDecimal expectedAmount) {
        var secondGetResult = getRequest(uuid, HttpStatus.OK.value())
                .extract()
                .as(WalletDto.class);

        assertionWalletInfo(secondWalletOperation, expectedAmount, secondGetResult);
    }

    private void createOperationWallet(CreateOrUpdateWalletDto dto,  BigDecimal amount) {
        var result = postRequest(dto, HttpStatus.CREATED.value())
                .extract()
                .as(WalletDto.class);

        assertionWalletInfo(dto, amount, result);
    }

    private void createOperationWallet(CreateOrUpdateWalletDto dto) {
        var result = postRequest(dto, HttpStatus.CREATED.value())
                .extract()
                .as(WalletDto.class);

        assertionWalletInfo(dto, dto.getAmount(), result);
    }

    @Test
    void createdWalletWithZeroAmountTest() {
        var walletOperation = new CreateOrUpdateWalletDto();
        walletOperation.setWalletId(UUID.randomUUID());
        walletOperation.setAmount(BigDecimal.ZERO);
        walletOperation.setOperationType(OperationType.DEPOSIT);

        postRequest(walletOperation, HttpStatus.BAD_REQUEST.value());
    }

    private ValidatableResponse postRequest(CreateOrUpdateWalletDto dto, int statusCode) {
        String postUrl = "/api/v1/wallet";
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .and()
                .body(dto)
                .when()
                .post(postUrl)
                .then()
                .statusCode(statusCode)
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

    private ValidatableResponse getRequest(UUID uuid, int statusCode) {
        return given(requestSpecification)
                .when()
                .get(V_1_WALLETS + uuid)
                .then()
                .statusCode(statusCode)
                .body("size()", is(2))
                .log()
                .ifValidationFails(LogDetail.ALL);
    }

}
