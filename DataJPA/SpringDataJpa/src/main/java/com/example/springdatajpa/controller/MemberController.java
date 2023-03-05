package com.example.springdatajpa.controller;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++)
            memberRepository.save(new Member("user" + i, i));
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

    @GetMapping(value = "/members")
    public Page<MemberDto> list(Pageable pageable) {
//        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> memberDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        return memberDto;
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    @GetMapping(value = "/members2")
    public Page<Member> list2(@PageableDefault(size = 4, sort = "username") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

}
