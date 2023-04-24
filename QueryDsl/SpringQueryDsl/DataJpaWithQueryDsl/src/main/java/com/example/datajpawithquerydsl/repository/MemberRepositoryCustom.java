package com.example.datajpawithquerydsl.repository;

import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

}
