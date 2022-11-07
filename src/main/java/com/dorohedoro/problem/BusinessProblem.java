package com.dorohedoro.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BusinessProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("/duplicate");

    public BusinessProblem(String message) {
        super(TYPE, "服务器错误", Status.INTERNAL_SERVER_ERROR, message);
    }
}

