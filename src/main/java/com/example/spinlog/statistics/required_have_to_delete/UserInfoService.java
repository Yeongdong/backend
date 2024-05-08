package com.example.spinlog.statistics.required_have_to_delete;

import com.example.spinlog.user.entity.Mbti;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserInfoService {
    public Mbti getUserMBTI() {
        // TODO 현재 세션 이용해 인증 한 유저의 mbti 반환해야 함
        return Mbti.ISTJ;
    }
}
