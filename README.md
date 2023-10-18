# 智能BI项目文档

## 项目效果预览

登录界面

![image-20231018163437013](https://github.com/Liumingyao666/github-img/blob/master/img/image-20231018163437013.png?raw=true)



智能分析界面

![image-20231018163608684](https://github.com/Liumingyao666/github-img/blob/master/img/image-20231018163608684.png?raw=true)

智能分析（异步）界面

![image-20231018163652897](https://github.com/Liumingyao666/github-img/blob/master/img/image-20231018163652897.png?raw=true)

异步任务会给前端一个状态（图表生成中），在图表管理页面可以预览生成状态，等待AICG平台处理。

![image-20231018163742361](https://github.com/Liumingyao666/github-img/blob/master/img/image-20231018163742361.png?raw=true)

图表管理页面

查看已生成的表格

![image-20231018163823936](https://github.com/Liumingyao666/github-img/blob/master/img/image-20231018163823936.png?raw=true)





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





### Day6

1. 分析系统的不足
2. 分布式消息队列
3. 分布式消息队列RabbitMQ入门实战



#### 分析系统现状不足

> 单机系统的问题

已经经过了同步到异步的改造？

现状：目前的异步是通过本地的线程池实现的。



1. 无法集中限制，智能单机限制

   假如AI服务限制智能有2个用户同时使用，单个线程池可以限制最大核心线程数为2来实现。

   假设系统用户量增大，改为分布式，多台服务器，每个服务器都要有2个线程，就有可能有2N个线程，超过了AI服务的限制。

2. 任务由于是放在内存中执行的，可能会丢失

   虽然可以人工从数据库捞出来再重试，但是其实需要额外开发（比如定时任务）。这种重试的场景是非常典型的，其实是不需要我们开发者过于关心，或者自己实现的。

   解决方案：把任务放在一个可以持久化存储的硬盘

3. 优化：如果你的系统功能越来越多，长耗时任务越来越多，系统会越来越复杂（比如要开多个线程池，资源可能会出现项目抢占）

   服务拆分（应用解耦）：其实我们可以把长耗时，消耗很多的任务把它单独抽成一个程序，不要影响主业务。

   解决方案：可以有一个中间人，让中间人帮我们去连接两个系统（比如核心系统和智能生成业务）



#### 分布式消息队列

中间件

连接多个系统，帮助多个系统紧密协作的技术（或者组件）。

比如：Redis,消息队列，分布式存储Etcd

![image-20231014100213687](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014100213687.png)



消息队列

存储消息的队列。

关键词：存储、消息、队列

存储：存数据

消息：某种数据结构，比如字符串、对象、二进制数据、json 等等

队列：先进先出的数据结构

消息队列是特殊的数据库么？可以这么理解。

应用场景（作用）：在多个不同的系统、应用之间实现消息的传输（也可以存储）。不需要考虑传输应用的编程语言、系统、框架等等。

可以让 java 开发的应用发消息，让 php 开发的应用收消息，这样就不用把所有代码写到同一个项目里（应用解耦）。



消息队列的模型

生产者：Producer，类比为快递员，发送消息的人（客户端）
消费者：Consumer，类比为取快递的人，接受读取消息的人（客户端）
消息：Message，类比为快递，就是生产者要传输给消费者的数据
消息队列：Queue

为什么不接传输，要用消息队列？生产者不用关心你的消费者要不要消费、什么时候消费，我只需要把东西给消息队列，我的工作就算完成了。
生产者和消费者实现了解耦，互不影响。

![image-20231014100409701](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014100409701.png)



为什么要用消息队列？

1）异步处理
生产者发送完消息之后，可以继续去忙别的，消费者想什么时候消费都可以，不会产生阻塞。
2）削峰填谷
先把用户的请求放到消息队列中，消费者（实际执行操作的应用）可以按照自己的需求，慢慢去取。
原本：12 点时来了 10 万个请求，原本情况下，10万个请求都在系统内部立刻处理，很快系统压力过大就宕机了。
现在：把这 10万个请求放到消息队列中，处理系统以自己的恒定速率（比如每秒 1 个）慢慢执行，从而保护系统、稳定处理。



分布式消息队列的优势

1）数据持久化：它可以把消息集中存储到硬盘里，服务器重启就不会丢失
2）可扩展性：可以根据需求，随时增加（或减少）节点，继续保持稳定的服务
3）应用解耦：可以连接各个不同语言、框架开发的系统，让这些系统能够灵活传输读取数据

应用解耦的优点：

以前，把所有功能放到同一个项目中，调用多个子功能时，一个环节错，系统就整体出错：

![image-20231014100726391](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014100726391.png)



使用消息队列进行解耦：

1. 一个系统挂了，不影响另一个系统

2. 系统挂了并恢复后，仍然可以取出信息，继续执行业务逻辑

3. 只要发送消息到队列，就可以立刻返回，不用同步调用所有系统，性能更高

   ![image-20231014100959870](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014100959870.png)

4）发布订阅

如果一个非常大的系统要给其他子系统发送通知，最简单直接的方式是大系统直接依次调用小系统：

问题：

1. 每次发通知都要调用很多系统，很麻烦，有可能失败
2. 新出现的项目（或者说大项目感知不到的项目）无法得到通知

![image-20231014101315142](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014101315142.png)



解决方案：大的核心系统始终往一个地方（消息队列）去发消息，其它系统都去订阅这个消息队列（读取这个消息队列中的信息）

![image-20231014101445433](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014101445433.png)



应用场景

1. 耗时的场景（异步）
2. 高并发场景（异步、削峰填谷）
3. 分布式系统协作（尤其是跨团队、跨业务协作，应用解耦）
4. 强稳定性的场景（比如金融业务，持久化、可靠性、削峰填谷）



消息队列的缺点

要给系统引入额外的中间件，系统会更复杂、额外维护中间件、额外的费用（部署）成本

消息队列：消息丢失、顺序性、重复消费、数据的一致性（分布式系统就要考虑）

也可以叫分布式场景下需要考虑的问题





主流分布式消息队列选型

主流技术

1. activemq
2. rabbitmq
3. kafka
4. rocketmq
5. zeromq
6. pulsar（云原生）
7. Apache InLong (Tube)





#### RabbitMQ入门实战

特点：生态好，好学习、易于理解，时效性强，支持很多不同语言的客户端，扩展性、可用性都很不错。
学习性价比非常高的消息队列，适用于绝大多数中小规模分布式系统。

官方网站：[https://www.rabbitmq.com](https://www.rabbitmq.com/getstarted.html)



基本概念
AMQP 协议：https://www.rabbitmq.com/tutorials/amqp-concepts.html
高级消息队列协议（Advanced Message Queue Protocol）

生产者：发消息到某个交换机
消费者：从某个队列中取消息
交换机（Exchange）：负责把消息 转发 到对应的队列
队列（Queue）：存储消息的
路由（Routes）：转发，就是怎么把消息从一个地方转到另一个地方（比如从生产者转发到某个队列）

![img](D:\liumingyao\study\文档\智能BI项目文档.assets\1686835103366-bdb220cd-b177-4f41-982d-8451e5f6ebfe.png)







快速入门

MQ官方教程：https://www.rabbitmq.com/getstarted.html



单向发送

> Hello World

文档：https://www.rabbitmq.com/tutorials/tutorial-one-java.html

一个生产者给一个队列发消息，一个消费者从这个队列取消息。1对1

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686836521822-053f7420-498d-4539-9721-ae0bc6e5b012.png)

引入消息队列Java客户端：

~~~xml
<!-- https://mvnrepository.com/artifact/com.rabbitmq/amqp-client -->
<dependency>
  <groupId>com.rabbitmq</groupId>
  <artifactId>amqp-client</artifactId>
  <version>5.17.0</version>
</dependency>
~~~



生产者代码

~~~java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Send {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, 				message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
~~~

Channel 频道：理解为操作消息队列的 client（比如 jdbcClient、redisClient），提供了和消息队列 server 建立通信的传输方法（为了复用连接，提高传输效率）。程序通过 channel 操作 rabbitmq（收发消息）



创建消息队列：

参数：

queueName：消息队列名称（注意，同名称的消息队列，只能用同样的参数创建一次）

durabale：消息队列重启后，消息是否丢失

exclusive：是否只允许当前这个创建消息队列的连接操作消息队列

autoDelete：没有人用队列后，是否要删除队列



执行程序后，可以看到有1条消息：

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686837082571-81894d7d-eaac-444e-9ccd-601d226f2c9c.png)



消费者代码：

~~~java
public class SingleConsumer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        // 定义了如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 消费消息，会持续阻塞
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
~~~

启动消费者后，可以看到消息被消费了：
![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686837388735-5aead597-0ee7-4221-81a2-9f41f4666c07.png)







多消费者

官方教程：https://www.rabbitmq.com/tutorials/tutorial-two-java.html

场景：多个机器同时去接收并处理任务（尤其是每个机器的处理能力有限）

一个生产者给一个队列发消息，多个消费者从这个队列取消息。1对多

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686837446793-34600b8b-907d-4c0a-8200-f077b3175c32.png)

1）队列持久化

durable参数设置为true，服务器重启后队列不丢失：

~~~java
channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
~~~

2）消息持久化

指定 MessageProperties.PERSISTENT_TEXT_PLAIN 参数：

~~~java
channel.basicPublish("", TASK_QUEUE_NAME,
        MessageProperties.PERSISTENT_TEXT_PLAIN,
        message.getBytes("UTF-8"));
~~~

生产者代码：

使用Scanner接受用户输入，便于发送多条消息

~~~java
public class MultiProducer {

  private static final String TASK_QUEUE_NAME = "multi_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
  }
}
~~~



控制单个消费者的处理任务积压数：

每个消费者最多同时处理1个任务

~~~java
channel.basicQos(1);
~~~



消息确认机制：

为了保证消息成功被消费，rabbitmq提供了消息确认机制，当消费者接收到消息后，比如要给一个反馈：

- ack：消费成功
- nack：消费失败
- reject：拒绝

如果告诉rabbitmq服务器消费成功，服务器才会放心的移除消息。

支持配置autoack，会自动执行ack命令，接收到消息立刻就成功了。

~~~java
 channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
~~~

建议autoack改为false，根据实际情况，去手动确认。

指定确认某条消息：
~~~java
channel.basicAck(delivery.getEnvelope().getDeliveryTag(), );
~~~

第二个参数 multiple 批量确认：是指是否要一次性确认所有的历史消息直到当前这条



指定拒绝某条消息：

~~~java
channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false)
~~~

第3个参数表示是否重新入队，可用于重试。



消费者代码：

~~~java
public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        // 建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            channel.basicQos(1);

            // 定义了如何处理消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                try {
                    // 处理工作
                    System.out.println(" [x] Received '" + "编号:" + finalI + ":" + message + "'");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // 停 20 秒，模拟机器处理能力有限
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 开启消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}
~~~

2个小技巧：

1. 使用Scanner接受用户输入，便于快速发送多条消息
2. 使用for循环创建多个消费者，便于快速验证队列模型工作机制。



交换机

一个生产者给多个队列发消息，1个生产者对多个队列。

交换机的作用：提供消息转发功能，类似于网络路由器

要解决的问题：怎么把消息转发到不同的队列上，好让消费者从不同的队列消费。



绑定：交换机和队列关联起来，也可以叫路由，算是一个算法或转发策略

绑定代码：

~~~java
 channel.queueBind(queueName, EXCHANGE_NAME, "绑定规则");
~~~

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686839285368-841c3ded-965b-4ed7-8214-091ac7f2c922.png)



