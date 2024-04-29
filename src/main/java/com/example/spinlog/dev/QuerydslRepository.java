package com.example.spinlog.dev;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@Transactional
public class QuerydslRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public QuerydslRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Member> findByName(String name){
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        if(StringUtils.hasText(name)){
            builder.and(member.name.like("%"+name+"%"));
        }

        return query
                .select(member)
                .from(member)
                .where(builder)
                .fetch();
    }
}
