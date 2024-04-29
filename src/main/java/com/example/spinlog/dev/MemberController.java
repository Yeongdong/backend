package com.example.spinlog.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    private final MapperRepository memberMapper;
    private final QuerydslRepository querydslRepository;

    @PutMapping("/members")
    public void saveMember(@RequestBody String name){
        memberRepository.save(new Member(name));
    }

    @DeleteMapping("/members/{id}")
    public int deleteMember(@PathVariable Long id){
        if(!memberRepository.existsById(id))
            return -1;
        memberRepository.deleteById(id);
        return 0;
    }

    @GetMapping("/members/{id}")
    public Member getMember(@PathVariable Long id){
        return memberRepository.findById(id)
                .orElse(new Member("null member"));
    }

    @GetMapping("/members")
    public List<Member> getMembers(){
        return memberRepository.findAll();
    }

    @GetMapping("/members/mybatis/{name}")
    public List<Member> findByNameUsingMybatis(@PathVariable String name){
        return memberMapper.findByName(name);
    }

    @GetMapping("/members/querydsl/{name}")
    public List<Member> findByNameUsingQuerydsl(@PathVariable String name){
        return querydslRepository.findByName(name);
    }

    @GetMapping("/health")
    public void healthCheck() {}
}