教程：https://www.rabbitmq.com/tutorials/tutorial-three-java.html

交换机有多种类别;fanout, direct,topic,headers



fanout

扇出，广播

特点：消息会被转发到所有绑定到该交换机的队列

场景：很适用于发布订阅的场景。比如写日志，可以多个系统间共享

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1686839285368-841c3ded-965b-4ed7-8214-091ac7f2c922-169726500464113.png)



![image-20231014143015575](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014143015575.png)

生产者代码：

~~~java
public class FanoutProducer {

  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        // 创建交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
  }
}
~~~

消费者代码

注意：

1. 消费者和生产者要绑定同一个交换机
2. 要先有队列，才能绑定



~~~java
public class FanoutConsumer {

  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel1 = connection.createChannel();
    Channel channel2 = connection.createChannel();
    // 声明交换机
    channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");
    // 创建队列，随机分配一个队列名称
    String queueName = "xiaowang_queue";
    channel1.queueDeclare(queueName, true, false, false, null);
    channel1.queueBind(queueName, EXCHANGE_NAME, "");

    String queueName2 = "xiaoli_queue";
    channel2.queueDeclare(queueName2, true, false, false, null);
    channel2.queueBind(queueName2, EXCHANGE_NAME, "");
    channel2.queueBind(queueName2, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [小王] Received '" + message + "'");
    };

    DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      System.out.println(" [小李] Received '" + message + "'");
    };
    channel1.basicConsume(queueName, true, deliverCallback1, consumerTag -> { });
    channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
  }
}
~~~

效果：所有的消费者都能收到消息



Direct交换机

官方教程：https://www.rabbitmq.com/tutorials/tutorial-four-java.html

绑定：可以让交换机和队列进行关联，可以指定让交换机把什么样的消息发送给哪个队列（类似于计算机网络中，两个路由器，或者网络设备相互连接，也可以理解为网线）

routingKey：路由键，控制消息要转发给那个队列的（IP地址）



特点：消息会根据路由键转发到指定的队列

场景：特定的消息只交给特定的系统（程序来处理）

绑定关系：完全匹配字符串

![img](D:\liumingyao\study\文档\智能BI项目文档.assets\1687007686788-76df21d6-428d-4d2d-abb7-4da819faf775.png)



可以绑定同样的路由键。

比如发日志的场景，希望用独立的程序来处理不同级别的日志，比如 C1 系统处理 error 日志，C2 系统处理其他级别的日志

![img](D:\liumingyao\study\文档\智能BI项目文档.assets\1687007976055-a38c33d2-490f-4582-b9ea-36b881c2101e.png)

![image-20231014144559541](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014144559541.png)

