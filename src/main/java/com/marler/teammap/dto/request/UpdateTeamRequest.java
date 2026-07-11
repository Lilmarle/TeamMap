package com.marler.teammap.dto.request;

import lombok.Data;

/**
 * 更新球队信息请求 DTO
 */
@Data
public class UpdateTeamRequest {
    private Long teamId;            // 球队ID
    private String teamName;        // 队伍名称
    private String teamShortName;   // 队伍简称
    private String teamLogo;        // 队伍Logo URL地址
    private String teamDescription; // 队伍描述
    private Integer gender;         // 性别：1-男，2-女，3-混合
    private Integer sportType;      // 运动类型：1-足球，2-篮球，3-排球
    private Integer teamRank;       // 队伍级别：1-院队，2-校队，3-班队
    private Long collegeId;         // 学院ID（校队可为空）
}
