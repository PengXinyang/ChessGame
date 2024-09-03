create database chess_db;
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
                        `match` tinyint(4) NOT NULL DEFAULT '0' COMMENT '匹配状态 0未匹配 1已匹配',
                        PRIMARY KEY (`uid`),
                        UNIQUE KEY `uid` (`uid`),
                        UNIQUE KEY `account` (`account`),
                        UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

ALTER TABLE `user` ADD COLUMN `match` INT DEFAULT 0;
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
    `chess_name` varchar(5) not null comment '棋子名称',
    `color` int(11) not null default 0 comment '棋子颜色'
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子表';
/*!40101 SET character_set_client = @saved_cs_client */;

insert into chess values (1,'车',1),(2,'马',1),(3,'相',1),(4,'士',1),(5,'帅',1),
                         (6,'士',1),(7,'相',1),(8,'马',1),(9,'车',1),(10,'炮',1),
                         (11,'炮',1),(12,'兵',1),(13,'兵',1),(14,'兵',1),(15,'兵',1),
                         (16,'兵',1);
insert into chess values (17,'車',0),(18,'馬',0),(19,'象',0),(20,'仕',0),(21,'将',0),
                         (22,'仕',0),(23,'象',0),(24,'馬',0),(25,'車',0),(26,'砲',0),
                         (27,'砲',0),(28,'卒',0),(29,'卒',0),(30,'卒',0),(31,'卒',0),
                         (32,'卒',0);


DROP TABLE IF EXISTS `chess_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
create table `chess_stats`(
    `cid` int(11) not null default 0 comment '棋子id',
    `x` int(5) not null default 0 comment '坐标x',
    `y` int(5) not null default 0 comment '坐标y',
    `color` int(11) not null default 0 comment '棋子颜色',
    `ate` int(11) not null default 0 comment '是否被吃掉',
    `room_id` int(11) not null default 0 comment '房间id',
    primary key (`cid`,`room_id`)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 红方 (color = 1)
insert into chess_stats (cid, x, y, color, ate) values (1, 0, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (2, 1, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (3, 2, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (4, 3, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (5, 4, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (6, 5, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (7, 6, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (8, 7, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (9, 8, 0, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (10, 1, 2, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (11, 7, 2, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (12, 0, 3, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (13, 2, 3, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (14, 4, 3, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (15, 6, 3, 1, 0);
insert into chess_stats (cid, x, y, color, ate) values (16, 8, 3, 1, 0);

-- 黑方 (color = 0)
insert into chess_stats (cid, x, y, color, ate) values (17, 0, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (18, 1, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (19, 2, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (20, 3, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (21, 4, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (22, 5, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (23, 6, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (24, 7, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (25, 8, 9, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (26, 1, 7, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (27, 7, 7, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (28, 0, 6, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (29, 2, 6, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (30, 4, 6, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (31, 6, 6, 0, 0);
insert into chess_stats (cid, x, y, color, ate) values (32, 8, 6, 0, 0);


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

DROP TABLE IF EXISTS `chess_move`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chess_move` (
                                 `cid` int(11) NOT NULL COMMENT '棋子id',
                                 `room_id` int(11) NOT NULL COMMENT '房间id',
                                 `fromX` int(11) NOT NULL COMMENT '原来的横坐标',
                                 `fromY` int(11) NOT NULL COMMENT '原来的纵坐标',
                                 `toX` int(11) NOT NULL COMMENT '到达的横坐标',
                                 `toY` int(11) NOT NULL COMMENT '到达的纵坐标',
                                 `uid` int(11) NOT NULL COMMENT '下棋者id',
                                 PRIMARY KEY (`cid`),
                                 UNIQUE KEY `cid` (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='棋子移动表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `Room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Room` (
                              `room_id` int(11) NOT NULL auto_increment COMMENT '房间id',
                              `room_name` varchar(200) comment '房间名称',
                              `uid_red` int(11) COMMENT '红方uid',
                              `uid_black` int(11) COMMENT '黑方uid',
                              PRIMARY KEY (`room_id`),
                              UNIQUE KEY `cid` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='游戏房间表';
/*!40101 SET character_set_client = @saved_cs_client */;

delete from chess_stats where room_id = 1;


insert into chess_stats (cid, x, y, color, ate, room_id) values (1, 0, 0, 1, 0, 1),
                                                                 (2, 1, 0, 1, 0, 1),
                                                                  (3, 2, 0, 1, 0, 1),
                                                                   (4, 3, 0, 1, 0, 1),
                                                                    (5, 4, 0, 1, 0, 1),
                                                                     (6, 5, 0, 1, 0, 1),
                                                                      (7, 6, 0, 1, 0, 1),
                                                                       (8, 7, 0, 1, 0, 1),
                                                                        (9, 8, 0, 1, 0, 1),
                                                                         (10, 1, 2, 1, 0, 1),
                                                                          (11, 7, 2, 1, 0, 1),
                                                                           (12, 0, 3, 1, 0, 1),
                                                                            (13, 2, 3, 1, 0, 1),
                                                                             (14, 4, 3, 1, 0, 1),
                                                                              (15, 6, 3, 1, 0, 1),
                                                                               (16, 8, 3, 1, 0, 1),
                                                                                (17, 0, 9, 0, 0, 1),
                                                                                 (18, 1, 9, 0, 0, 1),
                                                                                  (19, 2, 9, 0, 0, 1),
                                                                                   (20, 3, 9, 0, 0, 1),
                                                                                    (21, 4, 9, 0, 0, 1),
                                                                                     (22, 5, 9, 0, 0, 1),
                                                                                      (23, 6, 9, 0, 0, 1),
                                                                                       (24, 7, 9, 0, 0, 1),
                                                                                        (25, 8, 9, 0, 0, 1),
                                                                                         (26, 1, 7, 0, 0, 1),
                                                                                          (27, 7, 7, 0, 0, 1),
                                                                                           (28, 0, 6, 0, 0, 1),
                                                                                            (29, 2, 6, 0, 0, 1),
                                                                                             (30, 4, 6, 0, 0, 1),
                                                                                              (31, 6, 6, 0, 0, 1),
                                                                                               (32, 8, 6, 0, 0, 1);