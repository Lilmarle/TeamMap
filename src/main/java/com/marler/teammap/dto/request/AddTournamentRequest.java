package com.marler.teammap.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建赛事请求 DTO
 */
@Data
public class AddTournamentRequest {
    private String name;        // 赛事名称
    private Integer type;       // 运动类型：1-足球，2-篮球，3-排球
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;   // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;     // 结束时间
}
