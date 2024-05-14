package com.example.spinlog.utils;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterParser {

    private final static String REGEX = ",";

    public static List<RegisterType> parseRegisterTypes(String input) {
        List<String> registerTypeStrings = parseCommaSeparatedString(input);
        return registerTypeStrings.stream()
                .map(RegisterType::valueOf)
                .toList();
    }

    public static List<Emotion> parseEmotions(String input) {
        List<String> registerTypeStrings = parseCommaSeparatedString(input);
        return registerTypeStrings.stream()
                .map(Emotion::valueOf)
                .toList();
    }

    // 쉼표로 구분된 문자열을 리스트로 파싱하는 메서드
    public static List<String> parseCommaSeparatedString(String input) {
        return parseAndTrim(input);
    }

    // 쉼표로 구분된 실수값을 리스트로 파싱하는 메서드
    public static List<Float> parseCommaSeparatedFloat(String input) {
        return parseAndTrim(input).stream()
                .map(Float::parseFloat)
                .toList();
    }

    // URL 디코딩하여 리스트로 파싱하는 메서드 (단어는 쉼표로 구분)
    public static List<String> decodeAndParse(String input) {
        String decodedInput = decode(input);
        return parseAndTrim(decodedInput);
    }

    // 구분자로 문자열을 나누고 공백을 제거한 후 리스트로 변환하는 메서드
    private static List<String> parseAndTrim(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        return Stream.of(input.split(REGEX))
                .map(String::trim)
                .toList();
    }

    // URL 디코딩하는 메서드
    private static String decode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return URLDecoder.decode(input, StandardCharsets.UTF_8);
    }
}
