package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentTeamInfoVO {
    private Long relId;
    private Long tournamentId;
    private String tournamentName;
    private Long teamId;
    private String teamName;
    private String teamShortName;
    private String teamLogo;
    private Integer sportType;
    private String sportTypeName;
    private Integer gender;
    private String genderName;
    private Integer status;
    private String statusName;
    private Integer memberCount;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
}
