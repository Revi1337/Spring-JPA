package com.example.jpawithquerydsl;

import com.example.jpawithquerydsl.entity.Member;
import com.example.jpawithquerydsl.entity.Team;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local") // local 일떄만 동작
@Component
public class InitMember {

    private final InitMemberService initMemberService;
    public InitMember(InitMemberService initMemberService) {
        System.out.println("InitMember RequiredConstructor called");
        this.initMemberService = initMemberService;
    }

    @PostConstruct
    public void init() {
        System.out.println("InitMember PostConstruct Called ");
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        private final EntityManager entityManager;
        public InitMemberService(EntityManager entityManager) {
            System.out.println("InitMemberService RequiredConstructor called");
            this.entityManager = entityManager;
        }

        @Transactional
        public void init() {
            System.out.println("InitMemberService.init() called");
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            entityManager.persist(teamA);
            entityManager.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                entityManager.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }
}