生产者代码：

~~~java
public class DirectProducer {

  private static final String EXCHANGE_NAME = "direct-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            String[] strings = userInput.split(" ");
            if (strings.length < 1) {
                continue;
            }
            String message = strings[0];
            String routingKey = strings[1];

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + " with routing:" + routingKey + "'");
        }

    }
  }
}
~~~

消费者代码：

~~~java
public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 创建队列，随机分配一个队列名称
        String queueName = "xiaoyu_queue";
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "xiaoyu");

        // 创建队列，随机分配一个队列名称
        String queueName2 = "xiaopi_queue";
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueBind(queueName2, EXCHANGE_NAME, "xiaopi");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback xiaoyuDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoyu] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback xiaopiDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaopi] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, true, xiaoyuDeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, true, xiaopiDeliverCallback, consumerTag -> {
        });
    }
}
~~~





topic交换机

官方教程：https://www.rabbitmq.com/tutorials/tutorial-five-java.html

特点：消息会根据一个 模糊的 路由键转发到指定的队列

场景：特定的一类消息可以交给特定的一类系统（程序）来处理

绑定关系：可以模糊匹配多个绑定

- *：匹配一个单词，比如 *.orange，那么 a.orange、b.orange 都能匹配
- \#：匹配 0 个或多个单词，比如 a.#，那么 a.a、a.b、a.a.a 都能匹配

