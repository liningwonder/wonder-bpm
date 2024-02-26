/**
 * Copyright (c) 2024 the UnionPay. All rights reserved.
 *
 * @Author Ning Li
 * @Date 2024/2/23
 * @Description TODO
 */

package org.wonder.bpm.sa;

import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfigure {
	/**
	 * 重写 Sa-Token 框架内部算法策略
	 */
	@Autowired
	public void rewriteSaStrategy() {
		// 重写 Token 生成策略
		SaStrategy.instance.createToken = (loginId, loginType) -> {
			return SaFoxUtil.getRandomString(60);    // 随机60位长度字符串
		};
	}
}
