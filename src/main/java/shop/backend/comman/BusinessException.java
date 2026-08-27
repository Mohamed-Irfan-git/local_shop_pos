package shop.backend.comman;

/** Thrown for domain-rule violations (e.g. cancelling an already-cancelled payment, duplicate SKU). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}