注意，这里的匹配和 MySQL 的like 的 % 不一样，只能按照单词来匹配，每个 '.' 分隔单词，如果是 '#.'，其实可以忽略，匹配 0 个词也 ok

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1687009111794-08bd54bb-234d-4280-a604-852e2b01840c.png)

应用场景：

老板要下发一个任务，让多个组来处理

![image-20231014145031412](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014145031412.png)

生产者代码：

~~~java
public class TopicProducer {

  private static final String EXCHANGE_NAME = "topic-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            String[] strings = userInput.split(" ");
            if (strings.length < 1) {
                continue;
            }
            String message = strings[0];
            String routingKey = strings[1];

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + " with routing:" + routingKey + "'");
        }
    }
  }
}
~~~

消费者代码：

~~~java
public class TopicConsumer {

  private static final String EXCHANGE_NAME = "topic-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");

      // 创建队列
      String queueName = "frontend_queue";
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, EXCHANGE_NAME, "#.前端.#");

      // 创建队列
      String queueName2 = "backend_queue";
      channel.queueDeclare(queueName2, true, false, false, null);
      channel.queueBind(queueName2, EXCHANGE_NAME, "#.后端.#");

      // 创建队列
      String queueName3 = "product_queue";
      channel.queueDeclare(queueName3, true, false, false, null);
      channel.queueBind(queueName3, EXCHANGE_NAME, "#.产品.#");

      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      DeliverCallback xiaoaDeliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(" [xiaoa] Received '" +
                  delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
      };

      DeliverCallback xiaobDeliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(" [xiaob] Received '" +
                  delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
      };

      DeliverCallback xiaocDeliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(" [xiaoc] Received '" +
                  delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
      };

      channel.basicConsume(queueName, true, xiaoaDeliverCallback, consumerTag -> {
      });
      channel.basicConsume(queueName2, true, xiaobDeliverCallback, consumerTag -> {
      });
      channel.basicConsume(queueName3, true, xiaocDeliverCallback, consumerTag -> {
      });
  }
}
~~~



Headers交换机

类似主题和直接交换机，可以根据 headers 中的内容来指定发送到哪个队列
由于性能差、比较复杂，一般不推荐使用。

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1687011466810-28a7cdc5-e42c-45f5-aa8c-2635c41e0a89.png)



RPC

支持用消息队列来模拟 RPC 的调用，但是一般没必要，直接用 Dubbo、GRPC 等 RPC 框架就好了。



#### 核心特性

消息过期机制

官方文档：https://www.rabbitmq.com/ttl.html

可以给每条消息指定一个有效期，一段时间内未被消费者处理，就过期了。

示例场景：消费者（库存系统）挂了，一个订单 15 分钟还没被库存系统处理，这个订单其实已经失效了，哪怕库存系统再恢复，其实也不用扣减库存。

适用场景：清理过期数据、模拟延迟队列的实现（不开会员就慢速）、专门让某个程序处理过期请求



1）给队列中的所有消息指定过期时间

~~~java
// 创建队列，指定消息过期参数
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-message-ttl", 5000);
// args 指定参数
channel.queueDeclare(QUEUE_NAME, false, false, false, args);
~~~

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1687012172430-53fff46b-6383-40c9-99ae-bc96667f71d4.png)

如果在过期时间内，还没有消费者取消息，消息才会过期。

注意，如果消息已经接收到，但是没确认，是不会过期的。

> 如果消息处于待消费状态并且过期时间到达后，消息将被标记为过期。但是，如果消息已经被消费者消费，并且正在处理过程中，即使过期时间到达，消息仍然会被正常处理



