/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.mcpgateway.config;

import cn.zhuatech.mcpgateway.model.*;
import cn.zhuatech.mcpgateway.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(OperatingUnitRepository operatingUnits, WorkRecordRepository orders,
                           ResourceRegisterRepository resources, ReviewRecordRepository reviewRecords,
                           UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (operatingUnits.count() > 0) return;
            OperatingUnit primaryUnit = operatingUnits.save(new OperatingUnit("MCPGATEWAY-PLATFORM", "Agent 平台组", "AI 基础平台", 180));
            OperatingUnit secondaryUnit = operatingUnits.save(new OperatingUnit("MCPGATEWAY-DATA", "数据连接器组", "数据平台", 120));
            OperatingUnit tertiaryUnit = operatingUnits.save(new OperatingUnit("MCPGATEWAY-OFFICE", "办公自动化组", "企业应用中心", 96));

            WorkRecord t1 = orders.save(new WorkRecord("MCP-260801-018", "CRM-CONNECTOR", "CRM 客户查询工具接入", primaryUnit, 24, 16, 1, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "READ-SCOPE"));
            WorkRecord t2 = orders.save(new WorkRecord("MCP-260801-021", "DOC-SEARCH", "企业知识库检索服务接入", secondaryUnit, 18, 8, 0, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "DATA-SCOPE"));
            WorkRecord t3 = orders.save(new WorkRecord("MCP-260802-006", "MAIL-SENDER", "邮件发送工具写权限审批", tertiaryUnit, 12, 0, 0, LocalDate.now().plusDays(3), WorkRecord.Status.RELEASED, "WRITE-HIGH"));
            WorkRecord t4 = orders.save(new WorkRecord("MCP-260728-015", "CALENDAR-READ", "日程只读工具季度复核", tertiaryUnit, 20, 20, 1, LocalDate.now(), WorkRecord.Status.COMPLETED, "READ-LOW"));

            resources.saveAll(List.of(
                new ResourceRegister("GW-EDGE-01", "生产 MCP 网关集群", primaryUnit, ResourceRegister.Status.RUNNING, 96),
                new ResourceRegister("GW-SANDBOX-02", "工具沙箱执行池", primaryUnit, ResourceRegister.Status.IDLE, 82),
                new ResourceRegister("GW-AUDIT-03", "调用审计流水", secondaryUnit, ResourceRegister.Status.RUNNING, 99),
                new ResourceRegister("GW-SECRET-04", "短期凭证签发服务", tertiaryUnit, ResourceRegister.Status.ALARM, 68)
            ));
            reviewRecords.saveAll(List.of(
                new ReviewRecord("POL-260801-032", t1, "工具参数最小化复核", 6, 0, ReviewRecord.Result.PASSED, "何谨"),
                new ReviewRecord("POL-260801-011", t2, "数据范围校验", 3, 0, ReviewRecord.Result.PASSED, "陆遥"),
                new ReviewRecord("POL-260801-018", t4, "访问日志完整性检查", 5, 1, ReviewRecord.Result.FAILED, "何谨"),
                new ReviewRecord("POL-260802-003", t3, "外部写操作审批", 4, 0, ReviewRecord.Result.PENDING, "陆遥")
            ));
            String demo = encoder.encode("Demo@2026");
            users.saveAll(List.of(
                new UserAccount("operator", demo, "陆遥", UserAccount.Role.DOMAIN_USER, "MCPGATEWAY-PLATFORM"),
                new UserAccount("planner", demo, "何谨", UserAccount.Role.DOMAIN_OPERATOR, null),
                new UserAccount("quality", demo, "顾清", UserAccount.Role.QUALITY, null),
                new UserAccount("admin", encoder.encode("ZhuaTech@2026"), "系统管理员", UserAccount.Role.ADMIN, null)
            ));
        };
    }
}
