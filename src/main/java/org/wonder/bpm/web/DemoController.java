/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/20
 * @Description TODO
 */

package org.wonder.bpm.web;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Controller
public class DemoController {
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private HistoryService historyService;
	
	@GetMapping("/def")
	@ResponseBody
    public String def() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
		for (ProcessDefinition processDefinition : list) {
			System.out.println(processDefinition.getId());
			System.out.println(processDefinition.getName());
			System.out.println(processDefinition.getKey());
			System.out.println(processDefinition.getDeploymentId());
			System.out.println(processDefinition.getVersion());
			System.out.println(processDefinition.getResourceName());
			System.out.println(processDefinition.getDescription());
		}
	    return "ok ";
    }
	
	@Transactional(rollbackFor = Exception.class)
	@GetMapping("/bpm")
	@ResponseBody
	public void bpmn() {
		try {
			//addInputStream()
			//repositoryService.createDeployment().addClasspathResource("demo.bpmn").name("test").deploy();
			InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:demo.bpmn"));
			//addInputStream方法里面的s即为文件名,即对应act_re_procdef的RESOURCE_NAME_
			repositoryService.createDeployment().addInputStream("demo2.bpmn", inputStream).name("test2").deploy();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		List<Deployment> list = repositoryService.createDeploymentQuery().deploymentId("payment-retrieval:2:bfea32ed-cfde-11ee-afd6-00ff10dd333e").list();
		System.out.println(list.size());
		ProcessInstance instance = runtimeService.startProcessInstanceById("payment-retrieval:2:bfea32ed-cfde-11ee-afd6-00ff10dd333e");
		ProcessInstance instance2 = runtimeService.startProcessInstanceByKey("收款");
		System.out.println(instance.getBusinessKey());
		return "ok ";
	}
	
	@GetMapping("/runInfo")
	@ResponseBody
	public String runInfo() {
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
		for (ProcessInstance instance : list) {
			System.out.println(instance.getBusinessKey());
			System.out.println(instance.getCaseInstanceId());
			System.out.println(instance.getProcessDefinitionId());
			System.out.println(instance.getId());
		}
		return "ok " + list.size();
	}
	
	
	@GetMapping("/job")
	@ResponseBody
	public String job() {
		List<Task> list = taskService.createTaskQuery().list();
		return "ok " + list.size();
	}
	
	@GetMapping("/list")
	@ResponseBody
	public String list() {
		long count = repositoryService.createDeploymentQuery().count();
		List<Deployment> list = repositoryService.createDeploymentQuery().deploymentName("test2").list();
		for (Deployment deployment : list) {
			System.out.println(deployment.getId());
			System.out.println(deployment.getName());
			System.out.println(deployment.getDeploymentTime());
			System.out.println(deployment.getSource());
		}
		return count + " ok " + list.size();
	}
	
	@GetMapping("/delete")
	@ResponseBody
	public String delete() {
		List<Deployment> list = repositoryService.createDeploymentQuery().deploymentName("test").list();
		for (Deployment deployment : list) {
			//级联删除
			repositoryService.deleteDeployment(deployment.getId());
		}
		return "ok ";
	}
	
	
	@GetMapping("/task")
	@ResponseBody
	public String task() {
		List<Task> list = taskService.createTaskQuery().taskAssignee("demo").list();
		Task task2 = taskService.createTaskQuery().taskAssignee("demo").singleResult();
		for (Task task : list) {
			taskService.claim(task.getId(), "demo");
			taskService.complete(task.getId());
		}
		return "ok " + list.size();
	}
	
	@GetMapping("/history")
	@ResponseBody
	public String history() {
		List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
		return "ok " + list.size();
	}
	
}