消费者代码：

~~~java
public class TtlConsumer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 创建队列，指定消息过期参数
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-message-ttl", 5000);
        // args 指定参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, args);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        // 定义了如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 消费消息，会持续阻塞
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }
}
~~~

生产者代码：

~~~java
public class TtlProducer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
//        factory.setUsername();
//        factory.setPassword();
//        factory.setPort();

        // 建立连接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 发送消息
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
~~~



2）给某条消息指定过期时间

语法：

~~~java
// 给消息指定过期时间
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
        .expiration("1000")
        .build();
channel.basicPublish("my-exchange", "routing-key", properties, message.getBytes(StandardCharsets.UTF_8));
~~~



实例代码：

~~~java
public class TtlProducer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
//        factory.setUsername();
//        factory.setPassword();
//        factory.setPort();

        // 建立连接、创建频道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 发送消息
            String message = "Hello World!";

            // 给消息指定过期时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .expiration("1000")
                    .build();
            channel.basicPublish("my-exchange", "routing-key", properties, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
~~~





消息确认机制

官方文档：https://www.rabbitmq.com/confirms.html

为了保证消息成功被消费（快递成功被取走），rabbitmq 提供了消息确认机制，当消费者接收到消息后，比如要给一个反馈：

- ack：消费成功
- nack：消费失败
- reject：拒绝

如果告诉 rabbitmq 服务器消费成功，服务器才会放心地移除消息。

支持配置 autoack，会自动执行 ack 命令，接收到消息立刻就成功了。

~~~java
 channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
       });
~~~



**一般情况，建议 autoack 改为 false，根据实际情况，去手动确认。**



指定确认某条消息：

~~~java
channel.basicAck(delivery.getEnvelope().getDeliveryTag(), );
~~~

第二个参数 multiple 批量确认：是指是否要一次性确认所有的历史消息直到当前这条



指定拒绝某条消息：

~~~java
channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
~~~

第 3 个参数表示是否重新入队，可用于重试





死信队列

官方文档：https://www.rabbitmq.com/dlx.html

为了保证消息的可靠性，比如每条消息都成功消费，需要提供一个容错机制，即：失败的消息怎么处理？

死信：过期的消息、拒收的消息、消息队列满了、处理失败的消息的统称

死信队列：专门处理死信的队列（注意，它就是一个普通队列，只不过是专门用来处理死信的，你甚至可以理解这个队列的名称叫 “死信队列”）

死信交换机：专门给死信队列转发消息的交换机（注意，它就是一个普通交换机，只不过是专门给死信队列发消息而已，理解为这个交换机的名称就叫 “死信交换机”）。也存在路由绑定

死信可以通过死信交换机绑定到死信队列。

![image-20231014145949831](D:\liumingyao\study\文档\智能BI项目文档.assets\image-20231014145949831.png)

实现：

1）创建死信交换机和死信队列，并且绑定关系

![image.png](D:\liumingyao\study\文档\智能BI项目文档.assets\1687013888188-1bde4fc6-73c1-48e4-b9c8-7d231a4e5a72.png)

2）给失败之后需要容错处理的队列绑定死信交换机

~~~java
// 指定死信队列参数
Map<String, Object> args = new HashMap<>();
// 要绑定到哪个交换机
args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
// 指定死信要转发到哪个死信队列
args.put("x-dead-letter-routing-key", "waibao");

// 创建队列，随机分配一个队列名称
String queueName = "xiaodog_queue";
channel.queueDeclare(queueName, true, false, false, args);
channel.queueBind(queueName, EXCHANGE_NAME, "xiaodog");
~~~

3）可以给要容错的队列指定死信之后的转发规则，死信应该再转发到哪个死信队列

~~~java
// 指定死信要转发到哪个死信队列
args.put("x-dead-letter-routing-key", "waibao");
~~~

4）可以通过程序来读取死信队列中的消息，从而进行处理

