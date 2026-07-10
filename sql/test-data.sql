-- ============================================
-- CodeVault 测试数据
-- 在 init.sql 执行后再执行此脚本插入演示数据
-- ============================================

USE code_vault;

-- ----------------------------
-- 测试用户（密码都是 user123，BCrypt加密）
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '开发者小明', 'USER', 1),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '后端小王', 'USER', 1),
('user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '全栈阿强', 'USER', 1);

-- ----------------------------
-- 代码片段数据
-- ----------------------------
INSERT INTO `snippet` (`user_id`, `title`, `description`, `content`, `language`, `category_id`, `view_count`, `like_count`, `collect_count`, `is_public`, `status`) VALUES
(1, 'Spring Boot 统一响应封装', '使用泛型封装统一的API响应结果，包含状态码、消息和数据', '@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
}', 'Java', 1, 156, 23, 12, 1, 1),

(1, 'MyBatis 动态 SQL 分页查询', '使用 MyBatis 动态 SQL 实现带条件的分页查询，支持关键词搜索和多字段筛选', '<select id="findPublicSnippets" resultType="Snippet">
    SELECT * FROM snippet
    WHERE status = 1 AND is_public = 1
    <if test="keyword != null and keyword != \'\'">
        AND (title LIKE CONCAT("%", #{keyword}, "%")
        OR description LIKE CONCAT("%", #{keyword}, "%"))
    </if>
    <if test="categoryId != null">
        AND category_id = #{categoryId}
    </if>
    <if test="language != null and language != \'\'">
        AND language = #{language}
    </if>
    ORDER BY create_time DESC
    LIMIT #{offset}, #{pageSize}
</select>', 'Java', 1, 89, 15, 8, 1, 1),

(2, 'JWT Token 生成与验证工具类', '基于 jjwt 库实现 JWT Token 的生成、解析和验证，支持从 Token 中提取用户ID和用户名', '@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
}', 'Java', 1, 234, 45, 28, 1, 1),

(2, 'Redis 缓存热门数据', '使用 RedisTemplate 实现热门片段的缓存管理，包含刷新、获取和清除操作', '@Component
public class HotSnippetCache {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private SnippetMapper snippetMapper;
    
    private static final String CACHE_KEY = "hot_snippets";
    private static final long CACHE_TTL = 30;
    
    public List<Snippet> getHotSnippets() {
        List<Snippet> cached = (List<Snippet>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            return cached;
        }
        return refreshCache();
    }
}', 'Java', 1, 178, 32, 19, 1, 1),

(3, 'Python 快速排序实现', '经典的快速排序算法Python实现，包含递归分区和基准选择', 'def quick_sort(arr):
    if len(arr) <= 1:
        return arr
    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]
    return quick_sort(left) + middle + quick_sort(right)

# 测试
arr = [3, 6, 8, 10, 1, 2, 1]
print(quick_sort(arr))  # [1, 1, 2, 3, 6, 8, 10]', 'Python', 4, 312, 67, 41, 1, 1),

(3, 'JavaScript 防抖函数', '前端常用的防抖函数，限制事件触发频率，优化搜索框等高频触发场景', 'function debounce(func, wait) {
    let timeout;
    return function(...args) {
        const context = this;
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            func.apply(context, args);
        }, wait);
    };
}

// 使用示例
const handleSearch = debounce((query) => {
    console.log("搜索:", query);
    // 发送请求...
}, 500);

input.addEventListener("input", (e) => handleSearch(e.target.value));', 'JavaScript', 2, 445, 89, 56, 1, 1),

(1, 'MySQL 索引优化实践', '通过添加索引将模糊搜索查询从 300ms 优化到 100ms 以内的实战经验', '-- 优化前：全表扫描，查询耗时 300ms+
SELECT * FROM snippet 
WHERE title LIKE "%Spring%" OR description LIKE "%Spring%";

-- 优化后：添加全文索引，查询耗时 <100ms
ALTER TABLE snippet ADD FULLTEXT INDEX ft_title_desc(title, description);

SELECT * FROM snippet 
WHERE MATCH(title, description) AGAINST("Spring" IN NATURAL LANGUAGE MODE);', 'SQL', 3, 267, 51, 33, 1, 1),

(2, 'Docker Compose 一键部署', '使用 docker-compose 编排 MySQL + Redis + Spring Boot 应用，实现一键启动', 'version: "3.8"
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: code_vault
    ports:
      - "3306:3306"
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis', 'YAML', 5, 198, 38, 22, 1, 1),

(1, 'Linux Shell 监控脚本', '使用 psutil 库定期采集系统资源指标，配合 Prometheus + Grafana 可视化', '#!/bin/bash
# 系统资源监控脚本

CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | awk "{print $2}" | cut -d\'%\' -f1)
MEM_USAGE=$(free | grep Mem | awk "{printf \"%.2f\", $3/$2 * 100.0}")
DISK_USAGE=$(df -h / | tail -1 | awk "{print $5}" | cut -d\'%\' -f1)

echo "CPU: ${CPU_USAGE}%"
echo "Memory: ${MEM_USAGE}%"
echo "Disk: ${DISK_USAGE}%"

# 超过阈值发送告警
if (( $(echo "$CPU_USAGE > 80" | bc -l) )); then
    echo "告警: CPU使用率超过80%"
fi', 'Shell', 5, 156, 29, 15, 1, 1),

(3, 'Vue3 组合式 API 示例', '使用 Vue3 Composition API 实现一个简单的计数器组件', '<template>
  <div class="counter">
    <h2>计数器: {{ count }}</h2>
    <button @click="increment">+1</button>
    <button @click="decrement">-1</button>
    <button @click="reset">重置</button>
  </div>
</template>

<script setup>
import { ref } from "vue";

const count = ref(0);

const increment = () => count.value++;
const decrement = () => count.value--;
const reset = () => { count.value = 0; };
</script>', 'JavaScript', 2, 389, 72, 48, 1, 1);

-- ----------------------------
-- 片段-标签关联
-- ----------------------------
INSERT INTO `snippet_tag` (`snippet_id`, `tag_id`) VALUES
(1, 10), (1, 11),          -- Spring Boot + API
(2, 2), (2, 5),            -- MyBatis + MySQL
(3, 10), (3, 11),          -- Java + API
(4, 6), (4, 10),           -- Redis + Java
(5, 8), (5, 12),           -- Python + 排序
(6, 9), (6, 2),            -- JavaScript + Vue
(7, 5), (7, 13),           -- MySQL + 动态规划
(8, 7), (8, 10),           -- Docker + Java
(9, 14), (9, 15),          -- Linux + Git
(10, 2), (10, 9);          -- Vue + JavaScript

-- ----------------------------
-- 点赞数据
-- ----------------------------
INSERT INTO `like` (`user_id`, `snippet_id`) VALUES
(1, 3), (1, 5), (1, 6),
(2, 1), (2, 4), (2, 7),
(3, 1), (3, 2), (3, 8);

-- ----------------------------
-- 收藏数据
-- ----------------------------
INSERT INTO `collection` (`user_id`, `snippet_id`) VALUES
(1, 2), (1, 4), (1, 7),
(2, 1), (2, 5), (2, 8),
(3, 3), (3, 6), (3, 9);
