# -*- coding: utf-8 -*-
"""
商品图片导入工具
================
功能：将本地商品图片按分类匹配到数据库商品，生成 SQL 更新脚本

使用方法：
1. 将此脚本放到项目根目录（与 uploads/ 同级）
2. 准备图片文件夹，按分类名称组织，例如：
   images/
   ├── 数码家电/
   │   ├── iphone15.jpg
   │   ├── mate60.png
   │   └── ...
   ├── 服饰鞋包/
   │   ├── 连衣裙.jpg
   │   └── ...
   └── ...
3. 运行：python import_product_images.py --images-dir ./images
4. 生成的文件：
   - uploads/ 目录下的图片文件（可直接部署到服务器）
   - update_product_images.sql（在服务器数据库中执行）

分类名称映射（文件夹名 -> 数据库分类）：
  服饰鞋包、美妆个护、数码家电、家居生活、母婴玩具、食品生鲜
  运动户外、图书办公、汽车用品、珠宝配饰、医药健康、虚拟服务
"""

import os
import sys
import shutil
import uuid
import argparse
from datetime import datetime
from pathlib import Path

# ============================================================
# 分类名称 -> category_id 映射（一级分类）
# ============================================================
CATEGORY_MAP = {
    "服饰鞋包": {"id": 1, "subcategories": {"女装": 13, "男装": 14, "童装": 15, "内衣": 16, "鞋靴": 17, "箱包皮具": 18}},
    "美妆个护": {"id": 2, "subcategories": {"护肤": 19, "彩妆": 20, "香水": 21, "美发护发": 22, "身体护理": 23, "口腔护理": 24, "美容仪": 25}},
    "数码家电": {"id": 3, "subcategories": {"手机": 26, "电脑": 27, "平板": 28, "智能设备": 29, "摄影摄像": 30, "家用电器": 31}},
    "家居生活": {"id": 4, "subcategories": {"家纺": 32, "厨具": 33, "收纳整理": 34, "清洁用品": 35, "灯具照明": 36}},
    "母婴玩具": {"id": 5, "subcategories": {"益智玩具": 37, "积木拼图": 38, "娃娃公仔": 39, "模型手办": 40, "奶粉辅食": 41, "纸尿裤": 42}},
    "食品生鲜": {"id": 6, "subcategories": {"休闲零食": 43, "生鲜水果": 44, "茶饮冲调": 45, "粮油调味": 46}},
    "运动户外": {"id": 7, "subcategories": {"运动鞋服": 47, "健身器材": 48, "户外装备": 49, "骑行运动": 50}},
    "图书办公": {"id": 8, "subcategories": {"教材教辅": 51, "办公文具": 52, "办公设备": 53}},
    "汽车用品": {"id": 9, "subcategories": {"车饰": 54, "车载电器": 55, "养护用品": 56}},
    "珠宝配饰": {"id": 10, "subcategories": {"黄金珠宝": 57, "时尚饰品": 58, "眼镜": 59}},
    "医药健康": {"id": 11, "subcategories": {"中西药品": 60, "保健品": 61, "医疗器械": 62}},
    "虚拟服务": {"id": 12, "subcategories": {"充值缴费": 63, "生活服务": 64}},
}

# 数据库中的现有商品（来自 init.sql）
# 格式：(id, name, category_id, category_name)
EXISTING_PRODUCTS = [
    (1, 'iPhone 15 Pro 256GB 深空黑', 26, '手机'),
    (2, '华为Mate60 Pro 旗舰手机', 26, '手机'),
    (3, 'MacBook Pro 14英寸 M3芯片', 27, '电脑'),
    (4, 'ThinkPad X1 Carbon 轻薄商务本', 27, '电脑'),
    (5, '法式复古碎花连衣裙', 13, '女装'),
    (6, '韩版简约纯色T恤', 13, '女装'),
    (7, '男士商务休闲夹克', 14, '男装'),
    (8, '运动休闲跑步鞋', 17, '鞋靴'),
    (9, 'SK-II护肤精华套装', 19, '护肤'),
    (10, '迪奥烈艳蓝金唇膏', 20, '彩妆'),
    (11, '全棉四件套简约套件', 32, '家纺'),
    (12, '不锈钢炒锅不粘锅', 33, '厨具'),
    (13, '乐高城市系列积木玩具', 37, '益智玩具'),
]

