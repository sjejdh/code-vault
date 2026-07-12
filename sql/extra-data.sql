SET NAMES utf8mb4;
USE code_vault;

-- ============================
-- 第二批新增标签
-- ============================
INSERT IGNORE INTO `tag` (`name`) VALUES
('微服务'), ('消息队列'), ('Kafka'), ('RabbitMQ'), ('Spring Cloud'),
('Elasticsearch'), ('MongoDB'), ('Linux命令'), ('Git技巧'), ('递归'),
('贪心'), ('二叉树'), ('图论'), ('前端工程化'), ('CSS'),
('Node.js'), ('网络编程'), ('TCP/IP'), ('设计原则'), ('函数式编程');

-- ============================
-- 第二批代码片段（20条）
-- ============================
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

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
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
WHERE MATCH(title, description) AGAINST("Spring" IN NATURAL LANGUAGE_MODE);', 'SQL', 3, 267, 51, 33, 1, 1),

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
echo "Disk: ${DISK}%"

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
</script>', 'JavaScript', 2, 389, 72, 48, 1, 1),

(1, 'Java 线程池配置与使用',
'使用 ThreadPoolExecutor 自定义线程池，设置核心线程数、最大线程数、拒绝策略，避免使用 Executors 快捷方法导致的 OOM 风险',
'@Configuration
public class ThreadPoolConfig {

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,                      // corePoolSize
            8,                      // maxPoolSize
            60L, TimeUnit.SECONDS,  // keepAliveTime
            new LinkedBlockingQueue<>(200),
            new ThreadFactoryBuilder().setNameFormat("biz-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}',
'Java', 1, 203, 41, 25, 1, 1),

(1, '单例模式（双重检查锁）',
'线程安全的单例模式实现，使用 volatile + 双重检查锁定（DCL），兼顾性能与正确性',
'public class Singleton {

    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}',
'Java', 1, 187, 35, 20, 1, 1),

(2, 'Spring Boot 全局异常处理',
'使用 @RestControllerAdvice 统一捕获 Controller 层异常，按异常类型返回不同的错误码和提示信息',
'@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.error(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleUnknown(Exception e) {
        log.error("未知异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}',
'Java', 1, 276, 58, 34, 1, 1),

(2, 'MyBatis-Plus 批量插入优化',
'使用 MyBatis-Plus 的 saveBatch 方法配合 JDBC batch 参数，将 1000 条数据的插入时间从 8s 优化到 0.5s',
'@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> {

    @Transactional(rollbackFor = Exception.class)
    public void batchImport(List<UserDTO> dtoList) {
        List<User> users = dtoList.stream().map(dto -> {
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setNickname(dto.getNickname());
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            return user;
        }).collect(Collectors.toList());

        this.saveBatch(users, 500);
        log.info("批量导入完成，共 {} 条", users.size());
    }
}',
'Java', 1, 345, 72, 45, 1, 1),

(3, 'Python 装饰器实现日志记录',
'使用 Python 装饰器模式为函数自动添加入参和返回值的日志记录，支持自定义日志级别',
'import functools
import logging

logger = logging.getLogger(__name__)

def log_call(level=logging.INFO):
    """函数调用日志装饰器"""
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            logger.log(level, f"调用 {func.__name__}, args={args}, kwargs={kwargs}")
            try:
                result = func(*args, **kwargs)
                logger.log(level, f"{func.__name__} 返回: {result}")
                return result
            except Exception as e:
                logger.error(f"{func.__name__} 异常: {e}", exc_info=True)
                raise
        return wrapper
    return decorator

# 使用示例
@log_call()
def calculate_discount(price, discount):
    return round(price * discount, 2)',
'Python', 6, 167, 30, 18, 1, 1),

(3, 'JavaScript 深拷贝工具函数',
'支持循环引用、Date、RegExp、Map、Set 等复杂类型的深拷贝实现',
'function deepClone(obj, hash = new WeakMap()) {
    if (obj === null || typeof obj !== "object") return obj;
    if (hash.has(obj)) return hash.get(obj);
    if (obj instanceof Date) return new Date(obj);
    if (obj instanceof RegExp) return new RegExp(obj);
    if (obj instanceof Map) {
        const clone = new Map();
        hash.set(obj, clone);
        obj.forEach((val, key) =>
            clone.set(deepClone(key, hash), deepClone(val, hash)));
        return clone;
    }
    const clone = Array.isArray(obj) ? []
        : Object.create(Object.getPrototypeOf(obj));
    hash.set(obj, clone);
    Reflect.ownKeys(obj).forEach(key => {
        clone[key] = deepClone(obj[key], hash);
    });
    return clone;
}',
'JavaScript', 2, 289, 56, 37, 1, 1),

