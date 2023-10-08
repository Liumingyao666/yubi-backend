# 智能BI项目文档

## 项目介绍

BI（商业智能）：数据可视化，报表可视化系统

主流BI平台：帆软BI，小马BI，微软Power BI

传统BI平台：

1. 需要人工上传数据
2. 需要人工托选分析要用到的数据行和列（数据分析师）
3. 需要人工选择图表类型（数据分析师）
4. 生成图表并保存配置

智能BI平台：

区别于传统的BI，用户（数据分析者）只需要导入最原始的数据集，输入想要进行分析的目标（比如帮我分析一下网站的增长趋势），就能利用AI自动生成一个符合要求的图标以及结论。

优点：让不会数据分析的同学也能通过输入目标快速完成数据分析，大幅节约人力成本。

会用到AI接口。



## 需求分析

1. 智能分析：用户输入目标和原始数据（图表类型），可以自动生成图表和分析结论
2. 图表管理
3. 图表生成的异步化（消息队列）
4. 对接AI能力



## 架构图

基础流程：客户端输入分析诉求和原始数据，向业务后端发送请求。业务后端利用 AI 服务处理客户端数据，保持到数据库，并生成图表。处理后的数据由业务后端发送给 AI 服务，AI 服务生成结果并返回给后端，最终将结果返回给客户端展示。

![image-20231005150042006](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231005150042006.png)



假设一个 AI 服务生成图表和分析结果要等 50 秒，如果有大量用户需要生成图表，每个人都需要等待 50 秒，那么 AI 服务可能无法承受这种压力。为了解决这个问题，可以采用消息队列技术。通过消息队列，用户可以提交生成图表的请求，这些请求会进入队列，AI 服务会依次处理队列中的请求，从而避免了同时处理大量请求造成的压力，同时也能更好地控制资源的使用。

优化流程（异步化）：客户端输入分析诉求和原始数据，向业务后端发送请求。业务后端将请求事件放入消息队列，并为客户端生成取餐号，让要生成图表的客户端去排队，消息队列根据 AI 服务负载情况，定期检查进度，如果 AI 服务还能处理更多的图表生成请求，就向任务处理模块发送消息。
任务处理模块调用 AI 服务处理客户端数据，AI 服务异步生成结果返回给后端并保存到数据库，当后端的 AI 服务生成完毕后，可以通过向前端发送通知的方式，或者通过业务后端监控数据库中图表生成服务的状态，来确定生成结果是否可用。若生成结果可用，前端即可获取并处理相应的数据，最终将结果返回给客户端展示。（在此期间，用户可以去做自己的事情）

![image-20231005150112288](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231005150112288.png)



## 技术栈

前端

1. React
2. Umi + Ant Design Pro
3. 可视化开发库（Echarts + HighCharts + AntV）
4. umi openapi代码生成（自动生成后端调用代码）

后端：

1. Spring Boot（万用Java后端项目模板，快速搭建基础框架  https://github.com/Liumingyao666/springboot-init）
2. MySQL数据库
3. MyBatis Plus数据访问框架
4. 消息队列 RabbitMQ
5. AI能力
6. Excel的上传和数据的解析（Easy Excel）
7. Swagger + Knife4j项目接口文档
8. Hutool工具库



## 项目计划

### Day 1

前端项目初始化

后端项目初始化

前端开发

- 快速开发登录功能
- 图标分析页面的开发
- 图标管理页面的开发

后端开发

- 库表设计
- 图表管理开发
- 文件上传接口开发

前后端业务流程跑通



#### 前端项目初始化

官方文档：https://pro.ant.design/

Node.js18版本

1. 官网下载：https://nodejs.org/en （选则LTS稳定版本）

2. **nvm工具可以快捷切换node.js版本** https://nvm.uihtm.com/download.html

​		安装node.js版本

​		`nvm -v` 查看nvm版本

​		`nvm list available`显示可下载版本的部分列表

​		`nvm install latest`安装最新版本

​		`nvm install` 版本号 安装指定的node.js版本

​		`nvm list`或`nvm ls`查看目前已经安装的版本

​		`nvm use`版本号 使用指定版本的node.js

