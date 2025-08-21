# FASF - 轻量级声明式HTTP调用框架

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## 简介

FASF（Fast API Service Framework）是一个基于Spring Boot 3的轻量级、简单易用、可扩展以及高性能的声明式HTTP调用框架。旨在解决日常项目开发中各类接口调用的痛点，包括公司内部系统间调用和第三方OpenAPI集成。

通过FASF，您可以快速完成接口接入并打包成starter应用到您的项目中，让API调用变得像本地方法调用一样简单。

## 🎯 核心优势

### 为什么选择FASF？

在日常项目开发中，我们经常会遇到各类的接口调用：
- 公司内部各个系统间的调用（会员系统、人力系统、费用系统等）
- 第三方OpenAPI调用（高德地图、企业微信等）

这些接口的接入往往耗费大量的人力资源在接口开发、沟通以及接口联调上面。

### 与OpenFeign的区别

**OpenFeign** 主要应用于Spring Cloud内的微服务调用，天然支持负载均衡策略。

**FASF** 则更专注于API调用的starter场景，具有更轻量级的依赖和更高程度的定制能力。

以网约车项目为例：
- OpenFeign：用于服务之间的内部调用（如订单服务调用计价服务）
- FASF：用于计价服务调用高德地图路径规划API获取订单里程

## 🚀 技术特性

### 轻量级架构
基于Spring Boot 3，核心依赖简洁：
- spring-boot-starter
- spring-boot-starter-web
- spring-boot-starter-webflux
- slf4j-api

### 简单易用
- 基于Spring Boot Starter机制
- 通过动态代理实现API接口实例创建并注册到Spring容器
- 类似MyBatis Mapper的使用方式

### 高性能网络通信
- 基于Netty Reactor的非阻塞I/O模型
- 可配置的连接池和线程池
- 支持高并发场景

### 异步处理能力
- 全面支持CompletableFuture异步编程模型
- 通过定义API返回参数为CompletableFuture<T>即可获得异步实例

### 监控和日志
- 集成MDC上下文传递机制
- 详细的请求/响应日志记录
- 完善的异常处理和错误日志记录

### 可扩展性
提供多个扩展点：
- **HttpClient**: 可自定义HTTP客户端实现
- **RequestInterceptor**: 请求拦截器，支持数据加密、添加请求头等
- **ResponseInterceptor**: 响应拦截器，支持数据解密等操作

### 重试机制
- 基于注解的重试策略（@Retry）
- 支持HTTP状态码408, 429, 500, 502, 503, 504重试
- 可配置重试次数和延迟时间

## 📖 使用示例

项目提供了一个完整的高德地图API调用示例（fasf-client模块）：
1. 启动FasfClientApplication
2. 测试高德地图行政区域查询接口
3. 使用您的高德地图API密钥替换配置

## 快速开始
通过以上介绍，您已经对FASF项目有了初步了解，并成功运行了高德地图调用示例。接下来，我们将一步步指导您完成自定义接口的接入：
1. 定义API接口
首先，像接入高德地图一样，您需要明确目标接口的具体信息，包括：
接口地址

请求参数

请求方式

响应数据格式

然后在 fasf-api 模块内创建一个新的接口类，命名为 XXXApi，参考 org.fasf.api.AMapApi 的实现方式。

>💡 默认包扫描：框架默认扫描 org.fasf.api 包下的接口并生成代理类。如果您的接口位于其他包下，可以通过修改 fasf-client 的配置文件来扩展扫描路径：

> fasf.api.basePackages=org.fasf.api,{your package}
2. 打包Starter
将您的API模块打包成Spring Boot Starter：
建议将 fasf-api 模块的 artifactId 修改为 XXX-fasf-api-spring-boot-starter
3. 集成到项目
将打包好的jar包添加到您的目标项目依赖中
在项目中使用 XXXApi：
>   @Autowired
> 
>   private XXXApi xxxApi;
> 
>  // 像使用本地方法一样调用远程接口
> 
>   CompletableFuture<String> result = xxxApi.yourMethod(params);

此时，XXXApi 已经以Bean的形式存在于Spring容器中，您可以像 org.fasf.client.service.impl.AMapServiceImpl 一样轻松使用它
   
## 📋 兼容性

- **Java版本**: 17+
- **Spring Boot版本**: 3.x.x
- **本版本为初始版本**，无历史版本兼容性问题

## 🤝 联系方式

感谢您关注FASF项目！在使用过程中如有任何疑问或建议，请通过以下方式联系：

📧 邮箱: 812650934@qq.com

欢迎提交Issue和Pull Request，让我们一起完善这个项目！

## 📄 License

本项目采用Apache 2.0许可证，详情请见[LICENSE](LICENSE)文件。
