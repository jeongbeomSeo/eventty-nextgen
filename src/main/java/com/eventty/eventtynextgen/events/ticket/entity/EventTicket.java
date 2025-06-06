package com.eventty.eventtynextgen.events.ticket.entity;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.ticket.entity.enums.EventTicketQuantityLimitType;
import com.eventty.eventtynextgen.events.ticket.entity.enums.EventTicketStatusType;
import com.eventty.eventtynextgen.events.ticket.entity.enums.EventTicketPurchaseLimitPolicyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "event_ticket")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_basic_id", nullable = false)
    private Long eventBasicId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Long price;

    @Comment("총 수량 제한 정책")
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_limit_policy", nullable = false)
    private EventTicketQuantityLimitType quantityLimitPolicy;

    @Comment("수량 제한의 최대값")
    @Column(name = "max_quantity_limit")
    private Integer maxQuantityLimit;

    @Comment("발급된 수량")
    @Column(name = "issued_quantity", nullable = false)
    @ColumnDefault("0")
    private Integer issuedQuantity;

    @Comment("1인당 구매 제한 정책")
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_limit_policy", nullable = false)
    private EventTicketPurchaseLimitPolicyType purchaseLimitPolicy;

    @Comment("1인당 구매 제한 수량")
    @Column(name = "max_quantity_per_user", nullable = false)
    private Integer maxQuantityPerUser;

    @Comment("판매 시작일시")
    @Column(name = "sale_start_at", nullable = false)
    private LocalDateTime saleStartAt;

    @Comment("판매 종료일시")
    @Column(name = "sale_end_at", nullable = false)
    private LocalDateTime saleEndAt;

    @Comment("티켓 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventTicketStatusType status;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public EventTicket(Long eventBasicId, String name, String description, Long price, EventTicketQuantityLimitType quantityLimitPolicy,
        Integer maxQuantityLimit,
        Integer issuedQuantity, EventTicketPurchaseLimitPolicyType purchaseLimitPolicy, Integer maxQuantityPerUser, LocalDateTime saleStartAt,
        LocalDateTime saleEndAt, EventTicketStatusType status, boolean isDeleted, LocalDateTime deletedAt) {
        this.eventBasicId = eventBasicId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityLimitPolicy = quantityLimitPolicy;
        this.maxQuantityLimit = maxQuantityLimit;
        this.issuedQuantity = issuedQuantity;
        this.purchaseLimitPolicy = purchaseLimitPolicy;
        this.maxQuantityPerUser = maxQuantityPerUser;
        this.saleStartAt = saleStartAt;
        this.saleEndAt = saleEndAt;
        this.status = status;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static EventTicket of(Long eventBasicId, String name, String description, Long price, EventTicketQuantityLimitType quantityLimitPolicy,
        Integer maxQuantityLimit, EventTicketPurchaseLimitPolicyType purchaseLimitPolicy, Integer maxQuantityPerUser,
        LocalDateTime saleStartAt, LocalDateTime saleEndAt, EventTicketStatusType status) {
        return EventTicket.builder()
            .eventBasicId(eventBasicId)
            .name(name)
            .description(description)
            .price(price)
            .quantityLimitPolicy(quantityLimitPolicy)
            .maxQuantityLimit(maxQuantityLimit)
            .issuedQuantity(0)
            .purchaseLimitPolicy(purchaseLimitPolicy)
            .maxQuantityPerUser(maxQuantityPerUser)
            .saleStartAt(saleStartAt)
            .saleEndAt(saleEndAt)
            .status(status)
            .isDeleted(false)
            .deletedAt(null)
            .build();
    }
}
