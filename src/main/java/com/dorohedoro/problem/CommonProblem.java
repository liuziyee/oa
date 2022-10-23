package com.dorohedoro.problem;

import lombok.Data;

@Data
public class CommonProblem extends RuntimeException {

    private String msg;
    private int code = 500;

    public CommonProblem(String msg) {
        super(msg);
        this.msg = msg;
    }

    public CommonProblem(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public CommonProblem(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public CommonProblem(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}
