package com.example.springdatajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
public class Item implements Persistable<String> {

//    @Id @GeneratedValue
//    private Long id;

    @Id
    private String id;

    @CreatedDate
    private LocalDate createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
