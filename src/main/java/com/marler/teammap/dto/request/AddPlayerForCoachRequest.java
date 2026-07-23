package com.marler.teammap.dto.request;

import lombok.Data;

/**
 * 为教练/领队（role >= 3）添加球员记录请求 DTO
 * <p>
 * 教练、领队等非队员角色也可以上场踢球，需要一个球员记录才能参与比赛。
 * 此 DTO 用于为已有的角色 >= 3 的队伍成员创建对应的球员记录。
 */
@Data
public class AddPlayerForCoachRequest {
    /** 球衣名称（印名） */
    private String jerseyName;

    /** 球衣号码（1-99） */
    private Integer jerseyNumber;

    /** 位置（如：前锋、后卫、守门员） */
    private String position;
}
