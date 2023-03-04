package com.example.homeworkcrawler.application.extractor;

import com.example.homeworkcrawler.util.ListUtil;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NumberAlphabetExtractor {

    public static final Pattern NUMBER_AND_ALPHABET_PATTERN = Pattern.compile("[^A-Za-z\\d]");

    public String extractDeDuplicatedNumberAndAlphabet(List<String> texts) {
        String allNumberAndAlphabet = ListUtil.stream(texts)
                                              .map(this::extractNumberAndAlphabet)
                                              .collect(Collectors.joining());
        return deDuplicationAndMerge(allNumberAndAlphabet);
    }

    private String extractNumberAndAlphabet(String text) {
        Matcher matcher = NUMBER_AND_ALPHABET_PATTERN.matcher(text);
        return matcher.replaceAll(StringUtils.EMPTY);
    }

    private String deDuplicationAndMerge(String allNumberAndAlphabet) {
        if (StringUtils.isBlank(allNumberAndAlphabet)) {
            return StringUtils.EMPTY;
        }

        // 중복 제거 & 숫자 정렬(TreeSet). 단, 여기에서 대/소문자는 중복 제거는 하되 정렬은 불필요
        Set<Character> numberSet = new TreeSet<>();
        Set<Character> upperCaseSet = new HashSet<>();
        Set<Character> lowerCaseSet = new HashSet<>();
        for (char each : allNumberAndAlphabet.toCharArray()) {
            if (Character.isUpperCase(each)) {
                upperCaseSet.add(each);
            } else if (Character.isLowerCase(each)) {
                lowerCaseSet.add(each);
            } else {
                numberSet.add(each);
            }
        }

        return merge(numberSet, upperCaseSet, lowerCaseSet);
    }

    private String merge(Set<Character> numberSet, Set<Character> upperCaseSet, Set<Character> lowerCaseSet) {
        LinkedList<Character> numberQueue = new LinkedList<>(numberSet);
        LinkedList<Pair<Character, Character>> alphabetQueue = makeAlphabetQueue(upperCaseSet, lowerCaseSet);
        int length = Math.max(numberQueue.size(), alphabetQueue.size());
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Pair<Character, Character> pair = alphabetQueue.poll();
            if (pair != null) {
                strBuilder.append(pair.getLeft() != null ? pair.getLeft() : StringUtils.EMPTY);
                strBuilder.append(pair.getRight() != null ? pair.getRight() : StringUtils.EMPTY);
            }
            strBuilder.append(numberQueue.peek() != null ? numberQueue.poll() : StringUtils.EMPTY);
        }

        return strBuilder.toString();
    }

    /**
     * 대문자나 소문자가 Set 에 존재하는 경우에만 큐를 생성하여 반환
     * @return LinkedList<Pair < Character / upperCase /, Character / lowerCase />>
     */
    private LinkedList<Pair<Character, Character>> makeAlphabetQueue(Set<Character> upperCaseSet,
                                                                     Set<Character> lowerCaseSet) {
        LinkedList<Pair<Character, Character>> result = new LinkedList<>();
        for (int i = 0; i < 26; i++) {
            char upperCase = (char) (65 + i);
            char lowerCase = (char) (97 + i);
            if (upperCaseSet.contains(upperCase) || lowerCaseSet.contains(lowerCase)) {
                result.offer(Pair.of(upperCaseSet.contains(upperCase) ? upperCase : null,
                                     lowerCaseSet.contains(lowerCase) ? lowerCase : null));
            }
        }
        return result;
    }
}
