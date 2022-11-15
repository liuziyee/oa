package com.dorohedoro.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class ServerProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("/server");

    public ServerProblem(String detail) {
        super(TYPE, "服务器错误", Status.INTERNAL_SERVER_ERROR, detail);
    }

    public ServerProblem(Status status, String detail) {
        super(TYPE, "服务器错误", status, detail);
    }
}

