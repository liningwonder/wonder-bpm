/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/23
 * @Description TODO
 */

package org.wonder.bpm.web.login;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonder.bpm.common.Result;
import org.wonder.bpm.common.ResultCodeEnum;
import org.wonder.bpm.dto.LoginDTO;
import org.wonder.bpm.vo.TokenVO;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@PostMapping("/login")
	public Result<TokenVO> login(@RequestBody LoginDTO loginDTO) {
		//TODO查询数据库，判断用户状态，获取用户ID 不增加记住我功能
		StpUtil.login(10001, false);
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		TokenVO tokenVO = new TokenVO();
		BeanUtils.copyProperties(tokenInfo, tokenVO);
		return Result.build(ResultCodeEnum.SC_OK, tokenVO);
	}
	
}
