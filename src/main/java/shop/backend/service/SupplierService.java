package shop.backend.service;


import shop.backend.dto.request.SupplierRequest;
import shop.backend.dto.response.SupplierBalanceResponse;
import shop.backend.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Long id, SupplierRequest request);
    SupplierResponse getById(Long id);
    List<SupplierResponse> getAll(boolean activeOnly);
    void deactivate(Long id);

    /** Outstanding = confirmed GRNs - active payments, computed live (spec section 19). */
    SupplierBalanceResponse getBalance(Long supplierId);
    List<SupplierBalanceResponse> getAllBalances();
}