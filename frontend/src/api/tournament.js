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
  }
}
