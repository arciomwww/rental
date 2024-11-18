package com.example.rental.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "application_account")
public class ApplicationAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}
