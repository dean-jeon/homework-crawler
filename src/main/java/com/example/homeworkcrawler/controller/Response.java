package com.example.homeworkcrawler.controller;

public record Response<T>(boolean success, T data, String message) {

    public static <T> Response<T> ok(T data) {
        return new Response<>(true, data, null);
    }

    public static <T> Response<T> okWithEmpty() {
        return new Response<>(true, null, null);
    }

    public static <T> Response<T> error(Exception e) {
        return new Response<>(false, null, e.getMessage());
    }

    public static <T> Response<T> errorWith(String message) {
        return error(new Exception(message));
    }
}
