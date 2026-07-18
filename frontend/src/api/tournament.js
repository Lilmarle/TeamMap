import request from './request'

/**
 * 赛事 API
 */
export const tournamentApi = {
  /**
   * 获取所有赛事
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getAll() {
    return request.get('/tournament')
  },

  /**
   * 获取自己创建的赛事（需登录）
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getMy() {
    return request.get('/tournament/my')
  },

  /**
   * 添加赛事
   * @param {Object} data - 赛事数据
   * @returns {Promise}
   */
  add(data) {
    return request.post('/tournament', data)
  },

  /**
   * 删除赛事
   * @param {number} id - 赛事ID
   * @returns {Promise}
   */
  delete(id) {
    return request.delete(`/tournament/${id}`)
  },

  /**
   * 获取赛事关联的球队列表
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getTournamentTeams(tournamentId) {
    return request.get(`/tournament-team/${tournamentId}/teams`)
  },

  /**
   * 主办方批量邀请球队加入赛事
   * @param {number} tournamentId - 赛事ID
   * @param {number[]} teamIds - 球队ID列表
   * @returns {Promise}
   */
  inviteBatch(tournamentId, teamIds) {
    return request.post(`/tournament-team/invite/batch/${tournamentId}`, teamIds)
  },

  /**
   * 主办方从赛事中移除球队
   * @param {number} tournamentId - 赛事ID
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  removeTeam(tournamentId, teamId) {
    return request.delete(`/tournament-team/${tournamentId}/${teamId}`)
  },

  // ==================== 小组赛相关 ====================

  /**
   * 获取赛事的所有小组
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getGroupStages(tournamentId) {
    return request.get(`/group-stage/tournament/${tournamentId}`)
  },

  /**
   * 添加单个小组
   * @param {Object} data - 小组数据
   * @returns {Promise}
   */
  addGroupStage(data) {
    return request.post('/group-stage', data)
  },

  /**
   * 批量添加小组
   * @param {Object} data - 批量小组数据 { tournamentId, groups: [...] }
   * @returns {Promise}
   */
  batchAddGroupStages(data) {
    return request.post('/group-stage/batch', data)
  },

  /**
   * 获取小组详情（含球队成绩，已联查球队名称和Logo）
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageDetail(groupStageId) {
    return request.get(`/group-stage/${groupStageId}/detail`)
  },

  /**
   * 删除小组（同时移除已分配的球队）
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  deleteGroupStage(groupStageId) {
    return request.delete(`/group-stage/${groupStageId}`)
  },

  /**
   * 获取某小组的所有球队及成绩
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageTeams(groupStageId) {
    return request.get(`/group-stage-team/group/${groupStageId}`)
  },

  /**
   * 为单个小组批量添加球队
   * @param {Object} data - { groupStageId, teamIds: [...] }
   * @returns {Promise}
   */
  addTeamsToGroup(data) {
    return request.post('/group-stage-team/batch', data)
  },

  /**
   * 为赛事所有小组分配球队
   * @param {Object} data - { tournamentId, assignments: [...] }
   * @returns {Promise}
   */
  assignTeamsToGroups(data) {
    return request.post('/group-stage-team/tournament', data)
  },

  // ==================== 比赛管理 ====================

  /**
   * 手动添加一场比赛（通用，可用于淘汰赛）
   * @param {Object} data - { tournamentId, team1Id, team2Id, stage, name?, matchTime?, location? }
   * @returns {Promise}
   */
  addMatch(data) {
    return request.post('/match', data)
  },

  /**
   * 修改比赛信息（比分、状态等）
   * @param {number} id - 比赛ID
   * @param {Object} data - { team1Score?, team2Score?, status?, stage?, name?, matchTime?, location? }
   * @returns {Promise}
   */
  updateMatch(id, data) {
    return request.put(`/match/${id}`, data)
  },

  /**
   * 获取某赛事的所有比赛
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getTournamentMatches(tournamentId) {
    return request.get(`/match/tournament/${tournamentId}`)
  },

  /**
   * 获取某小组的所有比赛
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageMatches(groupStageId) {
    return request.get(`/match/group-stage/${groupStageId}`)
  },

  // ==================== 赛程生成 ====================

  /**
   * 为指定小组生成赛程
   * @param {number} groupStageId - 小组ID
   * @param {Object} params - { startTime, intervalDays, matchIntervalMinutes, location }
   * @returns {Promise}
   */
  generateGroupSchedule(groupStageId, params = {}) {
    return request.post(`/group-stage/${groupStageId}/schedule`, params)
  },

  /**
   * 为整个赛事所有小组统一生成赛程
   * @param {number} tournamentId - 赛事ID
   * @param {Object} params - { startTime, matchIntervalMinutes, location }
   * @returns {Promise}
   */
  generateAllGroupSchedules(tournamentId, params = {}) {
    return request.post(`/group-stage/tournament/${tournamentId}/schedule`, params)
  }
}
