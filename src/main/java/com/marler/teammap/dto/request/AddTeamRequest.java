package com.marler.teammap.dto.request;

import com.marler.teammap.pojo.Team;
import lombok.Data;

/**
 * 添加球队请求 DTO
 */
@Data
public class AddTeamRequest {
    private Team team;
    private Integer rank;       // 队伍级别：1-院队，2-校队，3-班队
    private Long collegeId;     // 学院ID（校队可为空）
}
