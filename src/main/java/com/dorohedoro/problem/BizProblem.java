package com.dorohedoro.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BizProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("/internal_server_error");

    public BizProblem(String detail) {
        super(TYPE, "服务器错误", Status.INTERNAL_SERVER_ERROR, detail);
    }
}

