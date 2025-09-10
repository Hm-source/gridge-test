package org.example.gridgestagram.repository.subscription.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "terms")
    private String terms;

    @Column(name = "price", precision = 10)
    private BigDecimal price;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Builder.Default
    @OneToMany(mappedBy = "subscription", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<SubscriptionHistory> subscriptionHistories = new ArrayList<>();

    public static Subscription create(String name,
        BigDecimal price, String terms) {
        return Subscription.builder()
            .name(name)
            .price(price)
            .terms(terms)
            .build();
    }
}