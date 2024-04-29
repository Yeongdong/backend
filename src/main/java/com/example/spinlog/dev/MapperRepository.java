package com.example.spinlog.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MapperRepository {
    private final MemberMapper memberMapper;
    public List<Member> findByName(String name){
        return memberMapper.findByName(name);
    }
}
