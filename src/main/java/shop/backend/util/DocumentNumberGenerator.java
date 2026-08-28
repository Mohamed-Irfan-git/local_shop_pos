package shop.backend.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates human-readable, sequential-looking document numbers (SALE-2026-000123 style).
 * Uses a per-JVM atomic counter seeded per day; the actual DB UNIQUE constraint on each
 * *_number column is the real safety net across concurrent POS terminals — if two terminals
 * race and collide, the insert fails fast and the caller retries with a fresh number.
 */
@Component
public class DocumentNumberGenerator {

    private final AtomicLong saleSeq = new AtomicLong(0);
    private final AtomicLong grnSeq = new AtomicLong(0);
    private final AtomicLong supplierPaymentSeq = new AtomicLong(0);
    private final AtomicLong expenseSeq = new AtomicLong(0);

    public String nextSaleNumber() {
        return format("SALE", saleSeq.incrementAndGet());
    }

    public String nextGrnNumber() {
        return format("GRN", grnSeq.incrementAndGet());
    }

    public String nextSupplierPaymentNumber() {
        return format("SPAY", supplierPaymentSeq.incrementAndGet());
    }

    public String nextExpenseNumber() {
        return format("EXP", expenseSeq.incrementAndGet());
    }

    private String format(String prefix, long seq) {
        int year = LocalDate.now().getYear();
        return "%s-%d-%06d".formatted(prefix, year, seq);
    }
}