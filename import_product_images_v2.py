# -*- coding: utf-8 -*-
"""
商品图片批量导入工具 v2
========================
支持结构：大类文件夹 / 小类文件夹 / 图片文件

使用方法：
1. 准备图片文件夹，结构如下：
   电商产品图片/
   ├── 01_服饰鞋包/
   │   ├── 男装/
   │   │   ├── 夹克_01.jpg      ← 商品"男士商务休闲夹克"的第1张图
   │   │   ├── 夹克_02.jpg      ← 第2张图
   │   │   └── 夹克_03.jpg      ← 第3张图
   │   ├── 女装/
   │   │   ├── 连衣裙_01.jpg
   │   │   ├── 连衣裙_02.jpg
   │   │   └── 连衣裙_03.jpg
   │   └── ...
   └── ...

2. 运行：python import_product_images_v2.py --images-dir "电商产品图片"
3. 生成的文件：
   - uploads/ 目录下的图片文件
   - update_product_images.sql（在服务器数据库中执行）
"""

import os
import sys
import shutil
import uuid
import argparse
from datetime import datetime
from collections import defaultdict

# ============================================================
# 分类名称映射（支持多种命名格式）
# ============================================================
CATEGORY_MAP = {
    # 服饰鞋包
    "服饰鞋包": {"id": 1, "subs": {"女装": 13, "男装": 14, "童装": 15, "内衣": 16, "鞋靴": 17, "箱包皮具": 18}},
    # 美妆个护
    "美妆个护": {"id": 2, "subs": {"护肤": 19, "彩妆": 20, "香水": 21, "美发护发": 22, "身体护理": 23, "口腔护理": 24, "美容仪": 25}},
    # 数码家电
    "数码家电": {"id": 3, "subs": {"手机": 26, "电脑": 27, "平板": 28, "智能设备": 29, "摄影摄像": 30, "家用电器": 31}},
    # 家居生活
    "家居生活": {"id": 4, "subs": {"家纺": 32, "厨具": 33, "收纳整理": 34, "清洁用品": 35, "灯具照明": 36}},
    # 母婴玩具
    "母婴玩具": {"id": 5, "subs": {"益智玩具": 37, "积木拼图": 38, "娃娃公仔": 39, "模型手办": 40, "奶粉辅食": 41, "纸尿裤": 42}},
    # 食品生鲜
    "食品生鲜": {"id": 6, "subs": {"休闲零食": 43, "生鲜水果": 44, "茶饮冲调": 45, "粮油调味": 46}},
    # 运动户外
    "运动户外": {"id": 7, "subs": {"运动鞋服": 47, "健身器材": 48, "户外装备": 49, "骑行运动": 50}},
    # 图书办公
    "图书办公": {"id": 8, "subs": {"教材教辅": 51, "办公文具": 52, "办公设备": 53}},
    # 汽车用品
    "汽车用品": {"id": 9, "subs": {"车饰": 54, "车载电器": 55, "养护用品": 56}},
    # 珠宝配饰
    "珠宝配饰": {"id": 10, "subs": {"黄金珠宝": 57, "时尚饰品": 58, "眼镜": 59}},
    # 医药健康
    "医药健康": {"id": 11, "subs": {"中西药品": 60, "保健品": 61, "医疗器械": 62}},
    # 虚拟服务
    "虚拟服务": {"id": 12, "subs": {"充值缴费": 63, "生活服务": 64}},
}

# 数据库中的现有商品
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

SUPPORTED_EXTENSIONS = {'.jpg', '.jpeg', '.png', '.gif', '.webp'}


def normalize_name(name):
    """标准化名称：去掉前缀编号、下划线等"""
    # 去掉 "01_" 这样的前缀
    if '_' in name and name.split('_')[0].isdigit():
        name = '_'.join(name.split('_')[1:])
    return name.strip()


def find_category_by_folder_name(folder_name):
    """根据文件夹名找到对应的一级分类"""
    normalized = normalize_name(folder_name)
    for cat_name in CATEGORY_MAP:
        if cat_name in normalized or normalized in cat_name:
            return cat_name
    return None


def find_subcategory_by_folder_name(sub_folder_name, top_cat):
    """根据小类文件夹名找到对应的二级分类ID"""
    if not top_cat or top_cat not in CATEGORY_MAP:
        return None
    subs = CATEGORY_MAP[top_cat]["subs"]
    normalized = sub_folder_name.strip()
    for sub_name, sub_id in subs.items():
        if sub_name in normalized or normalized in sub_name:
            return sub_id
    return None


