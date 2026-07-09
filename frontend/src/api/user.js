import request from './request'

/**
 * 用户 API
 */
export const userApi = {
  /**
   * 用户登录
   */
  login(data) {
    return request.post('/user/login', data)
  },

  /**
   * 用户注册
   */
  register(data) {
    return request.post('/user/register', data)
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    return request.get('/user/current')
  },

  /**
   * 根据ID获取用户
   */
  getUserById(id) {
    return request.get(`/user/${id}`)
  },

  /**
   * 更新用户信息
   */
  updateUser(data) {
    return request.put('/user/update', data)
  },

  /**
   * 删除用户
   */
  deleteUser(id) {
    return request.delete(`/user/${id}`)
  }
}
