package com.example.lifechallenge.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseBody<T>{
    private final String StatusCode;
    private final String Status;
    private final T data;
}
