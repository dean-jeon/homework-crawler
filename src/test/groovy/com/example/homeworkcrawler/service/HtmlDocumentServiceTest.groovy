package com.example.homeworkcrawler.service

import spock.lang.Specification
import spock.lang.Unroll

class HtmlDocumentServiceTest extends Specification {

    HtmlDocumentService sut

    void setup() {
        sut = new HtmlDocumentService()
    }

    @Unroll
    def "test getCorrectedUrl"() {
        expect:
        sut.getCorrectedUrl(url) == result

        where:
        result                            || url
        "https://www.kia.com"             || "www.kia.com"
        "https://kia.com"                 || "kia.com"
        "https://kia.com"                 || "https://kia.com"
        "https://kia.com/a/b/c/d?e=hello" || "https://kia.com/a/b/c/d?e=hello"
        "http://kia.com"                  || "http://kia.com"
    }
}
