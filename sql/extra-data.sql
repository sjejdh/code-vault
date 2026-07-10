USE code_vault;

-- ============================
-- 新增标签（15个）
-- ============================
INSERT IGNORE INTO `tag` (`name`) VALUES
('TypeScript'), ('设计模式'), ('并发'), ('正则表达式'),
('性能优化'), ('Go'), ('C/C++'), ('单元测试'),
('安全'), ('Nginx'), ('算法'), ('多线程'),
('缓存策略'), ('接口设计'), ('数据结构');

-- ============================
-- 新增代码片段（15条）
-- ============================
INSERT INTO `snippet` (`user_id`, `title`, `description`, `content`, `language`, `category_id`, `view_count`, `like_count`, `collect_count`, `is_public`, `status`) VALUES

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
    if (obj === null || typeof obj !== ''object'') return obj;
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
    if (obj instanceof Set) {
        const clone = new Set();
        hash.set(obj, clone);
        obj.forEach(val => clone.add(deepClone(val, hash)));
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
ORDER BY category_name, rn;

-- 统计每个用户的snippet数量及占比
SELECT
    u.nickname,
    COUNT(*) AS snippet_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 1) AS pct
FROM snippet s
JOIN user u ON s.user_id = u.id
WHERE s.status = 1
GROUP BY u.id, u.nickname
ORDER BY snippet_count DESC;',
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
    ));

// 扁平化提取所有标签并去重排序
List<String> allTags = snippets.stream()
    .flatMap(s -> s.getTags().stream())
    .distinct()
    .sorted()
    .collect(Collectors.toList());',
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
    return left if left < len(nums) and nums[left] == target else -1

def right_bound(nums, target):
    """查找 target 最后一次出现的位置"""
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = (left + right) // 2
        if nums[mid] <= target:
            left = mid + 1
        else:
            right = mid - 1
    return right if right >= 0 and nums[right] == target else -1',
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
}

// DTO 使用
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;

    @Phone
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
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
    ''https://api.example.com/1'',
    ''https://api.example.com/2'',
    ''https://api.example.com/3'',
    ''https://api.example.com/4'',
    ''https://api.example.com/5''
];
const data = await asyncPool(3, urls, async (url) => {
    const res = await fetch(url);
    return res.json();
});
console.log("下载完成 " + data.length + " 条数据");',
'JavaScript', 2, 234, 45, 29, 1, 1);

-- ============================
-- 片段-标签关联（新增片段 ID 11~25 对应的标签）
-- 标签 ID 映射：16=TypeScript, 17=设计模式, 18=并发, 19=正则表达式,
--   20=性能优化, 21=Go, 22=C/C++, 23=单元测试, 24=安全, 25=Nginx,
--   26=算法, 27=多线程, 28=缓存策略, 29=接口设计, 30=数据结构
-- 旧标签：1=Spring Boot, 2=MyBatis, 3=Vue, 4=React, 5=MySQL,
--   6=Redis, 7=Docker, 8=Python, 9=JavaScript, 10=Java,
--   11=API, 12=排序, 13=动态规划, 14=Linux, 15=Git
-- ============================
INSERT IGNORE INTO `snippet_tag` (`snippet_id`, `tag_id`) VALUES
(11, 10), (11, 27),            -- Java线程池 -> Java, 多线程
(11, 18),                       -- Java线程池 -> 并发
(12, 10), (12, 17),            -- 单例模式 -> Java, 设计模式
(13, 10), (13, 11),            -- 全局异常处理 -> Java, API
(14, 2), (14, 10),            -- MyBatis-Plus批量插入 -> MyBatis, Java
(14, 20),                       -- MyBatis-Plus批量插入 -> 性能优化
(15, 8),                        -- Python装饰器 -> Python
(16, 9), (16, 30),             -- JS深拷贝 -> JavaScript, 数据结构
(17, 5),                        -- MySQL窗口函数 -> MySQL
(18, 6), (18, 10),            -- Redis分布式锁 -> Redis, Java
(18, 18),                       -- Redis分布式锁 -> 并发
(19, 21), (19, 18),            -- Go并发下载 -> Go, 并发
(20, 10), (20, 20),            -- Java Stream API -> Java, 性能优化
(21, 25),                       -- Nginx配置 -> Nginx
(21, 7),                        -- Nginx配置 -> Docker
(22, 8), (22, 26),             -- Python二分查找 -> Python, 算法
(23, 22), (23, 26),            -- C链表反转 -> C/C++, 算法
(23, 30),                       -- C链表反转 -> 数据结构
(24, 10), (24, 17),            -- 参数校验 -> Java, 设计模式
(24, 24),                       -- 参数校验 -> 安全
(25, 9), (25, 18);             -- Promise并发控制 -> JavaScript, 并发

-- ============================
-- 额外点赞数据
-- ============================
INSERT IGNORE INTO `like` (`user_id`, `snippet_id`) VALUES
(1, 11), (1, 13), (1, 18), (1, 23), (1, 20),
(2, 12), (2, 14), (2, 17), (2, 21), (2, 16),
(3, 11), (3, 15), (3, 19), (3, 22), (3, 25), (3, 24);

-- ============================
-- 额外收藏数据
-- ============================
INSERT IGNORE INTO `collection` (`user_id`, `snippet_id`) VALUES
(1, 12), (1, 18), (1, 21), (1, 23),
(2, 11), (2, 15), (2, 20), (2, 24),
(3, 13), (3, 19), (3, 25), (3, 14);