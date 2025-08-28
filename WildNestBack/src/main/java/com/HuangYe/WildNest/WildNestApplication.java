package com.HuangYe.WildNest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * WildNest 酒吧H5项目启动类
 * 
 * @author HuangYe
 * @version 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableAspectJAutoProxy
public class WildNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(WildNestApplication.class, args);
		System.out.println("\n========================================");
		System.out.println("🍺 WildNest 酒吧H5项目启动成功！");
		System.out.println("📖 API文档地址: http://localhost:8080/api/swagger-ui.html");
		System.out.println("📊 数据库监控: http://localhost:8080/api/druid/index.html");
		System.out.println("========================================\n");
	}

}
