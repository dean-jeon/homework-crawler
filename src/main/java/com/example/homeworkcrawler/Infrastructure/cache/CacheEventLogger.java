package com.example.homeworkcrawler.Infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(CacheEvent<?, ?> event) {
        log.info("cache event. type: {}, key: {}, oldValue: {}, newValue:{}",
                 event.getType(), event.getKey(), event.getOldValue(), event.getNewValue());
    }
}
