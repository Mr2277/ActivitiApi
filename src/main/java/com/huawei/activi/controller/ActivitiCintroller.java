package com.huawei.activi.controller;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            System.out.println("DeployId:" + processDefinition.getDeploymentId());
            System.out.println("DeployName:" + processDefinition.getName());
            System.out.println("DeployKey:" + processDefinition.getKey());
        }
    }

    @RequestMapping("/startActiviti")
    public void startActiciti(@RequestParam("instanceKey") String instanceKey) {
        Map<String, Object> variables=new HashMap<String,Object>();
        /*
        variables.put("zichan", "30003086");
        variables.put("zichanzong", "30003087");
        variables.put("fengkong", "301,302,303");
        */
        //runtimeService.startProcessInstanceByKey()
        /*
        variables.put("teamLeader", "30000669");
        variables.put("deptLeader", "30003080");
        variables.put("ceo", "30003086");
        */
        variables.put("create", "1091");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(instanceKey, variables);
        System.out.println("proInsKey:" + processInstance.getBusinessKey());
        System.out.println("deployId:" + processInstance.getDeploymentId());
        System.out.println("deployName:" + processInstance.getName());
        System.out.println("proDefId:" + processInstance.getProcessDefinitionId());
    }

    @RequestMapping("/tasks")
    public void queryTaskList(@RequestParam("assignee") String assignee, @RequestParam("proInsId") String proInsId) {
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee(assignee)
                //.taskCandidateUser(assignee)
                .processInstanceId(proInsId)
                .list();
        System.out.println(taskList.size());
        for (Task task : taskList) {
            System.out.println("id=" + task.getId());
            System.out.println("name=" + task.getName());
            System.out.println("assinee=" + task.getAssignee());
            System.out.println("createTime=" + task.getCreateTime());
            System.out.println("executionId=" + task.getExecutionId());
            System.out.println("流程定义ID:" + task.getProcessDefinitionId());
            System.out.println("流程实例ID:" + task.getProcessInstanceId());
            System.out.println("执行对象ID:" + task.getExecutionId());
            System.out.println("任务ID:" + task.getId());//任务ID:10004
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务的创建时间:" + task.getCreateTime());
        }
    }

    @RequestMapping("/complete")
    public void complete(@RequestParam("taskId") String taskId, @RequestParam("assinee") String assinee, @RequestParam("result") String result) {
        // 由于流程用户上下文对象是线程独立的，所以要在需要的位置设置，要保证设置和获取操作在同一个线程中
        Authentication.setAuthenticatedUserId(assinee);// 批注人的名称  一定要写，不然查看的时候不知道人物信息
        // 添加批注信息
        String comment = result.equals("1") ? String.format("%s审批成功", assinee) : String.format("%s审批失败", assinee);
        taskService.addComment(taskId, null, comment);//comment为批注内容
        Map<String, Object> variables = new HashMap<>();
        /*
        variables.put("teamLeader", "30000669");
        variables.put("deptLeader", "30003080");
        variables.put("ceo", "30003086");
        */
        variables.put("result", result);

        //variables.put("manager", "30003086");
        taskService.setVariables(taskId, variables);
        taskService.complete(taskId, variables);
    }

    @RequestMapping("/history")
    public void history(@RequestParam("proInsId") String proInsId) {
        // 通过流程实例查询所有的(用户任务类型)历史活动
        List<HistoricActivityInstance> hais = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(proInsId).activityType("userTask").list();
        System.out.println(hais.size());
        for (HistoricActivityInstance h : hais) {
            System.out.println(h.getTaskId());
            List<Comment> comments = taskService.getTaskComments(h.getTaskId());
            for (Comment comment : comments) {
                System.out.println("message:" + comment.getFullMessage());
                System.out.println("userId:" + comment.getUserId());
            }
        }
    }

    @RequestMapping("/addGroup")
    public void addGroup() {
        taskService.addCandidateUser("10004", "ymm");
    }
}
