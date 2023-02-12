# 1 系统介绍

本项目为 Bark 辅助处理项目，项目服务端放置在 Bark 服务端前端，用于接收和处理客户端的 Bark 格式报文的转发请求。

**本项目实现的功能：**

- 需要对原始通知信息进行处理，经过修改后再提交给 Bark 服务端

- 将通知转发到不同平台上

**本项目已实现的服务转发平台：**

- 统一通知推送服务（forward-server，基于 [websocket-forward](https://github.com/Orainge/websocket-forward) 开发）
  - MacOS X 通知（命令行模式）
  - Windows 通知（待开发）
- [Nextcloud 通知（API 模式）](https://github.com/nextcloud/admin_notifications)

**Bark （IOS通知服务）介绍：**

- [Bark](https://github.com/Finb/Bark)

- [Bark Server](https://github.com/Finb/bark-server)

**本项目参考了 [apprise](https://github.com/caronc/apprise) 的设计模式，其它参考资料如下：**

- [MacOS 通知](https://github.com/julienXX/terminal-notifier)
- [Windows 通知](https://github.com/caronc/apprise/wiki/Notify_windows)
- [macos-alert](https://gitee.com/xiaozhuai/macos-alert)

## 1.1 系统组成

该系统包含以下两个部分：

- 服务端：管理客户端，接收客户端的连接，向各接收端分发消息。
- 客户端：连接服务端，接收来自服务端通知信息，可以扩展开发，实现更多通知的方式。
- 本项目集成了信息转发框架进行二次开发：[websocket-forward](https://github.com/Orainge/websocket-forward)

# 2 技术说明

## 2.1 系统项目介绍

该系统由Maven进行管理，包含 2 个 model：

- bark-processor-forward-client：通知客户端
- bark-processor-server：服务端

其中，客户端和服务端依赖 websocket-forward-utils，因此如果运行项目时提示找不到相应类时，需要手动安装依赖到本地 Maven 仓库。

- 源码地址：[websocket-forward](https://github.com/Orainge/websocket-forward)

```sh
cd /path/to/project # 进入项目目录
cd websocket-forward-utils # 进入工具包目录

# 以下安装方式二选一
# 安装到本地 Maven 仓库（同时安装源码）
mvn source:jar install 
# 安装到本地 Maven 仓库（不安装源码）
mvn install
```

## 2.2 使用场景

- 目前正在使用Bark服务，不想改变现有的网络架构
- 需要转发消息到不同的平台上（跨平台通知）

## 2.2 系统网络架构图

![系统网络架构图](https://cdn.jsdelivr.net/gh/Orainge/bark-processor@master/pic/pic5.png)

## 2.4 系统处理流程图

- 消息提交到服务器后，先经过 repeat-filter 处理，如果重复了则该消息不会继续发送。
- 消息经过 processor-list 里的 processor 处理，改变原始信息内容。
- 消息处理完成后：
  - 发送给 Bark 服务端，经由 Bark 服务端分发到 Bark 客户端。
  - 分发给 forwarder-list 里的 forwarder，将信息分发出去。

![系统处理流程图](https://cdn.jsdelivr.net/gh/Orainge/bark-processor@master/pic/pic4.png)



# 3 系统运行

## 3.1 客户端

客户端打包完成后会生成 .jar 包，该 .jar 包可直接运行。

```sh
java -jar bark-processor-forward-client-1.0.jar
```

## 3.2 服务端

服务端打包完成后会生成 .jar 包，该 .jar 包可直接运行。

```sh
java -jar bark-processor-server-1.0.jar
```

# 4 系统配置

## 4.1 服务端配置

### 4.1.1 Bark 客户端&消息处理配置

- 文件名：`application-dev.yml`/`application-prod.yml`

```yaml
api:
  url: http://127.0.0.1:8080 # bark 服务端地址
```

- 文件名：`api-config.yml`

```yaml
# Bark 客户端配置
device:
  config:
    - device-key: # Bark 服务的 token
      # 重复发送拦截规则
      # 如果符合规则，则该消息会检测是否重复发送
      # 如果重复发送，则拦截，不提交到 Bark 服务端
      # 关键字 (title-keyword / content-keyword) 匹配模式：正则表达式
      repeat-filter-list: 
        - title-keyword: "" # 标题里包含什么字符串就进行拦截(留空/null表示任意匹配)
          content-keyword: "" # 内容里包含什么字符串就进行拦截(留空/null表示任意匹配)
          interval: 0 # 间隔几秒内只能重复发送一次 (0表示不设置间隔)
      # 处理规则
      # 如果符合规则，则该消息会使用 (processor-name) 处理后再提交到 Bark 服务端
      # 适合一些特殊情况，需要对提交的 Bark 信息处理后再提交到 Bark 服务端
      # 关键字 (title-keyword / content-keyword) 匹配模式：正则表达式
      # forwarder-name: 处理器 Bean 名称，需要继承 com.orainge.bark_processor.server.process.processor.Processor
      processor-list: # 处理规则
        - title-keyword: "" # 标题里包含什么字符串就进行拦截(留空/null表示任意匹配)
          content-keyword: "" # 内容里包含什么字符串就进行拦截(留空/null表示任意匹配)
          processor-name: demoProcessor
      # 转发规则
      # 将收到的 Bark 信息进行转发
      # 关键字 (title-keyword / content-keyword) 匹配模式：正则表达式
      # forwarder-name: 转发器 Bean 名称，需要继承 com.orainge.bark_processor.server.process.forwarder.Forwarder
      # 系统自带的 forwarder-name:
      # 	[forwardServerForwarder] 统一通知推送服务
      # 	[nextcloudForwarder] Nextcloud 通知服务
      forwarder-list:
        - title-keyword: "" # 标题里包含什么字符串就进行拦截(留空/null表示任意匹配)
          content-keyword: "" # 内容里包含什么字符串就进行拦截(留空/null表示任意匹配)
          forwarder-name: forwardServerForwarder
        - title-keyword: (标题1|标题2) # e.g. 标题里包含关键字【标题1】或【标题2】即匹配
          content-keyword: ""
          forwarder-name: nextcloudForwarder

# 调试模式
debug:
  # 是否启用调试模式 "true"-是 "false"-否
  enable: "false"
```

### 4.1.2 统一通知推送服务配置

- 文件名：`forward-server-config.yml`

```yaml
# 【统一通知推送服务】服务端配置
websocket-server:
  response:
    time-out: 20000 # websocket 中等待回报文的时间(单位：毫秒)

# 【统一通知推送服务】允许的客户端列表
websocket-client:
  list:
    - id: XXX # 客户端 ID
      key: XXX # 密钥
      description: "XXX" # 客户端描述
      # keep-alive: true # 是否由服务器定时发送 ping-pong 信息进行保活 (默认不开启)
```

### 4.1.3 Nextcloud 配置

- 文件名称：`nextcloud-config.yml`

```yaml
nextcloud:
  host-list:
  - host: cloud.xxx.com:9999 # Nextcloud 服务地址（使用https连接）
    admin-user: adminUser # 要推送的账号所属群组的管理员账号，即该管理员账号能管理目标接收账号
    admin-password: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX # 管理员账号 token，创建方式见 6.1 如何获取 Nextcloud 用户 token
    # 接收通知的目标账户
    notification-user-list:
      - user1
      - user2
```

## 4.2 统一通知推送服务客户端配置

```yaml
# 【统一通知推送服务】服务端配置
websocket-server:
  # bark-processor 服务端 URL/exchange (http->ws; https->wss)
  url: ws://localhost:9422/exchange
  # 连接服务端失败后等待多少秒后重试 (默认 5 秒)
  reconnect-wait: 5

# 【统一通知推送服务】客户端配置
websocket-client:
  id: XXX # 客户端 ID
  key: XXX # 密钥

# 调试模式
debug:
  # 是否启用调试模式 "true"-是 "false"-否
  enable: "false"

# terminal-notifier 配置
terminal-notifier:
  exec-path: "/path/to/terminal-notifier"
```

# 5 二次开发

## 5.1 自定义处理器(Processor)

继承`com.orainge.bark_processor.server.process.processor.Processor`后，自定义处理逻辑，将 bean 名称加入 `processor-list` 即可

## 5.2 自定义转发器(Forwarder)

继承`com.orainge.bark_processor.server.process.forwarder.Forwarder`后，自定义处理逻辑，将 bean 名称加入 `forwarder-list` 即可

# 6 其它配置

## 6.1 如何获取 Nextcloud 用户 token

- 页面地址：nextcloud服务地址/settings/user/security

- 设置-安全-设备和活动链接：页面最底端输入应用名（任意即可），然后点击【创建新应用密码】，即可得到用户token。

![Nextcloud 创建 token](https://cdn.jsdelivr.net/gh/Orainge/bark-processor@master/pic/pic3.png)

## 6.2 macOS 如何显示通知

- 通过在 macOS 上执行 shell 命令，即可实现对应效果

- 在 JAVA 中执行上述命令，即可显示通知。

### 6.2.1 浮窗通知

浮窗通知使用第三方组件[terminal-notifier](https://github.com/julienXX/terminal-notifier)

```shell
terminal-notifier -title "通知标题" -subtitle "副标题" -message "通知内容" -contentImage http://png地址.png
```

### 6.2.2 弹窗通知

- 弹窗通知默认使用使用系统命令`osascript`

```shell
osascript -e 'display dialog "通知内容" buttons {"确定"} default button 1 with title "标题"'
```

- 也可以使用第三方组件[macos-alert](https://gitee.com/xiaozhuai/macos-alert/)，可在`com.orainge.bark_processor.forward.client.util.macos.MacOSAlertUtil`修改；客户端配置如下

```yaml
# macos-alert 配置
macos-alert:
  # ICON 文件夹路径
  icon-folder-path: "/path/to/icon"
  # macos-alert 执行文件路径
  exec-path: "/path/to/macos-alert"
```

## 6.3 客户端运行监控脚本

- 通过在 crontab 中加入计划任务，实现客户端保活。
- 监控脚本：`/bark-processor-forward-client/src/main/resources/shell/bark_processor_client_monitor.sh`