(1, 'MySQL 窗口函数排名查询',
'使用 ROW_NUMBER、RANK、DENSE_RANK 窗口函数实现分类排名和 TopN 查询',
'-- 查询每个分类下浏览量 Top3 的代码片段
SELECT * FROM (
    SELECT
        s.id, s.title, c.name AS category_name, s.view_count,
        ROW_NUMBER() OVER (
            PARTITION BY s.category_id ORDER BY s.view_count DESC
        ) AS rn
    FROM snippet s
    LEFT JOIN category c ON s.category_id = c.id
    WHERE s.status = 1 AND s.is_public = 1
) ranked
WHERE rn <= 3
ORDER BY category_name, rn;',
'SQL', 3, 234, 43, 28, 1, 1),

(2, 'Redis 分布式锁实现',
'基于 Redis SETNX + Lua 脚本实现可重入的分布式锁，支持自动续期和超时释放',
'@Component
@Slf4j
public class RedisDistributedLock {

    @Resource
    private StringRedisTemplate redisTemplate;

    public boolean tryLock(String lockKey, String requestId, long expireSeconds) {
        Boolean result = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, requestId, expireSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    public boolean unlock(String lockKey, String requestId) {
        String script =
            "if redis.call(''get'', KEYS[1]) == ARGV[1] then " +
            "   return redis.call(''del'', KEYS[1]) " +
            "else " +
            "   return 0 " +
            "end";
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey), requestId);
        return Long.valueOf(1L).equals(result);
    }
}',
'Java', 1, 312, 65, 40, 1, 1),

(3, 'Go 并发下载器',
'使用 goroutine + channel + sync.WaitGroup 实现并发 HTTP 下载，限制最大并发数',
'package main

import (
    "fmt"
    "io"
    "net/http"
    "os"
    "sync"
)

func download(url, filename string, wg *sync.WaitGroup, sem chan struct{}) {
    defer wg.Done()
    sem <- struct{}{}
    defer func() { <-sem }()

    resp, err := http.Get(url)
    if err != nil {
        fmt.Printf("下载失败 %s: %v\n", url, err)
        return
    }
    defer resp.Body.Close()

    f, _ := os.Create(filename)
    defer f.Close()
    io.Copy(f, resp.Body)
    fmt.Printf("下载完成: %s\n", filename)
}

func main() {
    urls := []struct{ url, name string }{
        {"https://example.com/f1", "file1.html"},
        {"https://example.com/f2", "file2.html"},
    }
    var wg sync.WaitGroup
    sem := make(chan struct{}, 3)

    for _, item := range urls {
        wg.Add(1)
        go download(item.url, item.name, &wg, sem)
    }
    wg.Wait()
    fmt.Println("全部下载完成")
}',
'Go', 1, 198, 38, 22, 1, 1),

(1, 'Java Stream API 数据处理',
'使用 Java 8 Stream API 进行集合的过滤、分组、聚合操作，替代传统 for 循环',
'// 按语言分组统计snippet数量和平均浏览量
Map<String, SnippetStats> stats = snippets.stream()
    .filter(s -> s.getStatus() == 1)
    .collect(Collectors.groupingBy(
        Snippet::getLanguage,
        Collectors.collectingAndThen(
            Collectors.summarizingInt(Snippet::getViewCount),
            stat -> new SnippetStats(
                (int) stat.getCount(),
                (long) stat.getSum(),
                (double) stat.getAverage()
            )
        )
    ));

// 找出每个分类下浏览量最高的snippet
Map<Long, Optional<Snippet>> topByCategory = snippets.stream()
    .collect(Collectors.groupingBy(
        Snippet::getCategoryId,
        Collectors.maxBy(
            Comparator.comparingInt(Snippet::getViewCount))
    ));',
'Java', 1, 256, 48, 30, 1, 1),

(2, 'Nginx 反向代理与负载均衡配置',
'Nginx 配置反向代理到多个后端服务，使用 upstream 实现轮询和权重负载均衡',
'upstream backend_servers {
    server 127.0.0.1:8080 weight=3;
    server 127.0.0.1:8081 weight=2;
    server 127.0.0.1:8082 backup;
}

server {
    listen 80;
    server_name api.codevault.com;
    client_max_body_size 10m;

    location /static/ {
        alias /var/www/code-vault/static/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }

    location /api/ {
        proxy_pass http://backend_servers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 10s;
        proxy_read_timeout 30s;
    }
}',
'Nginx', 5, 321, 61, 39, 1, 1),

(3, 'Python 二分查找与变体',
'二分查找的多种变体实现：标准查找、左边界、右边界，附带详细注释',
'def binary_search(nums, target):
    """标准二分查找"""
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = (left + right) // 2
        if nums[mid] == target:
            return mid
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1

def left_bound(nums, target):
    """查找 target 第一次出现的位置"""
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = (left + right) // 2
        if nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return left if left < len(nums) and nums[left] == target else -1',
'Python', 4, 278, 52, 35, 1, 1),

