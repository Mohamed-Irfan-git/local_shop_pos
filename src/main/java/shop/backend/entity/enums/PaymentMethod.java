package shop.backend.entity.enums;

/**
 * Shared across sale_payments, supplier_payments and expenses.
 * Not every method applies to every table (e.g. customer sales don't use CHEQUE
 * in V1) — validity per-context is enforced in the service layer, not here.
 */
public enum PaymentMethod {
    CASH,
    CARD,
    BANK_TRANSFER,
    CHEQUE
}