package com.marler.teammap.dto.response;

import java.util.List;

/**
 * 小组详情 VO（包含小组信息 + 球队成绩）
 */
public class GroupStageDetailVO {
    private Integer id;                 // 小组ID
    private String name;                // 小组名称
    private Integer teamCount;          // 球队总数
    private Integer directAdvance;      // 直接出线数
    private Integer indirectAdvance;    // 间接出线数
    private List<TeamScoreItem> teams;  // 球队成绩列表

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(Integer teamCount) {
        this.teamCount = teamCount;
    }

    public Integer getDirectAdvance() {
        return directAdvance;
    }

    public void setDirectAdvance(Integer directAdvance) {
        this.directAdvance = directAdvance;
    }

    public Integer getIndirectAdvance() {
        return indirectAdvance;
    }

    public void setIndirectAdvance(Integer indirectAdvance) {
        this.indirectAdvance = indirectAdvance;
    }

    public List<TeamScoreItem> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamScoreItem> teams) {
        this.teams = teams;
    }

    /**
     * 球队成绩项
     */
    public static class TeamScoreItem {
        private Integer teamId;            // 队伍ID
        private String teamName;           // 队伍名称
        private String teamShortName;      // 队伍简称
        private String logo;               // 队伍Logo
        private Integer win;               // 胜场数
        private Integer draw;              // 平场数
        private Integer loss;              // 负场数
        private Integer points;            // 积分
        private Integer goalsFor;          // 进球数（得分）
        private Integer goalsAgainst;      // 失球数（失分）
        private Integer goalDifference;    // 净胜球

        public Integer getTeamId() {
            return teamId;
        }

        public void setTeamId(Integer teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamShortName() {
            return teamShortName;
        }

        public void setTeamShortName(String teamShortName) {
            this.teamShortName = teamShortName;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public Integer getWin() {
            return win;
        }

        public void setWin(Integer win) {
            this.win = win;
        }

        public Integer getDraw() {
            return draw;
        }

        public void setDraw(Integer draw) {
            this.draw = draw;
        }

        public Integer getLoss() {
            return loss;
        }

        public void setLoss(Integer loss) {
            this.loss = loss;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        public Integer getGoalsFor() {
            return goalsFor;
        }

        public void setGoalsFor(Integer goalsFor) {
            this.goalsFor = goalsFor;
        }

        public Integer getGoalsAgainst() {
            return goalsAgainst;
        }

        public void setGoalsAgainst(Integer goalsAgainst) {
            this.goalsAgainst = goalsAgainst;
        }

        public Integer getGoalDifference() {
            return goalDifference;
        }

        public void setGoalDifference(Integer goalDifference) {
            this.goalDifference = goalDifference;
        }
    }
}
