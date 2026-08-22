/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.mcpgateway.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 对 MCP 工具调用执行本地、确定性的最小权限决策。 */
@Service
public class McpAccessPolicyService {
    public record Request(@NotBlank String serverName, @NotBlank String toolName,
                          @NotBlank String userRole, boolean readOnly,
                          boolean sensitiveData, boolean externalWrite,
                          boolean approvedServer, boolean scopedCredential) {}
    public record Result(String serverName, String toolName, int riskScore,
                         String decision, List<String> reasons, List<String> auditFields) {}

    public Result decide(Request request) {
        int score = 5;
        List<String> reasons = new ArrayList<>();
        if (!request.approvedServer()) { score += 55; reasons.add("服务端未进入可信注册表"); }
        if (!request.scopedCredential()) { score += 30; reasons.add("凭证未限定到当前服务与工具"); }
        if (request.sensitiveData()) { score += 20; reasons.add("调用涉及敏感数据"); }
        if (request.externalWrite()) { score += 25; reasons.add("工具会对外部系统产生写操作"); }
        if (request.readOnly()) score = Math.max(0, score - 10);
        if ("ADMIN".equalsIgnoreCase(request.userRole())) score = Math.max(0, score - 5);
        score = Math.min(score, 100);
        String decision = score >= 70 ? "BLOCK" : score >= 35 ? "REVIEW" : "ALLOW";
        if (reasons.isEmpty()) reasons.add("可信服务、范围凭证与只读策略均满足");
        return new Result(request.serverName(), request.toolName(), score, decision, reasons,
            List.of("requestId", "subject", "server", "tool", "argumentsDigest", "decision", "latencyMs"));
    }
}
