package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.SupplierRequest;
import shop.backend.dto.response.SupplierBalanceResponse;
import shop.backend.dto.response.SupplierResponse;
import shop.backend.entity.Supplier;
import shop.backend.mapper.SupplierMapper;
import shop.backend.repository.GrnRepository;
import shop.backend.repository.SupplierPaymentRepository;
import shop.backend.repository.SupplierRepository;
import shop.backend.service.SupplierService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final GrnRepository grnRepository;
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = findEntity(id);
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        if (request.getIsActive() != null) {
            supplier.setIsActive(request.getIsActive());
        }
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(Long id) {
        return supplierMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAll(boolean activeOnly) {
        List<Supplier> suppliers = activeOnly ? supplierRepository.findByIsActiveTrue() : supplierRepository.findAll();
        return suppliers.stream().map(supplierMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        Supplier supplier = findEntity(id);
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierBalanceResponse getBalance(Long supplierId) {
        Supplier supplier = findEntity(supplierId);
        return buildBalance(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierBalanceResponse> getAllBalances() {
        return supplierRepository.findAll().stream().map(this::buildBalance).collect(Collectors.toList());
    }

    private SupplierBalanceResponse buildBalance(Supplier supplier) {
        BigDecimal totalGrn = grnRepository.sumConfirmedTotalBySupplier(supplier.getId());
        BigDecimal totalPaid = supplierPaymentRepository.sumActiveTotalBySupplier(supplier.getId());
        return SupplierBalanceResponse.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .totalGrn(totalGrn)
                .totalPaid(totalPaid)
                .outstanding(totalGrn.subtract(totalPaid))
                .build();
    }

    private Supplier findEntity(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Supplier", id));
    }
}