~~~java
// 创建队列，随机分配一个队列名称
String queueName = "laoban_dlx_queue";
channel.queueDeclare(queueName, true, false, false, null);
channel.queueBind(queueName, DEAD_EXCHANGE_NAME, "laoban");

String queueName2 = "waibao_dlx_queue";
channel.queueDeclare(queueName2, true, false, false, null);
channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "waibao");

DeliverCallback laobanDeliverCallback = (consumerTag, delivery) -> {
    String message = new String(delivery.getBody(), "UTF-8");
    // 拒绝消息
    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
    System.out.println(" [laoban] Received '" +
            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
};

DeliverCallback waibaoDeliverCallback = (consumerTag, delivery) -> {
    String message = new String(delivery.getBody(), "UTF-8");
    // 拒绝消息
    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
    System.out.println(" [waibao] Received '" +
            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
};

channel.basicConsume(queueName, false, laobanDeliverCallback, consumerTag -> {
});
channel.basicConsume(queueName2, false, waibaoDeliverCallback, consumerTag -> {
});

~~~

完整生产者代码：

~~~java
public class DlxDirectProducer {

    private static final String DEAD_EXCHANGE_NAME = "dlx-direct-exchange";
    private static final String WORK_EXCHANGE_NAME = "direct2-exchange";


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明死信交换机
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");

            // 创建队列，随机分配一个队列名称
            String queueName = "laoban_dlx_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, DEAD_EXCHANGE_NAME, "laoban");

            String queueName2 = "waibao_dlx_queue";
            channel.queueDeclare(queueName2, true, false, false, null);
            channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "waibao");

            DeliverCallback laobanDeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 拒绝消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [laoban] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            DeliverCallback waibaoDeliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 拒绝消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [waibao] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            channel.basicConsume(queueName, false, laobanDeliverCallback, consumerTag -> {
            });
            channel.basicConsume(queueName2, false, waibaoDeliverCallback, consumerTag -> {
            });


            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String userInput = scanner.nextLine();
                String[] strings = userInput.split(" ");
                if (strings.length < 1) {
                    continue;
                }
                String message = strings[0];
                String routingKey = strings[1];

                channel.basicPublish(WORK_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + " with routing:" + routingKey + "'");
            }

        }
    }
}
~~~

完整消费者代码：

~~~java
public class DlxDirectConsumer {

    private static final String DEAD_EXCHANGE_NAME = "dlx-direct-exchange";

    private static final String WORK_EXCHANGE_NAME = "direct2-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(WORK_EXCHANGE_NAME, "direct");

        // 指定死信队列参数
        Map<String, Object> args = new HashMap<>();
        // 要绑定到哪个交换机
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        // 指定死信要转发到哪个死信队列
        args.put("x-dead-letter-routing-key", "waibao");

        // 创建队列，随机分配一个队列名称
        String queueName = "xiaodog_queue";
        channel.queueDeclare(queueName, true, false, false, args);
        channel.queueBind(queueName, WORK_EXCHANGE_NAME, "xiaodog");

        Map<String, Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        args2.put("x-dead-letter-routing-key", "laoban");

        // 创建队列，随机分配一个队列名称
        String queueName2 = "xiaocat_queue";
        channel.queueDeclare(queueName2, true, false, false, args2);
        channel.queueBind(queueName2, WORK_EXCHANGE_NAME, "xiaocat");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback xiaoyuDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [xiaodog] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback xiaopiDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [xiaocat] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, false, xiaoyuDeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, false, xiaopiDeliverCallback, consumerTag -> {
        });
    }
}
~~~





#### RabbitMQ重点知识

1. 消息队列的概念，模型，应用场景
2. 交换机的类别，路由绑定的关系
3. 消息可靠性
   - 消息确认机制（ack，nack， reject）
   - 消息持久化（durable）
   - 消息过期机制
   - 死信队列
