package shop.backend.service;

import shop.backend.dto.request.BankRequest;
import shop.backend.dto.response.BankResponse;

import java.util.List;

public interface BankService {
    BankResponse create(BankRequest request);
    List<BankResponse> getAll(boolean activeOnly);
    void deactivate(Long id);
}