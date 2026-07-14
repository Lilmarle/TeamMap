package com.marler.teammap.dto.request;

/**
 * 生成小组赛赛程请求
 * <p>
 * 每天固定三个比赛时段：12:00、14:00、16:00
 */
public class GenerateGroupScheduleRequest {

    /** 第一个比赛日的日期（格式：yyyy-MM-dd），为空则从当前日期开始 */
    private String startDate;

    /** 比赛地点（可选，所有比赛共用） */
    private String location;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
