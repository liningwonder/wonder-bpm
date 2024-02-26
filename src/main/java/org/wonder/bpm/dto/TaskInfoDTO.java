/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/22
 * @Description TODO
 */

package org.wonder.bpm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TaskInfoDTO {
	@NotBlank(message = "task id can not be blank")
	private String taskId;
}
