-- ----------------------------
-- Smart seat reservation module
-- ----------------------------
ALTER TABLE `student` ADD COLUMN `credit_score` int NOT NULL DEFAULT 5 COMMENT '违约积分';
INSERT INTO `student`(`s_id`,`s_name`,`password`,`s_class`,`s_year`,`s_major`,`s_phone_number`,`credit_score`)
SELECT 'super_admin','系统管理员','111','','','','',5
WHERE NOT EXISTS (SELECT 1 FROM `student` WHERE `s_id` = 'super_admin');

INSERT INTO `student`(`s_id`,`s_name`,`password`,`s_class`,`s_year`,`s_major`,`s_phone_number`,`credit_score`)
SELECT 'analyst','数据分析员','111','','','','',5
WHERE NOT EXISTS (SELECT 1 FROM `student` WHERE `s_id` = 'analyst');

DROP TABLE IF EXISTS `smart_violation`;
DROP TABLE IF EXISTS `smart_seat_reservation`;
DROP TABLE IF EXISTS `smart_seat`;

CREATE TABLE `smart_seat` (
  `seat_id` varchar(64) NOT NULL COMMENT '座位编号',
  `room_id` varchar(32) NOT NULL COMMENT '教室编号',
  `building_id` varchar(32) NOT NULL COMMENT '楼编号',
  `seat_no` varchar(32) NOT NULL COMMENT '座位号',
  `row_index` int DEFAULT 1 COMMENT '平面图行',
  `col_index` int DEFAULT 1 COMMENT '平面图列',
  `tags` varchar(255) DEFAULT NULL COMMENT '座位标签',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT 'active/disabled',
  `assigned_major` varchar(255) DEFAULT NULL COMMENT '院系专属',
  PRIMARY KEY (`seat_id`),
  UNIQUE KEY `uk_smart_seat_no` (`room_id`,`building_id`,`seat_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='智慧预约座位表';

CREATE TABLE `smart_seat_reservation` (
  `reservation_id` varchar(64) NOT NULL COMMENT '预约编号',
  `s_id` varchar(32) NOT NULL COMMENT '学号',
  `seat_id` varchar(64) NOT NULL COMMENT '座位编号',
  `seat_no` varchar(32) NOT NULL COMMENT '座位号',
  `room_id` varchar(32) NOT NULL COMMENT '教室编号',
  `room_name` varchar(255) DEFAULT NULL COMMENT '教室名称',
  `building_id` varchar(32) NOT NULL COMMENT '楼编号',
  `building_name` varchar(255) DEFAULT NULL COMMENT '教学楼',
  `reservation_date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` varchar(32) NOT NULL COMMENT '待签到/已签到/已取消/违约',
  `checkin_code` varchar(5) DEFAULT NULL COMMENT '5位动态签到码',
  `checkin_time` datetime DEFAULT NULL COMMENT '签到时间',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`reservation_id`),
  UNIQUE KEY `uk_smart_seat_time_status` (`seat_id`,`reservation_date`,`start_time`,`status`),
  KEY `idx_smart_student` (`s_id`),
  KEY `idx_smart_room_date` (`room_id`,`reservation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='智慧座位预约记录';

CREATE TABLE `smart_violation` (
  `violation_id` varchar(64) NOT NULL COMMENT '违约编号',
  `reservation_id` varchar(64) NOT NULL COMMENT '预约编号',
  `s_id` varchar(32) NOT NULL COMMENT '学号',
  `s_name` varchar(255) DEFAULT NULL COMMENT '学生姓名',
  `score_change` int NOT NULL COMMENT '积分变动',
  `reason` varchar(500) DEFAULT NULL COMMENT '原因',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`violation_id`),
  KEY `idx_smart_violation_student` (`s_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='智慧座位违约记录';

INSERT INTO `smart_seat`(`seat_id`,`room_id`,`building_id`,`seat_no`,`row_index`,`col_index`,`tags`,`status`,`assigned_major`) VALUES
('101-S01','101','1','S01',1,1,'靠窗,安静区','active',NULL),
('101-S02','101','1','S02',1,2,'插座,靠窗,安静区','active',NULL),
('101-S03','101','1','S03',1,3,'安静区','active',NULL),
('101-S04','101','1','S04',1,4,'插座,安静区','active',NULL),
('101-S05','101','1','S05',1,5,'靠窗','active',NULL),
('101-S06','101','1','S06',1,6,'插座,靠窗','disabled',NULL),
('101-S07','101','1','S07',2,1,'走道','active',NULL),
('101-S08','101','1','S08',2,2,'插座,走道','active',NULL),
('101-S09','101','1','S09',2,3,'安静区','active','软件工程'),
('101-S10','101','1','S10',2,4,'插座,安静区,院系专属','active','软件工程'),
('101-S11','101','1','S11',2,5,'靠窗','active',NULL),
('101-S12','101','1','S12',2,6,'插座,靠窗','active',NULL),
('201-S01','201','2','S01',1,1,'靠窗,安静区','active',NULL),
('201-S02','201','2','S02',1,2,'插座,靠窗','active',NULL),
('201-S03','201','2','S03',1,3,'安静区','active',NULL),
('201-S04','201','2','S04',1,4,'插座,安静区','active',NULL),
('201-S05','201','2','S05',1,5,'靠窗','active',NULL),
('201-S06','201','2','S06',1,6,'插座,靠窗','disabled',NULL);
