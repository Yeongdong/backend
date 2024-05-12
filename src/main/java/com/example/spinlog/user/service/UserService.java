package com.example.spinlog.user.service;

import com.example.spinlog.user.dto.request.UpdateUserRequestDto;
import com.example.spinlog.user.dto.response.ViewUserResponseDto;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ViewUserResponseDto findUser(String authenticationName) {
        User foundUser = getUser(authenticationName);

        return ViewUserResponseDto.of(foundUser);
    }

    @Transactional
    public void updateUserInfo(String authenticationName, UpdateUserRequestDto requestDto) {
        User foundUser = getUser(authenticationName);

        foundUser.change(requestDto.getMbti(), requestDto.getGender() ,requestDto.getBudget());
    }

    private User getUser(String authenticationName) {
        return userRepository.findByAuthenticationName(authenticationName).orElseThrow(() ->
                new NoSuchElementException(authenticationName + "에 해당하는 사용자를 찾을 수 없습니다.")
        );
    }

}
