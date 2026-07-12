package com.marler.teammap.dto.request;

import lombok.Data;

/**
 * 创建赛事请求 DTO
 */
@Data
public class AddTournamentRequest {
    private String name;        // 赛事名称
    private Integer type;       // 运动类型：1-足球，2-篮球，3-排球
    private String startTime;   // 开始时间
    private String endTime;     // 结束时间
}
