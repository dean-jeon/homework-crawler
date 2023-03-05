package com.example.homeworkcrawler.service;

import com.example.homeworkcrawler.exception.JSoapDocumentTimeoutException;
import com.example.homeworkcrawler.exception.NotFoundJSoapDocumentException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HtmlDocumentService {

    private static final String DEFAULT_SCHEME = "https://";
    private static final int TIME_OUT_MILLIS = 3000;

    @Retryable(
      value = {JSoapDocumentTimeoutException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000)
    )
    public Document getHtmlDocument(String url) {
        try {
            return Jsoup.connect(getCorrectedUrl(url))
                        .timeout(TIME_OUT_MILLIS)
                        .userAgent("Mozilla")
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .execute()
                        .parse();
        } catch (SocketTimeoutException e) {
            throw new JSoapDocumentTimeoutException(String.format("[%s] %s", e.getMessage(), url));
        } catch (UnknownHostException e) {
            throw new NotFoundJSoapDocumentException("[Unknown host] " + url);
        } catch (Exception e) {
            throw new NotFoundJSoapDocumentException(String.format("[%s] %s", e.getMessage(), url));
        }
    }

    private String getCorrectedUrl(String url) {
        URI uri = URI.create(url);
        if (StringUtils.isBlank(uri.getScheme())) {
            return DEFAULT_SCHEME + url;
        }
        return url;
    }
}