nvm切换国内镜像 （切换之后再安装nodejs）

阿里云镜像

~~~bash
nvm npm_mirror https://npmmirror.com/mirrors/npm/
nvm node_mirror https://npmmirror.com/mirrors/node/
~~~

腾讯云镜像

~~~
nvm npm_mirror http://mirrors.cloud.tencent.com/npm/
nvm node_mirror http://mirrors.cloud.tencent.com/nodejs-release/
~~~



步骤：

1. 按照官方文档初始化
2. 项目试允许（npm run dev/start）
3. 代码托管
4. 移除不需要的能力（比如国际化）

移除国际化问题解决：

> 任何开源项目的报错，都可以直接问作者（官方团队）或者搜 github issues 区
>
> 1. 找到开源地址：https://github.com/ant-design/ant-design-pro
> 2. 搜索issues：https://github.com/ant-design/ant-design-pro/issues/10452
> 3. 前端本地执行：yarn add eslint-config-prettier --dev yarn add eslint-plugin-unicorn --dev
> 4. 修改 node_modules/@umijs/lint/dist/config/eslint/index.js， 注释掉 es2022

路由不显示名称

> 给 config/route.ts 的路由加 name



#### 后端初始化

使用后端项目模板： https://github.com/Liumingyao666/springboot-init

用法参考README.md，默认情况下只要执行 create_table.sql 文件创建数据库表，然后修改 application.yml 中的数据库地址为你自己的数据库即可。

端口占用问题

> 查看进程
>
> netstat -ano|findstr xxx
>
> 关闭进程
>
> taskkill -pid xxx-f



#### 库表设计

--图表信息表

~~~sql
-- 图表表
create table if not exists chart
(
   id           bigint auto_increment comment 'id' primary key,
   goal				 text  null comment '分析目标',
   chartData    text  null comment '图表数据',
   chartType	   varchar(128) null comment '图表类型',
   genChart		 text	 null comment '生成的图表数据',
   genResult		 text	 null comment '生成的分析结论',
   userId       bigint null comment '创建用户 id',
   createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
   updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
   isDelete     tinyint      default 0                 not null comment '是否删除'
   ) comment '图表信息表' collate = utf8mb4_unicode_ci;
~~~



#### 前后端基础开发

1. 图表信息的crud

