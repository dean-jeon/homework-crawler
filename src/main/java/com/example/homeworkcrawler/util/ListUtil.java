package com.example.homeworkcrawler.util;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ListUtil {

    public static <T, U> List<U> convert(final List<T> list, final Function<T, U> transform) {
        if (list == null) {
            return Collections.emptyList();
        }
        if (transform == null) {
            throw new IllegalArgumentException("function not defined");
        }
        return list.stream().map(transform).collect(toList());
    }

    public static <T> Stream<T> stream(Collection<T> list) {
        if (list == null) {
            return Stream.empty();
        }
        return list.stream();
    }
}
