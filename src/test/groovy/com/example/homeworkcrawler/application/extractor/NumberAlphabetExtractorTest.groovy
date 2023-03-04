package com.example.homeworkcrawler.application.extractor

import spock.lang.Specification
import spock.lang.Unroll

class NumberAlphabetExtractorTest extends Specification {

    NumberAlphabetExtractor sut

    void setup() {
        sut = new NumberAlphabetExtractor()
    }

    @Unroll
    def "test extractDeDuplicatedNumberAndAlphabet"() {
        expect:
        sut.extractDeDuplicatedNumberAndAlphabet(texts) == result

        where:
        result                || texts
        ""                    || null
        ""                    || []
        ""                    || ["한글입니다<>.,"]
        ""                    || ["!@@#^%#&^%&&<>.,"]
        "Aa1B2C4DdefghIilmtv" || ["html124divABCDefgtaBleImg1"]
        "Aa0B7c8Dd9efhlo"     || [" 제네시스 A 공식 B7890 웹사이트 c 본문 def 바로가기 모델 모델 ", " 기아 - a hello D"]
        "a0b1Z23456789"       || ["000112233445566778899Zab"]
        "12345"               || ["54321"]
        "abCcdef"             || ["abcdefC"]
    }
}
