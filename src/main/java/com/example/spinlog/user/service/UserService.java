package com.example.spinlog.user.service;

import com.example.spinlog.global.security.utils.SecurityUtils;
import com.example.spinlog.user.dto.request.UpdateUserRequestDto;
import com.example.spinlog.user.dto.response.ViewUserResponseDto;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @PreAuthorize("authentication")
    public ViewUserResponseDto findUser() {
        String authenticationName = SecurityUtils.getAuthenticationName();

        User foundUser = getUser(authenticationName);
        return ViewUserResponseDto.of(foundUser);
    }

    @Transactional
    @PreAuthorize("authentication")
    public void updateUserInfo(UpdateUserRequestDto requestDto) {
        String authenticationName1 = SecurityUtils.getAuthenticationName();

        User foundUser = getUser(authenticationName1);
        foundUser.change(requestDto.getMbti(), requestDto.getGender() ,requestDto.getBudget());
    }

    private User getUser(String authenticationName) {
        return userRepository.findByAuthenticationName(authenticationName).orElseThrow(() ->
                new NoSuchElementException(authenticationName + "에 해당하는 사용자를 찾을 수 없습니다.")
        );
    }

}
