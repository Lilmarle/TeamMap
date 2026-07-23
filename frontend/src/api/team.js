import request from './request'

/**
 * 球队 API
 */
export const teamApi = {
  /**
   * 获取所有球队信息
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getAllTeams() {
    return request.get('/teams')
  },

  /**
   * 根据运动类型查询球队列表
   * @param {number} type - 运动类型：1-足球，2-篮球，3-排球
   * @returns {Promise}
   */
  getTeamsByType(type) {
    return request.get(`/teams/type/${type}`)
  },

  /**
   * 添加球队
   * @param {Object} data - 球队数据
   * @returns {Promise}
   */
  addTeam(data) {
    return request.post('/teams', data)
  },

  /**
   * 修改球队信息
   * @param {Object} data - 更新数据
   * @returns {Promise}
   */
  updateTeam(data) {
    return request.put('/teams/' + data.teamId, data)
  },

  /**
   * 删除球队
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  deleteTeam(teamId) {
    return request.delete('/teams/' + teamId)
  },

  /**
   * 加入球队（添加队伍成员）
   * @param {Object} data - { teamId, userId, role }
   * @returns {Promise}
   */
  joinTeam(data) {
    return request.post('/team-members', data)
  },

  /**
   * 查询当前用户的队伍成员信息
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getCurrentMembership() {
    return request.get('/team-members/current')
  },

  /**
   * 查询指定球队的成员列表
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  getTeamMembers(teamId) {
    return request.get('/team-members/by-team', { params: { teamId } })
  },

  /**
   * 更新队伍成员信息（含球员信息联动更新）
   * @param {number} memberId - 队伍成员ID
   * @param {Object} data - { portraitPhoto, jerseyName, jerseyNumber, position }
   * @returns {Promise}
   */
  updateTeamMember(memberId, data) {
    return request.put(`/team-members/${memberId}/portrait`, data)
  },

  /**
   * 为队伍成员注册球员记录
   * @param {number} memberId - 队伍成员ID
   * @param {Object} data - { jerseyName, jerseyNumber, position }
   * @returns {Promise}
   */
  registerPlayerForMember(memberId, data) {
    return request.post(`/team-members/${memberId}/player-registration`, data)
  }
}
