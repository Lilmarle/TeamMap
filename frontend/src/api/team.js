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
    return request.get('/team/list')
  },

  /**
   * 添加球队
   * @param {Object} data - 球队数据
   * @returns {Promise}
   */
  addTeam(data) {
    return request.post('/team/add', data)
  },

  /**
   * 修改球队信息
   * @param {Object} data - 更新数据
   * @returns {Promise}
   */
  updateTeam(data) {
    return request.post('/team/update', data)
  }
}
