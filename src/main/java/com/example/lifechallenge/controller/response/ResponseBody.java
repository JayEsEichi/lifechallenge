package com.example.lifechallenge.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseBody<T>{
    private final int statusCode;
    private final String status;
    private final T data;
}
