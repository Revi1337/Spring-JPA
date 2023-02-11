package org.example.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "ORDERS")
public class Order {

    @Id @GeneratedValue @Column(name = "ORDER_ID") private Long id;

    @ManyToOne @JoinColumn(name = "MEMBER_ID") private Member member;

    private LocalDateTime orderDate;

    @Enumerated(value = EnumType.STRING) private OrderStatus status;

    @OneToMany(mappedBy = "item") private List<OrderItem> orderItems = new ArrayList<>(); // TODO 연관관계의 주인은 OrderItem 의 item 필드

    public void addOrderItem(OrderItem orderItem) { // TODO 양방향 편의 메서드
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}