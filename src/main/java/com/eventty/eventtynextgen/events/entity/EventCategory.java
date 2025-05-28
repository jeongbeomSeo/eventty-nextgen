package com.eventty.eventtynextgen.events.entity;

import com.eventty.eventtynextgen.events.entity.enums.CategoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CategoryType name;

    public EventCategory(CategoryType name) {
        this.name = name;
    }
}
