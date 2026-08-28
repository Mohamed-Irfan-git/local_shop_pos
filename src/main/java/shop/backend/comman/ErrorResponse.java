package shop.backend.comman;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
}