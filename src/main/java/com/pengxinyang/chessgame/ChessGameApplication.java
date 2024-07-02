package com.pengxinyang.chessgame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pengxinyang.chessgame.mapper")
public class ChessGameApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChessGameApplication.class, args);
	}
}
