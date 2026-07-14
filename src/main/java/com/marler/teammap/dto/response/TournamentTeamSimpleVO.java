package com.marler.teammap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentTeamSimpleVO {
    private Long teamId;
    private String teamName;
    private String teamLogo;
}
