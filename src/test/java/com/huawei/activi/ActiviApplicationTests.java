package com.huawei.activi;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ActiviApplicationTests {

    @Test
    void contextLoads() {
    }


    /**
     *
     * 流程的启动(流程实例的产生) new Class->Object
     * 涉及到的表 :
     * 			act_ru_execution  流程实例表
     * 			act_ru_task 会产生一条待执行的任务记录
     * 			act_hi_taskinst 也会产生一条历史任务记录(注意:endtime is null)
     * 注意: 以流程定义的key启动的话,默认会进入版本最新的流程
     */
    private ProcessEngine processEngine;
    @Test
    public void testStartProcess(){
        String processDefinitionKey = "groupTaskDelegate";
        //流程定义的key启动的话,默认会进入版本最新的流程
        ProcessInstance processInstance = processEngine.getRuntimeService() //与流程实例运行相关的服务类
                .startProcessInstanceByKey(processDefinitionKey);//启动流程 ,生成一个流程实例

        System.out.println("流程部署的ID:"+processInstance.getDeploymentId());
        System.out.println("流程定义的ID:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID:"+processInstance.getProcessInstanceId());

    }

    /**
     * 流程处理过程：查询个人任务
     * 处理流程的步骤:查询个人任务 完成个人任务
     * 涉及到的表：act_ru_task
     *
     */
    @Test
    public void testQueryMyTask(){
        String assignee = "张三";
        String processinstanceId = "147501";
        List<Task> taskList= processEngine.getTaskService()  //跟任务处理相关的服务类
                .createTaskQuery() //创建一个任务查询
                .taskAssignee(assignee) //加入查询条件
                .processInstanceId(processinstanceId)
                .list();					//返回形式

        if(taskList!=null&&taskList.size()>0){
            for(Task task:taskList){
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("流程实例ID:"+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("任务ID:"+task.getId());//任务ID:10004
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());

            }
        }
    }


    @Test
    public void testQueryMyGroupTask(){
        String assignee = "田七";
        String processinstanceId = "147501";
        List<Task> taskList= processEngine.getTaskService()  //跟任务处理相关的服务类
                .createTaskQuery() //创建一个任务查询
                .taskCandidateUser(assignee) //加入查询条件
                .processInstanceId(processinstanceId)
                .list();					//返回形式

        if(taskList!=null&&taskList.size()>0){
            for(Task task:taskList){
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("流程实例ID:"+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("任务ID:"+task.getId());//任务ID:130004
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());

            }
        }
    }

    //因为一组人都具有任务权限 ,我们得到任务ID之后,可以根据任务ID来查询哪些人具有此次任务的处理权限
    @Test
    public void testQueryGroupUserByTaskId(){
        String taskId ="147504";
        List<IdentityLink> identityLinkList = processEngine.getTaskService()
                .getIdentityLinksForTask(taskId);
        if(identityLinkList!=null&&identityLinkList.size()>0){
            for(IdentityLink identityLink:identityLinkList){
                System.out.println(identityLink.getGroupId());
                System.out.println(identityLink.getUserId());
                System.out.println(identityLink.getTaskId());
            }
        }
    }


    //任务拾取
//我们设置的任务处理人员暂时都只是候选人 ,并不是实际处理人,必须经过任务拾取的过程来确定谁来处理任务 (task assignee)
//任务拾取的过程,就是给执行任务表指定assginee字段值的过程
    @Test
    public void claimTask(){
        String taskId = "157502";
        String userId = "胡八";
        processEngine.getTaskService().claim(taskId, userId);

        //任务拾取以后, 可以回退给组
        //processEngine.getTaskService().setAssignee(taskId, null);
        //任务拾取以后,可以转给别人去处理(别人可以是组成员也可以不是)
        //processEngine.getTaskService().claim(taskId, "xiaoxi");
    }


    /**
     * 流程处理过程：完成个人任务
     * 处理流程的步骤:查询个人任务 完成个人任务
     * 涉及到的表：act_ru_task
     *
     */
    @Test
    public void testCompleteMyTask(){
        String taskId = "157502"; //
        Map<String,Object> mapVariables = new HashMap<String,Object>();
        mapVariables.put("manageId", "部门经理");

        processEngine.getTaskService()
                .complete(taskId,mapVariables);


        System.out.println("第三个任务完成");

	/*String taskId = "25002";
	processEngine.getTaskService()
					.complete(taskId);
	System.out.println("审批任务完成");*/
    }


    /**
     * 在流程的执行过程中,我们需要查询流程执行到了那一个状态
     */
    @Test
    public void testQueryProinstanceState(){
        String processInstanceId = "115001";
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if(processInstance!=null){
            System.out.println("当前流程执行到:"+processInstance.getActivityId());
        }else{
            System.out.println("当前流程已结束");

        }
    }


    /**
     * 当某个任务结束以后,在任务表act_ru_task中的记录会被删除 ,
     * 但是act_hi_taskinst记录的endtime会加上 ,所以我们可以从这个表查询我们的任务执行历史记录
     *
     *
     */
    @Test
    public void testQueryHistoryTask(){
        String assignee = "vicky";
        List<HistoricTaskInstance> historyTaskList = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .list();
        if(historyTaskList!=null&&historyTaskList.size()>0){
            for(HistoricTaskInstance hisTask:historyTaskList){
                System.out.println("流程定义ID:"+hisTask.getProcessDefinitionId());
                System.out.println("流程实例ID:"+hisTask.getProcessInstanceId());
                System.out.println("执行对象ID:"+hisTask.getExecutionId());
                System.out.println("历史任务ID:"+hisTask.getId());//任务ID:12502
                System.out.println("历史任务名称:"+hisTask.getName());
                System.out.println("历史任务的结束时间:"+hisTask.getEndTime());
                System.out.println("---------------------");


            }
        }
    }

    /**
     * 当某个流程实例执行完以后以后,在流程实例表表act_ru_execution中的记录会被删除 ,
     * 但是act_hi_procinst表中记录的endtime会加上 ,所以我们可以从这个表查询我们的历史流程实例记录
     *
     */
    @Test
    public void testQueryHistoryProcessInstance(){
        String processInstanceId = "130001";
        HistoricProcessInstance hisProcessInst = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if(hisProcessInst!=null){
            System.out.println("流程定义ID:"+hisProcessInst.getProcessDefinitionId());
            System.out.println("执行对象ID:"+hisProcessInst.getId());
            System.out.println("---------------------");
        }


        //activiti支持链式编程
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("diagrams/groupTaskDelegate.bpmn")
                .addClasspathResource("diagrams/groupTaskDelegate.png")
                .name("分配角色任务")
                .deploy();
        System.out.println("流程部署ID:"+deploy.getId());
        System.out.println("流程部署的名称:"+deploy.getName());

        IdentityService identityService =  processEngine.getIdentityService();
        //创建角色
        /*
        identityService.saveGroup(new GroupEntity("部门主管"));
        identityService.saveGroup(new GroupEntity("部门经理"));
        identityService.saveGroup(new GroupEntity("CTO"));
        //创建用户
        identityService.saveUser(new UserEntity("张三"));
        identityService.saveUser(new UserEntity("李四"));
        identityService.saveUser(new UserEntity("王五"));
        identityService.saveUser(new UserEntity("赵六"));
        identityService.saveUser(new UserEntity("田七"));
        identityService.saveUser(new UserEntity("胡八"));

        //创建角色和用户的对应关系
        identityService.createMembership("张三", "部门主管");
        identityService.createMembership("李四", "部门主管");
        identityService.createMembership("王五", "部门经理");
        identityService.createMembership("赵六", "部门经理");
        identityService.createMembership("田七", "CTO");
        identityService.createMembership("胡八", "CTO");
        */
    }

}
