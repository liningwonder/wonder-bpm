/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/22
 * @Description TODO
 */

package org.wonder.bpm.web;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wonder.bpm.common.Result;
import org.wonder.bpm.common.ResultCodeEnum;
import org.wonder.bpm.dto.StartProcessDTO;
import org.wonder.bpm.dto.TaskInfoDTO;
import org.wonder.bpm.vo.TaskInfoVO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bpm")
public class BpmController {
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@PostMapping("/deploy")
	@ResponseBody
	public Result<String> deploy(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
		if (StringUtils.isBlank(name) || null == file) {
			return Result.build(ResultCodeEnum.SC_BODY_ERROR, "file is null");
		}
		try (InputStream inputStream = file.getInputStream();) {
			String fileName = file.getOriginalFilename();
			//addInputStream 中的filename 即为 act_re_procdef RESOURCE_NAME_   filename一定是 .bpmn的后缀
			//name方法即为  act_re_deployment NAME_
			repositoryService.createDeployment().addInputStream(fileName, inputStream).name(name).deploy();
		} catch (IOException e) {
			e.printStackTrace();
			return Result.build(ResultCodeEnum.SC_UNKNOWN_ERROR, "系统异常");
		}
		return Result.build(ResultCodeEnum.SC_OK, "ok");
	}
	
	@PostMapping("/start")
	@ResponseBody
	public Result<String> start(@RequestBody StartProcessDTO dto) {
		ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionName(dto.getProcessName()).singleResult();
		//Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(dto.getProcessName()).singleResult();
		Map<String, Object> variables = new HashMap<>();
		variables.put("preUser", "currentUser");
		runtimeService.startProcessInstanceById(definition.getId(), variables);
		return Result.build(ResultCodeEnum.SC_OK, "ok");
	}
	
	@GetMapping("/listTasks")
	@ResponseBody
	public Result<List<TaskInfoVO>> listTasks() {
		List<Task> list = taskService.createTaskQuery().list();
		List<TaskInfoVO> resultList = new ArrayList<>();
		for (Task task : list) {
			TaskInfoVO vo = new TaskInfoVO();
			vo.setTaskId(task.getId());
			vo.setTaskName(task.getName());
			resultList.add(vo);
		}
		return Result.build(ResultCodeEnum.SC_OK, resultList);
	}
	
	@PostMapping("/complete")
	@ResponseBody
	public Result<String> complete(@RequestBody TaskInfoDTO dto) {
		Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
		if (null == task) {
			return Result.build(ResultCodeEnum.SC_OK, "task is null");
		}
		Map<String, Object> variables = new HashMap<>();
		variables.put("preUser", "currentUserTask");
		taskService.complete(task.getId(), variables);
		return Result.build(ResultCodeEnum.SC_OK, "ok");
	}
}