def get_all_images(images_dir):
    """
    扫描所有图片，返回结构：
    {
      "大类名/小类名": ["图片路径1", "图片路径2", ...],
      ...
    }
    """
    result = defaultdict(list)

    for top_item in os.listdir(images_dir):
        top_path = os.path.join(images_dir, top_item)
        if not os.path.isdir(top_path):
            continue

        top_cat = find_category_by_folder_name(top_item)
        if not top_cat:
            print(f"  ⚠️  无法识别大类文件夹：{top_item}，跳过")
            continue

        for sub_item in os.listdir(top_path):
            sub_path = os.path.join(top_path, sub_item)
            if not os.path.isdir(sub_path):
                continue

            sub_cat_id = find_subcategory_by_folder_name(sub_item, top_cat)
            if not sub_cat_id:
                print(f"  ⚠️  无法识别小类文件夹：{top_item}/{sub_item}，跳过")
                continue

            key = f"{top_cat}/{sub_item}"
            for file in os.listdir(sub_path):
                ext = os.path.splitext(file)[1].lower()
                if ext in SUPPORTED_EXTENSIONS:
                    result[key].append(os.path.join(sub_path, file))

            # 按文件名排序
            result[key].sort()

    return dict(result)


def match_products_to_images(images_by_category):
    """
    将商品与图片匹配
    返回：[(product_id, product_name, [image_url1, image_url2, ...], category_key)]
    """
    matches = []

    for product in EXISTING_PRODUCTS:
        product_id, product_name, category_id, category_name = product

        # 找到该商品所属的分类key
        target_key = None
        for key, cat_id in [(k, find_subcategory_by_folder_name(k.split('/')[1], k.split('/')[0]))
                            for k in images_by_category.keys()]:
            if cat_id == category_id:
                target_key = key
                break

        if not target_key:
            print(f"  ⚠️  商品 [{product_name}] 未找到对应分类的图片")
            continue

        images = images_by_category[target_key]
        if not images:
            print(f"  ⚠️  分类 [{target_key}] 没有图片")
            continue

        # 尝试按文件名匹配该商品的图片
        product_images = []
        product_keywords = extract_keywords(product_name)

        for img_path in images:
            img_name = os.path.splitext(os.path.basename(img_path))[0].lower()
            if any(kw in img_name for kw in product_keywords):
                product_images.append(img_path)

        # 如果没匹配到，按顺序分配3张
        if not product_images:
            # 找到该分类下已分配的图片数量
            used_count = sum(1 for m in matches if m[3] == target_key) * 3
            available = images[used_count:used_count + 3]
            product_images = available
            if product_images:
                print(f"  🔄 商品 [{product_name}] 按顺序分配 {len(product_images)} 张图片")
        else:
            print(f"  ✅ 商品 [{product_name}] 匹配到 {len(product_images)} 张图片")

        if product_images:
            matches.append((product_id, product_name, product_images, target_key))

    return matches


def extract_keywords(product_name):
    """从商品名中提取关键词用于匹配"""
    name = product_name.lower()
    # 常见关键词
    keywords = []

    # 品牌/型号关键词
    brand_keywords = {
        'iphone': ['iphone'],
        '华为': ['华为', 'huawei', 'mate'],
        'mate': ['mate', '华为'],
        'macbook': ['macbook', 'mac'],
        'thinkpad': ['thinkpad'],
        'sk-ii': ['skii', 'sk-ii', 'sk2'],
        '迪奥': ['迪奥', 'dior'],
        '乐高': ['乐高', 'lego'],
    }

    for brand, kws in brand_keywords.items():
        if brand in name:
            keywords.extend(kws)

    # 通用商品关键词
    general_keywords = {
        '连衣裙': ['连衣裙', '裙', 'dress'],
        't恤': ['t恤', 'tshirt', 'tee'],
        '夹克': ['夹克', 'jacket'],
        '跑步鞋': ['跑步鞋', '运动鞋', '跑鞋', '鞋'],
        '运动鞋': ['运动鞋', '跑步鞋', '鞋'],
        '护肤': ['护肤', '精华'],
        '唇膏': ['唇膏', '口红'],
        '口红': ['口红', '唇膏'],
        '四件套': ['四件套', '床品'],
        '炒锅': ['炒锅', '锅'],
        '积木': ['积木', 'lego'],
    }

    for item, kws in general_keywords.items():
        if item in name:
            keywords.extend(kws)

    # 如果没有特定关键词，提取所有中文字符和英文单词
    if not keywords:
        import re
        # 提取中文字符
        chinese = re.findall(r'[\u4e00-\u9fff]+', name)
        keywords.extend(chinese)
        # 提取英文单词
        english = re.findall(r'[a-zA-Z]+', name)
        keywords.extend([e.lower() for e in english])

    return list(set(keywords))


