package com.huawei.activi.controller;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class MyTakeListener implements ExecutionListener {



    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("take");

    }
}
