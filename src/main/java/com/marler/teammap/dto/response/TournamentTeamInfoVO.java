package com.marler.teammap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
}