(1, 'C 语言链表反转（迭代+递归）',
'单链表反转的两种经典实现方式，面试高频题',
'// 方法一：迭代反转（空间 O(1)）
Node* reverseList(Node* head) {
    Node *prev = NULL, *curr = head, *next = NULL;
    while (curr != NULL) {
        next = curr->next;
        curr->next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}

// 方法二：递归反转（空间 O(n)）
Node* reverseListRecursive(Node* head) {
    if (head == NULL || head->next == NULL) {
        return head;
    }
    Node* newHead = reverseListRecursive(head->next);
    head->next->next = head;
    head->next = NULL;
    return newHead;
}',
'C/C++', 4, 356, 78, 52, 1, 1),

(2, 'Spring Boot 参数校验与自定义注解',
'使用 @Valid + 自定义校验注解实现接口参数校验，包括手机号、枚举值等业务规则',
'// 自定义手机号校验注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "手机号格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 校验器实现
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String REGEX = "^1[3-9]\\d{9}$";
    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        return value.matches(REGEX);
    }
}',
'Java', 1, 245, 47, 31, 1, 1),

(3, 'JavaScript Promise 并发控制',
'限制同时执行的异步任务数量，适用于批量请求、文件上传等需要限流的场景',
'async function asyncPool(limit, items, iteratorFn) {
    const results = [];
    const executing = new Set();

    for (const [index, item] of items.entries()) {
        const p = Promise.resolve().then(() => iteratorFn(item, index));
        results.push(p);
        executing.add(p);

        const clean = () => executing.delete(p);
        p.then(clean, clean);

        if (executing.size >= limit) {
            await Promise.race(executing);
        }
    }
    return Promise.all(results);
}

// 使用示例：最多 3 个并发下载
const urls = [
    "https://api.example.com/1",
    "https://api.example.com/2",
    "https://api.example.com/3"
];
const data = await asyncPool(3, urls, async (url) => {
    const res = await fetch(url);
    return res.json();
});',
'JavaScript', 2, 234, 45, 29, 1, 1),

-- ===== 第二批新增片段（20条，ID 26~45）=====

(1, 'RabbitMQ 消息队列生产者与消费者',
'使用 Spring AMQP 实现消息队列的生产者发送和消费者监听，包含死信队列和消息确认机制',
'@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "codevault.snippet";

    @Bean
    public Queue snippetQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        template.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) log.error("消息发送失败: {}", cause);
        });
        return template;
    }
}

@Component
@Slf4j
public class SnippetEventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void onSnippetCreated(SnippetCreatedEvent event) {
        log.info("收到片段创建事件: {}", event.getSnippetId());
        // 异步处理：更新搜索索引、通知关注者等
    }
}',
'Java', 1, 189, 36, 21, 1, 1),

(3, 'React Hooks 自定义 Hook：useLocalStorage',
'封装 localStorage 操作为 React Hook，支持类型推断和自动 JSON 序列化',
'import { useState, useEffect } from "react";

function useLocalStorage<T>(key: string, initialValue: T) {
    const [storedValue, setStoredValue] = useState<T>(() => {
        try {
            const item = window.localStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            return initialValue;
        }
    });

    useEffect(() => {
        try {
            window.localStorage.setItem(key, JSON.stringify(storedValue));
        } catch (error) {
            console.error("localStorage 写入失败:", error);
        }
    }, [key, storedValue]);

    return [storedValue, setStoredValue] as const;
}

// 使用示例
const [theme, setTheme] = useLocalStorage("theme", "dark");',
'TypeScript', 2, 167, 33, 20, 1, 1),

(2, 'Elasticsearch 全文搜索配置',
'Spring Data Elasticsearch 整合配置，实现代码片段的全文检索和高亮显示',
'@Configuration
@EnableElasticsearchRepositories(basePackages = "com.codevault.repository")
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(host, 9200, "http")));
    }
}

@Service
public class SnippetSearchService {

    @Resource
    private SnippetEsRepository esRepository;

    public Page<SnippetEsDoc> search(String keyword, int page, int size) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders
                .multiMatchQuery(keyword, "title", "description", "content")
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
            .withHighlightFields(
                new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>").fragmentSize(200))
            .withPageable(PageRequest.of(page, size))
            .build();
        return esRepository.search(query);
    }
}',
'Java', 3, 213, 42, 27, 1, 1),

(1, 'Spring Cloud Gateway 路由配置',
'使用 Spring Cloud Gateway 实现动态路由、限流和统一鉴权，替代传统 Nginx 反向代理',
'@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("snippet-service", r -> r
                .path("/api/snippet/**")
                .filters(f -> f
                    .addRequestHeader(GatewayHeader.X_TRACE_ID, UUID.randomUUID().toString())
                    .filter(rateLimiter(c -> c.setReplenishRate(100).setBurstCapacity(200))))
                .uri("lb://snippet-service"))
            .route("user-service", r -> r
                .path("/api/user/**")
                .uri("lb://user-service"))
            .build();
    }
}

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/api/user/login") || path.startsWith("/api/snippet/public")) {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -100; }
}',
'Java', 1, 267, 50, 32, 1, 1),

