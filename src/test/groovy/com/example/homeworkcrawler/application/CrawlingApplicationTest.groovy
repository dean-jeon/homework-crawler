package com.example.homeworkcrawler.application

import com.example.homeworkcrawler.application.extractor.NumberAlphabetExtractor
import com.example.homeworkcrawler.exception.JSoapDocumentTimeoutException
import com.example.homeworkcrawler.exception.NotFoundJSoapDocumentException
import com.example.homeworkcrawler.service.HtmlDocumentService
import org.jsoup.nodes.Document
import spock.lang.Specification

import java.util.concurrent.ForkJoinPool

class CrawlingApplicationTest extends Specification {

    CrawlingApplication sut
    HtmlDocumentService htmlDocumentService
    NumberAlphabetExtractor numberAlphabetExtractor
    ForkJoinPool commonForkJoinPool

    void setup() {
        htmlDocumentService = Mock()
        numberAlphabetExtractor = new NumberAlphabetExtractor()
        commonForkJoinPool = new ForkJoinPool(1)
        sut = new CrawlingApplication(
                htmlDocumentService,
                numberAlphabetExtractor,
                commonForkJoinPool
        )
    }

    def "When document search is successful, then should return correct result"() {
        given:
        Document doc = Mock()
        doc.html() >> "html124divABCDefgtaBleImg1"
        htmlDocumentService.getHtmlDocument("https://www.genesis.com") >> doc

        when:
        def result = sut.findDeDuplicatedNumberAndAlphabet(["https://www.genesis.com"])

        then:
        result == "Aa1B2C4DdefghIilmtv"
    }

    def "When searched document is empty, then should return empty result"() {
        given:
        Document doc = Mock()
        doc.html() >> ""
        htmlDocumentService.getHtmlDocument("https://www.genesis.com") >> doc

        when:
        def result = sut.findDeDuplicatedNumberAndAlphabet(["https://www.genesis.com"])

        then:
        result == ""
    }

    def "When document search is failed, should throw exception with message"() {
        given:
        htmlDocumentService.getHtmlDocument("https://www.genesis.com") >>
                { throw new JSoapDocumentTimeoutException("[error] https://www.genesis.com") }

        when:
        sut.findDeDuplicatedNumberAndAlphabet(["https://www.genesis.com"])

        then:
        def e = thrown NotFoundJSoapDocumentException
        e.message == "[error] https://www.genesis.com"
    }

    def "When all document search is failed, should throw exception with all messages"() {
        given:
        htmlDocumentService.getHtmlDocument("https://www.genesis.com") >>
                { throw new JSoapDocumentTimeoutException("[error] https://www.genesis.com") }
        htmlDocumentService.getHtmlDocument("https://www.kia.com") >>
                { throw new NotFoundJSoapDocumentException("[error] https://www.kia.com") }

        when:
        sut.findDeDuplicatedNumberAndAlphabet(["https://www.genesis.com", "https://www.kia.com"])

        then:
        def e = thrown NotFoundJSoapDocumentException
        e.message == "[error] https://www.genesis.com,[error] https://www.kia.com"
    }

    def "When even one document search is failed, should throw exception with messages"() {
        given:
        Document doc = Mock()
        doc.html() >> "html124divABCDefgtaBleImg1"
        htmlDocumentService.getHtmlDocument("https://www.genesis.com") >> doc
        htmlDocumentService.getHtmlDocument("https://www.kia.com") >>
                { throw new JSoapDocumentTimeoutException("[error] https://www.kia.com") }

        when:
        sut.findDeDuplicatedNumberAndAlphabet(["https://www.genesis.com", "https://www.kia.com"])

        then:
        def e = thrown NotFoundJSoapDocumentException
        e.message == "[error] https://www.kia.com"
    }
}
