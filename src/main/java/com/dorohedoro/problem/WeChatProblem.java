package com.dorohedoro.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class WeChatProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("/wechat");

    public WeChatProblem(String detail) {
        super(TYPE, "微信API错误", Status.FAILED_DEPENDENCY, detail);
    }
}
