/*
 Navicat MySQL Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3336
 Source Schema         : p2p_transaction_1

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 16/07/2022 11:41:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for claim
-- ----------------------------
DROP TABLE IF EXISTS `claim`;
CREATE TABLE `claim`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `PROJECT_ID` bigint(20) NULL DEFAULT NULL COMMENT '标的标识',
  `PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的编码',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '发标人用户标识(冗余)',
  `SOURCE_TENDER_ID` bigint(20) NOT NULL COMMENT '投标信息标识(转让来源)',
  `ROOT_PROJECT_ID` bigint(20) NULL DEFAULT NULL COMMENT '原始标的标识(冗余)',
  `ROOT_PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始标的编码(冗余)',
  `ASSIGNMENT_REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '债权转让 请求流水号',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `FK_Reference_17`(`PROJECT_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '债权转让标的附加信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of claim
-- ----------------------------

-- ----------------------------
-- Table structure for project_0
-- ----------------------------
DROP TABLE IF EXISTS `project_0`;
CREATE TABLE `project_0`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '发标人用户标识',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发标人用户编码',
  `PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的编码',
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的名称',
  `DESCRIPTION` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '标的描述',
  `TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的类型',
  `PERIOD` int(11) NULL DEFAULT NULL COMMENT '标的期限(单位:天)',
  `ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(投资人视图)',
  `BORROWER_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(借款人视图)',
  `COMMISSION_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(平台佣金，利差)',
  `REPAYMENT_WAY` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '还款方式',
  `AMOUNT` decimal(10, 2) NULL DEFAULT NULL COMMENT '募集金额',
  `PROJECT_STATUS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的状态',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '可用状态',
  `IS_ASSIGNMENT` tinyint(4) NULL DEFAULT NULL COMMENT '是否是债权出让标',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发标请求流水号',
  `MODIFY_DATE` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_0
-- ----------------------------
INSERT INTO `project_0` VALUES (1539536790002634754, 11, 'USR_15269A25592F475A99D2A6379899165E', 'PRO_0533276D26E3407B83EA172888B8EDFA', '施猴女士第2次借款', '个人生活消费', 'NEW', 360, 0.15, 0.15, 0.00, 'FIXED_REPAYMENT', 2000.00, 'FULLY', '2022-06-22 17:12:29', 1, 0, 'REQ_15C51AC01498406CA7D3160B97CF8417', '2022-06-23 16:09:46');
INSERT INTO `project_0` VALUES (1539537097013104642, 11, 'USR_15269A25592F475A99D2A6379899165E', 'PRO_702F480B28924C8190715E2F0EA461CD', '施猴女士第4次借款', '个人生活消费', 'NEW', 360, 0.15, 0.15, 0.00, 'FIXED_REPAYMENT', 20000.00, 'FULLY', '2022-06-22 17:13:42', 1, 0, 'REQ_421AE00E047144E8AADE2821BE2E257B', '2022-06-23 16:08:12');

-- ----------------------------
-- Table structure for project_1
-- ----------------------------
DROP TABLE IF EXISTS `project_1`;
CREATE TABLE `project_1`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '发标人用户标识',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发标人用户编码',
  `PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的编码',
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的名称',
  `DESCRIPTION` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '标的描述',
  `TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的类型',
  `PERIOD` int(11) NULL DEFAULT NULL COMMENT '标的期限(单位:天)',
  `ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(投资人视图)',
  `BORROWER_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(借款人视图)',
  `COMMISSION_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(平台佣金，利差)',
  `REPAYMENT_WAY` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '还款方式',
  `AMOUNT` decimal(10, 2) NULL DEFAULT NULL COMMENT '募集金额',
  `PROJECT_STATUS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的状态',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '可用状态',
  `IS_ASSIGNMENT` tinyint(4) NULL DEFAULT NULL COMMENT '是否是债权出让标',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发标请求流水号',
  `MODIFY_DATE` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_1
-- ----------------------------
INSERT INTO `project_1` VALUES (1539536471688515585, 11, 'USR_15269A25592F475A99D2A6379899165E', 'PRO_1372E83869034FFC9E137045A5D2BB21', '施猴女士第1次借款', '个人生活消费', 'NEW', 360, 0.15, 0.15, 0.00, 'FIXED_REPAYMENT', 2000.00, 'FULLY', '2022-06-22 17:11:11', 1, 0, 'REQ_A3ABD2D24C744D8CA2089EDA8143EDFA', '2022-06-23 16:19:01');
INSERT INTO `project_1` VALUES (1539536981413892033, 13, '8877', 'PRO_8888', '测试888', '测试888', 'NEW', 360, 0.33, 0.33, 0.00, 'FIXED_REPAYMENT', 20000.00, '2111', '2022-06-24 22:13:14', 0, 0, 'REQ_34999', '2022-07-02 22:29:04');
INSERT INTO `project_1` VALUES (1539536981413892097, 11, 'USR_15269A25592F475A99D2A6379899165E', 'PRO_C07FB88D234D42AA9361AE42CDB53B6A', '施猴女士第3次借款', '个人生活消费', 'NEW', 360, 0.15, 0.15, 0.00, 'FIXED_REPAYMENT', 20000.00, 'FULLY', '2022-06-22 17:13:14', 1, 0, 'REQ_ED42339DA4B2462E841BF6B0C86EE9CB', '2022-06-23 16:09:09');

-- ----------------------------
-- Table structure for tender_0
-- ----------------------------
DROP TABLE IF EXISTS `tender_0`;
CREATE TABLE `tender_0`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '投标人用户标识',
  `CONSUMER_USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标人用户名',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标人用户编码',
  `PROJECT_ID` bigint(20) NULL DEFAULT NULL COMMENT '标的标识',
  `PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的编码',
  `AMOUNT` decimal(10, 0) NULL DEFAULT NULL COMMENT '投标冻结金额',
  `TENDER_STATUS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标状态',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标/债权转让 请求流水号',
  `STATUS` tinyint(4) NULL DEFAULT NULL COMMENT '可用状态，0-未同步，1-已同步',
  `PROJECT_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的名称',
  `PROJECT_PERIOD` int(11) NULL DEFAULT NULL COMMENT '标的期限(单位:天) -- 冗余字段',
  `PROJECT_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(投资人视图) -- 冗余字段',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '投标信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tender_0
-- ----------------------------
INSERT INTO `tender_0` VALUES (1541697352325148674, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1541687203548102658, 'PRO_F7E6A8EE636D473EAF0B514C60CFFEAE', 2000, 'LOAN', '2022-06-28 16:17:46', 'REQ_5E101AB965894AF1A9AF02D65F02E3AF', 1, '宇文伟噎女士第1次借款', 360, 0.15);
INSERT INTO `tender_0` VALUES (1541992830593970178, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1541991148732592130, 'PRO_BE0F6E73FF5E43CA8E0D4694B3433BB8', 4000, 'LOAN', '2022-06-29 11:51:54', 'REQ_13A90C1A765E473FBEDF85B8502BB2FF', 1, '宇文伟噎女士第3次借款', 360, 0.05);
INSERT INTO `tender_0` VALUES (1543247479594631170, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543165556713185282, 'PRO_27DE2AE0B1314CA18EDD050871AAFAA3', 4000, 'LOAN', '2022-07-02 22:57:25', 'REQ_B62D1CE985314D3A8CD02271455B6CCC', 1, '宇文伟噎女士第8次借款', 360, 0.05);
INSERT INTO `tender_0` VALUES (1543251367005601793, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543165556713185282, 'PRO_27DE2AE0B1314CA18EDD050871AAFAA3', 5000, 'LOAN', '2022-07-02 23:12:53', 'REQ_369A8A3093874A918C0AA1FD259E2AD2', 1, '宇文伟噎女士第8次借款', 360, 0.05);
INSERT INTO `tender_0` VALUES (1543253987971678209, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543163157344804866, 'PRO_E45D9AE258E0469C9783DD45E0E59A38', 3000, 'FROZEN', '2022-07-02 23:23:18', 'REQ_5CD432E9393747A4A1D65A2619C5B7CB', 1, '宇文伟噎女士第4次借款', 360, 0.05);
INSERT INTO `tender_0` VALUES (1543254101247246337, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1541986720675811330, 'PRO_C82A6B51FCBE4B3CB3EAA7467BBB949E', 8000, 'FROZEN', '2022-07-02 23:23:45', 'REQ_A64921A4C49C4C1AB59AC3CE6C874288', 1, '宇文伟噎女士第2次借款', 360, 0.05);

-- ----------------------------
-- Table structure for tender_1
-- ----------------------------
DROP TABLE IF EXISTS `tender_1`;
CREATE TABLE `tender_1`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CONSUMER_ID` bigint(20) NOT NULL COMMENT '投标人用户标识',
  `CONSUMER_USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标人用户名',
  `USER_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标人用户编码',
  `PROJECT_ID` bigint(20) NULL DEFAULT NULL COMMENT '标的标识',
  `PROJECT_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的编码',
  `AMOUNT` decimal(10, 0) NULL DEFAULT NULL COMMENT '投标冻结金额',
  `TENDER_STATUS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标状态',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投标/债权转让 请求流水号',
  `STATUS` tinyint(4) NULL DEFAULT NULL COMMENT '可用状态，0-未同步，1-已同步',
  `PROJECT_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标的名称',
  `PROJECT_PERIOD` int(11) NULL DEFAULT NULL COMMENT '标的期限(单位:天) -- 冗余字段',
  `PROJECT_ANNUAL_RATE` decimal(10, 2) NULL DEFAULT NULL COMMENT '年化利率(投资人视图) -- 冗余字段',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '投标信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tender_1
-- ----------------------------
INSERT INTO `tender_1` VALUES (1541441494353772546, 9, '48150B5EAD884FC8BF4DB5CD69522458', 'USR_DE7A36C53CDA43CE9CD9CFCF47F4CBC5', 1539536981413892097, 'PRO_C07FB88D234D42AA9361AE42CDB53B6A', 8000, 'FROZEN', '2022-06-27 23:21:06', 'REQ_87615C8072E84EC39CE0CE8A56D188B5', 1, '施猴女士第3次借款', 360, 0.15);
INSERT INTO `tender_1` VALUES (1541444451312930817, 9, '48150B5EAD884FC8BF4DB5CD69522458', 'USR_DE7A36C53CDA43CE9CD9CFCF47F4CBC5', 1539536981413892097, 'PRO_C07FB88D234D42AA9361AE42CDB53B6A', 10000, 'FROZEN', '2022-06-27 23:32:50', 'REQ_062BD24724BA43E7B2EDE7E7ADA98195', 1, '施猴女士第3次借款', 360, 0.15);
INSERT INTO `tender_1` VALUES (1541666159466643458, 9, '48150B5EAD884FC8BF4DB5CD69522458', 'USR_DE7A36C53CDA43CE9CD9CFCF47F4CBC5', 1539536981413892097, 'PRO_C07FB88D234D42AA9361AE42CDB53B6A', 2000, 'FROZEN', '2022-06-28 14:13:49', 'REQ_25F392D8E19F46809D316BB0100262A2', 1, '施猴女士第3次借款', 360, 0.15);
INSERT INTO `tender_1` VALUES (1543247877017518081, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543165467395481601, 'PRO_6652A9285C2D47B59AF97AA05B995142', 4000, 'LOAN', '2022-07-02 22:59:01', 'REQ_7D67EACB169F423D8726EF50ADCB05C8', 1, '宇文伟噎女士第7次借款', 360, 0.05);
INSERT INTO `tender_1` VALUES (1543249118745411586, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543165400387280897, 'PRO_16FB6C7AE54649FC85D22CEB4D1EC2D6', 2000, 'LOAN', '2022-07-02 23:03:57', 'REQ_92938ECB53A94E919C905558FC1C38D1', 1, '宇文伟噎女士第6次借款', 360, 0.05);
INSERT INTO `tender_1` VALUES (1543253847542185986, 1013, 'E75B462D9DA74B52BF7577C99E381BA4', 'USR_7D14883543C444D89A8D37B932A9EBF7', 1543164927311732737, 'PRO_2FA7A9AF9EF84997BEA68B767A0E9F7F', 3000, 'FROZEN', '2022-07-02 23:22:43', 'REQ_48DE51F889274CE4B6E8CD88B9503B83', 1, '宇文伟噎女士第5次借款', 360, 0.05);

SET FOREIGN_KEY_CHECKS = 1;