2. 前端调用后端：

   使用 ant design pro 自带的 openapi 工具，根据后端的 swagger 接口文档数据自动生成对应的请求 service 代码。

   ~~~js
   openAPI: [
       {
         requestLibPath: "import { request } from '@umijs/max'",
         // 或者使用在线的版本
         schemaPath: "http://localhost:8101/api/v2/api-docs",
         projectName: 'yubi',
         // schemaPath: join(__dirname, 'oneapi.json'),
         mock: false,
       },
   ~~~

   注意：前端须更改对应的请求地址为你的后端地址，方法：在 app.tsx 里修改 request.baseURL

   ~~~js
   /**
    * app.tsx文件
    * @name request 配置，可以配置错误处理
    * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
    * @doc https://umijs.org/docs/max/request#配置
    */
   export const request = {
     baseURL: 'http://localhost:8101',
     withCredentials: true,
     ...errorConfig,
   };
   ~~~



### Day2

#### 后端启动项目端口冲突问题解决

原因：Windows Hyper-V 虚拟化平台占用了端口

先使用：netsh interface ipv4 show excludedportrange protocol=tcp 查看被占用的端口，然后选择一个没被占用的端口启动项目



#### 智能分析业务开发

业务流程：

1. 用户输入
   - 分析目标
   - 上传原始数据（excel）
   - 更精细化的控制图表：比如图表类型，图表名称等
2. 后端校验
   - 校验用户的输入是否合法（比如长度）
   - 成本控制（次数统计和校验，鉴权等）
3. 把处理后的数据输入给AI模型（调用AI接口），让AI模型给我们提供图标信息，结论文本
4. 调用时加系统预设（提前告诉他职责、功能、回复格式要求） + 分析目标 + 压缩后的数据
5. 图标信息（是一段json配置，是一段代码），结论文本在前端进行展示



#### 使用csv对excel文件的数据进行提取和压缩

开源库：https://easyexcel.opensource.alibaba.com/docs/current/

~~~java
 /**
     * excel 转 csv
     *
     * @param multipartFile
     * @return
     */
    public static String excelToCsv(MultipartFile multipartFile){
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("excel读取失败", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }

        // 转化为csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(data -> ObjectUtils.isNotEmpty(data)).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }
~~~

~~~java
 /**
     * 智能分析
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
     String name = genChartByAiRequest.getName();
     String goal = genChartByAiRequest.getGoal();
     String chartType = genChartByAiRequest.getChartType();

     // 校验
     ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
     ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

     // 用户输入
     StringBuilder userInput = new StringBuilder();
     userInput.append("你是一名数据分析师，接下来我会给你我的分析目标和原始数据，请告诉我分析结论").append("\n");
     userInput.append("分析目标: ").append(goal).append("\n");
     //压缩后的数据
     String result = ExcelUtils.excelToCsv(multipartFile);
     userInput.append("原始数据: ").append(result).append("\n");
     return ResultUtils.success(userInput.toString());


    }
~~~





### Day3

#### 3种AI调用方式

1. 直接调用官方接口

   比如OpenAI或者其他AI原始大模型官网的接口

   官方文档：https://platform.openai.com/docs/api-reference

   优点：不经封装，最灵活，最原始

   缺点：要钱，要魔法

   本质上OpenAI就是提供了HTTP接口，我们可以用任何语言去调用

   1）在请求头中指定OPENAI_API_KEY

   Authorization: Bearer OPENAI_API_KEY

   2）找到你要使用的接口，比如 AI 对话接口：https://platform.openai.com/docs/api-reference/chat/create

   3）按照接口文档的示例，构造 HTTP 请求，比如用 Hutool 工具类、或者 HTTPClient

~~~java
/**
 * AI 对话（需要自己创建请求响应对象）
 *
 * @param request
 * @param openAiApiKey
 * @return
 */
public CreateChatCompletionResponse createChatCompletion(CreateChatCompletionRequest request, String openAiApiKey) {
    if (StringUtils.isBlank(openAiApiKey)) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未传 openAiApiKey");
    }
    String url = "https://api.openai.com/v1/chat/completions";
    String json = JSONUtil.toJsonStr(request);
    String result = HttpRequest.post(url)
    .header("Authorization", "Bearer " + openAiApiKey)
    .body(json)
    .execute()
    .body();
    return JSONUtil.toBean(result, CreateChatCompletionResponse.class);
}
~~~



2. 使用云服务商提供的封装接口

   比如：Azure云

   优点：本地都能用

   缺点：依然要钱，而且可能比调用原始接口更贵



3. 鱼聪明AI开发平台

   鱼聪明 AI 提供了现成的 SDK 来让大家更方便地使用 AI 能力。

   鱼聪明 AI 网站：https://yucongming.com/

   参考 sdk 项目文档快速使用：https://github.com/liyupi/yucongming-java-sdk



#### 智能接口实现

后端流程

1. 构造用户请求（用户消息，csv数据， 图表类型）
2. 调用鱼聪明sdk，得到AI的响应结果
3. 从AI响应结果中，取出需要的信息
4. 保存图表到数据库



### Day4

#### 系统优化

现在的网站足够安全吗？

1. 如果用户上传一个超大的文件怎么办？
2. 如果用户用科技疯狂点击提交，怎么办？
3. 如果AI的生成太慢（比如需要一分钟），又有很多用户要同时生成，给系统造成了压力，怎么兼顾用户体验和系统的可用性？



安全性

如果用户上传一个超大的文件怎么办？比如 1000 G？

**只要涉及到用户自主上传的操作，一定要校验文件（图像）**

校验的维度：

- 文件的大小
- 文件的后缀
- 文件的内容（成本要高一些）
- 文件的合规性（比如敏感内容，建议用第三方的审核功能）

~~~java
		// 文件校验
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();

        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过1MB");

        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
~~~





扩展点：接入腾讯云的图片万象数据审核（COS 对象存储的审核功能）





#### 数据存储

现状：我们把每个图表的原始数据全部存放在了同一个数据表（chart 表）的字段里。

问题：

1. 如果用户上传的原始数据量很大、图表数日益增多，查询 Chart 表就会很慢。

2. 对于 BI 平台，用户是有查看原始数据、对原始数据进行简单查询的需求的。现在如果把所有数据存放在一个

   字段（列）中，查询时，只能取出这个列的所有内容。



解决方案 => 分库分表：

把每个图表对应的原始数据单独保存为一个新的数据表，而不是都存在一个字段里。

优点：

1. 存储时，能够分开存储，互不影响（也能增加安全性）
2. 查询时，可以使用各种sql语句灵活取出需要的字段，插叙性能更快



实现

分开存储：

1. 存储图表信息时，不把数据存储为字段，而是新建一个chart_{图表id} 的数据表

通过图表 id、数据列名、数据类型等字段，生成以下 SQL 语句，并且执行即可：

~~~sql
-- auto-generated definition
create table chart_1659210482555121666
(
    日期  int null,
    用户数 int null
);
~~~



分开查询

1. 以前直接查询图表，取 chartData 字段；现在改为读取 chart_{图表id} 的数据表

~~~sql
select * from chart_{1659210482555121666}
~~~



具体实现：MyBatis的动态SQL（根据代码灵活的动态生成）

1. 想清楚哪些是需要动态替换的，比如要查询的数据表名 chart_{1659210482555121666}
2. 在 ChartMapper.xml 中定义 sql 语句

以下这种方式最灵活，但是要小心 SQL 注入风险：

比如：select * from chart_12345 where id = 1 or 1 = 1;

~~~xml
<select id="queryChartData" parameterType="string" resultType="map">
    ${querySql}
</select>
~~~



在 ChartMapper 中定义方法，方法名和上一步的 select 的 id 相同：

~~~java
List<Map<String, Object>> queryChartData(String querySql);
~~~



测试调用

~~~java
 String chartId = "1659210482555121666";
        String querySql = String.format("select * from chart_%s", chartId);
        List<Map<String, Object>> resultData = chartMapper.queryChartData(querySql);
        System.out.println(resultData);
~~~



分库分表

- 水平分表
- 垂直分库在大型互联网应用中，为了应对高并发、海量数据等挑战，往往需要对数据库进行拆分。常见的拆分方式有水平分表和垂直分库两种。

水平分表（Sharding）
水平分表是将同一张表中的数据按一定的规则划分到不同的物理存储位置上，以达到分摊单张表的数据及访问压力的目的。对于SQL分为两类：id-based分表和range-based分表。

水平分表的优点：

- 单个表的数据量减少，查询效率提高；
- 可以通过增加节点，提高系统的扩展性和容错性。

水平分表的缺点：

- 事务并发处理复杂度增加，需要增加分布式事务的管理，性能和复杂度都有所牺牲；
- 跨节点查询困难，需要设计跨节点的查询模块。

垂直分库（Vertical Partitioning）
垂直分库，指的是根据业务模块的不同，将不同的字段或表分到不同的数据库中。垂直分库基于数据库内核支持，对应用透明，无需额外的开发代码，易于维护升级。

垂直分库的优点：

- 减少单个数据库的数据量，提高系统的查询效率
- 增加了系统的可扩展性，比水平分表更容易实现。

垂直分库的缺点：

- 不同数据库之间的维护和同步成本较高；
- 现有系统的改造存在一定的难度；
- 系统的性能会受到数据库之间互相影响的影响。

需要根据实际的业务场景和技术架构情况，综合考虑各种因素来选择适合自己的分库分表策略。



#### 限流

现在的问题：使用系统是需要消耗成本的，用户有可能疯狂刷量，让你破产。

解决问题：

1. 控制成本 => 限制用户调用总次数
2. 用户在短时间内疯狂使用，导致服务器资源被占满，其他用户无法使用 => 限流

思考限流阈值多大合适？参考正常用户的使用，比如限制单个用户在每秒只能使用 1 次。



限流的几种算法

建议阅读文章：https://juejin.cn/post/6967742960540581918

1）固定窗口限流
单位时间内允许部分操作
1 小时只允许 10 个用户操作

优点：最简单
缺点：可能出现流量突刺
比如：前 59 分钟没有 1 个操作，第 59 分钟来了 10 个操作；第 1 小时 01 分钟又来了 10 个操作。相当于 2 分钟内执行了 20 个操作，服务器仍然有高峰危险。

2）滑动窗口限流
单位时间内允许部分操作，但是这个单位时间是滑动的，需要指定一个滑动单位
比如滑动单位 1min：
开始前：
0s      1h     2h
一分钟后：
1min   1h1min

优点：能够解决上述流量突刺的问题，因为第 59 分钟时，限流窗口是 59 分 ~ 1小时 59 分，这个时间段内只能接受 10 次请求，只要还在这个窗口内，更多的操作就会被拒绝。
缺点：实现相对复杂，限流效果和你的滑动单位有关，滑动单位越小，限流效果越好，但往往很难选取到一个特别合适的滑动单位。

3）漏桶限流（推荐）
以 固定的速率 处理请求（漏水），当请求桶满了后，拒绝请求。
每秒处理 10 个请求，桶的容量是 10，每 0.1 秒固定处理一次请求，如果 1 秒内来了 10 个请求；都可以处理完，但如果 1 秒内来了 11 个请求，最后那个请求就会溢出桶，被拒绝。

优点：能够一定程度上应对流量突刺，能够固定速率处理请求，保证服务器的安全
缺点：没有办法迅速处理一批请求，只能一个一个按顺序来处理（固定速率的缺点）

4）令牌桶限流（推荐）
管理员先生成一批令牌，每秒生成 10 个令牌；当用户要操作前，先去拿到一个令牌，有令牌的人就有资格执行操作、能同时执行操作；拿不到令牌的就等着

优点：能够并发处理同时的请求，并发性能会更高
需要考虑的问题：还是存在时间单位选取的问题





限流粒度

1. 针对某个方法限流，即单位时间内最多允许同时 XX 个操作使用这个方法
2. 针对某个用户限流，比如单个用户单位时间内最多执行 XX 次操作
3. 针对某个用户 x 方法限流，比如单个用户单位时间内最多执行 XX 次这个方法





限流的实现

Redisson 内置了一个限流工具类，可以帮助你利用 Redis 来存储、来统计。

官方项目仓库和文档：https://github.com/redisson/redisson



步骤：

1. 引入 Redisson 代码包：

~~~xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.21.3</version>
</dependency>
~~~

2. 创建 RedissonConfig 配置类，用于初始化 RedissonClient 对象单例：

~~~java
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
        .setDatabase(database)
        .setAddress("redis://" + host + ":" + port);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
~~~

3. 编写 RedisLimiterManager：

什么是 Manager？专门提供 RedisLimiter 限流基础服务的（ 提供了通用的能力，可以放到任何一个项目里 ）

~~~java
/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
~~~

4. 单元测试：

~~~java
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        String userId = "1";
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }
}
~~~

