╔══════════════════════════════════════════════════════╗
║          图书管理系统 - 一键启动说明书                 ║
╚══════════════════════════════════════════════════════╝

【前置条件（只需安装以下软件）】

  1. Node.js 12 或以上
     下载：https://nodejs.org/
     验证：命令行执行 node --version

  无需 Java / Maven / MySQL！（使用 Node.js 模拟后端）


【快速开始】

  双击 start.bat → 自动完成以下操作：
    1. 检查 Node.js 环境
    2. 安装前端依赖 npm install（仅首次运行）
    3. 安装模拟后端依赖（仅首次运行）
    4. 启动模拟后端服务（端口 9090）
    5. 启动前端服务（端口 8080）
    6. 自动打开浏览器访问 http://localhost:8080


【停止服务】

  双击 stop.bat → 自动关闭前后端服务


【演示账号】

  管理员：admin / 123456
  用  户：user1 / 123456


【访问地址】

  前端页面：http://localhost:8080
  后端接口：http://localhost:9090


【目录结构】

  BookManageSystem\
  ├── start.bat                  ← 双击启动
  ├── stop.bat                   ← 双击停止
  ├── mock-server\               ← 模拟后端（Node.js + Express）
  │   ├── server.js              ← 所有 API 接口 + 模拟数据
  │   └── package.json
  └── src\                       ← 前端（Vue 2 + Element UI）
      ├── router\index.js        ← 路由配置
      └── utils\request.js       ← API 请求配置


【模拟后端说明】

  由于本机无 Java 环境，系统使用 Node.js 模拟后端来代替 Spring Boot。
  模拟后端包含：

  - 完整的 CRUD API（管理员/用户/图书/分类/借阅/预约）
  - 内置演示数据（10本图书、3个用户、3条公告等）
  - 分页、搜索、统计图表接口
  - 文件上传下载接口
  - 用户认证 token 机制

  数据存储在内存中，重启后会恢复到初始演示数据。


【常见问题】

  Q: 双击 start.bat 后出现中文乱码怎么办？
  A: 右键 cmd 窗口标题栏 → 属性 → 字体 → 选择"新宋体"或"Consolas"

  Q: 提示"未找到 Node.js"怎么办？
  A: 需要安装 Node.js，下载地址：https://nodejs.org/

  Q: 端口被占用怎么办？
  A: 先双击 stop.bat 释放端口，然后重新运行 start.bat

  Q: 首次启动很慢？
  A: 首次需要下载 npm 依赖，请耐心等待几分钟

  Q: 数据能保存吗？
  A: 模拟后端数据存储在内存中，重启后会恢复初始数据。
     如需真实数据库，请配置 Java + MySQL + Spring Boot 环境，
     原始后端代码在 springboot\ 目录下。