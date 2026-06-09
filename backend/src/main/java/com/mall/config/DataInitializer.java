package com.mall.config;

import com.mall.module.user.entity.MemberType;
import com.mall.module.user.mapper.MemberTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MemberTypeMapper memberTypeMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        createPointsRecordTable();
        createUserRoleTable();
        migrateCartTable();
        migrateOrderItemTable();
        migrateOrderTable();

        try {
            List<MemberType> existingTypes = memberTypeMapper.selectAll();
            
            if (existingTypes == null || existingTypes.isEmpty()) {
                insertMemberTypes();
            }
        } catch (Exception e) {
            System.out.println("检测到 member_type 表不存在，正在创建...");
            createMemberTypeTable();
            insertMemberTypes();
        }
    }

    private void createPointsRecordTable() {
        String sql = "CREATE TABLE IF NOT EXISTS points_record (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "points INT NOT NULL, " +
                "type VARCHAR(20) NOT NULL COMMENT 'ADD-增加, CONSUME-消费', " +
                "description VARCHAR(200), " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        jdbcTemplate.execute(sql);
        System.out.println("points_record 表创建成功！");
    }

    private void createUserRoleTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_role (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "role_code VARCHAR(20) NOT NULL COMMENT '角色代码', " +
                "role_name VARCHAR(50) NOT NULL COMMENT '角色名称', " +
                "permissions TEXT COMMENT '权限列表', " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE, " +
                "UNIQUE KEY uk_user_id (user_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        jdbcTemplate.execute(sql);
        System.out.println("user_role 表创建成功！");
    }

    private void migrateCartTable() {
        try {
            // 检查 cart 表是否有 sku_id 字段
            String checkSkuIdSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'cart' AND column_name = 'sku_id'";
            Integer skuIdCount = jdbcTemplate.queryForObject(checkSkuIdSql, Integer.class);

            if (skuIdCount == null || skuIdCount == 0) {
                System.out.println("正在迁移 cart 表，添加 sku_id 字段...");
                jdbcTemplate.execute("ALTER TABLE cart ADD COLUMN sku_id INT DEFAULT NULL COMMENT 'SKU规格ID' AFTER product_id");
            }

            // 检查 cart 表是否有 specs 字段
            String checkSpecsSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'cart' AND column_name = 'specs'";
            Integer specsCount = jdbcTemplate.queryForObject(checkSpecsSql, Integer.class);

            if (specsCount == null || specsCount == 0) {
                System.out.println("正在迁移 cart 表，添加 specs 字段...");
                jdbcTemplate.execute("ALTER TABLE cart ADD COLUMN specs VARCHAR(200) DEFAULT NULL COMMENT '规格描述' AFTER sku_id");
            }

            // 检查并更新唯一键
            String checkIndexSql = "SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'cart' AND index_name = 'uk_user_product_sku'";
            Integer indexCount = jdbcTemplate.queryForObject(checkIndexSql, Integer.class);

            if (indexCount == null || indexCount == 0) {
                // 尝试删除旧的唯一键
                try {
                    jdbcTemplate.execute("ALTER TABLE cart DROP INDEX uk_user_product");
                } catch (Exception e) {
                    // 忽略错误，可能不存在
                }

                // 添加新的唯一键
                System.out.println("正在迁移 cart 表，更新唯一键...");
                jdbcTemplate.execute("ALTER TABLE cart ADD UNIQUE KEY uk_user_product_sku (user_id, product_id, sku_id)");
            }

            System.out.println("cart 表检查完成！");
        } catch (Exception e) {
            System.out.println("cart 表迁移失败: " + e.getMessage());
        }
    }

    private void migrateOrderItemTable() {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'specs'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE order_item ADD COLUMN specs VARCHAR(200) DEFAULT NULL COMMENT '规格描述'");
                System.out.println("order_item 表添加 specs 字段完成！");
            }
        } catch (Exception e) {
            System.out.println("order_item 表迁移失败: " + e.getMessage());
        }
    }

    private void migrateOrderTable() {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'order' AND column_name = 'group_order_no'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE `order` ADD COLUMN group_order_no VARCHAR(50) DEFAULT NULL COMMENT '同次购买关联号'");
                System.out.println("order 表添加 group_order_no 字段完成！");
            }
        } catch (Exception e) {
            System.out.println("order 表迁移失败: " + e.getMessage());
        }
    }

    private void createMemberTypeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS member_type (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "level_name VARCHAR(50) NOT NULL, " +
                "level_code VARCHAR(20) NOT NULL UNIQUE, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "duration_days INT NOT NULL, " +
                "points_bonus INT DEFAULT 0, " +
                "privileges TEXT, " +
                "description VARCHAR(200), " +
                "sort_order INT DEFAULT 0, " +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        jdbcTemplate.execute(sql);
        System.out.println("member_type 表创建成功！");
    }

    private void insertMemberTypes() {
        MemberType silver = new MemberType();
        silver.setLevelName("银卡会员");
        silver.setLevelCode("SILVER");
        silver.setPrice(new BigDecimal("29.90"));
        silver.setDurationDays(30);
        silver.setPointsBonus(100);
        silver.setPrivileges("95折优惠,双倍积分");
        silver.setDescription("适合偶尔购物的用户");
        silver.setSortOrder(1);
        memberTypeMapper.insert(silver);

        MemberType gold = new MemberType();
        gold.setLevelName("金卡会员");
        gold.setLevelCode("GOLD");
        gold.setPrice(new BigDecimal("89.90"));
        gold.setDurationDays(90);
        gold.setPointsBonus(500);
        gold.setPrivileges("9折优惠,双倍积分,生日礼包");
        gold.setDescription("最受欢迎的会员选择");
        gold.setSortOrder(2);
        memberTypeMapper.insert(gold);

        MemberType diamond = new MemberType();
        diamond.setLevelName("钻石会员");
        diamond.setLevelCode("DIAMOND");
        diamond.setPrice(new BigDecimal("199.00"));
        diamond.setDurationDays(180);
        diamond.setPointsBonus(1500);
        diamond.setPrivileges("85折优惠,三倍积分,生日礼包,优先发货");
        diamond.setDescription("尊享钻石特权");
        diamond.setSortOrder(3);
        memberTypeMapper.insert(diamond);

        MemberType platinum = new MemberType();
        platinum.setLevelName("铂金会员");
        platinum.setLevelCode("PLATINUM");
        platinum.setPrice(new BigDecimal("399.00"));
        platinum.setDurationDays(365);
        platinum.setPointsBonus(5000);
        platinum.setPrivileges("8折优惠,四倍积分,专属客服,优先发货,专属活动");
        platinum.setDescription("顶级会员尊享");
        platinum.setSortOrder(4);
        memberTypeMapper.insert(platinum);

        System.out.println("会员类型数据初始化完成！");
    }
}
