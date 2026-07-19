package com.marler.teammap.dto.request;

/**
 * 添加赛事-球队关联请求
 */
public class AddTournamentTeamRequest {
    private Long tournamentId;
    private Long teamId;

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
