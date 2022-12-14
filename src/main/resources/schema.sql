SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `param_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数名',
  `param_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数值',
  `status` tinyint(1) UNSIGNED NOT NULL COMMENT '状态(0不可用,1可用)',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_param_key`(`param_key`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

INSERT INTO `sys_config` VALUES (1, 'attendance_start_time', '06:00', 1, '上班考勤开始时间');
INSERT INTO `sys_config` VALUES (2, 'attendance_time', '08:30', 1, '上班时间');
INSERT INTO `sys_config` VALUES (3, 'attendance_end_time', '09:30', 1, '上班考勤截止时间');
INSERT INTO `sys_config` VALUES (4, 'closing_start_time', '16:30', 1, '下班考勤开始时间');
INSERT INTO `sys_config` VALUES (5, 'closing_time', '17:30', 1, '下班时间');
INSERT INTO `sys_config` VALUES (6, 'closing_end_time', '23:59', 1, '下班考勤截止时间');
INSERT INTO `sys_config` VALUES (7, 'check_distance', 'available', 1, '检查签到地点是否在公司附近');
INSERT INTO `sys_config` VALUES (8, 'checkin_distance', '3000', 1, '签到有效距离');

DROP TABLE IF EXISTS `action`;
CREATE TABLE `action`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行为编号',
  `action_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行为名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_action_name`(`action_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='行为表' ROW_FORMAT=DYNAMIC;

INSERT INTO `action` VALUES (1, 'INSERT', '添加');
INSERT INTO `action` VALUES (2, 'DELETE', '删除');
INSERT INTO `action` VALUES (3, 'UPDATE', '修改');
INSERT INTO `action` VALUES (4, 'SELECT', '查询');
INSERT INTO `action` VALUES (5, 'APPROVAL', '审批');
INSERT INTO `action` VALUES (6, 'EXPORT', '导出');
INSERT INTO `action` VALUES (7, 'BACKUP', '备份');

DROP TABLE IF EXISTS `checkin`;
CREATE TABLE `checkin`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '签到地址',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区划',
  `status` tinyint(3) UNSIGNED NOT NULL COMMENT '考勤结果(0缺勤,1正常,2迟到)',
  `risk` int(255) UNSIGNED NULL DEFAULT 0 COMMENT '疫情风险(1低风险,2中风险,3高风险)',
  `date` date NOT NULL COMMENT '签到日期',
  `create_time` datetime(0) NOT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id_and_date`(`user_id`, `date`) USING BTREE,
  INDEX `idx_date`(`date`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='签到表' ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `city`;
CREATE TABLE `city`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `city` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '城市名称',
  `code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=330 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='疫情城市表' ROW_FORMAT=DYNAMIC;

