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

![image-20231005150042006]([C:\Users\LiuMingyao\AppData\Roaming\Typora\typora-user-images\image-20231005150042006.png](https://github.com/Liumingyao666/github-img/blob/master/image-20231005150042006.png?raw=true))



假设一个 AI 服务生成图表和分析结果要等 50 秒，如果有大量用户需要生成图表，每个人都需要等待 50 秒，那么 AI 服务可能无法承受这种压力。为了解决这个问题，可以采用消息队列技术。通过消息队列，用户可以提交生成图表的请求，这些请求会进入队列，AI 服务会依次处理队列中的请求，从而避免了同时处理大量请求造成的压力，同时也能更好地控制资源的使用。

优化流程（异步化）：客户端输入分析诉求和原始数据，向业务后端发送请求。业务后端将请求事件放入消息队列，并为客户端生成取餐号，让要生成图表的客户端去排队，消息队列根据 AI 服务负载情况，定期检查进度，如果 AI 服务还能处理更多的图表生成请求，就向任务处理模块发送消息。
任务处理模块调用 AI 服务处理客户端数据，AI 服务异步生成结果返回给后端并保存到数据库，当后端的 AI 服务生成完毕后，可以通过向前端发送通知的方式，或者通过业务后端监控数据库中图表生成服务的状态，来确定生成结果是否可用。若生成结果可用，前端即可获取并处理相应的数据，最终将结果返回给客户端展示。（在此期间，用户可以去做自己的事情）

![image-20231005150112288]([C:\Users\LiuMingyao\AppData\Roaming\Typora\typora-user-images\image-20231005150112288.png](https://github.com/Liumingyao666/github-img/blob/master/image-20231005150112288.png?raw=true))



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



### 前后端基础开发

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

   





