# 支持的图片格式
SUPPORTED_EXTENSIONS = {'.jpg', '.jpeg', '.png', '.gif', '.webp'}


def find_category_for_product(product):
    """根据商品的 category_id 找到对应的一级分类名称"""
    for cat_name, cat_info in CATEGORY_MAP.items():
        for sub_name, sub_id in cat_info["subcategories"].items():
            if sub_id == product[2]:
                return cat_name, sub_name
    return None, None


def get_image_files(directory):
    """获取目录下所有支持的图片文件"""
    files = []
    if not os.path.isdir(directory):
        return files
    for f in os.listdir(directory):
        ext = os.path.splitext(f)[1].lower()
        if ext in SUPPORTED_EXTENSIONS:
            files.append(f)
    return sorted(files)


def generate_new_filename(original_name):
    """生成新的文件名：UUID + 原始扩展名"""
    ext = os.path.splitext(original_name)[1].lower()
    return str(uuid.uuid4()) + ext


def import_images(images_dir, output_dir, project_root):
    """
    主导入逻辑
    :param images_dir: 图片集根目录（包含分类子文件夹）
    :param output_dir: 输出目录（SQL文件输出位置）
    :param project_root: 项目根目录（uploads/ 所在位置）
    """
    uploads_dir = os.path.join(project_root, "uploads")
    date_str = datetime.now().strftime("%Y/%m/%d")
    upload_date_dir = os.path.join(uploads_dir, date_str.replace("/", os.sep))

    # 确保目录存在
    os.makedirs(upload_date_dir, exist_ok=True)

    # 收集所有分类文件夹
    category_dirs = {}
    for item in os.listdir(images_dir):
        item_path = os.path.join(images_dir, item)
        if os.path.isdir(item_path) and item in CATEGORY_MAP:
            category_dirs[item] = item_path

    if not category_dirs:
        print("❌ 错误：在图片目录中未找到匹配的分类文件夹！")
        print(f"   支持的分类名称：{', '.join(CATEGORY_MAP.keys())}")
        print(f"   图片目录：{images_dir}")
        return

    print(f"📁 找到 {len(category_dirs)} 个分类文件夹：{', '.join(category_dirs.keys())}")
    print()

    # 按分类组织商品
    products_by_category = {}
    for product in EXISTING_PRODUCTS:
        top_cat, sub_cat = find_category_for_product(product)
        if top_cat:
            if top_cat not in products_by_category:
                products_by_category[top_cat] = []
            products_by_category[top_cat].append(product)

    # 匹配图片和商品
    results = []  # [(product_id, product_name, image_url, original_file)]
    unmatched_products = list(EXISTING_PRODUCTS)
    used_images = set()

    # 策略1：按文件名关键词匹配商品
    for cat_name, cat_path in category_dirs.items():
        if cat_name not in products_by_category:
            continue
        products = products_by_category[cat_name]
        images = get_image_files(cat_path)

        for product in products:
            if product not in unmatched_products:
                continue
            best_match = None
            best_score = 0
            for img in images:
                if img in used_images:
                    continue
                # 计算文件名和商品名的匹配度
                score = calculate_match_score(product[1], os.path.splitext(img)[0])
                if score > best_score:
                    best_score = score
                    best_match = img

            if best_match and best_score >= 0.3:
                new_filename = generate_new_filename(best_match)
                image_url = f"/api/uploads/{date_str}/{new_filename}"
                src_path = os.path.join(cat_path, best_match)
                dst_path = os.path.join(upload_date_dir, new_filename)
                shutil.copy2(src_path, dst_path)
                results.append((product[0], product[1], image_url, best_match))
                used_images.add(best_match)
                unmatched_products.remove(product)
                print(f"  ✅ 商品 [{product[1]}] -> 图片 [{best_match}]")

    # 策略2：对未匹配的商品，按分类随机分配剩余图片
    for cat_name, cat_path in category_dirs.items():
        if cat_name not in products_by_category:
            continue
        products = products_by_category[cat_name]
        images = get_image_files(cat_path)
        remaining_images = [img for img in images if img not in used_images]

        for product in list(unmatched_products):
            top_cat, _ = find_category_for_product(product)
            if top_cat != cat_name or not remaining_images:
                continue
            img = remaining_images.pop(0)
            new_filename = generate_new_filename(img)
            image_url = f"/api/uploads/{date_str}/{new_filename}"
            src_path = os.path.join(cat_path, img)
            dst_path = os.path.join(upload_date_dir, new_filename)
            shutil.copy2(src_path, dst_path)
            results.append((product[0], product[1], image_url, img))
            used_images.add(img)
            unmatched_products.remove(product)
            print(f"  🔄 商品 [{product[1]}] -> 图片 [{img}] (自动分配)")

    # 策略3：跨分类分配剩余图片
    if unmatched_products:
        all_remaining_images = []
        for cat_name, cat_path in category_dirs.items():
            images = get_image_files(cat_path)
            for img in images:
                if img not in used_images:
                    all_remaining_images.append((cat_path, img))

        for product in list(unmatched_products):
            if not all_remaining_images:
                break
            cat_path, img = all_remaining_images.pop(0)
            new_filename = generate_new_filename(img)
            image_url = f"/api/uploads/{date_str}/{new_filename}"
            src_path = os.path.join(cat_path, img)
            dst_path = os.path.join(upload_date_dir, new_filename)
            shutil.copy2(src_path, dst_path)
            results.append((product[0], product[1], image_url, img))
            used_images.add(img)
            unmatched_products.remove(product)
            print(f"  ⚡ 商品 [{product[1]}] -> 图片 [{img}] (跨分类分配)")

    print()
    print(f"📊 匹配结果：{len(results)}/{len(EXISTING_PRODUCTS)} 个商品已分配图片")
    if unmatched_products:
        print(f"⚠️  以下 {len(unmatched_products)} 个商品未匹配到图片：")
        for p in unmatched_products:
            print(f"   - {p[1]} (分类: {p[3]})")

    # 统计未使用的图片
    all_images_count = sum(len(get_image_files(cp)) for cp in category_dirs.values())
    unused_count = all_images_count - len(used_images)
    if unused_count > 0:
        print(f"📷 剩余 {unused_count} 张图片未使用（可作为后续新增商品的素材）")

    # 生成 SQL
    if results:
        sql_path = os.path.join(output_dir, "update_product_images.sql")
        generate_sql(results, sql_path)
        print(f"\n📄 SQL 文件已生成：{sql_path}")
        print(f"📁 图片已复制到：{upload_date_dir}")
        print()
        print("=" * 60)
        print("📋 部署步骤：")
        print("  1. 将 uploads/ 目录上传到服务器后端 jar 包同级目录")
        print("  2. 在服务器数据库中执行 update_product_images.sql")
        print("  3. 重启后端服务（如果正在运行）")
        print("=" * 60)