5. 应用到要限流的方法中，比如智能分析接口：

~~~java
User loginUser = userService.getLoginUser(request);
// 限流判断，每个用户一个限流器
redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
// 后续操作
xxxxx
~~~





### Day5

1. 系统问题分析
2. 异步化业务流程分析
3. 线程池
4. 系统异步化改造开发



#### 系统问题分析

问题场景：调用的服务处理能力有限，或者接口的处理（或返回）时长较长时，就应该考虑异步化了。

1. 用户等待时间有点长（因为要等 AI 生成）
2. 业务服务器可能会有很多请求在处理，导致系统资源紧张，严重时导致服务器宕机或者无法处理新的请求
3. 调用的第三方服务（AI 能力）的处理能力是有限的，比如每 3 秒只能处理 1 个请求，会导致 AI 处理不过来，严重时 AI 可能会对咱们的后台系统拒绝服务。



#### 业务流程分析

标准异步化的业务流程

1. 当用户要进行耗时很长的操作时，点击提交后，不需要在界面傻等，而是应该把这个任务保存到数据库中记录下来
2. 用户要执行新任务时：

​		a任务提交成功：

​		ⅰ如果我们的程序还有多余的空闲线程，可以立刻去做这个任务

​		 ⅱ如果我们的程序的线程都在繁忙，无法继续处理，那就放到等待队列里