INSERT INTO `city` VALUES (1, '阿坝市', 'ab');
INSERT INTO `city` VALUES (2, '安康市', 'ak');
INSERT INTO `city` VALUES (3, '阿克苏市', 'aks');
INSERT INTO `city` VALUES (4, '阿拉善市', 'alsm');
INSERT INTO `city` VALUES (5, '安顺市', 'anshun');
INSERT INTO `city` VALUES (6, '安庆市', 'aq');
INSERT INTO `city` VALUES (7, '鞍山市', 'as');
INSERT INTO `city` VALUES (8, '安阳市', 'ay');
INSERT INTO `city` VALUES (9, '百色市', 'baise');
INSERT INTO `city` VALUES (10, '白山市', 'baishan');
INSERT INTO `city` VALUES (11, '宝鸡市', 'baoji');
INSERT INTO `city` VALUES (12, '巴中市', 'bazhong');
INSERT INTO `city` VALUES (13, '蚌埠市', 'bb');
INSERT INTO `city` VALUES (14, '白城市', 'bc');
INSERT INTO `city` VALUES (15, '保定市', 'bd');
INSERT INTO `city` VALUES (16, '博尔塔拉市', 'betl');
INSERT INTO `city` VALUES (17, '北海市', 'bh');
INSERT INTO `city` VALUES (18, '毕节市', 'bijie');
INSERT INTO `city` VALUES (19, '北京市', 'bj');
INSERT INTO `city` VALUES (20, '西双版纳市', 'bn');
INSERT INTO `city` VALUES (21, '亳州市', 'bozhou');
INSERT INTO `city` VALUES (22, '保山市', 'bs');
INSERT INTO `city` VALUES (23, '包头市', 'bt');
INSERT INTO `city` VALUES (24, '本溪市', 'bx');
INSERT INTO `city` VALUES (25, '白银市', 'by');
INSERT INTO `city` VALUES (26, '巴彦淖尔市', 'bycem');
INSERT INTO `city` VALUES (27, '巴音郭楞市', 'bygl');
INSERT INTO `city` VALUES (28, '滨州市', 'bz');
INSERT INTO `city` VALUES (29, '沧州市', 'cangzhou');
INSERT INTO `city` VALUES (30, '长春市', 'cc');
INSERT INTO `city` VALUES (31, '成都市', 'cd');
INSERT INTO `city` VALUES (32, '赤峰市', 'cf');
INSERT INTO `city` VALUES (33, '常德市', 'changde');
INSERT INTO `city` VALUES (34, '长治市', 'changzhi');
INSERT INTO `city` VALUES (35, '潮州市', 'chaozhou');
INSERT INTO `city` VALUES (36, '承德市', 'chengde');
INSERT INTO `city` VALUES (37, '郴州市', 'chenzhou');
INSERT INTO `city` VALUES (38, '池州市', 'chizhou');
INSERT INTO `city` VALUES (39, '崇左市', 'chongzuo');
INSERT INTO `city` VALUES (40, '滁州市', 'chuzhou');
INSERT INTO `city` VALUES (41, '昌吉市', 'cj');
INSERT INTO `city` VALUES (42, '重庆市', 'cq');
INSERT INTO `city` VALUES (43, '长沙市', 'cs');
INSERT INTO `city` VALUES (44, '楚雄市', 'cx');
INSERT INTO `city` VALUES (45, '朝阳市', 'cy');
INSERT INTO `city` VALUES (46, '常州市', 'cz');
INSERT INTO `city` VALUES (47, '大理市', 'dali');
INSERT INTO `city` VALUES (48, '达州市', 'dazhou');
INSERT INTO `city` VALUES (49, '丹东市', 'dd');
INSERT INTO `city` VALUES (50, '德阳市', 'deyang');
INSERT INTO `city` VALUES (51, '东莞市', 'dg');
INSERT INTO `city` VALUES (52, '德宏市', 'dh');
INSERT INTO `city` VALUES (53, '迪庆市', 'diqing');
INSERT INTO `city` VALUES (54, '大连市', 'dl');
INSERT INTO `city` VALUES (55, '大庆市', 'dq');
INSERT INTO `city` VALUES (56, '大同市', 'dt');
INSERT INTO `city` VALUES (57, '定西市', 'dx');
INSERT INTO `city` VALUES (58, '大兴安岭市', 'dxal');
INSERT INTO `city` VALUES (59, '东营市', 'dy');
INSERT INTO `city` VALUES (60, '德州市', 'dz');
INSERT INTO `city` VALUES (61, '鄂尔多斯市', 'erds');
INSERT INTO `city` VALUES (62, '恩施市', 'es');
INSERT INTO `city` VALUES (63, '鄂州市', 'ez');
INSERT INTO `city` VALUES (64, '防城港市', 'fcg');
INSERT INTO `city` VALUES (65, '佛山市', 'fs');
INSERT INTO `city` VALUES (66, '抚顺市', 'fushun');
INSERT INTO `city` VALUES (67, '抚州市', 'fuzhou');
INSERT INTO `city` VALUES (68, '阜新市', 'fx');
INSERT INTO `city` VALUES (69, '阜阳市', 'fy');
INSERT INTO `city` VALUES (70, '福州市', 'fz');
INSERT INTO `city` VALUES (71, '广安市', 'ga');
INSERT INTO `city` VALUES (72, '赣州市', 'ganzhou');
INSERT INTO `city` VALUES (73, '甘孜市', 'ganzi');
INSERT INTO `city` VALUES (74, '贵港市', 'gg');
INSERT INTO `city` VALUES (75, '桂林市', 'gl');
INSERT INTO `city` VALUES (76, '广元市', 'guangyuan');
INSERT INTO `city` VALUES (77, '果洛市', 'guoluo');
INSERT INTO `city` VALUES (78, '固原市', 'guyuan');
INSERT INTO `city` VALUES (79, '贵阳市', 'gy');
INSERT INTO `city` VALUES (80, '广州市', 'gz');
INSERT INTO `city` VALUES (81, '淮安市', 'ha');
INSERT INTO `city` VALUES (82, '海北市', 'haibei');
INSERT INTO `city` VALUES (83, '海东市', 'haidong');
INSERT INTO `city` VALUES (84, '海南市', 'hainan');
INSERT INTO `city` VALUES (85, '汉中市', 'hanzhong');
INSERT INTO `city` VALUES (86, '鹤壁市', 'hb');
INSERT INTO `city` VALUES (87, '河池市', 'hc');
INSERT INTO `city` VALUES (88, '邯郸市', 'hd');
INSERT INTO `city` VALUES (89, '哈尔滨市', 'heb');
INSERT INTO `city` VALUES (90, '鹤岗市', 'hegang');
INSERT INTO `city` VALUES (91, '黑河市', 'heihe');
INSERT INTO `city` VALUES (92, '河源市', 'heyuan');
INSERT INTO `city` VALUES (93, '菏泽市', 'heze');
INSERT INTO `city` VALUES (94, '贺州市', 'hezhou');
INSERT INTO `city` VALUES (95, '合肥市', 'hf');
INSERT INTO `city` VALUES (96, '黄冈市', 'hg');
INSERT INTO `city` VALUES (97, '怀化市', 'hh');
INSERT INTO `city` VALUES (98, '呼伦贝尔市', 'hlbe');
INSERT INTO `city` VALUES (99, '葫芦岛市', 'hld');
INSERT INTO `city` VALUES (100, '哈密市', 'hm');
INSERT INTO `city` VALUES (101, '淮南市', 'hn');
INSERT INTO `city` VALUES (102, '红河市', 'honghe');
INSERT INTO `city` VALUES (103, '衡水市', 'hs');
INSERT INTO `city` VALUES (104, '黄石市', 'hshi');
INSERT INTO `city` VALUES (105, '和田市', 'ht');
INSERT INTO `city` VALUES (106, '呼和浩特市', 'hu');
INSERT INTO `city` VALUES (107, '淮北市', 'huaibei');
INSERT INTO `city` VALUES (108, '黄南市', 'huangnan');
INSERT INTO `city` VALUES (109, '黄山市', 'huangshan');
INSERT INTO `city` VALUES (110, '惠州市', 'huizhou');
INSERT INTO `city` VALUES (111, '湖州市', 'huzhou');
INSERT INTO `city` VALUES (112, '海西市', 'hx');
INSERT INTO `city` VALUES (113, '衡阳市', 'hy');
INSERT INTO `city` VALUES (114, '杭州市', 'hz');
INSERT INTO `city` VALUES (115, '吉安市', 'ja');
INSERT INTO `city` VALUES (116, '晋城市', 'jc');
INSERT INTO `city` VALUES (117, '景德镇市', 'jdz');
INSERT INTO `city` VALUES (118, '金华市', 'jh');
INSERT INTO `city` VALUES (119, '焦作市', 'jiaozuo');
INSERT INTO `city` VALUES (120, '金昌市', 'jinchang');
INSERT INTO `city` VALUES (121, '荆门市', 'jingmen');
INSERT INTO `city` VALUES (122, '荆州市', 'jingzhou');
INSERT INTO `city` VALUES (123, '济宁市', 'jining');
INSERT INTO `city` VALUES (124, '锦州市', 'jinzhou');
INSERT INTO `city` VALUES (125, '鸡西市', 'jixi');
INSERT INTO `city` VALUES (126, '济源市', 'jiyuan');
INSERT INTO `city` VALUES (127, '九江市', 'jj');
INSERT INTO `city` VALUES (128, '吉林市', 'jl');
INSERT INTO `city` VALUES (129, '江门市', 'jm');
INSERT INTO `city` VALUES (130, '佳木斯市', 'jms');
INSERT INTO `city` VALUES (131, '济南市', 'jn');
INSERT INTO `city` VALUES (132, '酒泉市', 'jq');
INSERT INTO `city` VALUES (133, '嘉兴市', 'jx');
INSERT INTO `city` VALUES (134, '揭阳市', 'jy');
INSERT INTO `city` VALUES (135, '嘉峪关市', 'jyg');
INSERT INTO `city` VALUES (136, '晋中市', 'jz');
INSERT INTO `city` VALUES (137, '喀什市', 'kashi');
INSERT INTO `city` VALUES (138, '开封市', 'kf');
INSERT INTO `city` VALUES (139, '克拉玛依市', 'klmy');
INSERT INTO `city` VALUES (140, '昆明市', 'km');
INSERT INTO `city` VALUES (141, '克孜勒苏市', 'kzls');
INSERT INTO `city` VALUES (142, '六安市', 'la');
INSERT INTO `city` VALUES (143, '来宾市', 'lb');
INSERT INTO `city` VALUES (144, '聊城市', 'lc');
INSERT INTO `city` VALUES (145, '娄底市', 'ld');
INSERT INTO `city` VALUES (146, '乐山市', 'leshan');
INSERT INTO `city` VALUES (147, '廊坊市', 'lf');
INSERT INTO `city` VALUES (148, '漯河市', 'lh');
INSERT INTO `city` VALUES (149, '凉山市', 'liangshan');
INSERT INTO `city` VALUES (150, '辽阳市', 'liaoyang');
INSERT INTO `city` VALUES (151, '辽源市', 'liaoyuan');
INSERT INTO `city` VALUES (152, '临沧市', 'lincang');
INSERT INTO `city` VALUES (153, '临汾市', 'linfen');
INSERT INTO `city` VALUES (154, '临沂市', 'linyi');
INSERT INTO `city` VALUES (155, '丽水市', 'lishui');
INSERT INTO `city` VALUES (156, '柳州市', 'liuzhou');
INSERT INTO `city` VALUES (157, '丽江市', 'lj');
INSERT INTO `city` VALUES (158, '吕梁市', 'll');
INSERT INTO `city` VALUES (159, '陇南市', 'ln');
INSERT INTO `city` VALUES (160, '龙岩市', 'longyan');
INSERT INTO `city` VALUES (161, '六盘水市', 'lps');
INSERT INTO `city` VALUES (162, '泸州市', 'luzhou');
INSERT INTO `city` VALUES (163, '洛阳市', 'ly');
INSERT INTO `city` VALUES (164, '连云港市', 'lyg');
INSERT INTO `city` VALUES (165, '兰州市', 'lz');
INSERT INTO `city` VALUES (166, '马鞍山市', 'mas');
INSERT INTO `city` VALUES (167, '牡丹江市', 'mdj');
INSERT INTO `city` VALUES (168, '茂名市', 'mm');
INSERT INTO `city` VALUES (169, '眉山市', 'ms');
INSERT INTO `city` VALUES (170, '绵阳市', 'my');
INSERT INTO `city` VALUES (171, '梅州市', 'mz');
INSERT INTO `city` VALUES (172, '南充市', 'nanchong');
INSERT INTO `city` VALUES (173, '宁波市', 'nb');
INSERT INTO `city` VALUES (174, '南昌市', 'nc');
INSERT INTO `city` VALUES (175, '宁德市', 'nd');
INSERT INTO `city` VALUES (176, '内江市', 'neijiang');
INSERT INTO `city` VALUES (177, '南京市', 'nj');
INSERT INTO `city` VALUES (178, '南宁市', 'nn');
INSERT INTO `city` VALUES (179, '南平市', 'np');
INSERT INTO `city` VALUES (180, '南通市', 'nt');
INSERT INTO `city` VALUES (181, '怒江市', 'nujiang');
INSERT INTO `city` VALUES (182, '南阳市', 'ny');
INSERT INTO `city` VALUES (183, '平顶山市', 'pds');
INSERT INTO `city` VALUES (184, '普洱市', 'pe');
INSERT INTO `city` VALUES (185, '盘锦市', 'pj');
INSERT INTO `city` VALUES (186, '平凉市', 'pl');
INSERT INTO `city` VALUES (187, '莆田市', 'pt');
INSERT INTO `city` VALUES (188, '萍乡市', 'px');
INSERT INTO `city` VALUES (189, '濮阳市', 'py');
INSERT INTO `city` VALUES (190, '攀枝花市', 'pzh');
INSERT INTO `city` VALUES (191, '青岛市', 'qd');
INSERT INTO `city` VALUES (192, '黔东南市', 'qdn');
INSERT INTO `city` VALUES (193, '秦皇岛市', 'qhd');
INSERT INTO `city` VALUES (194, '潜江市', 'qianjiang');
INSERT INTO `city` VALUES (195, '庆阳市', 'qingyang');
INSERT INTO `city` VALUES (196, '钦州市', 'qinzhou');
INSERT INTO `city` VALUES (197, '曲靖市', 'qj');
INSERT INTO `city` VALUES (198, '黔南市', 'qn');
INSERT INTO `city` VALUES (199, '齐齐哈尔市', 'qqhr');
INSERT INTO `city` VALUES (200, '七台河市', 'qth');
INSERT INTO `city` VALUES (201, '衢州市', 'quzhou');
INSERT INTO `city` VALUES (202, '黔西南市', 'qxn');
INSERT INTO `city` VALUES (203, '清远市', 'qy');
INSERT INTO `city` VALUES (204, '泉州市', 'qz');
INSERT INTO `city` VALUES (205, '日照市', 'rz');
INSERT INTO `city` VALUES (206, '三亚市', 'sanya');
INSERT INTO `city` VALUES (207, '韶关市', 'sg');
INSERT INTO `city` VALUES (208, '上海市', 'sh');
INSERT INTO `city` VALUES (209, '邵阳市', 'shaoyang');
INSERT INTO `city` VALUES (210, '十堰市', 'shiyan');
INSERT INTO `city` VALUES (211, '朔州市', 'shuozhou');
INSERT INTO `city` VALUES (212, '石家庄市', 'sjz');
INSERT INTO `city` VALUES (213, '商洛市', 'sl');
INSERT INTO `city` VALUES (214, '三明市', 'sm');
INSERT INTO `city` VALUES (215, '三门峡市', 'smx');
INSERT INTO `city` VALUES (216, '神农架市', 'snj');
INSERT INTO `city` VALUES (217, '松原市', 'songyuan');
INSERT INTO `city` VALUES (218, '四平市', 'sp');
INSERT INTO `city` VALUES (219, '商丘市', 'sq');
INSERT INTO `city` VALUES (220, '上饶市', 'sr');
INSERT INTO `city` VALUES (221, '汕头市', 'st');
INSERT INTO `city` VALUES (222, '宿州市', 'su');
INSERT INTO `city` VALUES (223, '绥化市', 'suihua');
INSERT INTO `city` VALUES (224, '遂宁市', 'suining');
INSERT INTO `city` VALUES (225, '随州市', 'suizhou');
INSERT INTO `city` VALUES (226, '宿迁市', 'suqian');
INSERT INTO `city` VALUES (227, '苏州市', 'suzhou');
INSERT INTO `city` VALUES (228, '汕尾市', 'sw');
INSERT INTO `city` VALUES (229, '绍兴市', 'sx');
INSERT INTO `city` VALUES (230, '沈阳市', 'sy');
INSERT INTO `city` VALUES (231, '双鸭山市', 'sys');
INSERT INTO `city` VALUES (232, '深圳市', 'bendibao.com/news/yqdengji/');
INSERT INTO `city` VALUES (233, '石嘴山市', 'szs');
INSERT INTO `city` VALUES (234, '泰安市', 'ta');
INSERT INTO `city` VALUES (235, '塔城市', 'tacheng');
INSERT INTO `city` VALUES (236, '泰州市', 'taizhou');
INSERT INTO `city` VALUES (237, '铜川市', 'tc');
INSERT INTO `city` VALUES (238, '通化市', 'th');
INSERT INTO `city` VALUES (239, '天水市', 'tianshui');
INSERT INTO `city` VALUES (240, '天津市', 'tj');
INSERT INTO `city` VALUES (241, '吐鲁番市', 'tlf');
INSERT INTO `city` VALUES (242, '天门市', 'tm');
INSERT INTO `city` VALUES (243, '通辽市', 'tongliao');
INSERT INTO `city` VALUES (244, '铜陵市', 'tongling');
INSERT INTO `city` VALUES (245, '铜仁市', 'tr');
INSERT INTO `city` VALUES (246, '唐山市', 'ts');
INSERT INTO `city` VALUES (247, '太原市', 'ty');
INSERT INTO `city` VALUES (248, '台州市', 'tz');
INSERT INTO `city` VALUES (249, '威海市', 'weihai');
INSERT INTO `city` VALUES (250, '潍坊市', 'wf');
INSERT INTO `city` VALUES (251, '武汉市', 'wh');
INSERT INTO `city` VALUES (252, '乌兰察布市', 'wlcb');
INSERT INTO `city` VALUES (253, '乌鲁木齐市', 'wlmq');
INSERT INTO `city` VALUES (254, '渭南市', 'wn');
INSERT INTO `city` VALUES (255, '文山市', 'ws');
INSERT INTO `city` VALUES (256, '乌海市', 'wuhai');
INSERT INTO `city` VALUES (257, '芜湖市', 'wuhu');
INSERT INTO `city` VALUES (258, '武威市', 'wuwei');
INSERT INTO `city` VALUES (259, '吴忠市', 'wuzhong');
INSERT INTO `city` VALUES (260, '梧州市', 'wuzhou');
INSERT INTO `city` VALUES (261, '无锡市', 'wx');
INSERT INTO `city` VALUES (262, '温州市', 'wz');
INSERT INTO `city` VALUES (263, '五指山市', 'wzs');
INSERT INTO `city` VALUES (264, '西安市', 'xa');
INSERT INTO `city` VALUES (265, '兴安市', 'xam');
INSERT INTO `city` VALUES (266, '许昌市', 'xc');
INSERT INTO `city` VALUES (267, '襄阳市', 'xf');
INSERT INTO `city` VALUES (268, '孝感市', 'xg');
INSERT INTO `city` VALUES (269, '湘潭市', 'xiangtan');
INSERT INTO `city` VALUES (270, '湘西市', 'xiangxi');
INSERT INTO `city` VALUES (271, '咸宁市', 'xianning');
INSERT INTO `city` VALUES (272, '仙桃市', 'xiantao');
INSERT INTO `city` VALUES (273, '咸阳市', 'xianyang');
INSERT INTO `city` VALUES (274, '新余市', 'xinyu');
INSERT INTO `city` VALUES (275, '忻州市', 'xinzhou');
INSERT INTO `city` VALUES (276, '锡林郭勒市', 'xl');
INSERT INTO `city` VALUES (277, '厦门市', 'xm');
INSERT INTO `city` VALUES (278, '西宁市', 'xn');
INSERT INTO `city` VALUES (279, '邢台市', 'xt');
INSERT INTO `city` VALUES (280, '宣城市', 'xuancheng');
INSERT INTO `city` VALUES (281, '新乡市', 'xx');
INSERT INTO `city` VALUES (282, '信阳市', 'xy');
INSERT INTO `city` VALUES (283, '徐州市', 'xz');
INSERT INTO `city` VALUES (284, '雅安市', 'ya');
INSERT INTO `city` VALUES (285, '延边市', 'yanbian');
INSERT INTO `city` VALUES (286, '盐城市', 'yancheng');
INSERT INTO `city` VALUES (287, '阳泉市', 'yangquan');
INSERT INTO `city` VALUES (288, '宜宾市', 'yb');
INSERT INTO `city` VALUES (289, '银川市', 'yc');
INSERT INTO `city` VALUES (290, '云浮市', 'yf');
INSERT INTO `city` VALUES (291, '伊春市', 'yich');
INSERT INTO `city` VALUES (292, '宜昌市', 'yichang');
INSERT INTO `city` VALUES (293, '宜春市', 'yichun');
INSERT INTO `city` VALUES (294, '伊犁市', 'yili');
INSERT INTO `city` VALUES (295, '鹰潭市', 'yingtan');
INSERT INTO `city` VALUES (296, '益阳市', 'yiyang');
INSERT INTO `city` VALUES (297, '阳江市', 'yj');
INSERT INTO `city` VALUES (298, '营口市', 'yk');
INSERT INTO `city` VALUES (299, '榆林市', 'yl');
INSERT INTO `city` VALUES (300, '延安市', 'yn');
INSERT INTO `city` VALUES (301, '永州市', 'yongzhou');
INSERT INTO `city` VALUES (302, '玉树市', 'ys');
INSERT INTO `city` VALUES (303, '烟台市', 'yt');
INSERT INTO `city` VALUES (304, '玉林市', 'yulin');
INSERT INTO `city` VALUES (305, '运城市', 'yuncheng');
INSERT INTO `city` VALUES (306, '玉溪市', 'yx');
INSERT INTO `city` VALUES (307, '岳阳市', 'yy');
INSERT INTO `city` VALUES (308, '扬州市', 'yz');
INSERT INTO `city` VALUES (309, '枣庄市', 'zaozhuang');
INSERT INTO `city` VALUES (310, '淄博市', 'zb');
INSERT INTO `city` VALUES (311, '自贡市', 'zg');
INSERT INTO `city` VALUES (312, '珠海市', 'zh');
INSERT INTO `city` VALUES (313, '张掖市', 'zhangye');
INSERT INTO `city` VALUES (314, '漳州市', 'zhangzhou');
INSERT INTO `city` VALUES (315, '湛江市', 'zhanjiang');
INSERT INTO `city` VALUES (316, '舟山市', 'zhoushan');
INSERT INTO `city` VALUES (317, '株洲市', 'zhuzhou');
INSERT INTO `city` VALUES (318, '镇江市', 'zj');
INSERT INTO `city` VALUES (319, '张家界市', 'zjj');
INSERT INTO `city` VALUES (320, '张家口市', 'zjk');
INSERT INTO `city` VALUES (321, '周口市', 'zk');
INSERT INTO `city` VALUES (322, '驻马店市', 'zmd');
INSERT INTO `city` VALUES (323, '肇庆市', 'zq');
INSERT INTO `city` VALUES (324, '中山市', 'zs');
INSERT INTO `city` VALUES (325, '昭通市', 'zt');
INSERT INTO `city` VALUES (326, '遵义市', 'zunyi');
INSERT INTO `city` VALUES (327, '中卫市', 'zw');
INSERT INTO `city` VALUES (328, '资阳市', 'zy');
INSERT INTO `city` VALUES (329, '郑州市', 'zz');

DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dept_name`(`dept_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表' ROW_FORMAT=DYNAMIC;

INSERT INTO `dept` VALUES (11, '保安部');
INSERT INTO `dept` VALUES (5, '后勤部');
INSERT INTO `dept` VALUES (4, '市场部');
INSERT INTO `dept` VALUES (3, '技术部');
INSERT INTO `dept` VALUES (1, '管理部');
INSERT INTO `dept` VALUES (2, '行政部');

DROP TABLE IF EXISTS `face_model`;
CREATE TABLE `face_model`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键值',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `face_model` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户人脸模型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `workday`;
CREATE TABLE `workday`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date` date NULL DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_date`(`date`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作日表' ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `holiday`;
CREATE TABLE `holiday`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date` date NOT NULL COMMENT '日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_date`(`date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='节假日表' ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'UUID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会议题目',
  `creator_id` bigint(200) NOT NULL COMMENT '创建人ID',
  `date` date NOT NULL COMMENT '日期',
  `place` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开会地点',
  `start` time(0) NOT NULL COMMENT '开始时间',
  `end` time(0) NOT NULL COMMENT '结束时间',
  `type` smallint(6) NOT NULL COMMENT '会议类型(1线上,2线下)',
  `members` json NOT NULL COMMENT '参与者',
  `desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会议内容',
  `instance_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID',
  `status` smallint(6) NOT NULL COMMENT '状态(1待审批,2审批未通过,3未开始,4进行中,5已结束)',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_creator_id`(`creator_id`) USING BTREE,
  INDEX `idx_date`(`date`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=48 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会议表' ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `module`;
CREATE TABLE `module`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块编号',
  `module_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_module_code`(`module_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='模块表' ROW_FORMAT=DYNAMIC;

INSERT INTO `module` VALUES (1, 'EMPLOYEE', '员工管理');
INSERT INTO `module` VALUES (2, 'DEPT', '部门管理');
INSERT INTO `module` VALUES (3, 'MEETING', '会议管理');
INSERT INTO `module` VALUES (4, 'WORKFLOW', '工作流管理');

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `permission_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限',
  `module_id` bigint(20) UNSIGNED NOT NULL COMMENT '模块ID',
  `action_id` bigint(20) UNSIGNED NOT NULL COMMENT '行为ID',
  `desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_name`(`permission_name`) USING BTREE,
  UNIQUE INDEX `uk_module_id_and_action_id`(`module_id`, `action_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表' ROW_FORMAT=DYNAMIC;

INSERT INTO `permission` VALUES (0, 'ROOT', 0, 0, '超级管理员');
INSERT INTO `permission` VALUES (1, 'EMPLOYEE:INSERT', 1, 1, '员工新增');
INSERT INTO `permission` VALUES (2, 'EMPLOYEE:DELETE', 1, 2, '员工删除');
INSERT INTO `permission` VALUES (3, 'EMPLOYEE:UPDATE', 1, 3, '员工编辑');
INSERT INTO `permission` VALUES (4, 'EMPLOYEE:SELECT', 1, 4, '员工查询');
INSERT INTO `permission` VALUES (5, 'DEPT:INSERT', 2, 1, '部门新增');
INSERT INTO `permission` VALUES (6, 'DEPT:DELETE', 2, 2, '部门删除');
INSERT INTO `permission` VALUES (7, 'DEPT:UPDATE', 2, 3, '部门编辑');
INSERT INTO `permission` VALUES (8, 'DEPT:SELECT', 2, 4, '部门查询');
INSERT INTO `permission` VALUES (9, 'MEETING:INSERT', 3, 1, '会议新增');
INSERT INTO `permission` VALUES (10, 'MEETING:DELETE', 3, 2, '会议删除');
INSERT INTO `permission` VALUES (11, 'MEETING:UPDATE', 3, 3, '会议编辑');
INSERT INTO `permission` VALUES (12, 'MEETING:SELECT', 3, 4, '会议查询');
INSERT INTO `permission` VALUES (13, 'WORKFLOW:APPROVAL', 4, 5, '工作流审批');

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `permissions` json NOT NULL COMMENT '权限集合',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表' ROW_FORMAT=DYNAMIC;

INSERT INTO `role` VALUES (0, '超级管理员', '[0]');
INSERT INTO `role` VALUES (1, '总经理', '[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]');
INSERT INTO `role` VALUES (2, '部门经理', '[1, 3, 4, 5, 7, 8, 9, 11, 12, 13]');
INSERT INTO `role` VALUES (3, '普通员工', '[4, 8, 9, 12]');

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `open_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户唯一标识',
  `nickname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` enum('男','女') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `tel` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `hiredate` date NULL DEFAULT NULL COMMENT '入职日期',
  `roles` json NOT NULL COMMENT '角色集合',
  `root` tinyint(1) NOT NULL COMMENT '是否是超级管理员',
  `dept_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '部门编号',
  `status` tinyint(4) NOT NULL COMMENT '状态(0离职,1在职)',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_open_id`(`open_id`) USING BTREE,
  INDEX `uk_email`(`email`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工表' ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS=1;