(3, 'Python 字典树（Trie）实现',
'实现前缀树数据结构，支持单词插入、搜索和前缀匹配，常用于自动补全场景',
'class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False
        self.count = 0

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        node = self.root
        for ch in word:
            if ch not in node.children:
                node.children[ch] = TrieNode()
            node = node.children[ch]
        node.is_end = True
        node.count += 1

    def search(self, word: str) -> bool:
        node = self._find_node(word)
        return node is not None and node.is_end

    def starts_with(self, prefix: str) -> bool:
        return self._find_node(prefix) is not None

    def _find_node(self, prefix: str) -> TrieNode | None:
        node = self.root
        for ch in prefix:
            if ch not in node.children:
                return None
            node = node.children[ch]
        return node

# 使用示例
trie = Trie()
for word in ["python", "program", "progress", "java", "javascript"]:
    trie.insert(word)
print(trie.search("python"))     # True
print(trie.starts_with("pro"))   # True',
'Python', 4, 198, 40, 25, 1, 1),

(2, 'MongoDB 聚合管道统计查询',
'使用 MongoDB Aggregation Pipeline 实现多阶段数据聚合，替代复杂的多表 JOIN',
'@Service
public class SnippetStatsService {

    @Resource
    private MongoTemplate mongoTemplate;

    public List<LanguageStats> getStatsByLanguage() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is(1)),
            Aggregation.group("language")
                .count().as("count")
                .sum("viewCount").as("totalViews")
                .avg("viewCount").as("avgViews"),
            Aggregation.sort(Sort.Direction.DESC, "count"),
            Aggregation.limit(10)
        );
        AggregationResults<LanguageStats> results =
            mongoTemplate.aggregate(agg, "snippet", LanguageStats.class);
        return results.getMappedResults();
    }
}',
'Java', 3, 176, 34, 22, 1, 1),

(1, 'Git Rebase 与交互式变基',
'使用 git rebase -i 整理提交历史，合并零碎提交、修改提交信息、重排提交顺序',
'# 1. 合并最近 3 个提交为一个
git rebase -i HEAD~3
# 在编辑器中将 pick 改为 squash（保留提交信息）或 fixup（丢弃提交信息）

# 2. 修改某次提交的信息
git rebase -i HEAD~5
# 将目标提交的 pick 改为 reword

# 3. 将某次提交移到最前面
git rebase -i HEAD~4
# 剪切目标提交行，粘贴到列表最顶部

# 4. 变基过程中解决冲突后继续
git add .
git rebase --continue

# 5. 取消变基操作
git rebase --abort

# 注意：不要对已推送到远程的公共分支执行 rebase',
'Shell', 6, 312, 55, 38, 1, 1),

(3, 'CSS Grid 响应式布局',
'使用 CSS Grid 实现自适应的代码片段卡片布局，配合媒体查询适配不同屏幕尺寸',
'.snippet-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 20px;
    padding: 20px;
}

.snippet-card {
    background: #161b22;
    border: 1px solid #30363d;
    border-radius: 8px;
    padding: 20px;
    transition: border-color 0.2s, transform 0.15s;
}

.snippet-card:hover {
    border-color: #58a6ff;
    transform: translateY(-2px);
}

.snippet-card pre {
    background: #0d1117;
    border-radius: 6px;
    padding: 16px;
    overflow-x: auto;
    font-family: "Cascadia Code", "Fira Code", monospace;
    font-size: 13px;
    line-height: 1.5;
}

@media (max-width: 768px) {
    .snippet-grid {
        grid-template-columns: 1fr;
        padding: 12px;
    }
}',
'CSS', 2, 145, 28, 16, 1, 1),

(2, 'Kafka 生产者幂等性与事务消息',
'使用 Kafka Producer 的 enable.idempotence 和事务机制，确保消息精确一次语义',
'@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 开启幂等性（同一分区同一消息只消费一次）
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        // 开启事务支持
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "snippet-tx-01");
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// 事务消息发送
@Service
public class SnippetEventPublisher {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void publishWithTransaction(Snippet snippet) {
        // 1. 先保存数据库
        snippetRepository.save(snippet);
        // 2. 再发送消息（同一事务，要么都成功要么都失败）
        kafkaTemplate.send("snippet-events", new SnippetCreatedEvent(snippet.getId()));
    }
}',
'Java', 1, 234, 48, 30, 1, 1),

(3, 'Node.js Express 中间件模式',
'使用 Express 中间件实现请求日志、错误处理、限流等功能，理解洋葱模型执行顺序',
'const express = require("express");
const rateLimit = require("express-rate-limit");

const app = express();

// 1. 请求日志中间件
app.use((req, res, next) => {
    const start = Date.now();
    res.on("finish", () => {
        const duration = Date.now() - start;
        console.log(`${req.method} ${req.url} ${res.statusCode} ${duration}ms`);
    });
    next();
});

// 2. 限流中间件
const limiter = rateLimit({
    windowMs: 15 * 60 * 1000,  // 15 分钟
    max: 100,                    // 每个IP最多 100 次请求
    message: "请求过于频繁，请稍后再试"
});
app.use("/api/", limiter);

// 3. 统一错误处理中间件
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(err.status || 500).json({
        code: err.status || 500,
        message: err.message || "服务器内部错误"
    });
});