​		b任务提交失败：比如我们的程序所有线程都在忙， 任务队列满了

​		 ⅰ拒绝掉这个任务，再也不去执行

​		 ⅱ通过保存到数据库中的记录来看到提交失败的任务，并且在程序闲的时候，可以把任务从数据库中捞到程			序里，再去执行

3. 我们的程序（线程）从任务队列中取出任务依次执行，每完成一件事情要修改一下的任务的状态。

4. 用户可以查询任务的执行状态，或者在任务执行成功或失败时能得到通知（发邮件、系统消息提示、短信），从而优化体验

5. 如果我们要执行的任务非常复杂，包含很多环节，在每一个小任务完成时，要在程序（数据库中）记录一下任务的执行状态（进度）



我们系统的业务流程

1. 用户点击智能分析页的提交按钮时，先把图表立刻保存到数据库中（作为一个任务）
2. 用户可以在图表管理页面查看所有图表（已生成的、生成中的、生成失败）的信息和状态
3. 用户可以修改生成失败的图表信息，点击重新生成



优化流程（异步化）：

![image-20231008211712048](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231008211712048.png)

问题：

1. 任务队列的最大容量应该设置为多少？
2. 程序怎么从任务队列中取出任务去执行？这个任务队列的流程怎么实现？怎么保证程序最多同时执行多少个任务？