4. 延迟队列（类似死信队列）
5. 顺序消费，消息幂等性
6. 可扩展性（仅作了解）
   - 集群
   - 故障的恢复机制
   - 镜像
7. 运维监控警告（仅作了解）



相关资源推荐：

神光 Node.js RabbitMQ 入门文档：https://juejin.cn/post/7225474899480526885

推荐星球嘉宾 Yes 的专栏：https://t.zsxq.com/0fq7F1WAa





#### RabbitMQ项目实战

选择客户端

怎么在项目中使用 RabbitMQ？

1）使用官方的客户端。
优点：兼容性好，换语言成本低，比较灵活
缺点：太灵活，要自己去处理一些事情。比如要自己维护管理链接，很麻烦。

2）使用封装好的客户端，比如 Spring Boot RabbitMQ Starter
优点：简单易用，直接配置直接用，更方便地去管理连接
缺点：封装的太好了，你没学过的话反而不知道怎么用。不够灵活，被框架限制。

根据场景来选择，没有绝对的优劣：类似 jdbc 和 MyBatis

本次使用 Spring Boot RabbitMQ Starter（因为我们是 Spring Boot 项目）

如果你有一定水平，有基础，英文好，建议看官方文档，不要看过期博客！
https://spring.io/guides/gs/messaging-rabbitmq/



基础实战：

1）引入依赖

注意，使用的版本一定要和你的 springboot 版本一致！！！！！！！

~~~xml
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-amqp -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <version>2.7.2</version>
</dependency>
~~~



2）在yml中引入配置：

~~~properties
spring:
    rabbitmq:
        host: localhost
        port: 5672
        password: guest
        username: guest
~~~



3）创建交换机和队列

~~~java
/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class MqInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列，随机分配一个队列名称
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
        } catch (Exception e) {

        }

    }
}
~~~



4）生产者代码

~~~java
@Component
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
~~~



5）消费者代码

~~~java
@Component
@Slf4j
public class MyMessageConsumer {

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        channel.basicAck(deliveryTag, false);
    }

}
~~~



6）单元测试执行

~~~java
@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;

    @Test
    void sendMessage() {
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", "你好呀");
    }
}
~~~



#### BI项目改造

以前是把任务提交到线程池，然后在线程池提交中编写处理程序的代码，线程池内排队。

如果程序中断了，任务就没了，就丢了。



改造后的流程：

1. 把任务提交改为向队列发送消息

2. 写一个专门的接受消息的程序，处理任务

3. 如果程序中断了，消息未被确认，还会重发

4. 现在，消息全部集中发到消息队列，你可以部署多个后端，都从同一个地方取任务，从而实现了分布式负载均衡



实现步骤

1）创建交换机和队列

2）将线程池中的执行代码移到消费者类中

3）根据消费者的需求来确认消息的格式（chartId）

4）将提交线程池改造为发送消息到队列



验证

验证发现，如果程序中断了，没有 ack、也没有 nack（服务中断，没有任何响应），那么这条消息会被重新放到消息队列中，从而实现了每个任务都会执行。



#### 更多优化点

1. 支持用户查看图表原始数据
2. 图表数据分表存储，提高查询灵活性和性能
3. 支持分组（分标签）查看和检索图表
4. 增加更多可选参数来控制图表的生成，比如图表配色等
5. 支持用户对失败的图表进行手动重试
6. 限制用户同时生成图表的数量，防止单用户抢占系统资源
7. 统计用户生成图表的次数，甚至可以添加积分系统，消耗积分来智能分析
8. 支持编辑生成后的图表的信息。比如可以使用代码编辑器来编辑 Echarts 图表配置代码
9. 由于图表数据是静态的，很适合使用缓存来提高加载速度。
10. 使用死信队列来处理异常情况，将图表生成任务置为失败
11. 补充传统 BI 拥有的功能，把智能分析作为其中一个子模块。



























