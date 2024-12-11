package ru.example.javacode.service.convertor;

import org.mapstruct.Mapper;
import ru.example.javacode.models.Wallet;
import ru.example.javacode.service.dto.WalletDto;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletDto toWalletDto(Wallet wallet);

}
