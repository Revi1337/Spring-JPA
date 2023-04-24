package com.example.datajpawithquerydsl.controller;


import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.repository.MemberQueryDslRepository;
import com.example.datajpawithquerydsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
public class MemberController {

    private final MemberQueryDslRepository memberQueryDslRepository;

    private final MemberRepository memberRepository;

    // http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35
    @GetMapping(path = "/v1/members")
    public ResponseEntity<?> searchMemberV1(MemberSearchCondition memberSearchCondition) {
        return ResponseEntity.ok(
                memberQueryDslRepository.search(memberSearchCondition)
        );
    }

    @GetMapping(path = "/v2/members")
    public ResponseEntity<?> searchMemberV2(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return ResponseEntity.ok(
                memberRepository.searchPageSimple(memberSearchCondition, pageable)
        );
    }

    @GetMapping(path = "/v3/members")
    public ResponseEntity<?> searchMemberV3(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return ResponseEntity.ok(
                memberRepository.searchPageComplex(memberSearchCondition, pageable)
        );
    }

}
