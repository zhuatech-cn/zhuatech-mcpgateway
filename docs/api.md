# MCP Gateway API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 网关运行与接入指标 |
| GET | `/api/admin/work-orders` | MCP 服务接入任务 |
| GET | `/api/shopfloor/dashboard` | 接入工程师工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交联调和策略验证结果 |
| POST | `/api/shopfloor/access-decision` | 对 MCP 工具调用执行 ALLOW、REVIEW 或 BLOCK 判定 |

除登录外均需 JWT。访问判定只保存参数摘要；生产实现应在网关链路内补充 requestId、主体和完整审计上下文。
