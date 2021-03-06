/*
 Navicat MySQL Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3336
 Source Schema         : p2p_consumer

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 16/07/2022 11:40:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bank_card
-- ----------------------------
DROP TABLE IF EXISTS `bank_card`;
CREATE TABLE `bank_card`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '用户标识',
  `BANK_CODE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '银行编码',
  `BANK_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '银行名称',
  `CARD_NUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '银行卡号',
  `MOBILE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '银行预留手机号',
  `STATUS` bit(1) NULL DEFAULT NULL COMMENT '可用状态',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `FK_Reference_1`(`CONSUMER_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户绑定银行卡信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bank_card
-- ----------------------------
INSERT INTO `bank_card` VALUES (1, 5, '888', NULL, '888888', '15378791911', b'0');
INSERT INTO `bank_card` VALUES (3, 11, 'CCB', '中国建设银行', '6222807701917624', '18278539574', b'1');
INSERT INTO `bank_card` VALUES (4, 9, 'HXB', '华夏银行', '6226374064116779', '18278539575', b'1');
INSERT INTO `bank_card` VALUES (6, 4, 'BOC', '中国银行', '6227522042261302', '18278539577', b'1');
INSERT INTO `bank_card` VALUES (7, 3, 'BCOM', '交通银行', '6222601882590510', '18278539578', b'1');
INSERT INTO `bank_card` VALUES (8, 2, 'PAB', '平安银行', '6221550724496322', '18278539579', b'1');
INSERT INTO `bank_card` VALUES (9, 1014, 'ICBC', '中国工商银行', '6222109481381299', '15278538571', b'1');
INSERT INTO `bank_card` VALUES (10, 1013, 'BOC', '中国银行', '6227529891138879', '15278538572', b'1');
INSERT INTO `bank_card` VALUES (11, 1012, 'CMBC', '中国民生银行', '6226009771866718', '15278538573', b'1');

-- ----------------------------
-- Table structure for consumer
-- ----------------------------
DROP TABLE IF EXISTS `consumer`;
CREATE TABLE `consumer`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `FULLNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '真实姓名',
  `ID_NUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
  `MOBILE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '平台预留手机号',
  `USER_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型,个人or企业，预留',
  `ROLE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户角色.B借款人or I投资人',
  `AUTH_LIST` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存管授权列表',
  `IS_BIND_CARD` tinyint(1) NULL DEFAULT NULL COMMENT '是否已绑定银行卡',
  `LOAN_AMOUNT` decimal(10, 0) NULL DEFAULT NULL,
  `STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '可用状态-0 不可用, 1 可用',
  `IS_CARD_AUTH` tinyint(1) NULL DEFAULT NULL COMMENT '是否进行身份验证-0 未验证, 1 已验证',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求流水号',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1016 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'c端用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consumer
-- ----------------------------
INSERT INTO `consumer` VALUES (1, 'C771E5F2E8AE4A18857C033AC93D224C', '', NULL, 'REQ_09FAFF42AD7B403BB0A7DC5F547597FA', '15277000557', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (2, 'B0FA6E29C7594D1B95AD61FF4F0BD428', '陆丑', '315875199601152484', 'USR_C3A439866A754E34A93E6014BFD9B2B7', '18278539579', NULL, NULL, 'ALL', 1, 2000, 1, 0, 'REQ_AF266FA7EC634C29BB2E74C79F5EE1C7');
INSERT INTO `consumer` VALUES (3, 'C7596A9C17FF4DE88C6CD8DBDF676C66', '何箱砍', '912147197906289971', 'USR_3179F0CC9E0E4E2A9CC433DAB29A7430', '18278539578', NULL, NULL, 'ALL', 1, 2000, 1, 0, 'REQ_B30EE96C20D246F098A731CC6F0B42B6');
INSERT INTO `consumer` VALUES (4, '3398E3F06F7C4DE7A6A467C8613DEE87', '沈苫疽', '463770198207206703', 'USR_B332B47F3DB24D60950BB2240146A4C1', '18278539577', NULL, '', 'ALL', 1, 50000, 1, 0, 'REQ_DE5B158DC59D4AF689E0FC96FBE8FAA1');
INSERT INTO `consumer` VALUES (5, 'admin', '黄世仁', '888888', 'USR_6493FD544BF340B4B0242E862CF7D8E5', '15378791911', NULL, '', 'ALL', 1, 2000, 0, 0, 'REQ_E738E7FFB9144AA48F4D010240D35DD4');
INSERT INTO `consumer` VALUES (8, '9C31715548B74A3EB32D2CAC7E725E90', '', '', '', '18278539576', NULL, NULL, 'ALL', 0, 10000, 0, 0, '');
INSERT INTO `consumer` VALUES (9, '48150B5EAD884FC8BF4DB5CD69522458', '魏悼', '366345198810313088', 'USR_DE7A36C53CDA43CE9CD9CFCF47F4CBC5', '18278539575', NULL, '', 'ALL', 1, 2000, 1, 0, 'REQ_8258BA650BBF4362B6541A7717BFF8B7');
INSERT INTO `consumer` VALUES (11, 'FAF352335CBA40339DD52F530DC8A1AF', '施猴', '340193199803233763', 'USR_15269A25592F475A99D2A6379899165E', '18278539574', NULL, NULL, 'ALL', 1, 2000, 1, 0, 'REQ_C09DBACE9E264E9BBEA354C40144D7D5');
INSERT INTO `consumer` VALUES (62, 'EE693BEF0B74494696C6495D233A9AE8', '郑柑', '', 'USR_FD0AC2B63F72424DA0CE139C48CD10F0', '18278539572', NULL, NULL, 'ALL', 1, 2000, 0, 0, '');
INSERT INTO `consumer` VALUES (1001, '15378791981', '', NULL, NULL, '15378791981', NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1002, '15378791982', '', NULL, NULL, '15378791982', NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1003, '15378791983', '', NULL, NULL, '15378791983', NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1004, '15378791984', '', NULL, NULL, '15378791984', NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1005, '15378791985', '', NULL, NULL, '15378791985', NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1006, 'A7E121E3C9004A618CECED54A738697A', '', NULL, 'REQ_D728195E2D0A44C2889473BFAC0BC66F', '15278538579', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1007, '9EFFBE1BCDD7477E84D3E21157D9CDDA', '', NULL, 'REQ_D24C5B1FA4F54BE2877939F663C05ABF', '15278538578', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1008, 'E9F7A2001BE94FA898DEDD0A727436A9', '', NULL, 'REQ_BE265520420447F6B5F7A0BBC28F36C6', '15278538577', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1009, 'FDACFBA4C3E94C18BAF463B47C7A6BC9', '', NULL, 'REQ_688849ADBED74A33BE25F8F7E9E279AE', '15278538576', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1010, 'EF7413C34D734798A401CC77DA511B54', '', NULL, 'REQ_4775753C80D04DB0A9A7ED5B59A5FD1A', '15278538575', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1011, 'C5B228AA7CE74FE4B1FB67EA760ED7D4', '', NULL, 'REQ_1C1E2892201241BB9917215ABE43B4EA', '15278538574', NULL, '', NULL, 0, NULL, NULL, NULL, NULL);
INSERT INTO `consumer` VALUES (1012, '86A627ED69B64F2DB5356B4AE16A7063', '蔡禹', '134589197607074649', 'USR_26A5085DDB5C42A680ABD8645C1DF9DC', '15278538573', NULL, '', 'ALL', 1, 20000, 1, 0, 'REQ_4D4F8427236544F0ADD477CF71839CAC');
INSERT INTO `consumer` VALUES (1013, 'E75B462D9DA74B52BF7577C99E381BA4', '欧阳货', '617929198809167402', 'USR_7D14883543C444D89A8D37B932A9EBF7', '15278538572', NULL, '', 'ALL', 1, 20000, 1, 0, 'REQ_B14AA15CA1704B7382B46982E3B28E6E');
INSERT INTO `consumer` VALUES (1014, 'F07411A40D3343C994729408C978C80C', '宇文伟噎', '441625199710148725', 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', '15278538571', NULL, '', 'ALL', 1, 20000, 1, 1, 'REQ_576F7D59CCE9491BA63697F5379D3A8E');

-- ----------------------------
-- Table structure for consumer_details
-- ----------------------------
DROP TABLE IF EXISTS `consumer_details`;
CREATE TABLE `consumer_details`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '用户标识',
  `ID_CARD_PHOTO` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证照片面标识',
  `ID_CARD_EMBLEM` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证国徽面标识',
  `ADDRESS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '住址',
  `ENTERPRISE_MAIL` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业邮箱',
  `CONTACT_RELATION` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人关系',
  `CONTACT_NAME` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `CONTACT_MOBILE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人电话',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consumer_details
-- ----------------------------
INSERT INTO `consumer_details` VALUES (1, 1014, 'http://127.0.0.1:9000/flash/image/5bf49486212e4e06bb70a0f875c2756f.jpg', 'http://127.0.0.1:9000/flash/image/6dbdd1d0c5ea4f6f998d7af15ec58e89.jpg', '安徽省宿州市埇桥区朱仙庄镇', NULL, NULL, NULL, NULL, '2022-07-01 19:08:09');

-- ----------------------------
-- Table structure for recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '用户标识',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
  `AMOUNT` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '触发时间',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求流水号',
  `CALLBACK_STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '回调状态',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充值记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recharge_record
-- ----------------------------
INSERT INTO `recharge_record` VALUES (1, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 50.00, '2022-07-01 13:01:38', 'REQ_C6D80A3CFD0E44059AE49E253FA2B47C', 0);
INSERT INTO `recharge_record` VALUES (2, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 100.00, '2022-07-01 13:06:29', 'REQ_8C3996E998D146E0BFB78829FFE8BED9', 0);
INSERT INTO `recharge_record` VALUES (3, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 1000.00, '2022-07-01 13:06:57', 'REQ_2D9509B82DF74D42ABAA203BDEEA28EA', 0);
INSERT INTO `recharge_record` VALUES (4, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 300.00, '2022-07-01 13:13:06', 'REQ_7DE1A3CAAA2B4E3399831EAE1778DC41', 0);

-- ----------------------------
-- Table structure for withdraw_record
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_record`;
CREATE TABLE `withdraw_record`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '用户标识',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
  `AMOUNT` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `COMMISSION` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台佣金',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '触发时间',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求流水号',
  `CALLBACK_STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '回调状态',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '提现记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdraw_record
-- ----------------------------
INSERT INTO `withdraw_record` VALUES (1, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 100.00, NULL, '2022-07-01 13:10:30', 'REQ_AFCFA2CA47A44C63B5294B97BA20BB18', 0);
INSERT INTO `withdraw_record` VALUES (2, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 50.00, NULL, '2022-07-01 13:11:55', 'REQ_A9F9D6AC88F345A9AC6403E8F6F21A14', 0);
INSERT INTO `withdraw_record` VALUES (3, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 200.00, NULL, '2022-07-01 13:14:08', 'REQ_533EA55B331542D3B8A9D66037825C1B', 0);
INSERT INTO `withdraw_record` VALUES (4, 1014, 'USR_14D9D9D84128483F8FF0C9D0F7A1616B', 100.00, NULL, '2022-07-01 13:15:33', 'REQ_04870D387EEB4E0CB51731CA54C72B87', 0);

-- ----------------------------
-- View structure for balance_record_view
-- ----------------------------
DROP VIEW IF EXISTS `balance_record_view`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `balance_record_view` AS select `recharge_record`.`CONSUMER_ID` AS `CONSUMER_ID`,`recharge_record`.`USER_NO` AS `USER_NO`,`recharge_record`.`AMOUNT` AS `AMOUNT`,`recharge_record`.`CREATE_DATE` AS `CREATE_DATE`,`recharge_record`.`CALLBACK_STATUS` AS `CALLBACK_STATUS` from `recharge_record` where (`recharge_record`.`CALLBACK_STATUS` = 1);

SET FOREIGN_KEY_CHECKS = 1;
