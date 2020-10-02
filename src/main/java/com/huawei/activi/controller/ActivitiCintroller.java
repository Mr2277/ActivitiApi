package com.huawei.activi.controller;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
    public void deloy(@RequestParam("fileBpmn") String fileBpmnName) {
        /*
        String preFilePath = "processes/";
        String fileBpmn = preFilePath + fileBpmnName + ".bpmn20.xml";
        System.out.println(fileBpmnName);
        Deployment deployment = repositoryService.createDeployment()//创建一个部署对象
                   .name(fileBpmnName)//添加部署的名称
                   .key(fileBpmnName)
                   .addInputStream(fileBpmn, this.getClass().getClassLoader().getResourceAsStream(fileBpmn))
//                .addInputStream(png, this.getClass().getClassLoader().getResourceAsStream(png))
                   .deploy();//完成部署


        System.out.println(deployment.toString());
        System.out.println("Id:" + deployment.getId() + "  Name:" + deployment.getName() + "  key:" + deployment.getKey());
        */
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            System.out.println("DeployId:" + processDefinition.getDeploymentId());
            System.out.println("DeployName:" + processDefinition.getName());
            System.out.println("DeployKey:" + processDefinition.getKey());
        }
    }

}
