package shop.backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.comman.PageResponse;
import shop.backend.dto.request.GrnRequest;
import shop.backend.dto.response.GrnResponse;
import shop.backend.entity.enums.GrnStatus;
import shop.backend.repository.CustomUserDetails;
import shop.backend.service.GrnService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/grns")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class GrnController {

    private final GrnService grnService;

    @PostMapping
    public ResponseEntity<ApiResponse<GrnResponse>> createDraft(@Valid @RequestBody GrnRequest request,
                                                                @AuthenticationPrincipal CustomUserDetails principal) {
        GrnResponse response = grnService.createDraft(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("GRN draft created", response));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<GrnResponse>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("GRN confirmed", grnService.confirm(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<GrnResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("GRN cancelled", grnService.cancel(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrnResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(grnService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GrnResponse>>> search(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) GrnStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(grnService.search(supplierId, status, start, end, page, size)));
    }
}