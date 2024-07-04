use chess_db;
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `account` varchar(50) NOT NULL COMMENT '用户账号',
                        `password` varchar(255) NOT NULL COMMENT '用户密码',
                        `name` varchar(32) NOT NULL COMMENT '用户昵称',
                        `description` varchar(100) DEFAULT NULL COMMENT '个性签名',
                        `experience` int(11) NOT NULL DEFAULT '0' COMMENT '经验值',
                        `threshold` int(11) NOT NULL DEFAULT '100' COMMENT '经验阈值',
                        `level` double NOT NULL DEFAULT '1' COMMENT '等级',
                        `state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0正常 1注销',
                        `create_date` datetime NOT NULL COMMENT '创建时间',
                        `delete_date` datetime DEFAULT NULL COMMENT '注销时间',
                        `login_state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '登录状态 0未登录 1登录',
                        PRIMARY KEY (`uid`),
                        UNIQUE KEY `uid` (`uid`),
                        UNIQUE KEY `account` (`account`),
                        UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `chess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
create table `chess` (
    `cid` int(11) not null default 0 primary key comment '棋子id',
    `uid` int(11) NOT NULL COMMENT '用户ID',
    `chess_name` varchar(5) not null comment '棋子名称'
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `chess_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
create table `chess_stats`(
    `cid` int(11) not null default 0 primary key comment '棋子id',
    `x` int(5) not null default 0 comment '坐标x',
    `y` int(5) not null default 0 comment '坐标y'
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子状态表';
/*!40101 SET character_set_client = @saved_cs_client */;