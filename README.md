# 智慧停车场管理系统 (Smart Parking Management System)

Spring Boot + Vue 全栈项目，支持车位管理、停车记录、离场管理、预约管理、数据报表等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5、MyBatis、MySQL、JWT |
| 前端 | Vue 2、Element UI、ECharts |
| 工具 | Lombok、Hutool、PageHelper、SpringDoc (Swagger) |
| 构建 | Maven 3.9+、JDK 17 |

## 功能模块

- **车位管理**：车位 CRUD、按区域/状态筛选、分页查询
- **车位类型管理**：类型 CRUD（小型车/中型车/大型车/新能源车位等）
- **停车记录管理**：车辆入场登记、停车记录查询与维护
- **离场记录管理**：车辆出场登记、费用统计、历史离场记录查询
- **车主（驾驶员）管理**：车主信息 CRUD、关联车牌号
- **预约管理**：车位预约、预约状态跟踪
- **管理员管理**：管理员 CRUD、JWT 鉴权、角色区分
- **数据报表**：停车场收入 Top10 车位、高频车主 Top10、热门车位类型 Top10（ECharts 可视化图表）

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.0+

### 1. 创建数据库

```sql
CREATE DATABASE smart_parking DEFAULT CHARACTER SET utf8mb4;
```

### 2. 配置 application.yml

编辑 `springboot/src/main/resources/application.yml`，修改数据库连接信息。

### 3. 启动后端

```bash
cd springboot
mvn spring-boot:run
```

### 4. 启动前端

```bash
npm install
npm run serve
```

## 接口文档

启动后端后访问：http://localhost:8080/swagger-ui.html

## 项目结构

```
springboot/
├── src/main/java/com/example/springboot/
│   ├── common/          # 拦截器、跨域配置、Result 统一返回
│   ├── config/          # Spring 配置
│   ├── controller/      # REST 接口（ParkingSpot、ParkingRecord、Driver 等 8 个）
│   ├── entity/          # 实体类（ParkingSpot、ParkingRecord、ExitRecord 等 8 个）
│   ├── mapper/          # MyBatis Mapper 接口
│   ├── pojo/            # DTO、VO（报表数据传输对象）
│   ├── service/         # 业务逻辑接口与实现
│   └── utils/           # 工具类（TokenUtils JWT 等）
└── src/main/resources/
    ├── application.yml
    └── mapper/          # MyBatis XML 映射文件
```

## License

MIT