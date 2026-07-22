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
    return request.get('/tournaments')
  },

  /**
   * 获取自己创建的赛事（需登录）
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getMy() {
    return request.get('/tournaments/my')
  },

  /**
   * 添加赛事
   * @param {Object} data - 赛事数据
   * @returns {Promise}
   */
  add(data) {
    return request.post('/tournaments', data)
  },

  /**
   * 删除赛事
   * @param {number} id - 赛事ID
   * @returns {Promise}
   */
  delete(id) {
    return request.delete(`/tournaments/${id}`)
  },

  /**
   * 获取赛事关联的球队列表
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getTournamentTeams(tournamentId) {
    return request.get(`/tournament-teams/${tournamentId}/teams`)
  },

  /**
   * 主办方批量邀请球队加入赛事
   * @param {number} tournamentId - 赛事ID
   * @param {number[]} teamIds - 球队ID列表
   * @returns {Promise}
   */
  inviteBatch(tournamentId, teamIds) {
    return request.post('/tournament-teams/batch', { tournamentId, teamIds })
  },

  /**
   * 主办方从赛事中移除球队
   * @param {number} tournamentId - 赛事ID
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  removeTeam(tournamentId, teamId) {
    return request.delete(`/tournament-teams/${tournamentId}/${teamId}`)
  },

  /**
   * 审批球队申请（通过/驳回）
   * @param {number} relId - 关联记录ID
   * @param {number} status - 目标状态：2-已通过，3-未通过
   * @returns {Promise}
   */
  approveTeam(relId, status) {
    return request.patch(`/tournament-teams/${relId}/status`, { status })
  },

  /**
   * 按球队查询赛事的球员报名信息
   * @param {number} tournamentId - 赛事ID
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  getTeamPlayers(tournamentId, teamId) {
    return request.get('/tournament-players/by-team', { params: { tournamentId, teamId } })
  },

  // ==================== 小组赛相关 ====================

  /**
   * 获取赛事的所有小组
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getGroupStages(tournamentId) {
    return request.get(`/group-stages/tournament/${tournamentId}`)
  },

  /**
   * 添加单个小组
   * @param {Object} data - 小组数据
   * @returns {Promise}
   */
  addGroupStage(data) {
    return request.post('/group-stages', data)
  },

  /**
   * 批量添加小组
   * @param {Object} data - 批量小组数据 { tournamentId, groups: [...] }
   * @returns {Promise}
   */
  batchAddGroupStages(data) {
    return request.post('/group-stages/batch', data)
  },

  /**
   * 获取小组详情（含球队成绩，已联查球队名称和Logo）
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageDetail(groupStageId) {
    return request.get(`/group-stages/${groupStageId}/detail`)
  },

  /**
   * 删除小组（同时移除已分配的球队）
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  deleteGroupStage(groupStageId) {
    return request.delete(`/group-stages/${groupStageId}`)
  },

  /**
   * 获取某小组的所有球队及成绩
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageTeams(groupStageId) {
    return request.get(`/group-stage-teams/group/${groupStageId}`)
  },

  /**
   * 为单个小组批量添加球队
   * @param {Object} data - { groupStageId, teamIds: [...] }
   * @returns {Promise}
   */
  addTeamsToGroup(data) {
    return request.post('/group-stage-teams/batch', data)
  },

  /**
   * 为赛事所有小组分配球队
   * @param {Object} data - { tournamentId, assignments: [...] }
   * @returns {Promise}
   */
  assignTeamsToGroups(data) {
    return request.post('/group-stage-teams/tournament', data)
  },

  // ==================== 比赛管理 ====================

  /**
   * 手动添加一场比赛（通用，可用于淘汰赛）
   * @param {Object} data - { tournamentId, team1Id, team2Id, stage, name?, matchTime?, location? }
   * @returns {Promise}
   */
  addMatch(data) {
    return request.post('/matches', data)
  },

  /**
   * 修改比赛信息（比分、状态等）
   * @param {number} id - 比赛ID
   * @param {Object} data - { team1Score?, team2Score?, status?, stage?, name?, matchTime?, location? }
   * @returns {Promise}
   */
  updateMatch(id, data) {
    return request.put(`/matches/${id}`, data)
  },

  /**
   * 获取某赛事的所有比赛
   * @param {number} tournamentId - 赛事ID
   * @returns {Promise}
   */
  getTournamentMatches(tournamentId) {
    return request.get(`/matches/tournament/${tournamentId}`)
  },

  /**
   * 获取某小组的所有比赛
   * @param {number} groupStageId - 小组ID
   * @returns {Promise}
   */
  getGroupStageMatches(groupStageId) {
    return request.get(`/matches/group-stage/${groupStageId}`)
  },

  // ==================== 赛程生成 ====================

  /**
   * 为指定小组生成赛程
   * @param {number} groupStageId - 小组ID
   * @param {Object} params - { startTime, intervalDays, matchIntervalMinutes, location }
   * @returns {Promise}
   */
  generateGroupSchedule(groupStageId, params = {}) {
    return request.post(`/group-stages/${groupStageId}/schedule`, params)
  },

  /**
   * 为整个赛事所有小组统一生成赛程
   * @param {number} tournamentId - 赛事ID
   * @param {Object} params - { startTime, matchIntervalMinutes, location }
   * @returns {Promise}
   */
  generateAllGroupSchedules(tournamentId, params = {}) {
    return request.post(`/group-stages/tournament/${tournamentId}/schedule`, params)
  },

  // ==================== 比赛事件 ====================

  /**
   * 添加比赛事件
   * @param {Object} data - { matchId, sportType, teamId, playerId?, relatedPlayerId?, type, description?, eventTime, extraInfo? }
   * @returns {Promise}
   */
  addMatchEvent(data) {
    return request.post('/match-events', data)
  },

  /**
   * 获取某场比赛的所有事件
   * @param {number} matchId - 比赛ID
   * @returns {Promise}
   */
  getMatchEvents(matchId) {
    return request.get(`/match-events/${matchId}`)
  },

  /**
   * 获取某场比赛的事件统计（按队伍分组）
   * @param {number} matchId - 比赛ID
   * @returns {Promise}
   */
  getMatchEventStats(matchId) {
    return request.get(`/match-events/${matchId}/stats`)
  },

  /**
   * 获取比赛球员表现列表（含球员名称、球衣号码等）
   * @param {number} matchId - 比赛ID
   * @returns {Promise}
   */
  getMatchPlayers(matchId) {
    return request.get(`/match-players/performance/${matchId}`)
  }
}