def copy_images_and_generate_sql(matches, project_root):
    """复制图片并生成SQL"""
    uploads_dir = os.path.join(project_root, "uploads")
    date_str = datetime.now().strftime("%Y/%m/%d")
    upload_date_dir = os.path.join(uploads_dir, *date_str.split('/'))
    os.makedirs(upload_date_dir, exist_ok=True)

    sql_updates = []
    sql_inserts = []

    for product_id, product_name, image_paths, category_key in matches:
        image_urls = []
        for i, img_path in enumerate(image_paths):
            ext = os.path.splitext(img_path)[1].lower()
            new_name = f"{uuid.uuid4()}{ext}"
            dst_path = os.path.join(upload_date_dir, new_name)
            shutil.copy2(img_path, dst_path)
            url = f"/api/uploads/{date_str}/{new_name}"
            image_urls.append(url)

        # 第一张作为封面
        cover_url = image_urls[0]
        sql_updates.append(f"UPDATE `product` SET `cover_img` = '{cover_url}' WHERE `id` = {product_id}; -- {product_name}")

        # 所有图片插入 product_image
        for i, url in enumerate(image_urls):
            sql_inserts.append(
                f"INSERT INTO `product_image` (`product_id`, `image_url`, `sort_order`, `is_main`) "
                f"VALUES ({product_id}, '{url}', {i}, {1 if i == 0 else 0}); -- {product_name} 图{i+1}"
            )

    return sql_updates, sql_inserts, upload_date_dir


def main():
    parser = argparse.ArgumentParser(description='商品图片批量导入工具 v2')
    parser.add_argument('--images-dir', type=str, required=True, help='图片根目录路径')
    parser.add_argument('--output-dir', type=str, default=None, help='SQL输出目录')
    parser.add_argument('--project-root', type=str, default=None, help='项目根目录')

    args = parser.parse_args()

    images_dir = os.path.abspath(args.images_dir)
    output_dir = os.path.abspath(args.output_dir) if args.output_dir else os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.abspath(args.project_root) if args.project_root else os.path.dirname(os.path.abspath(__file__))

    if not os.path.isdir(images_dir):
        print(f"❌ 错误：图片目录不存在：{images_dir}")
        sys.exit(1)

    print("=" * 70)
    print("🛒 商品图片批量导入工具 v2")
    print("=" * 70)
    print(f"📁 图片目录：{images_dir}")
    print()

    # Step 1: 扫描所有图片
    print("📸 Step 1: 扫描图片文件夹...")
    images_by_category = get_all_images(images_dir)
    total_images = sum(len(v) for v in images_by_category.values())
    print(f"   找到 {len(images_by_category)} 个分类，共 {total_images} 张图片")
    print()

    # Step 2: 匹配商品
    print("🔗 Step 2: 匹配商品与图片...")
    matches = match_products_to_images(images_by_category)
    print(f"   成功匹配 {len(matches)}/{len(EXISTING_PRODUCTS)} 个商品")
    print()

    # Step 3: 复制图片 & 生成SQL
    if matches:
        print("💾 Step 3: 复制图片并生成SQL...")
        sql_updates, sql_inserts, upload_dir = copy_images_and_generate_sql(matches, project_root)

        # 写入SQL文件
        sql_path = os.path.join(output_dir, "update_product_images.sql")
        with open(sql_path, 'w', encoding='utf-8') as f:
            f.write("-- ============================================\n")
            f.write("-- 商品图片批量更新脚本（自动生成）\n")
            f.write(f"-- 生成时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(f"-- 更新数量：{len(matches)} 个商品\n")
            f.write("-- ============================================\n\n")

            f.write("-- Step 1: 更新 product 表封面图\n")
            f.write("-- ============================================\n")
            for sql in sql_updates:
                f.write(sql + "\n")
            f.write("\n")

            f.write("-- Step 2: 删除旧的占位符图片记录\n")
            f.write("-- ============================================\n")
            product_ids = [m[0] for m in matches]
            f.write(f"DELETE FROM `product_image` WHERE `product_id` IN ({', '.join(map(str, product_ids))});\n\n")

            f.write("-- Step 3: 插入新的商品图片记录\n")
            f.write("-- ============================================\n")
            for sql in sql_inserts:
                f.write(sql + "\n")
            f.write("\n")

            f.write("-- 执行完成！\n")

        print(f"   ✅ SQL 文件：{sql_path}")
        print(f"   ✅ 图片目录：{upload_dir}")
        print()
        print("=" * 70)
        print("📋 部署步骤：")
        print("   1. 将 uploads/ 目录上传到服务器（后端jar包同级目录）")
        print("   2. 在服务器执行：mysql -u root -p online_mall < update_product_images.sql")
        print("   3. 重启后端服务")
        print("=" * 70)
    else:
        print("❌ 没有匹配到任何商品，请检查图片文件夹结构")


if __name__ == '__main__':
    main()
