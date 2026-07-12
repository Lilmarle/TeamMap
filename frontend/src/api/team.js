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
    return request.get('/team')
  },

  /**
   * 添加球队
   * @param {Object} data - 球队数据
   * @returns {Promise}
   */
  addTeam(data) {
    return request.post('/team', data)
  },

  /**
   * 修改球队信息
   * @param {Object} data - 更新数据
   * @returns {Promise}
   */
  updateTeam(data) {
    return request.put('/team/' + data.teamId, data)
  },

  /**
   * 删除球队
   * @param {number} teamId - 球队ID
   * @returns {Promise}
   */
  deleteTeam(teamId) {
    return request.delete('/team/' + teamId)
  }
}
