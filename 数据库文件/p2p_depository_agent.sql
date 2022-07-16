/*
 Navicat MySQL Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3336
 Source Schema         : p2p_depository_agent

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 16/07/2022 11:41:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for depository_record
-- ----------------------------
DROP TABLE IF EXISTS `depository_record`;
CREATE TABLE `depository_record`  (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `REQUEST_NO` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求流水号',
  `REQUEST_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求类型:1.用户信息(新增、编辑)、2.绑卡信息',
  `OBJECT_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务实体类型',
  `OBJECT_ID` bigint(20) NULL DEFAULT NULL COMMENT '关联业务实体标识',
  `CREATE_DATE` datetime NULL DEFAULT NULL COMMENT '请求时间',
  `IS_SYN` tinyint(1) NULL DEFAULT NULL COMMENT '是否是同步调用',
  `REQUEST_STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '数据同步状态；0-未同步，1-已同步，2-银行存管系统处理失败',
  `CONFIRM_DATE` datetime NULL DEFAULT NULL COMMENT '消息确认时间',
  `RESPONSE_DATA` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '处理结果',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '存管交易记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of depository_record
-- ----------------------------
INSERT INTO `depository_record` VALUES (1537069232930447362, 'REQ_E738E7FFB9144AA48F4D010240D35DD4', 'CONSUMER_CREATE', 'Consumer', 5, '2022-06-15 21:47:16', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537095359627550721, 'REQ_332C214E6128480FA66D4BFD2689D19B', 'CONSUMER_CREATE', 'Consumer', 62, '2022-06-15 23:31:06', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537097657388916737, 'REQ_5A6C194427E045C3A593EB247EA501BC', 'CONSUMER_CREATE', 'Consumer', 11, '2022-06-15 23:40:14', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537113338939465730, 'REQ_6773B90618DE450A97CC67B50BC72E3E', 'CONSUMER_CREATE', 'Consumer', 9, '2022-06-16 00:42:33', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537789586078523393, 'REQ_364B9DCBAE0A47D5B35FC21E1CD2FB95', 'CONSUMER_CREATE', 'Consumer', 8, '2022-06-17 21:29:42', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537815028156162050, 'REQ_DD72400022BA4F6F94F8B85C223E6624', 'CONSUMER_CREATE', 'Consumer', 9, '2022-06-17 23:10:49', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1537816195774898177, 'REQ_8258BA650BBF4362B6541A7717BFF8B7', 'CONSUMER_CREATE', 'Consumer', 9, '2022-06-17 23:15:27', NULL, 1, '2022-06-17 23:44:19', NULL);
INSERT INTO `depository_record` VALUES (1537820413109751810, 'REQ_DE5B158DC59D4AF689E0FC96FBE8FAA1', 'CONSUMER_CREATE', 'Consumer', 4, '2022-06-17 23:32:11', NULL, 1, '2022-06-17 23:43:03', NULL);
INSERT INTO `depository_record` VALUES (1537823069282856961, 'REQ_B30EE96C20D246F098A731CC6F0B42B6', 'CONSUMER_CREATE', 'Consumer', 3, '2022-06-17 23:42:45', NULL, 1, '2022-06-17 23:42:57', NULL);
INSERT INTO `depository_record` VALUES (1537841918795415553, 'REQ_AF266FA7EC634C29BB2E74C79F5EE1C7', 'CONSUMER_CREATE', 'Consumer', 2, '2022-06-18 00:57:39', NULL, 1, '2022-06-18 00:58:04', NULL);
INSERT INTO `depository_record` VALUES (1539836704196775937, 'REQ_63CFD7A356A64F0C8DF0764C3DA349FF', 'CREATE', 'Project', 1539537920254316545, '2022-06-23 13:04:14', NULL, 1, '2022-06-23 13:04:14', '{\"signature\":\"MrEwY0XVyrZwJiwZzg1/Vsm5knIdVkPnuk5+8vz+lp4T/jMzI6Wrs+F7XQzSjjVAH3BV0GD1KvqMJE/ITkjFTg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_63CFD7A356A64F0C8DF0764C3DA349FF\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539837062004461570, 'REQ_63CFD7A356A64F0C8DF0764C3DA349FF', 'CREATE', 'Project', 1539537920254316545, '2022-06-23 13:05:39', NULL, 1, '2022-06-23 13:05:39', '{\"signature\":\"MrEwY0XVyrZwJiwZzg1/Vsm5knIdVkPnuk5+8vz+lp4T/jMzI6Wrs+F7XQzSjjVAH3BV0GD1KvqMJE/ITkjFTg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_63CFD7A356A64F0C8DF0764C3DA349FF\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539837152907612161, 'REQ_B8FE79C8CBB64F608DA16F99B604D763', 'CREATE', 'Project', 1539537800569851906, '2022-06-23 13:06:01', NULL, 1, '2022-06-23 13:06:01', '{\"signature\":\"HvjpYXCTESjsglW9wN3BjbYelSUvyO8Oo72zjbdqrmuKKHzDeEDL1eKSFcT0qpW1TcU9OC6Q7oVUfIcSvv+7Rw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_B8FE79C8CBB64F608DA16F99B604D763\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539837432403447810, 'REQ_8143C021F9F34D3CBD453E6C6EA17C05', 'CREATE', 'Project', 1539537783796830210, '2022-06-23 13:07:07', NULL, 1, '2022-06-23 13:07:08', '{\"signature\":\"hcJQVE+JCAAuofG6gezJhZZ5rVX9tCkb2RIERhViLXEY2Jk+LNJPVM7R2SLPOPxdpIZ1HqUuOA1up8yhN/xRZw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_8143C021F9F34D3CBD453E6C6EA17C05\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539837562590449666, 'REQ_4F28F0BA6E8F4D08B077BFA14DE606C4', 'CREATE', 'Project', 1539537690435817473, '2022-06-23 13:07:38', NULL, 1, '2022-06-23 13:07:39', '{\"signature\":\"eQr3rExnwv13JrSgD0mFhii3BEBUJCjd3JwhpGgEopD7bBkj2Lla4VRhv8aKI/GKNBbjXE/M2S797oak+YeI3A==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_4F28F0BA6E8F4D08B077BFA14DE606C4\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539874805183008770, 'REQ_421AE00E047144E8AADE2821BE2E257B', 'CREATE', 'Project', 1539537097013104642, '2022-06-23 15:35:37', NULL, 1, '2022-06-23 16:06:35', '{\"signature\":\"gexMjHyNk6lE7+NMvS0+RptNhxUWZO2vmS8pizN/y4gr/71/J+AQRo95+U85uaa43FhU1htC44N8kEK1oZibZQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_421AE00E047144E8AADE2821BE2E257B\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539883239953424385, 'REQ_ED42339DA4B2462E841BF6B0C86EE9CB', 'CREATE', 'Project', 1539536981413892097, '2022-06-23 16:09:08', NULL, 1, '2022-06-23 16:09:09', '{\"signature\":\"Wt1wc6psSj7XZg/jhVCsUWEG3+2DfWQ+ViZlcmXYlntFowvnqctVJmuSzYyNFoSl/hclsBoB4iRO/+3MtZNaVg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_ED42339DA4B2462E841BF6B0C86EE9CB\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539883394911981569, 'REQ_15C51AC01498406CA7D3160B97CF8417', 'CREATE', 'Project', 1539536790002634754, '2022-06-23 16:09:45', NULL, 1, '2022-06-23 16:09:46', '{\"signature\":\"K0CkcCwt7ojvyUuyh4lcm3e/n34ufBIHKcxmDCndvEi1AQWEGFDoroHKehkcwNOwvWGjAvXRF5anR3S74O08ww==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_15C51AC01498406CA7D3160B97CF8417\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539884314148331521, 'REQ_A3ABD2D24C744D8CA2089EDA8143EDFA', 'CREATE', 'Project', 1539536471688515585, '2022-06-23 16:13:24', NULL, 1, '2022-06-23 16:16:23', '{\"signature\":\"IN4/MkuVQLYzk5234p7dxy+z1kyNoSKnw+f+l7xCc5F2uDitH5b/wYVSG7NbiYxYGNKGtNGjcsFAUe9AizOFJg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_A3ABD2D24C744D8CA2089EDA8143EDFA\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1539886011071352834, 'REQ_FA09326D59DB4A20AE5FB5264CF8567C', 'CREATE', 'Project', 1539537515789193218, '2022-06-23 16:20:09', NULL, 1, '2022-06-23 16:41:39', '{\"signature\":\"VECtax82G5PgEuGdPRyeULhzsu/lm2FDrhxK/4KKcvrQZd8Q7Ro2aazSKkupbIUdGfrdVmKHZlpyib1txzfzYQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_FA09326D59DB4A20AE5FB5264CF8567C\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541441494806757377, 'REQ_87615C8072E84EC39CE0CE8A56D188B5', 'TENDER', 'UserAutoPreTransactionRequest', 1541441494353772546, '2022-06-27 23:21:06', NULL, 1, '2022-06-27 23:21:06', '{\"signature\":\"CI/3ubCVR9ABkJDIj36jzuVBc312DHh7TIe2FM6Y1KQdzKUmKLQaQHoZ/PXCqKUfCMDkqiSSNnYEQ1AIkGnDPQ==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_87615C8072E84EC39CE0CE8A56D188B5\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541444452554440705, 'REQ_062BD24724BA43E7B2EDE7E7ADA98195', 'TENDER', 'UserAutoPreTransactionRequest', 1541444451312930817, '2022-06-27 23:32:51', NULL, 1, '2022-06-27 23:32:51', '{\"signature\":\"IVoOLZm9wm3yOeckt9cl7kpsQKNeYv7e98U+89BtmBTHNdZEmJjUg528zMA4fZrbtQ2k/IH4U9hCtB17VpPw6Q==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_062BD24724BA43E7B2EDE7E7ADA98195\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541666166416617474, 'REQ_25F392D8E19F46809D316BB0100262A2', 'TENDER', 'UserAutoPreTransactionRequest', 1541666159466643458, '2022-06-28 14:13:51', NULL, 1, '2022-06-28 14:13:53', '{\"signature\":\"OvGbAlgb6EjnOnnJGvEI9kpkg9kwyL52ZT3ETRy/EIM/CJ6eYSDk0HxLRy/aKYE0R1XQ8bAnDl++GRC1azPjkA==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_25F392D8E19F46809D316BB0100262A2\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541666810665906178, 'REQ_B256E86D4F4A454E9463C3284B5D6823', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:16:25', NULL, 1, '2022-06-28 14:16:26', '{\"signature\":\"cjy2NVswiFUEWl3SkX+5SOhvW1TUxKQdzL6RNFe8is/7ZMGNnk/pIk6jN0ENqxtjXjz2MM7VqQrOnhc6VmOPCw==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_B256E86D4F4A454E9463C3284B5D6823\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541668091811868674, 'REQ_387E6F5778F04C1795315CFF31409550', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:21:31', NULL, 1, '2022-06-28 14:21:31', '{\"signature\":\"crzpCm9uHGFNOJw2YlZwKERh00/YabjdGBVHZLL2eQxyoXnarKrJKM0ztckHfp5CE8TTyXqSzLH7+vScdMr/Zg==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_387E6F5778F04C1795315CFF31409550\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541668534474518530, 'REQ_AACBFF26D5244FDA94AFA443F137B757', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:23:16', NULL, 1, '2022-06-28 14:23:17', '{\"signature\":\"Ty89M6qxlpbKQ9EGMTLc049Jg66iiC00tuPyg+CcXct3K6zanjx7xiybk70rHfVrqO7/v1x5maZ93Ebfgmp9TA==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_AACBFF26D5244FDA94AFA443F137B757\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541671260197498882, 'REQ_C656A2D933E9433D87979A94D75C3A48', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:34:06', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541671545619886081, 'REQ_9E6C9841ED9246A8B4653B882A68F73C', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:35:14', NULL, 1, '2022-06-28 14:35:14', '{\"signature\":\"A9vdqP1dchGtnigBlnw2e7ZO1eW7tN3OlJpntyOm4i5/bei5wL3wO1sdFKLqESigV3NnG3cJw5stqpAAs0FVTg==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_9E6C9841ED9246A8B4653B882A68F73C\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541671819008815105, 'REQ_20ECA1C9B4DF4DF49B3387CE0F5A5DA6', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:36:19', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541672324019793921, 'REQ_057C4714B4C240498D19EE980E70C75A', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:38:20', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541675581580521473, 'REQ_8C32CCB7E65449ACA7A65481FDB29268', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:51:16', NULL, 1, '2022-06-28 14:51:24', '{\"signature\":\"kLn0WugllHvdxOAD4JBISZTA6IwA0m2tNitw7VQbjoL3rTodBYV1j3IglYZ8hqIIPRo2/1Kf2Tx879Fewtuu6w==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_8C32CCB7E65449ACA7A65481FDB29268\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541675853954428929, 'REQ_94E9EE1031B341A18098379FC9EC2338', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:52:21', NULL, 1, '2022-06-28 14:52:22', '{\"signature\":\"j40HQCaLVpZzwcfsprEU02u0j4pBk5xZto3M+hY2GlZnkTbxZ9K9yhox+WCRC8nv2UAQfLWiZcbNceBi5XWuEg==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_94E9EE1031B341A18098379FC9EC2338\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541675973836025858, 'REQ_D9F7A42D43A347B5A9F47C8D7C706188', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:52:50', NULL, 1, '2022-06-28 14:52:50', '{\"signature\":\"BiCdq50FOxwCal0g5jSIrU7nycmU2g47sE8/hHtmgSz+rJRRBsdeTh+X7i/ffXJDnTYIHwaRzJxO4KOctX4okw==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_D9F7A42D43A347B5A9F47C8D7C706188\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541676396844167169, 'REQ_51707EC3059F48E2AA0FBDAC4CFFABFE', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 14:54:31', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541678402149298178, 'REQ_0C7971FDE1CC41389E43AD95773C79B7', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 15:02:29', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541679289613692930, 'REQ_C09DBACE9E264E9BBEA354C40144D7D5', 'CONSUMER_CREATE', 'Consumer', 11, '2022-06-28 15:06:00', NULL, 1, '2022-06-28 15:11:05', NULL);
INSERT INTO `depository_record` VALUES (1541681187427201026, 'REQ_B5D0DA4B2D584F87ACF38B694A9DA8DC', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 15:13:33', NULL, 1, '2022-06-28 15:13:33', '{\"signature\":\"dLnn34/z1lF4uI4IwVVM5JRJYp3iWQvd2tZI0glT+PKJd0IIeB39uLVw+M9pd6roOHsVhJ5a3OvrZ6sJxX24WQ==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_B5D0DA4B2D584F87ACF38B694A9DA8DC\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541681799451652097, 'REQ_756492C71DF048688BE26E4572AF2EB3', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 15:15:59', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1541682701231206401, 'REQ_1D0E44052D10410D9B64161C57ACCF29', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 15:19:34', NULL, 1, '2022-06-28 15:19:34', '{\"signature\":\"ENJFCsunU/ZN79/O5HBnx1trFDgro76MRc+PX3QK/69MvEaw91CJItBQef+wEc7vfpX9E/kAUkO5C3epQ35g6A==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_1D0E44052D10410D9B64161C57ACCF29\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541682809918205954, 'REQ_DE8C93ABB49241FAB93DC0C6B8F37CC4', 'FULL_LOAN', 'LoanRequest', 1539536981413892097, '2022-06-28 15:20:00', NULL, 1, '2022-06-28 15:20:00', '{\"signature\":\"YjqRk53+CTS0egEHFWGQioXKSR0ngc7GUpX9mBgoPKy+mWo/YVUhH/hcKElEMjTbAfMX2ulEMA/41gG6MXdTHA==\",\"respData\":{\"respMsg\":\"系统异常\",\"requestNo\":\"REQ_DE8C93ABB49241FAB93DC0C6B8F37CC4\",\"respCode\":\"00001\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541685801014800385, 'REQ_576F7D59CCE9491BA63697F5379D3A8E', 'CONSUMER_CREATE', 'Consumer', 1014, '2022-06-28 15:31:53', NULL, 1, '2022-06-28 15:32:05', NULL);
INSERT INTO `depository_record` VALUES (1541687283931951105, 'REQ_78DAC9E2DEF34A1891C4F56DE84314EB', 'CREATE', 'Project', 1541687203548102658, '2022-06-28 15:37:46', NULL, 1, '2022-06-28 15:37:48', '{\"signature\":\"NwwjHTn2VVRyfyhMNkoRQuiriZdq0IwDQlWqoFWw5bb9u+7b79kSCancc7IreustwiKjCVVlaX273ZiWHDOLCw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_78DAC9E2DEF34A1891C4F56DE84314EB\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541688959246020610, 'REQ_B14AA15CA1704B7382B46982E3B28E6E', 'CONSUMER_CREATE', 'Consumer', 1013, '2022-06-28 15:44:26', NULL, 1, '2022-06-28 15:44:36', NULL);
INSERT INTO `depository_record` VALUES (1541697353436639233, 'REQ_5E101AB965894AF1A9AF02D65F02E3AF', 'TENDER', 'UserAutoPreTransactionRequest', 1541697352325148674, '2022-06-28 16:17:47', NULL, 1, '2022-06-28 16:17:48', '{\"signature\":\"fwtGICS52TVX97Q3S0V4m/9ow/UGMEKMYGgGIPje7+2Irey3h4ynAlHJJrnvdU3dPpedmSkC1imubmz5EMlJzg==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_5E101AB965894AF1A9AF02D65F02E3AF\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541698135976325121, 'REQ_4D4F8427236544F0ADD477CF71839CAC', 'CONSUMER_CREATE', 'Consumer', 1012, '2022-06-28 16:20:54', NULL, 1, '2022-06-28 16:21:03', NULL);
INSERT INTO `depository_record` VALUES (1541698556941840386, 'REQ_5D8801486832453D8026E43B035F8955', 'TENDER', 'UserAutoPreTransactionRequest', 1541698556157505537, '2022-06-28 16:22:34', NULL, 1, '2022-06-28 16:22:34', '{\"signature\":\"Q5Up4tVpsqg73CeC251wanVlFxTU/DrtvzKd+oo2Mce+E/VFZAvcvB5trZl6h/X2eg7Og9CEIJYqUbZlrgdCZQ==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_5D8801486832453D8026E43B035F8955\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541699157171908609, 'REQ_C589DB6C03AD4A8E97AB97188DB32FA1', 'FULL_LOAN', 'LoanRequest', 1541687203548102658, '2022-06-28 16:24:57', NULL, 1, '2022-06-28 16:24:58', '{\"signature\":\"YmTvmD6g/WJ72xaLNOviTwsrNJh+YSoNC2yvu14MhhL4ehSuXUy4Xkmyd6XcbX8d9gX7QCNarehMTPFtUFw4AQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_C589DB6C03AD4A8E97AB97188DB32FA1\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541986804142452737, 'REQ_8CD6F9836C0C4144AE6CB4349C4F0F33', 'CREATE', 'Project', 1541986720675811330, '2022-06-29 11:27:56', NULL, 1, '2022-06-29 11:28:00', '{\"signature\":\"SHHT8crVPD/YRgw9K89ifYlOTw14SZrf+S4MzUjF6QXxuIfENo578xwT8pFfizxzdW6nN0OVzrB8bkUJb157jw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_8CD6F9836C0C4144AE6CB4349C4F0F33\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541992058858778626, 'REQ_33546F3BB72E4FD39C6A02F5D8B52D22', 'CREATE', 'Project', 1541991148732592130, '2022-06-29 11:48:50', NULL, 1, '2022-06-29 11:48:51', '{\"signature\":\"WpUO9KTS7fzxxJ6Bdc0swYNWo/LdCLsKlu0WOxx+3BFEG7PwAS6iU0H4RuPX9pihJKrrk7o4hNtIhAUFyyXa8g==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_33546F3BB72E4FD39C6A02F5D8B52D22\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541992831508295681, 'REQ_13A90C1A765E473FBEDF85B8502BB2FF', 'TENDER', 'UserAutoPreTransactionRequest', 1541992830593970178, '2022-06-29 11:51:55', NULL, 1, '2022-06-29 11:51:55', '{\"signature\":\"bE6q7G35MeqLCJu+UmDD3+9vH1JR0z2FYUxMNu9swqIm/sB9Yk5ThgJ8zOA6ihxgqvgsbBz512ut0y5YptUk7w==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_13A90C1A765E473FBEDF85B8502BB2FF\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541992983233048578, 'REQ_9040A0D5576746DEA669C00C6A03A8CF', 'TENDER', 'UserAutoPreTransactionRequest', 1541992982717181953, '2022-06-29 11:52:31', NULL, 1, '2022-06-29 11:52:31', '{\"signature\":\"INeAWHtHwYzOBRuXaXP/lY5jqohpb9CoaeyJOM+c8dYcQM2KEyDA7IcS+6N4FK2RddsrBzNJgEOsLvrbDxhv6Q==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_9040A0D5576746DEA669C00C6A03A8CF\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541994985174347777, 'REQ_22424945AC41470FA276B0211A883A3C', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:00:28', NULL, 1, '2022-06-29 12:00:29', '{\"signature\":\"Ki4aqUKp5H/HtNkGesjmuZgqO2xyYlI2GDG2nEUnI+dTLgLkdhrPj2oy+znYPQVx7Voxd7rUY8t9DWoymq6RVA==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_22424945AC41470FA276B0211A883A3C\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541998008529334273, 'REQ_4183ECBEB79849179A85796B6AA90EE2', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:12:29', NULL, 1, '2022-06-29 12:12:29', '{\"signature\":\"VNcHAKjh1nUE7PkQ0cuXm9p2ni/+z0WoDArrPEFVcrU0pq4B2BqZyeWhdCKq0/KfTduyrUZ5bpcqj53pzexpZw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_4183ECBEB79849179A85796B6AA90EE2\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1541999716877742081, 'REQ_38F0AB06400B4F1E87320960089E266E', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:19:16', NULL, 1, '2022-06-29 12:19:17', '{\"signature\":\"fdTOt7acAjzHTcxY8svi03cfh3+5HomNc3ebNpW/ZLxsKGjSTW+a9Hce5h0r1rCl2dfHT+HMjzfCpXzIj51Eug==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_38F0AB06400B4F1E87320960089E266E\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542000911885283330, 'REQ_7760167DA5A441C799732BEC8DF515D3', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:24:01', NULL, 1, '2022-06-29 12:24:02', '{\"signature\":\"Zj69zIebDFwgzh+SOWymn9gVlgIW1j99TwOD3Kx+LvPGe6bIJQdyhVqegba18/nQ51IXw0Ddf9+cIWlIzUKQHQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_7760167DA5A441C799732BEC8DF515D3\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542005596482449410, 'REQ_5A0953D4AC51418F924A1DE3C2A29D3D', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:42:38', NULL, 1, '2022-06-29 12:42:38', '{\"signature\":\"Lts4N5856RJrrIJIOaWSJMlOhM4wpQxszQG3jOG/TwjetuJsEIDbel/HbYF3Xt+fskZuvIlpKD/3yhAJFSDbMQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_5A0953D4AC51418F924A1DE3C2A29D3D\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542007175000371202, 'REQ_A6FAA01AE7CD40FBA77CAA70F6FCFB49', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:48:54', NULL, 1, '2022-06-29 12:48:55', '{\"signature\":\"Euo4H+7u85M/gS37cxDFtYTQ0wByBYaUsKyqnztqZ9pI9HCN5jeE/czC9cUxWsAPdMTBvqr6YsSQ8hym2TH+Ng==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_A6FAA01AE7CD40FBA77CAA70F6FCFB49\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542007830167433217, 'REQ_C71D21C9152649D29D210FE2C1D1AE5F', 'FULL_LOAN', 'LoanRequest', 1541991148732592130, '2022-06-29 12:51:30', NULL, 1, '2022-06-29 12:51:31', '{\"signature\":\"VjzDqai/yWwDtChOnx6/GjD2Ld8i1OLW0Q/woNDeq1DPGiRSWkUdvXu/LlpUe6vFl1Q9HN59hGsYuNnT58Tm+Q==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_C71D21C9152649D29D210FE2C1D1AE5F\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542059117392773121, 'REQ_4AF3AD9B098547D39A892E908679760D', 'REPAYMENT', 'UserAutoPreTransactionRequest', 1542007845602480129, '2022-06-29 16:15:17', NULL, 1, '2022-06-29 16:16:56', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542096264131342338, 'REQ_EB4A7ED2C04046B7BC4572311B84D2B6', 'REPAYMENT', 'Repayment', 1542007845602480129, '2022-06-29 18:42:54', NULL, 1, '2022-06-29 18:42:55', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542374633500090369, 'REQ_CDBD962180524FEE956E4F8FFE129A0C', 'REPAYMENT', 'UserAutoPreTransactionRequest', 1542007845858332673, '2022-06-30 13:09:02', NULL, 1, '2022-06-30 13:09:05', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542375540472193026, 'REQ_FD48F14FCAE54F049A168A480436D3E4', 'REPAYMENT', 'Repayment', 1542007845858332673, '2022-06-30 13:12:39', NULL, 1, '2022-06-30 13:12:40', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542375883406876674, 'REQ_340A78F252D5442CBED8D458A56DA8C7', 'REPAYMENT', 'UserAutoPreTransactionRequest', 1542007845929635841, '2022-06-30 13:14:01', NULL, 1, '2022-06-30 13:14:01', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542375889568309250, 'REQ_7EEE6B0A8E4C406295F929C936C99678', 'REPAYMENT', 'Repayment', 1542007845929635841, '2022-06-30 13:14:03', NULL, 1, '2022-06-30 13:14:03', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1542728979515965441, 'REQ_2474DF34642C4B82992A4E8AD30DDE46', 'REPAYMENT', 'UserAutoPreTransactionRequest', 1542007846059659266, '2022-07-01 12:37:05', NULL, 0, NULL, NULL);
INSERT INTO `depository_record` VALUES (1543163976068747266, 'REQ_24387481D12B4010B2CDC501A10E2360', 'CREATE', 'Project', 1543163157344804866, '2022-07-02 17:25:36', NULL, 1, '2022-07-02 17:25:40', '{\"signature\":\"hinfD74e6/QF5CDS0/KfGjqfbhWE1JjU9FDJhG/DAobeR3BKbsBg3zcU+crPhMu/tdizFxPLR8ZAeoHMVlxFbA==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_24387481D12B4010B2CDC501A10E2360\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543164991811735553, 'REQ_DC4C580F1B3640BEB6FF8E2D95712A26', 'CREATE', 'Project', 1543164927311732737, '2022-07-02 17:29:39', NULL, 1, '2022-07-02 17:29:40', '{\"signature\":\"O2YHby1yijgiL0c2iS2LDii265hebBQGVhhOGcevcVyMqQZWFJLb5kfBilGgd+E4W3Ad+1jcMV8W0Xc8z1fchw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_DC4C580F1B3640BEB6FF8E2D95712A26\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543165435359383554, 'REQ_5792E40F0C83413C9033C10E726C8D74', 'CREATE', 'Project', 1543165400387280897, '2022-07-02 17:31:25', NULL, 1, '2022-07-02 17:31:25', '{\"signature\":\"gYTbwFNorn8ApASKd61A1q1bWP1MmVZC+2vAt3qn1M/mWO2OMDtxFPoxnX2uA4ja9wjyRFHQtExd06ex4sOXpw==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_5792E40F0C83413C9033C10E726C8D74\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543165506918404098, 'REQ_95BE3078708A4B42B642B6025519C987', 'CREATE', 'Project', 1543165467395481601, '2022-07-02 17:31:42', NULL, 1, '2022-07-02 17:31:42', '{\"signature\":\"CIK9uYWiSiZwlsB8s6xR54l9RGeap5T9RlF3+ZwxQCMyS4p5oAw42/Tnfw0gONThqAn7+cJyqBm9j4nOkoRhkQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_95BE3078708A4B42B642B6025519C987\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543165611323019265, 'REQ_3CFE98E15D6B4D2B889FFDB25FBF1EE4', 'CREATE', 'Project', 1543165556713185282, '2022-07-02 17:32:07', NULL, 1, '2022-07-02 17:32:07', '{\"signature\":\"MjulAxYg99LGQCjHCcHi460kfMOeHsmjfoJFL0GPUu9m9Lv0lOGLWdhqpkYEKwdtQCWv+ZToFoi2SFypG3Hk0A==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_3CFE98E15D6B4D2B889FFDB25FBF1EE4\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543247490759815170, 'REQ_B62D1CE985314D3A8CD02271455B6CCC', 'TENDER', 'UserAutoPreTransactionRequest', 1543247479594631170, '2022-07-02 22:57:27', NULL, 1, '2022-07-02 22:57:30', '{\"signature\":\"SLkmy2e483VfmZlZ70Bll6dmGw+nloPqM2Af8Eo18GULw4W4z1BoBvQ4kYlyBy5xcmZ6uRxwLEuWEmc1X6v6nA==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_B62D1CE985314D3A8CD02271455B6CCC\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543247877407535106, 'REQ_7D67EACB169F423D8726EF50ADCB05C8', 'TENDER', 'UserAutoPreTransactionRequest', 1543247877017518081, '2022-07-02 22:59:01', NULL, 1, '2022-07-02 22:59:01', '{\"signature\":\"gzRC7HHu/6MOTSPq8e9S98F+Vu6JiAv36QejHN4NInSVSsQlHmXzoe3wPBgkZjtoFXf4uk20l+JUDR/X9HgGjg==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_7D67EACB169F423D8726EF50ADCB05C8\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543249119202537473, 'REQ_92938ECB53A94E919C905558FC1C38D1', 'TENDER', 'UserAutoPreTransactionRequest', 1543249118745411586, '2022-07-02 23:03:57', NULL, 1, '2022-07-02 23:03:57', '{\"signature\":\"jRc/LjsZJjM0eSLavqXMlknL+4XLFfQQpb7/vQXLMYR5pmJakKJo6DbIT0ZguniS+W6waUyCvbYcoDK4O0VcYw==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_92938ECB53A94E919C905558FC1C38D1\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543249208402800642, 'REQ_75164A31B7324E6BA681E0B418E15FEC', 'FULL_LOAN', 'LoanRequest', 1543165467395481601, '2022-07-02 23:04:18', NULL, 1, '2022-07-02 23:04:19', '{\"signature\":\"a0vl4VYIogW+EwbcSwlthqoLMc4u78DitJpQjUn9Pl0NfMkTMXULyA4HFHy6Sl/HUx6pOfY3zPZiLmNgSB+kZQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_75164A31B7324E6BA681E0B418E15FEC\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543251367781494786, 'REQ_369A8A3093874A918C0AA1FD259E2AD2', 'TENDER', 'UserAutoPreTransactionRequest', 1543251367005601793, '2022-07-02 23:12:53', NULL, 1, '2022-07-02 23:12:53', '{\"signature\":\"YmdMc/YeDKFm+YbunEQuIkqYBHHzhw7uwlb8ephGnizniJPEsF1eg8VRILWW/0rR+YXjhu26DhSTCIgqg19AIA==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_369A8A3093874A918C0AA1FD259E2AD2\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543253243985948673, 'REQ_C18BD7E4CF8D428EA1457BC22E019B06', 'FULL_LOAN', 'LoanRequest', 1543165556713185282, '2022-07-02 23:20:20', NULL, 1, '2022-07-02 23:20:21', '{\"signature\":\"ke66FCBGQ518sjM0ey6cHmHNkV5eSL4JYuKCb5hZWwF1aVPLqy5RppV+j92EaTwbxxYhH6TGP7dEW9zisB2JYg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_C18BD7E4CF8D428EA1457BC22E019B06\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543253848150274049, 'REQ_48DE51F889274CE4B6E8CD88B9503B83', 'TENDER', 'UserAutoPreTransactionRequest', 1543253847542185986, '2022-07-02 23:22:44', NULL, 1, '2022-07-02 23:22:45', '{\"signature\":\"ZLVz4ExLtnInIPTTYtDIp+OWsIS+8kH86NMUazQYMMSSznT16PVQMg55sSWlJ2u5c70LC2mjNyf1qDISJEp4/g==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_48DE51F889274CE4B6E8CD88B9503B83\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543253988424577025, 'REQ_5CD432E9393747A4A1D65A2619C5B7CB', 'TENDER', 'UserAutoPreTransactionRequest', 1543253987971678209, '2022-07-02 23:23:18', NULL, 1, '2022-07-02 23:23:18', '{\"signature\":\"htw3ANlT7V4Kkirbh2uEQqmDUBDKPTccSBrBz8rcb1eUOGCGBgCPCan+88fwaZT1XIi5TFzPjuD9YyIea04xVA==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_5CD432E9393747A4A1D65A2619C5B7CB\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543254101830168578, 'REQ_A64921A4C49C4C1AB59AC3CE6C874288', 'TENDER', 'UserAutoPreTransactionRequest', 1543254101247246337, '2022-07-02 23:23:45', NULL, 1, '2022-07-02 23:23:45', '{\"signature\":\"NYKJGqmtl65cpS6XclwOmoIuTsN74Dy+BKDzGZ5ug4n9TBACQ5d6YRuCde4RHfhLKSmbTAaWZg2X1d97wPLDKg==\",\"respData\":{\"bizType\":\"TENDER\",\"respMsg\":\"成功\",\"requestNo\":\"REQ_A64921A4C49C4C1AB59AC3CE6C874288\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543254218851250178, 'REQ_DD1A11DE78C3459EAB68F4E91EF9F994', 'FULL_LOAN', 'LoanRequest', 1543165400387280897, '2022-07-02 23:24:13', NULL, 1, '2022-07-02 23:24:13', '{\"signature\":\"flCClNLn3UTzlAx8GNC+pn9TsD73YhLg8+EGSLjtkFMEKxqepJz8CMdlgDTeMh8g3I3r6enERExwzZt3yCQ9BQ==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"REQ_DD1A11DE78C3459EAB68F4E91EF9F994\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543890748980711426, 'REQ_A70670E420CE430C9D71D89885BF916E', 'REPAYMENT', 'UserAutoPreTransactionRequest', 1542007846059659267, '2022-07-04 17:33:32', NULL, 1, '2022-07-04 17:37:01', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');
INSERT INTO `depository_record` VALUES (1543891631420329985, 'REQ_F968B256BABC4FB88F63EA5A55608FC2', 'REPAYMENT', 'Repayment', 1542007846059659267, '2022-07-04 17:37:04', NULL, 1, '2022-07-04 17:37:04', '{\"signature\":\"IgQDyz22+0jX/4NvNXHY00iJEt9OtlWfgnHc8qRT5mjK2y+hgFQ7i7oYdY62cfzcVrvQOcQ49ZLXphUH0bxMqg==\",\"respData\":{\"respMsg\":\"成功\",\"requestNo\":\"\",\"respCode\":\"00000\",\"status\":1}}');

SET FOREIGN_KEY_CHECKS = 1;
