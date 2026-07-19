import request from './request'

/**
 * 用户 API
 */
export const userApi = {
  /**
   * 用户登录
   */
  login(data) {
    return request.post('/users/login', data)
  },

  /**
   * 用户注册（role 可选，默认 1-普通用户，3-赛事管理员，4-系统管理员）
   */
  register(data) {
    return request.post('/users', data)
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    return request.get('/users/session')
  },

  /**
   * 根据ID获取用户
   */
  getUserById(id) {
    return request.get(`/users/${id}`)
  },

  /**
   * 更新用户信息
   */
  updateUser(data) {
    return request.put('/users/update', data)
  },

  /**
   * 删除用户
   */
  deleteUser(id) {
    return request.delete(`/users/${id}`)
  },

  /**
   * 修改密码
   */
  changePassword(data) {
    return request.put('/users/change-password', data)
  },

  /**
   * 获取用户详细信息（联表查询 user + user_profile）
   */
  getUserProfile(userId) {
    return request.get('/user-profiles/' + userId)
  },

  /**
   * 更新用户档案（昵称、头像、性别、年龄）
   */
  updateProfile(data) {
    return request.put('/user-profiles/' + data.userId, data)
  },

  /**
   * 上传头像文件
   */
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
