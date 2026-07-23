import request from './request'

/**
 * 球员 API
 */
export const playerApi = {
  /**
   * 获取所有球员信息
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getAllPlayers() {
    return request.get('/players')
  },

  /**
   * 按球队查询球员信息
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  getPlayersByTeam(teamId) {
    return request.get(`/players/by-team?teamId=${teamId}`)
  },

  /**
   * 按用户ID查询球员信息
   * @param {number} userId - 用户ID
   * @returns {Promise}
   */
  getPlayerByUserId(userId) {
    return request.get(`/players/by-user?userId=${userId}`)
  },

  /**
   * 注册球员（创建球员记录）
   * @param {Object} data - { tmId, jerseyName, jerseyNumber, position }
   * @returns {Promise}
   */
  addPlayer(data) {
    return request.post('/players', data)
  },

  /**
   * 修改球员信息
   * @param {number} id - 球员ID
   * @param {Object} data - { jerseyName, jerseyNumber, position, status }
   * @returns {Promise}
   */
  updatePlayer(id, data) {
    return request.put(`/players/${id}`, data)
  },

  /**
   * 删除球员
   * @param {number} id - 球员ID
   * @returns {Promise}
   */
  deletePlayer(id) {
    return request.delete(`/players/${id}`)
  }
}