#### 线程池

为什么需要线程池？

1. 线程的管理比较复杂（比如什么时候新增线程、什么时候减少空闲线程）
2. 任务存取比较复杂（什么时候接受任务、什么时候拒绝任务、怎么保证大家不抢到同一个任务）

线程池的作用：帮助你轻松管理线程、协调任务的执行过程。



线程池的实现

不用自己写，如果是在 Spring 中，可以用 ThreadPoolTaskExecutor 配合 @Async 注解来实现。（不太建议）

如果是在 Java 中，可以使用 JUC 并发编程包中的 ThreadPoolExecutor 来实现非常灵活地自定义线程池。



线程池参数

~~~java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {}
~~~



怎么确定线程池参数呢？结合实际情况（实际业务场景和系统资源）来测试调整，不断优化。

回归到我们的业务，要考虑系统最脆弱的环节（系统的瓶颈）在哪里？
现有条件：比如 AI 生成能力的并发是只允许 4 个任务同时去执行，AI 能力允许 20 个任务排队。

corePoolSize（核心线程数 => 正式员工数）：正常情况下，我们的系统应该能同时工作的线程数（随时就绪的状态）

maximumPoolSize（最大线程数 => 哪怕任务再多，你也最多招这些人）：极限情况下，我们的线程池最多有多少个线程？

keepAliveTime（空闲线程存活时间）：非核心线程在没有任务的情况下，过多久要删除（理解为开除临时工），从而释放无用的线程资源。

TimeUnit unit（空闲线程存活时间的单位）：分钟、秒

workQueue（工作队列）：用于存放给线程执行的任务，存在一个队列的长度（一定要设置，不要说队列长度无限，因为也会占用资源）

threadFactory（线程工厂）：控制每个线程的生成、线程的属性（比如线程名）

RejectedExecutionHandler（拒绝策略）：任务队列满的时候，我们采取什么措施，比如抛异常、不抛异常、自定义策略

>  资源隔离策略：比如重要的任务（VIP 任务）一个队列，普通任务一个队列，保证这两个队列互不干扰。





线程池的参数如何设置？

现有条件：比如 AI 生成能力的并发是只允许 4 个任务同时去执行，AI 能力允许 20 个任务排队。

corePoolSize（核心线程数 => 正式员工数）：正常情况下，可以设置为 2 - 4

maximumPoolSize：设置为极限情况，设置为 <= 4

keepAliveTime（空闲线程存活时间）：一般设置为秒级或者分钟级

TimeUnit unit（空闲线程存活时间的单位）：分钟、秒

workQueue（工作队列）：结合实际请况去设置，可以设置为 20

