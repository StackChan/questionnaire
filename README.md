# 软件系统开发实践作业阅读指南

---

## ReadMe文档包括

+ 使用Nacos对项目结构的重构情况

+ 推荐的审阅方法
+ 功能实现情况总结

## 使用Nacos对项目结构的重构情况:

- 四个模块均实现了 nacos配置中心的统一配置管理 和 nacos服务注册与发现
- 通过zuul``实现了 提供统一服务入口，让微服务对前端透明`` ,实现了==请求路由、负载均衡(zuul通过调用ribbon实现)、校验过滤==功能
- 通过feign实现微服务相互调用(本项目中,是project微服务 调用了user微服务,在ProjectController中体现),也能实现 负载均衡(project微服务实例 根据负载均衡策略调用user微服务实例)
- ProviderController(user微服务中) , feignclient/ProviderClient(project微服务中) 的构建 都是为了是实现:``feign通过伪HTTP的进行跨微服务接口调用``
- ZuulConfig(zuul网关配置类,在api-gateway中)  是为了实现了网关配置热更新



**Feign原理**:``伪HTTP请求``

> Feign英文表意为“假装，伪装，变形”，将HTTP报文请求方式 伪装为简单的java接口(内部,未通过TTTP)调用方式

（1）在微服务启动时，各微服务会向服务发现中心上报自身实例信息，

每个实例包括：

IP地址、端口号信息。

（2）微服务会定期从Nacos Server(服务发现中心)获取服务实例列表。 

（3）当ServiceA调用ServiceB时(本项目中,是project微服务 调用了user微服务,在ProjectController中体现)，ribbon组件从本地服务实例列表中查找ServiceB的实例。这时ribbon会通过用户所配置的**负载均衡策略**从中选择一个实例。 

（4）最终，Feign组件会通过ribbon选取的实例发送http请求。 

采用Feign+Ribbon的整合方式，是由Feign完成远程调用的整个流程。而Feign集成了Ribbon，Feign使用Ribbon 

完成调用实例的负载均衡。 



**zuul原理**:

从nacos服务中心获取服务注册表 ,根据网关配置 转发前端http请求,通过 Ribbon调用微服务实例 来进行负载均衡



(user,project微服务中都有LoadBalanceTest类,用于进行负载均衡测试)

![image-20220630210225807](image-20220630210225807.png)



## 审阅方法

+ 在保留项目结构的基础上进行了部分调整
  + 清空无关代码和固定代码、如：bean、common、dao等文件夹中的java代码
  + 项目主要分为四个模块：(同时,包括了pom文件与yml配置文件:yml文件在nacos配置中心统一管理)
    + 网关微服务(api-gateway)
    
      > 通过Zuul网关实现负载均衡
      >
      > Nacos配置中心 api-gateway.yml文件(发布在dev命名空间)内容:
      >
      > ```yaml
      > zuul:
      >   routes: 
      >     project: 
      >       # 不去前缀
      >       stripPrefix: 
      >         false 
      >       path: 
      >         /project/**
      >     user:
      >       stripPrefix: 
      >         false 
      >       path: 
      >         # 通往/admin/**的接口有user微服务处理
      >         # /LoadBalanceTest/**接口(get接口,比/admin的post接口更易观察,直接通过浏览器访问即可)
      >         # 写在user微服务中用于直白观察负载均衡已实现
      >         /admin/**
      > 
      > ribbon:
      >   NFLoadBalancerRuleClassName:
      >     com.netflix.loadbalancer.AvailabilityFilteringRule
      > ```
      >
      > zuul网关微服务(通过配置默认跑在56010端口)实现了
      >
      >   
      >
      > 前端HTTP请求统一向zuul网关微服务发送请求,网关代为转发给project和user微服务的外露接口(在common.js中更改了var httpRequestUrl = "http://127.0.0.1:56010";)
      >
      >  
      >
      > 总结地说,本项目中zuul网关微服务实现了:
      >
      > ==请求路由、负载均衡(zuul通过调用ribbon实现)、校验过滤==功能
    
    + 项目微服务(ms_project)相关
    
      > 项目微服务是一个整体,包含了项目管理,问卷管理
    
    + 用户微服务(ms_user)相关
    
      > 用户微服务包含了用户管理,由于第二次迭代中针对用户的功能,我们只保留了第二次迭代中我们增加功能(删改查)相关的代码
    
    + 前端静态(static)代码
    
      > 我们将web静态页面(原resource/static)单独抽取了出来,
      >
      > 符合实际业务场景中,各个微服务实例与处理 用户http请求的
  
