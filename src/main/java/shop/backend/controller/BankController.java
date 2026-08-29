package shop.backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.dto.request.BankRequest;
import shop.backend.dto.response.BankResponse;
import shop.backend.service.BankService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<BankResponse>> create(@Valid @RequestBody BankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Bank created", bankService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> getAll(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.success(bankService.getAll(activeOnly)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        bankService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Bank deactivated", null));
    }
}