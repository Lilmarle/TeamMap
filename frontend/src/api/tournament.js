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
  }
}
