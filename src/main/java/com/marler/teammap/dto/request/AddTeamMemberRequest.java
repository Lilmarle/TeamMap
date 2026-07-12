package com.marler.teammap.dto.request;

import lombok.Data;

/**
 * 添加队伍成员请求 DTO（同时创建球员记录）
 */
@Data
public class AddTeamMemberRequest {
    // TeamMember 字段
    private Long teamId;            // 队伍ID
    private Long userId;            // 用户ID
    private String portraitPhoto;   // 定妆照URL

    // Player 字段
    private String jerseyName;      // 球衣名称（印名）
    private Integer jerseyNumber;   // 球衣号码（1-99）
    private String position;        // 位置（如：前锋、后卫、守门员）
}
