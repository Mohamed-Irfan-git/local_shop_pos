package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.BankResponse;
import shop.backend.entity.Bank;

@Component
public class BankMapper {

    public BankResponse toResponse(Bank bank) {
        if (bank == null) return null;
        return BankResponse.builder()
                .id(bank.getId())
                .name(bank.getName())
                .isActive(bank.getIsActive())
                .build();
    }
}