def calculate_match_score(product_name, image_name):
    """
    计算商品名和图片文件名的匹配分数
    返回 0~1 之间的分数
    """
    # 关键词映射（商品名中的关键词 -> 可能的图片名关键词）
    keyword_aliases = {
        'iphone': ['iphone', 'apple', '苹果'],
        'huawei': ['huawei', 'mate', '华为'],
        'mate': ['huawei', 'mate', '华为'],
        'macbook': ['macbook', 'mac', 'apple'],
        'thinkpad': ['thinkpad', 'lenovo', '联想'],
        '连衣裙': ['连衣裙', 'dress', '裙'],
        't恤': ['t恤', 'tshirt', 'tee', '纯色'],
        '夹克': ['夹克', 'jacket', '外套'],
        '跑步鞋': ['跑步鞋', '运动鞋', 'shoes', '跑鞋'],
        '运动鞋': ['运动鞋', '跑步鞋', 'shoes', 'sneaker'],
        'sk-ii': ['skii', 'sk-ii', '护肤', '精华'],
        '护肤': ['护肤', 'skincare', '精华', '面膜'],
        '唇膏': ['唇膏', '口红', 'lipstick', 'dior'],
        '口红': ['口红', '唇膏', 'lipstick'],
        '四件套': ['四件套', '床品', 'bedding'],
        '炒锅': ['炒锅', '锅', 'pan', 'cook'],
        '乐高': ['乐高', 'lego', '积木'],
        '积木': ['积木', '乐高', 'lego', 'building'],
    }

    product_lower = product_name.lower()
    image_lower = image_name.lower()

    # 去掉扩展名后的图片名
    score = 0

    # 直接包含检查
    for keyword, aliases in keyword_aliases.items():
        if keyword in product_lower:
            for alias in aliases:
                if alias in image_lower:
                    score = max(score, 0.8)
                    break

    # 图片名包含商品名中的某个词
    product_words = set(product_lower.replace('-', ' ').replace('_', ' ').split())
    image_words = set(image_lower.replace('-', ' ').replace('_', ' ').replace('.', ' ').split())
    common_words = product_words & image_words
    if common_words:
        # 过滤掉太短的词
        meaningful_common = [w for w in common_words if len(w) > 1]
        if meaningful_common:
            score = max(score, 0.5)

    return score