threadFactory（线程工厂）：控制每个线程的生成、线程的属性（比如线程名）

RejectedExecutionHandler（拒绝策略）：抛异常，标记数据库的任务状态为 “任务满了已拒绝”

一般情况下，任务分为 IO 密集型和计算密集型两种。

计算密集型：吃 CPU，比如音视频处理、图像处理、数学计算等，一般是设置 corePoolSize 为 CPU 的核数 + 1（空余线程），可以让每个线程都能利用好 CPU 的每个核，而且线程之间不用频繁切换（减少打架、减少开销）

IO 密集型：吃带宽/内存/硬盘的读写资源，corePoolSize 可以设置大一点，一般经验值是 2n 左右，但是建议以 IO 的能力为主。





考虑导入百万数据到数据库，属于 IO 密集型任务、还是计算密集型任务？

IO密集型；导入数据到数据库涉及到大量的I/O操作，需要读取源数据文件并将其写入目标数据库中，尤其是百万条记录的数据导入，需要频繁的磁盘读写操作。因此，该任务需要大量的磁盘l/O和网络传输，而不涉及太多的计算操作。
相比之下，计算密集型任务更侧重于对数据进行大量的计算，例如复杂的数学运算、模拟、统计分析和图像处理等任务，这些任务需要耗费大量的CPU和内存资源。



开发

自定义线程池：

~~~java
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                count++;
                return thread;
            }
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4), threadFactory);
        return threadPoolExecutor;
    }
}
~~~





#### 图表异步化开发

实现工作流程

1. 给 chart 表新增任务状态字段（比如排队中、执行中、已完成、失败），任务执行信息字段（用于记录任务执行中、或者失败的一些信息）
2. 用户点击智能分析页的提交按钮时，先把图表立刻保存到数据库中，然后提交任务
3. 任务：先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
4. 用户可以在图表管理页面查看所有图表（已生成的、生成中的、生成失败）的信息和状态



库表设计

chart表新增字段：

~~~sql
status       varchar(128) not null default 'wait' comment 'wait,running,succeed,failed',
execMessage  text   null comment '执行信息',
~~~



任务执行逻辑

先修改任务状态为执行中，减少重复执行的风险、同时让用户知道执行状态。

~~~java
CompletableFuture.runAsync(() -> {
    // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
    Chart updateChart = new Chart();
    updateChart.setId(chart.getId());
    updateChart.setStatus("running");
    boolean b = chartService.updateById(updateChart);
    if (!b) {
        handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
        return;
    }
    // 调用 AI
    String result = aiManager.doChat(biModelId, userInput.toString());
    String[] splits = result.split("【【【【【");
    if (splits.length < 3) {
        handleChartUpdateError(chart.getId(), "AI 生成错误");
        return;
    }
    String genChart = splits[1].trim();
    String genResult = splits[2].trim();
    Chart updateChartResult = new Chart();
    updateChartResult.setId(chart.getId());
    updateChartResult.setGenChart(genChart);
    updateChartResult.setGenResult(genResult);
    updateChartResult.setStatus("succeed");
    boolean updateResult = chartService.updateById(updateChartResult);
    if (!updateResult) {
        handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
    }
}, threadPoolExecutor);
~~~



#### @TODO

1. 给任务的执行增加 guava Retrying 重试机制，保证系统可靠性。
2. 提前考虑到 AI 生成错误的情况，在后端进行异常处理（比如 AI 说了多余的话，提取正确的字符串）
3. 如果说任务根本没提交到队列中（或者队列满了），是不是可以用定时任务把失败状态的图表放到队列中（补偿）
4. 建议给任务的执行增加一个超时时间，超时自动标记为失败（超时控制）
5. 反向压力：https://zhuanlan.zhihu.com/p/404993753，通过调用的服务状态来选择当前系统的策略（比如根据 AI 服务的当前任务队列数来控制咱们系统的核心线程数），从而最大化利用系统资源。
6. 我的图表页面增加一个刷新、定时自动刷新的按钮，保证获取到图表的最新状态（前端轮询）
7. 任务执行成功或失败，给用户发送实时消息通知（实时：websocket、server side event）









































