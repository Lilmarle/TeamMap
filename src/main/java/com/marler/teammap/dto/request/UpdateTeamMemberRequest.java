package com.marler.teammap.dto.request;

import lombok.Data;

/**
 * 修改队伍成员信息请求 DTO
 * <p>
 * - 若有球员记录：可同时修改 team_member（定妆照）+ player（球衣名、号码、位置）
 * - 若无球员记录：只能修改 team_member（定妆照）
 */
@Data
public class UpdateTeamMemberRequest {
    /** 定妆照URL地址（team_member 字段） */
    private String portraitPhoto;

    // Player 字段（可选，仅当有球员记录时才生效）
    /** 球衣名称（印名） */
    private String jerseyName;

    /** 球衣号码（1-99） */
    private Integer jerseyNumber;

    /** 位置（如：前锋、后卫、守门员） */
    private String position;
}
