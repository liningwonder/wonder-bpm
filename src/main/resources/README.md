Camunda Doc
https://docs.camunda.org/manual/7.20/

Install

https://docs.camunda.org/get-started/quick-start/install/

https://camunda.com/download/platform-7/

https://downloads.camunda.cloud/release/camunda-bpm/run/7.19/camunda-bpm-run-7.19.0.zip

start.bat

用户名/密码
demo/demo

        <dependency>
            <groupId>org.camunda.bpm.springboot</groupId>
            <artifactId>camunda-bpm-spring-boot-starter</artifactId>
            <version>7.19.0</version>
        </dependency>

配置
https://docs.camunda.org/manual/7.16/user-guide/spring-boot-integration/configuration/

mysql 8.0 需要在连接参数增加

nullCatalogMeansCurrent=true

第一次运行
schema-update: true 可以生成表 第二次需要改为false

demo.bpm xml 文件解析

<bpmn:process id="payment-retrieval" name="收款" isExecutable="true" camunda:historyTimeToLive="180">


id对应act_re_procdef表的KEY_
name对应act_re_procdef的NAME_
文件名对应act_re_procdef的RESOURCE_NAME_

当执行
repositoryService.createDeployment().addClasspathResource("demo.bpmn").name("test").deploy();

act_re_procdef 会生成DEPLOYMENT_ID_


act_re_procdef表的ID_是  KEY_ : VERSION_ : DEPLOYMENT_ID_


processDefinitionKey

businessKey


