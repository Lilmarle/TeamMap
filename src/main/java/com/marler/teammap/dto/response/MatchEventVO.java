package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 比赛事件 VO（对应 v_match_event 视图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEventVO {
    private Long eventId;               // 事件ID
    private Long matchId;              // 比赛ID
    private Integer sportType;          // 运动类型
    private String sportTypeName;       // 运动类型名称
    private Integer teamId;             // 队伍ID
    private String teamName;            // 队伍名称（简称）
    private Integer playerId;           // 球员ID
    private String playerName;          // 球员名称（球衣名）
    private Integer playerJersey;       // 球员球衣号码
    private Integer relatedPlayerId;    // 关联球员ID
    private String relatedPlayerName;   // 关联球员名称
    private Integer relatedPlayerJersey; // 关联球员球衣号码
    private Integer type;               // 事件类型编号
    private String typeName;            // 事件类型名称
    private String description;         // 事件描述
    private Integer eventTime;          // 事件发生时间（分钟）
    private String eventTimeDisplay;    // 事件时间显示
    private String extraInfo;           // 扩展信息（JSON）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;   // 创建时间
}
