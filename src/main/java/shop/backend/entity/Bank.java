package shop.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}