def generate_sql(results, sql_path):
    """生成 SQL 更新脚本"""
    with open(sql_path, 'w', encoding='utf-8') as f:
        f.write("-- ============================================\n")
        f.write("-- 商品图片更新脚本（自动生成）\n")
        f.write(f"-- 生成时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write(f"-- 更新数量：{len(results)} 个商品\n")
        f.write("-- ============================================\n\n")

        # Step 1: 更新 product 表的 cover_img
        f.write("-- Step 1: 更新 product 表的封面图片\n")
        f.write("-- ============================================\n")
        for product_id, product_name, image_url, original_file in results:
            f.write(f"UPDATE `product` SET `cover_img` = '{image_url}' WHERE `id` = {product_id}; -- {product_name}\n")
        f.write("\n")

        # Step 2: 删除旧的 product_image 记录
        f.write("-- Step 2: 删除旧的占位符图片记录\n")
        f.write("-- ============================================\n")
        product_ids = [r[0] for r in results]
        f.write(f"DELETE FROM `product_image` WHERE `product_id` IN ({', '.join(map(str, product_ids))});\n\n")

        # Step 3: 插入新的 product_image 记录
        f.write("-- Step 3: 插入新的商品图片记录\n")
        f.write("-- ============================================\n")
        for product_id, product_name, image_url, original_file in results:
            f.write(f"INSERT INTO `product_image` (`product_id`, `image_url`, `sort_order`, `is_main`) VALUES ({product_id}, '{image_url}', 0, 1); -- {product_name} <- {original_file}\n")
        f.write("\n")

        f.write("-- 执行完成！\n")
        f.write("SELECT CONCAT('已更新 ', COUNT(*), ' 个商品的图片') AS result FROM `product` WHERE `cover_img` LIKE '/api/uploads/%';\n")


def main():
    parser = argparse.ArgumentParser(description='商品图片导入工具 - 将本地图片按分类匹配到数据库商品')
    parser.add_argument('--images-dir', type=str, required=True,
                        help='图片集根目录路径（包含分类子文件夹）')
    parser.add_argument('--output-dir', type=str, default=None,
                        help='SQL 文件输出目录（默认与脚本同目录）')
    parser.add_argument('--project-root', type=str, default=None,
                        help='项目根目录路径（默认为脚本所在目录的上级）')

    args = parser.parse_args()

    images_dir = os.path.abspath(args.images_dir)
    output_dir = os.path.abspath(args.output_dir) if args.output_dir else os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.abspath(args.project_root) if args.project_root else os.path.dirname(os.path.abspath(__file__))

    if not os.path.isdir(images_dir):
        print(f"❌ 错误：图片目录不存在：{images_dir}")
        sys.exit(1)

    print("=" * 60)
    print("🛒 商品图片导入工具")
    print("=" * 60)
    print(f"📁 图片目录：{images_dir}")
    print(f"📁 项目根目录：{project_root}")
    print(f"📄 SQL输出目录：{output_dir}")
    print()

    import_images(images_dir, output_dir, project_root)


if __name__ == '__main__':
    main()