app.listen(3000, () => console.log("Server running on port 3000"));',
'JavaScript', 2, 198, 39, 24, 1, 1),

(1, 'Java 观察者模式实现事件驱动',
'使用 Guava EventBus 或自定义实现观察者模式，实现模块间松耦合的事件通信',
'@Component
public class EventPublisher {

    @Resource
    private ApplicationEventPublisher publisher;

    public void publishSnippetCreated(Long snippetId, Long userId) {
        publisher.publishEvent(new SnippetCreatedEvent(snippetId, userId));
    }
}

// 事件定义
public record SnippetCreatedEvent(Long snippetId, Long userId) {}

// 事件监听：更新搜索索引
@Component
@Slf4j
public class SearchIndexListener {

    @Async
    @EventListener
    public void onSnippetCreated(SnippetCreatedEvent event) {
        log.info("更新搜索索引，片段ID: {}", event.snippetId());
        searchService.indexSnippet(event.snippetId());
    }
}

// 事件监听：发送通知
@Component
@Slf4j
public class NotificationListener {

    @Async
    @EventListener
    public void onSnippetCreated(SnippetCreatedEvent event) {
        log.info("发送创建通知给用户: {}", event.userId());
        notificationService.send(event.userId(), "你的代码片段已发布成功");
    }
}',
'Java', 1, 167, 32, 19, 1, 1),

(3, 'JavaScript 节流函数与虚拟滚动',
'节流函数限制高频事件触发，结合虚拟滚动优化长列表渲染性能',
'// 节流函数
function throttle(func, wait) {
    let lastTime = 0;
    return function(...args) {
        const now = Date.now();
        if (now - lastTime >= wait) {
            lastTime = now;
            func.apply(this, args);
        }
    };
}

// 虚拟滚动 Hook
function useVirtualScroll(containerHeight, itemHeight, items) {
    const [scrollTop, setScrollTop] = useState(0);
    const visibleCount = Math.ceil(containerHeight / itemHeight);
    const startIndex = Math.floor(scrollTop / itemHeight);
    const endIndex = Math.min(startIndex + visibleCount, items.length);
    const visibleItems = items.slice(startIndex, endIndex);
    const totalHeight = items.length * itemHeight;
    const offsetY = startIndex * itemHeight;

    const onScroll = throttle((e) => {
        setScrollTop(e.target.scrollTop);
    }, 16);

    return { visibleItems, totalHeight, offsetY, onScroll };
}',
'JavaScript', 2, 221, 43, 27, 1, 1),

(2, 'Linux 常用排查命令速查',
'生产环境快速定位问题的 Shell 命令集合，涵盖 CPU、内存、网络、磁盘和进程排查',
'# CPU 排查
top -bn1 | head -20                  # 查看进程 CPU 占用
ps -eo pid,ppid,cmd,%mem,%cpu --sort=-%cpu | head  # 按CPU排序

# 内存排查
free -h                              # 内存使用概览
ps aux --sort=-%mem | head 10        # 内存占用 Top10
jmap -histo:live <pid> | head 20     # Java 堆内存对象统计

# 网络排查
ss -tlnp                             # 查看监听端口
netstat -anp | grep ESTABLISHED | wc -l  # 活跃连接数
tcpdump -i any port 8080 -nn -A -c 10     # 抓包

# 磁盘 IO
iostat -x 1 3                        # 磁盘 IO 统计
iotop -oP                            # IO 占用排序

# 日志快速搜索
grep -rn "Exception" /var/log/app/ --include="*.log" | tail 50
zgrep "ERROR" app.log.2024-01-01.gz  # 搜索压缩日志',
'Shell', 6, 378, 72, 48, 1, 1),

(1, 'Spring Boot 优雅停机与健康检查',
'配置 Spring Boot 应用的优雅停机策略和 Actuator 健康检查端点',
'# application.yml
server:
  shutdown: graceful  # 开启优雅停机

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # 停机等待时间

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,shutdown
  endpoint:
    health:
      show-details: always
    shutdown:
      enabled: true  # 允许通过 HTTP 关停应用

# 自定义健康检查
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Resource
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(3)) {
                return Health.up().withDetail("database", "MySQL").build();
            }
        } catch (SQLException e) {
            return Health.down(e).build();
        }
        return Health.down().build();
    }
}',
'Java', 1, 201, 38, 23, 1, 1),

