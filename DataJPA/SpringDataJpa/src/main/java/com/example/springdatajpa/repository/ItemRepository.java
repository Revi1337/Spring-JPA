package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
