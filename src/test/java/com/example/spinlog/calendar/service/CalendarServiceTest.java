package com.example.spinlog.calendar.service;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import com.example.spinlog.calendar.dto.DaySpend;
import com.example.spinlog.calendar.dto.TotalCalendarResponseDto;
import com.example.spinlog.calendar.repository.CalenderRepository;
import com.example.spinlog.calendar.repository.dto.CalenderDto;
import com.example.spinlog.calendar.repository.dto.MonthSpendDto;
import com.example.spinlog.global.error.exception.user.UserNotFoundException;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.example.spinlog.utils.DateUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalendarServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    CalenderRepository calenderRepository;

    @InjectMocks
    CalendarService calendarService;

    @Nested
    class requestTotal{
        @Test
        void authenticationName에_해당하는_userId를_레포지토리에게_전달한다() throws Exception {
            // given
            String authenticationName = "name1";
                User user = User.builder()
                        .email("email1")
                        .authenticationName(authenticationName)
                        .build();
            when(userRepository.findByAuthenticationName(authenticationName)).thenReturn(Optional.of(user));
            setUserId(user, 1L);

            // when
            calendarService.requestTotal(authenticationName, "20240725");
            
            // then
            verify(calenderRepository)
                    .getMonthSpendList2(eq(user.getId()), any());
            /*verify(calenderRepository)
                    .getDaySpendList(eq(user.getId()), any());*/
        }

        @Test
        void selectDate에_해당하는_LocalDate를_레포지토리에게_전달한다() throws Exception {
            // given
            when(userRepository.findByAuthenticationName(any())).thenReturn(
                    Optional.of(User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()));
            String targetDate = "20240725";

            // when
            calendarService.requestTotal("any", targetDate);

            // then
            verify(calenderRepository)
                    .getMonthSpendList2(any(), eq(DateUtils.parseStringToDate(targetDate)));
            /*verify(calenderRepository)
                    .getDaySpendList(any(), eq(DateUtils.parseStringToDate(targetDate)));*/
        }

        @Test
        void 유효하지_않은_userName을_입력하면_UserNotFoundException이_발생한다() throws Exception {
            // given
            String invalidAuthenticationName = "invalid";
            
            // when // then
            assertThatThrownBy(() -> calendarService.requestTotal(invalidAuthenticationName, "20240725"))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"20241241", "20241325", "2024-07-25", "2024/07/25"})
        void 유효하지_않은_selectDate를_입력하면_DateTimeParseException이_발생한다(String invalidDate) throws Exception {
            // given
            when(userRepository.findByAuthenticationName(any())).thenReturn(
                    Optional.of(User.builder()
                            .email("email1")
                            .authenticationName("name1")
                            .build()));

            // when // then
            assertThatThrownBy(() -> calendarService.requestTotal("any", invalidDate))
                    .isInstanceOf(DateTimeParseException.class);
        }
        
        @Test
        void 레포지토리로부터_받은_객체를_가지고_TotalCalenderResponseDto를_생성하여_반환한다() throws Exception {
            // given
            String authenticationName = "name1";
            when(userRepository.findByAuthenticationName(any())).thenReturn(
                    Optional.of(User.builder()
                            .email("email1")
                            .authenticationName(authenticationName)
                            .build()));
            List<CalenderDto> monthSpendList = List.of(
                    new CalenderDto(1L, RegisterType.SPEND, 300, "content1", 3f, Emotion.PROUD, LocalDateTime.now())
            );
            when(calenderRepository.getMonthSpendList2(any(), any())).thenReturn(monthSpendList);
            
            // when
            TotalCalendarResponseDto response = calendarService.requestTotal(authenticationName, "20240725");

            // then
            assertThat(response.getMonthSpendList().size()).isEqualTo(1);
            assertThat(response.getMonthSpendList().get(0).getDaySpend()).isEqualTo(300);
            assertThat(response.getMonthSpendList().get(0).getDaySave()).isEqualTo(0);

        }
    }

    private void setUserId(User user, long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}