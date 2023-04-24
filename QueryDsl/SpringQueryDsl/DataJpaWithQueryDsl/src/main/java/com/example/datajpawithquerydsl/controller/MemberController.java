package com.example.datajpawithquerydsl.controller;


import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.repository.MemberQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
public class MemberController {

    private final MemberQueryDslRepository memberQueryDslRepository;

    // http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35
    @GetMapping(path = "/v1/members")
    public ResponseEntity<?> searchMemberV1(MemberSearchCondition memberSearchCondition) {
        return ResponseEntity.ok(
                memberQueryDslRepository.search(memberSearchCondition)
        );
    }

}
