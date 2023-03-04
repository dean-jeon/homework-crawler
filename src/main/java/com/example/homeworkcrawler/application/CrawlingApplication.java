package com.example.homeworkcrawler.application;

import com.example.homeworkcrawler.application.extractor.NumberAlphabetExtractor;
import com.example.homeworkcrawler.exception.NotFoundJSoapDocumentException;
import com.example.homeworkcrawler.service.HtmlDocumentService;
import com.example.homeworkcrawler.util.ListUtil;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlingApplication {

    private final HtmlDocumentService htmlDocumentService;
    private final NumberAlphabetExtractor numberAlphabetExtractor;
    private final ForkJoinPool commonForkJoinPool;

    @Cacheable(value = "deDuplicatedNumberAndAlphabet", key = "#urls")
    public String findDeDuplicatedNumberAndAlphabet(List<String> urls) {
        List<HtmlResult> htmlResults = findHtmlTextWithParallel(urls);

        validateHtmlText(htmlResults);

        return numberAlphabetExtractor.extractDeDuplicatedNumberAndAlphabet(ListUtil.convert(htmlResults,
                                                                                             HtmlResult::getHtml));
    }

    private void validateHtmlText(List<HtmlResult> htmlResults) {
        List<String> errorMessages = htmlResults.stream()
                                                .filter(it -> !it.isSuccess())
                                                .map(HtmlResult::getErrorMessage)
                                                .toList();
        if (!CollectionUtils.isEmpty(errorMessages)) {
            throw new NotFoundJSoapDocumentException(String.join(",", errorMessages));
        }
    }

    private List<HtmlResult> findHtmlTextWithParallel(List<String> urls) {
        return commonForkJoinPool.submit(
                                   () -> urls.parallelStream()
                                             .map(url -> {
                                                 try {
                                                     String html = htmlDocumentService.getHtmlDocument(url).html();
                                                     return HtmlResult.withSuccess(html);
                                                 } catch (Exception e) {
                                                     return HtmlResult.withError(e.getMessage());
                                                 }
                                             })
                                             .toList())
                                 .join();
    }

    @Getter
    private static class HtmlResult {
        private boolean success;
        private String html;
        private String errorMessage;

        static HtmlResult withSuccess(String html) {
            HtmlResult result = new HtmlResult();
            result.success = true;
            result.html = html;
            return result;
        }

        static HtmlResult withError(String errorMessage) {
            HtmlResult result = new HtmlResult();
            result.success = false;
            result.errorMessage = errorMessage;
            return result;
        }
    }
}
