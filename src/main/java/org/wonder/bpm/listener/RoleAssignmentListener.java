/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/22
 * @Description TODO
 */

package org.wonder.bpm.listener;

import lombok.Data;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Data
@Component("roleAssignmentListener")
public class RoleAssignmentListener implements TaskListener {
	
	private Expression roleExpression;
	
	private Expression userExpression;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		//角色名字可以放在userTask的Expression
		//String getEventName();
		if ("create".equals(delegateTask.getEventName())) {
			String role = roleExpression.getValue(delegateTask).toString();
			String preUser = delegateTask.getVariable("preUser").toString();
			//实现一个接口 userService(String role, String preUser)
			delegateTask.setAssignee("role");
		}
	}
}
