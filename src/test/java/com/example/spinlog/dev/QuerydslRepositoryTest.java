package com.example.spinlog.dev;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class QuerydslRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    QuerydslRepository querydslRepository;
    @Test
    @DisplayName("querydsl test")
    void querydsl_test() throws Exception {
        // given
        Member han1 = new Member("han1");
        memberRepository.save(han1);
        Member han2 = new Member("han2");
        memberRepository.save(han2);
        Member han3 = new Member("han3");
        memberRepository.save(han3);

        // when
        List<Member> list = querydslRepository.findByName("an");

        // then
        Assertions.assertThat(list)
                .hasSize(3)
                .contains(han1)
                .contains(han2)
                .contains(han3);
    }
}