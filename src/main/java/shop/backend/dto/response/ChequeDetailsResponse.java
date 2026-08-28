package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.ChequeStatus;

import java.time.LocalDate;

@Getter @Builder @AllArgsConstructor
public class ChequeDetailsResponse {
    private Long id;
    private Long bankId;
    private String bankName;
    private String chequeNumber;
    private LocalDate chequeDate;
    private LocalDate expectedPassDate;
    private ChequeStatus status;
}