CREATE TABLE `act_re_deployment` (
`ID_` varchar(64) COLLATE utf8mb3_bin NOT NULL,         对应act_re_procdef的DEPLOYMENT_ID_
`NAME_` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,  在使用.name("test")方法时指定，否则为NULL
`DEPLOY_TIME_` datetime DEFAULT NULL,
`SOURCE_` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
`TENANT_ID_` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL,
PRIMARY KEY (`ID_`),
KEY `ACT_IDX_DEPLOYMENT_NAME` (`NAME_`),
KEY `ACT_IDX_DEPLOYMENT_TENANT_ID` (`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;



CREATE TABLE `act_ge_bytearray` (
`ID_` varchar(64) COLLATE utf8mb3_bin NOT NULL,   对应act_re_deployment的ID_  对应act_re_procdef的DEPLOYMENT_ID_
`REV_` int DEFAULT NULL,    
`NAME_` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,   即为文件名对应act_re_procdef的RESOURCE_NAME_
`DEPLOYMENT_ID_` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL, 对应act_re_deployment的ID_  对应act_re_procdef的DEPLOYMENT_ID_
`BYTES_` longblob,                                              即为demo.bpm xml文件内容
`GENERATED_` tinyint DEFAULT NULL,
`TENANT_ID_` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL,
`TYPE_` int DEFAULT NULL,
`CREATE_TIME_` datetime DEFAULT NULL,
`ROOT_PROC_INST_ID_` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL,
`REMOVAL_TIME_` datetime DEFAULT NULL,
PRIMARY KEY (`ID_`),
KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
KEY `ACT_IDX_BYTEARRAY_ROOT_PI` (`ROOT_PROC_INST_ID_`),
KEY `ACT_IDX_BYTEARRAY_RM_TIME` (`REMOVAL_TIME_`),
KEY `ACT_IDX_BYTEARRAY_NAME` (`NAME_`),
CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;






https://www.jianshu.com/p/77766def8dae



5、使用Camunda的扩展来进行用户分配(User Assignment using Camunda Extensions):

5.1.分派人（Assignee）：
assignee属性定制扩展允许直接分配一个用户任务给指定的人。
<userTask id="theTask" name="my task" camunda:assignee="kermit" />

5.2.候选人（Candidate Users）：
这个candidateUsers属性定制扩展，允许一个用户作为一个任务的候选人
<userTask id="theTask" name="my task" camunda:candidateUsers="kermit, gonzo" />

5.3.候选组（Candidate Groups）：
这个candidateGroups属性定制扩展，允许你定制一个候选组给一个任务
<userTask id="theTask" name="my task" camunda:candidateGroups="management, accountancy" />

5.4.组合候选人和组（Combining Candidate Users and Groups）：
candidateUsers属性和candidateGroups属性可以同时定义，在同一个用户任务中


6.基于数据和服务逻辑的分配（Assignment based on Data and Service Logic）：
在上面的例子中，例如kermit或者management是一个常量，但是更多的情况下，在设计流程时，我们可能并不清楚知道指派人或者候选人，
或者候选人或指派人需要通过从数据库中获取，此时我们可以使用表达式或者task listener来实现

分配表达式（Assignment Expressions）：
分配表达式可以访问流程变量或者调用外部的bean和服务

6.1.使用流程变量（Using Process Variables）：
流程变量对于基于提前收集和计算的数据的分配是非常有用的 下面是一个展示将用户任务分配给流程发起人的例子：

<startEvent id="startEvent" camunda:initiator="starter" />
<userTask id="task" name="Clarify Invoice" camunda:assignee="${ starter }"/>

首先camunda:initiator表达式是被用作绑定发起流程的用户id，然后表达式${ starter }检索这个值并且用做这个任务的assignee
它可以使用所有的在用户任务中可见的流程变量在这个表达式中

6.2.调用一个服务或者一个bean（Invoking a Service / Bean）：

当使用spring或者CDI时，它可以委托给一个bean或者一个服务的实现。这种方式可以调用外部的复杂的分派逻辑，且不用显示的为它建模，且它调用的逻辑将会产生一个被用作分配的变量。
在下面这个例子中，assignee会通过调用基于spring或者CDI的bean的ldapService的findManagerOfEmployee()方法进行赋值。被传递的emp参数是一个流程变量。


<userTask id="task" name="My Task" camunda:candidateUsers="${ldapService.findAllSales()}"/>

assignee会通过调用基于spring或者CDI的bean的ldapService的findManagerOfEmployee()方法进行赋值。被传递的emp参数是一个流程变量。

方法返回一个String
<userTask id="task" name="My Task" camunda:assignee="${ldapService.findManagerForEmployee(emp)}"/>

类似的方法可以分配候选users和groups：
方法返回一个Collection
<userTask id="task" name="My Task"camunda:candidateUsers="${ldapService.findAllSales()}"/>


被传递的emp参数是一个流程变量


public class FakeLdapService {
public String findManagerForEmployee(String employee) {
return "Kermit The Frog";
}
public List<String> findAllSales() {
return Arrays.asList("kermit", "gonzo", "fozzie");
}
}

6.3.在listener中指派（Assignments in Listeners）：

这也可以使用task listeners去进行指派。下面的例子展示了一个创建事件的task listener：
<userTask id="task1" name="My task" >
<extensionElements>
<camunda:taskListener event="create" class="org.camunda.bpm.MyAssignmentHandler" />
</extensionElements>
</userTask>

public class MyAssignmentHandler implements TaskListener {
public void notify(DelegateTask delegateTask) {
// Execute custom identity lookups here
// and then for example call following methods:
delegateTask.setAssignee("kermit");
delegateTask.addCandidateUser("fozzie");
delegateTask.addCandidateGroup("management");
...
}
}


<camunda:taskListener class="org.camunda.bpm.MyAssignmentHandler" event="create" id="1" />










DelegateTask中获取流程变量，再使用service获根据变量获取处理人

preUser


分别对应3个listener

type  当前task中的user获取类型：

根据角色获取候选人 

根据前一个节点人的部门获取当前部门某个角色的的处理人

特定某个人






10.完成（Completion）：
完成是任务生命周期的一部分，与创建设置候选人等操作一起。（允许通过Java API操作）。通过传递变量完成一个任务，这个流程变量可以被选择性的检索：

taskService.complete(taskId, variables);

// or complete and retrieve the process variables
VariableMap processVariables = taskService.completeWithVariablesInReturn(taskId, variables, shouldDeserializeValues);






执行监听器-Execution Listener    start  end take

任务监听器-Task Listener         create assignment update complete delete timeout



1. 开始节点：start -》 end
2. 连线：take
3. 任务节点ExecutionListener：start
4. 任务节点TaskListener：create -》 assignment -》 complete
5. 任务节点ExecutionListener：end
6. 连线：take
7. 结束节点：start - 》 end



    String EVENTNAME_CREATE = "create";
    String EVENTNAME_ASSIGNMENT = "assignment";
    String EVENTNAME_COMPLETE = "complete";
    String EVENTNAME_UPDATE = "update";
    String EVENTNAME_DELETE = "delete";
    String EVENTNAME_TIMEOUT = "timeout";








1、根据上一节点的处理人获取处理人的部门领导

2、获取固定角色的人

3、获取部门内某个固定角色的处理人

Assignee  指派到人

Candidate users  指派多个人，逗号隔开            需先认领需先认领才能执行


Candidate Groups  指派多个组，逗号隔开           需先认领才能执行


流程涉设计时设定


流程启动时设定  支持表达式  取流程变量   ${user}


流转过程中设定



这也可以使用task listeners去进行指派。下面的例子展示了一个创建事件的task listener

task事件 

create assignment complete delete update timeout












会签


知会