(3, 'Python LRU 缓存实现',
'使用 OrderedDict 实现线程安全的 LRU 缓存，支持 get/put 操作和容量限制',
'from collections import OrderedDict
import threading

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = OrderedDict()
        self.lock = threading.Lock()

    def get(self, key: str):
        with self.lock:
            if key not in self.cache:
                return None
            self.cache.move_to_end(key)  # 标记为最近使用
            return self.cache[key]

    def put(self, key: str, value) -> None:
        with self.lock:
            if key in self.cache:
                self.cache.move_to_end(key)
            self.cache[key] = value
            if len(self.cache) > self.capacity:
                self.cache.popitem(last=False)  # 移除最久未使用的

    def __repr__(self) -> str:
        return f"LRUCache({self.capacity}) {list(self.cache.keys())}"

# 使用示例
cache = LRUCache(3)
cache.put("a", 1)
cache.put("b", 2)
cache.put("c", 3)
cache.put("d", 4)  # 淘汰 "a"
print(cache)  # LRUCache(3) ["b", "c", "d"]
print(cache.get("a"))  # None（已被淘汰）',
'Python', 4, 234, 46, 29, 1, 1),

(2, 'Docker 多阶段构建优化镜像体积',
'使用多阶段构建将 Java 应用 Docker 镜像从 600MB+ 压缩到 200MB 以内',
'# 第一阶段：编译
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# 第二阶段：运行
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

# 只复制编译产物，不包含源码和构建工具
COPY --from=builder /app/target/*.jar app.jar

# 非 root 用户运行
USER app
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]',
'Dockerfile', 5, 289, 56, 35, 1, 1),

(1, 'Java CompletableFuture 异步编排',
'使用 CompletableFuture 实现多个异步任务的并行执行、结果合并和异常处理',
'public class AsyncSnippetService {

    @Resource
    private SnippetMapper snippetMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 并行查询片段、标签、分类，三合一返回
     */
    public CompletableFuture<SnippetDetailVO> getDetailAsync(Long id) {
        CompletableFuture<Snippet> snippetFuture =
            CompletableFuture.supplyAsync(() -> snippetMapper.findById(id));

        CompletableFuture<List<Tag>> tagsFuture = snippetFuture.thenCompose(snippet ->
            CompletableFuture.supplyAsync(() -> tagMapper.findBySnippetId(snippet.getId()))
        );

        CompletableFuture<Category> categoryFuture = snippetFuture.thenCompose(snippet ->
            CompletableFuture.supplyAsync(() -> categoryMapper.findById(snippet.getCategoryId()))
        );

        return CompletableFuture.allOf(snippetFuture, tagsFuture, categoryFuture)
            .thenApply(v -> {
                SnippetDetailVO vo = new SnippetDetailVO();
                vo.setSnippet(snippetFuture.join());
                vo.setTags(tagsFuture.join());
                vo.setCategory(categoryFuture.join());
                return vo;
            })
            .exceptionally(ex -> {
                log.error("查询片段详情异常", ex);
                throw new BusinessException("查询失败");
            });
    }
}',
'Java', 1, 245, 47, 28, 1, 1),

(3, 'TypeScript 泛型工具类型',
'使用 TypeScript 内置工具类型实现类型安全的 API 响应处理和状态管理',
'// 1. Partial - 所有属性变为可选
interface SnippetForm {
    title: string;
    description: string;
    language: string;
    content: string;
}
type DraftSnippet = Partial<SnippetForm>;

// 2. Record - 构造键值对类型
type LanguageColor = Record<string, string>;
const colors: LanguageColor = {
    Java: "#b07219",
    Python: "#3572A5",
    JavaScript: "#f1e05a",
    Go: "#00ADD8",
};

// 3. Pick + Omit - 选取或排除属性
type SnippetSummary = Pick<SnippetForm, "title" | "language">;
type CreateSnippet = Omit<SnippetForm, "id" | "createTime">;

// 4. ReturnType - 提取函数返回类型
async function fetchSnippets(): Promise<SnippetSummary[]> {
    const res = await fetch("/api/snippet/public");
    return res.json();
}
type SnippetListResponse = Awaited<ReturnType<typeof fetchSnippets>>;',
'TypeScript', 2, 189, 38, 24, 1, 1),

