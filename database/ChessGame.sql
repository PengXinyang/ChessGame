use chess_db;
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `account` varchar(50) NOT NULL COMMENT '用户账号',
                        `password` varchar(255) NOT NULL COMMENT '用户密码',
                        `name` varchar(32) NOT NULL COMMENT '用户昵称',
                        `avatar` varchar(500) default null comment '用户头像',
                        `background` varchar(500) DEFAULT NULL COMMENT '主页背景图url',
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

--
-- Table structure for table `chat_detailed`
--

DROP TABLE IF EXISTS `chat_detailed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_detailed` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
                                 `post_id` int(11) NOT NULL COMMENT '消息发送者',
                                 `accept_id` int(11) NOT NULL COMMENT '消息接收者',
                                 `content` varchar(500) NOT NULL COMMENT '消息内容',
                                 `post_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送者是否删除',
                                 `accept_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接受者是否删除',
                                 `withdraw` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否撤回',
                                 `time` datetime NOT NULL COMMENT '消息发送时间',
                                 `status` int(4) not null default 1 comment '消息状态',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
                        `post_id` int(11) NOT NULL COMMENT '对象UID',
                        `accept_id` int(11) NOT NULL COMMENT '用户UID',
                        `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否移除聊天 0否 1是',
                        `unread_num` int(11) NOT NULL DEFAULT '0' COMMENT '消息未读数量',
                        `latest_time` datetime NOT NULL COMMENT '最近接收消息的时间或最近打开聊天窗口的时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `from_to` (post_id,accept_id),
                        UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='聊天表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `chess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
create table `chess` (
    `cid` int(11) not null default 0 primary key comment '棋子id',
    `uid` int(11) NOT NULL COMMENT '用户ID',
    `chess_name` varchar(5) not null comment '棋子名称',
    `color` int(11) not null default 0 comment '棋子颜色'
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `chess_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
create table `chess_stats`(
    `cid` int(11) not null default 0 primary key comment '棋子id',
    `x` int(5) not null default 0 comment '坐标x',
    `y` int(5) not null default 0 comment '坐标y',
    `color` int(11) not null default 0 comment '棋子颜色',
    `ate` int(11) not null default 0 comment '是否被吃掉'
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message_unread`
--

DROP TABLE IF EXISTS `message_unread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE message_unread (
                                `uid` int(11) NOT NULL COMMENT '用户ID',
                                `reply` int(11) NOT NULL DEFAULT '0' COMMENT '回复我的',
                                `at_num` int(11) NOT NULL DEFAULT '0' COMMENT '@我的',
                                `up_vote` int(11) NOT NULL DEFAULT '0' COMMENT '收到的赞',
                                `system_message` int(11) NOT NULL DEFAULT '0' COMMENT '系统通知',
                                `message` int(11) NOT NULL DEFAULT '0' COMMENT '我的消息',
                                `dynamic` int(11) NOT NULL DEFAULT '0' COMMENT '动态',
                                PRIMARY KEY (`uid`),
                                UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息未读数';