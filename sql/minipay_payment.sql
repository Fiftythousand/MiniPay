/*
 Navicat Premium Dump SQL

 Source Server         : testdb
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : minipay_payment

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 15/06/2026 11:11:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10, 2) NOT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `paid_at` datetime(6) NULL DEFAULT NULL,
  `payment_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_t4ffsaqe8d6i83gs100u2y3l1`(`payment_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payments
-- ----------------------------
INSERT INTO `payments` VALUES (1, 100.00, '2026-06-14 20:59:48.436714', 'f90f154ed3be4abe', '2026-06-14 20:59:48.424486', 'Paea3a9a3c1f5', 'SUCCESS');
INSERT INTO `payments` VALUES (2, 0.00, '2026-06-14 21:00:50.470105', 'c3e014f75c4c4ae6', NULL, 'Pb0ad27ac893f', 'FAILED');

SET FOREIGN_KEY_CHECKS = 1;