(2, 'Spring Boot 定时任务与分布式调度',
'使用 @Scheduled 实现定时任务，配合 Redis 实现分布式环境下的单点执行',
'@Component
@EnableScheduling
@Slf4j
public class ScheduledTasks {

    @Resource
    private StringRedisTemplate redisTemplate;
    @Resource
    private HotSnippetCache hotSnippetCache;

    /**
     * 每30分钟刷新热门片段缓存
     * 使用 Redis SETNX 保证分布式环境下只有一个节点执行
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void refreshHotSnippets() {
        String lockKey = "lock:scheduled:refresh-hot";
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "1", 25, TimeUnit.MINUTES);
        if (Boolean.TRUE.equals(acquired)) {
            try {
                log.info("开始刷新热门片段缓存...");
                hotSnippetCache.refreshCache();
                log.info("热门片段缓存刷新完成");
            } catch (Exception e) {
                log.error("刷新热门片段缓存失败", e);
            } finally {
                redisTemplate.delete(lockKey);
            }
        }
    }

    /**
     * 每天凌晨2点清理过期会话
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredSessions() {
        log.info("清理过期会话...");
    }
}',
'Java', 1, 198, 39, 24, 1, 1),

(3, 'Python 装饰器实现带过期时间的缓存',
'使用 functools.lru_cache 结合自定义装饰器，实现带 TTL 的函数结果缓存',
'import functools
import time
import threading

def cached_with_ttl(ttl_seconds: int):
    """带过期时间的缓存装饰器"""
    def decorator(func):
        cache = {}
        lock = threading.Lock()

        @functools.wraps(func)
        def wrapper(*args):
            now = time.time()
            with lock:
                if args in cache:
                    result, timestamp = cache[args]
                    if now - timestamp < ttl_seconds:
                        return result
            result = func(*args)
            with lock:
                cache[args] = (result, now)
            return result
        return wrapper
    return decorator

# 使用示例：缓存数据库查询结果5分钟
@cached_with_ttl(ttl_seconds=300)
def get_user_by_id(user_id: int):
    print(f"查询数据库: user_id={user_id}")
    # 模拟数据库查询
    return {"id": user_id, "name": "test"}',
'Python', 6, 178, 34, 21, 1, 1),

(1, 'Java 策略模式消除 if-else',
'使用策略模式 + 工厂模式消除代码中大量的 if-else 分支，提升可扩展性',
'// 1. 定义策略接口
public interface SnippetSortStrategy {
    List<Snippet> sort(List<Snippet> snippets);
    String getName();
}

// 2. 具体策略实现
@Component("sortByViews")
public class SortByViews implements SnippetSortStrategy {
    public List<Snippet> sort(List<Snippet> snippets) {
        return snippets.stream()
            .sorted(Comparator.comparingInt(Snippet::getViewCount).reversed())
            .collect(Collectors.toList());
    }
    public String getName() { return "按浏览量"; }
}

@Component("sortByLikes")
public class SortByLikes implements SnippetSortStrategy {
    public List<Snippet> sort(List<Snippet> snippets) {
        return snippets.stream()
            .sorted(Comparator.comparingInt(Snippet::getLikeCount).reversed())
            .collect(Collectors.toList());
    }
    public String getName() { return "按点赞数"; }
}

// 3. 策略工厂
@Service
public class SortStrategyFactory {
    private final Map<String, SnippetSortStrategy> strategyMap;

    public SortStrategyFactory(Map<String, SnippetSortStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public SnippetSortStrategy getStrategy(String type) {
        SnippetSortStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BusinessException("不支持的排序方式: " + type);
        }
        return strategy;
    }
}

// 使用：sortStrategyFactory.getStrategy("sortByViews").sort(snippets)',
'Java', 1, 256, 50, 32, 1, 1),

(3, 'CSS Flexbox 与 Grid 混合布局',
'Flexbox 处理一维布局，Grid 处理二维布局，两者配合实现复杂页面结构',
'/* 主布局：Flexbox 纵向排列 */
.app-layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    height: 64px;
    flex-shrink: 0;  /* 不被压缩 */
}

.main-area {
    display: flex;
    flex: 1;         /* 占据剩余空间 */
    min-height: 0;   /* 关键：允许 flex 子项滚动 */
}

/* 内容区：Grid 卡片网格 */
.content-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
    align-content: start;  /* 顶部对齐 */
    overflow-y: auto;       /* 内容溢出滚动 */
}

/* 侧边栏：Flex 固定宽度 */
.sidebar {
    width: 280px;
    flex-shrink: 0;
    border-left: 1px solid #30363d;
    overflow-y: auto;
}

/* 响应式：小屏时侧边栏收起 */
@media (max-width: 900px) {
    .main-area {
        flex-direction: column;
    }
    .sidebar {
        width: 100%;
        border-left: none;
        border-top: 1px solid #30363d;
    }
}',
'CSS', 2, 134, 26, 15, 1, 1);

