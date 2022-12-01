package com.dorohedoro.service.impl;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class NotifyService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {}
}
