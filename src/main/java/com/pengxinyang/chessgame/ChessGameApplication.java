package com.pengxinyang.chessgame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@SpringBootApplication
@MapperScan("com.pengxinyang.chessgame.mapper")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ChessGameApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChessGameApplication.class, args);
	}
}