-- ============================
-- 全部片段-标签关联
-- 标签映射（现有ID）：
-- 1=Spring Boot, 2=MyBatis, 3=Vue, 4=React, 5=MySQL, 6=Redis, 7=Docker,
-- 8=Python, 9=JavaScript, 10=Java, 11=API, 12=排序, 13=动态规划,
-- 14=Linux, 15=Git, 16=TypeScript, 17=设计模式, 18=并发, 19=正则表达式,
-- 20=性能优化, 21=Go, 22=C/C++, 23=单元测试, 24=安全, 25=Nginx,
-- 26=算法, 27=多线程, 28=缓存策略, 29=接口设计, 30=数据结构
-- 新增标签（第二批插入后）：
-- 31=微服务, 32=消息队列, 33=Kafka, 34=RabbitMQ, 35=Spring Cloud,
-- 36=Elasticsearch, 37=MongoDB, 38=Linux命令, 39=Git技巧, 40=递归,
-- 41=贪心, 42=二叉树, 43=图论, 44=前端工程化, 45=CSS,
-- 46=Node.js, 47=网络编程, 48=TCP/IP, 49=设计原则, 50=函数式编程
-- ============================
INSERT IGNORE INTO `snippet_tag` (`snippet_id`, `tag_id`) VALUES
-- 原有片段（ID 1~25）
(1, 10), (1, 11),             -- 1: Spring Boot 统一响应封装
(2, 2), (2, 5),               -- 2: MyBatis 动态 SQL
(3, 10), (3, 11),             -- 3: JWT Token
(4, 6), (4, 10),              -- 4: Redis 缓存
(5, 8), (5, 12),              -- 5: Python 快速排序
(6, 9), (6, 44),              -- 6: JS 防抖
(7, 5), (7, 20),              -- 7: MySQL 索引优化
(8, 7), (8, 10),              -- 8: Docker Compose
(9, 14), (9, 38),             -- 9: Linux Shell 监控
(10, 3), (10, 9),             -- 10: Vue3
(11, 10), (11, 27),           -- 11: 线程池
(11, 18),
(12, 10), (12, 17),           -- 12: 单例模式
(13, 10), (13, 11),           -- 13: 全局异常处理
(14, 2), (14, 10),            -- 14: MyBatis-Plus 批量插入
(14, 20),
(15, 8),                       -- 15: Python 装饰器
(16, 9), (16, 30),            -- 16: JS 深拷贝
(17, 5),                       -- 17: MySQL 窗口函数
(18, 6), (18, 10),            -- 18: Redis 分布式锁
(18, 18),
(19, 21), (19, 18),           -- 19: Go 并发下载
(20, 10), (20, 20),           -- 20: Java Stream API
(21, 25), (21, 7),            -- 21: Nginx
(22, 8), (22, 26),            -- 22: Python 二分查找
(23, 22), (23, 26),           -- 23: C 链表反转
(23, 30),
(24, 10), (24, 17),           -- 24: 参数校验
(24, 24),
(25, 9), (25, 18),            -- 25: Promise 并发控制
-- 新增片段（ID 26~45）
(26, 10), (26, 34),           -- 26: RabbitMQ 消息队列
(26, 32),
(27, 4), (27, 16),            -- 27: React Hooks useLocalStorage
(27, 44),
(28, 10), (28, 36),           -- 28: Elasticsearch
(28, 29),
(29, 10), (29, 35),           -- 29: Spring Cloud Gateway
(29, 31),
(29, 24),
(30, 8), (30, 42),            -- 30: Python 字典树
(30, 30),
(31, 10), (31, 37),           -- 31: MongoDB 聚合管道
(32, 14), (32, 39),           -- 32: Git Rebase
(32, 15),
(33, 9), (33, 45),            -- 33: CSS Grid 响应式布局
(33, 44),
(34, 10), (34, 33),           -- 34: Kafka 事务消息
(34, 32),
(35, 9), (35, 46),            -- 35: Node.js Express 中间件
(35, 29),
(36, 10), (36, 49),           -- 36: 观察者模式
(36, 17),
(37, 9), (37, 20),            -- 37: JS 节流+虚拟滚动
(37, 44),
(38, 14), (38, 38),           -- 38: Linux 排查命令
(39, 10), (39, 1),            -- 39: 优雅停机
(39, 7),
(40, 8), (40, 28),            -- 40: Python LRU 缓存
(40, 50),
(41, 7), (41, 5),             -- 41: Docker 多阶段构建
(42, 10), (42, 18),           -- 42: CompletableFuture
(42, 27),
(43, 16), (43, 44),           -- 43: TypeScript 泛型
(44, 10), (44, 28),           -- 44: 定时任务
(44, 6),
(45, 8), (45, 50),            -- 45: Python 缓存装饰器
(46, 10), (46, 17),           -- 46: 策略模式
(46, 49),
(47, 9), (47, 45);            -- 47: Flexbox+Grid 混合布局

-- ============================
-- 额外点赞数据
-- ============================
INSERT IGNORE INTO `like` (`user_id`, `snippet_id`) VALUES
(1, 26), (1, 30), (1, 34), (1, 40), (1, 42),
(2, 27), (2, 29), (2, 33), (2, 39), (2, 43),
(3, 28), (3, 31), (3, 35), (3, 38), (3, 41),
(1, 46), (1, 47), (2, 36), (2, 44), (3, 45);

-- ============================
-- 额外收藏数据
-- ============================
INSERT IGNORE INTO `collection` (`user_id`, `snippet_id`) VALUES
(1, 27), (1, 31), (1, 38), (1, 42), (1, 46),
(2, 26), (2, 30), (2, 35), (2, 41), (2, 44),
(3, 28), (3, 33), (3, 39), (3, 43), (3, 47);