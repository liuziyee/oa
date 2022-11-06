package com.dorohedoro.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DataDuplicateProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("/duplicate");

    public DataDuplicateProblem(String message) {
        super(TYPE, "重复数据", Status.CONFLICT, message);
    }
}
