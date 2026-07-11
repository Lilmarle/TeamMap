package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 球队信息视图 v_team_info 映射类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInfoVO {
    private Long teamId;
    private String teamName;
    private String teamShortName;
    private String teamLogo;
    private String teamDescription;
    private Integer gender;
    private String genderName;
    private String collegeFullName;
    private String collegeShortName;
    private Integer teamRank;
    private String rankName;
    private Integer sportType;
    private String sportTypeName;
    private Long memberCount;
    private Long captainCount;
    private Long playerCount;
    private Long coachCount;
    private Long leaderCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
