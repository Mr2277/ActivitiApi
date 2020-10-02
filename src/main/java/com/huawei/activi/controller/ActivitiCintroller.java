package com.huawei.activi.controller;

import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/activiti")
public class ActivitiCintroller {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RepositoryService repositoryService;


    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Autowired
    IdentityService identityService;

    @Autowired
    ManagementService managementService;

    @RequestMapping("/test")
    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/deloy")
    public void deloy(String fileBpmn) {

    }
}