+ 后端功能的主要实现逻辑存在于Controller文件和Service文件中
+ 在Controller中搜索"功能1"可以快速找到功能1相关的代码，其他功能相同

## 功能实现情况总结

#### 功能1*`（已实现）`*

问卷发起人登录后可以对项目进行操作、对问卷进行操作以及对 项目和问卷的关联关系、对每个问卷的状态等进行操作。 说明：项目是一组调查问卷的概念，针对一个调查目标，划分不 同的调查群体，针对每一个调查群体可以单独设置不同的调查问卷。 

#### 功能2*`（已实现）`*

问卷发起人可以进行项目的管理；可以查询项目，并针对查询得 到的结果项目进行删除、和修改的操作。修改和删除过程中要判断项 目状态，如果有正在进行的问卷，不可以修改和删除。也可以创建项 目，如果项目名称重复，需求重新命名之后创建。  

#### 功能3*`（已实现）`*

问卷发起人可以创建项目，创建项目需要填写项目的名称等信息， 可以选择当前创建项目需要关联的调查问卷，并可以设定调查问卷的 开始和结束时间，问卷是否开启等状态进行设定。 项目创建完成后，如果问卷处于开始和结束时间内，并且问卷处 于开启状态，可以通过发送链接的形式，选择问卷发送人，通过微信、 qq、邮箱三种形式，进行问卷的发送。

#### 功能4*`（已实现）`*

问卷发起人可以修改项目，通过查询项目结果可以选择修改的项 目。可以修改项目的信息、关联的项目问卷等。注意如果项目中的问 卷正在进行中，不能修改。 

#### 功能5*`（已实现）`*

问卷发起人可以删除项目，通过查询项目结果可以选择项目删除。 可以删除项目的信息、关联的项目问卷等。注意删除项目的时候如果 项目的问卷正在进行中，不能删除。

#### 功能6*`（已实现）`*

问卷发起人可以创建试卷，创建时可以选择之前创建过的问卷（实现了，但是只能按照当前项目的历史问卷，不能使用其它项目的历史问卷）， 导入后进行修改。保存之后更新为新问卷。也可以完全重新建立问卷。 创建问卷的题目包括选择题和填空题。 

#### 功能7*`（已实现）`*

问卷发起人可以进行问卷的修改，通过问卷的查询结果，查询到 项目需要修改的问卷后，可以进行问卷的修改。注意如果问卷已经被 发送过，则不能修改，如果问卷已经启动，需要停止之后再修改。 

#### 功能8*`（已实现）`*

问卷发起人可以进行问卷的删除，通过问卷的查询结果，查询到 项目需要删除的问卷后，可以进行问卷的删除。注意如果问卷已经被 发送过，则不能删除，如果问卷已经启动，需要停止之后再删除。

#### 功能9*`（已实现）`*

问卷发起人可以进行待问卷人员信息导入，包括学生和教师两种 角色，通过 excel 模板导入待问卷人员信息，学生包括 学号、姓名、 所属学校、专业、班级、性别、微信号、qq 号、手机号、电子邮箱。 教师包括教师号、姓名、所 属院校、性别、微信号、qq 号、电子邮 箱。 

#### 功能10*`（已实现）`*

待问卷人接收人接受到问卷后，可以对问卷进行回答，回答结 果提交后，系统进行记录，并可以进行相关的分析。

