import request from './request'

/**
 * 学院 API
 */
export const collegeApi = {
  /**
   * 获取所有学院列表
   * @returns {Promise<{code: number, data: Array, message: string}>}
   */
  getAll() {
    return request.get('/college')
  }
}
