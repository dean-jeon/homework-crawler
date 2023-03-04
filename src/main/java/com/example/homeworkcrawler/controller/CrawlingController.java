package com.example.homeworkcrawler.controller;

import com.example.homeworkcrawler.application.CrawlingApplication;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/crawling")
@RestController
public class CrawlingController {

    private final CrawlingApplication crawlingApplication;

    @GetMapping("/find-number-and-alphabet")
    public Response<String> findDeDuplicatedNumberAndAlphabet(@RequestParam(required = false) List<String> urls) {
        return Response.ok(crawlingApplication.findDeDuplicatedNumberAndAlphabet(urls));
    }
}
