package com.example.springdatajpa.controller;

import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }

    @GetMapping(value = "/members/{id}")
    public String findMember(@PathVariable(name = "id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping(value = "/members2/{id}")
    public String findMember2(@PathVariable(name = "id") Member member) {
        return member.getUsername();
    }

}
