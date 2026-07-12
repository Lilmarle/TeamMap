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
    return request.get('/user/session')
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
  },

  /**
   * 修改密码
   */
  changePassword(data) {
    return request.put('/user/change-password', data)
  },

  /**
   * 获取用户详细信息（联表查询 user + user_profile）
   */
  getUserProfile(userId) {
    return request.get('/user-profile/' + userId)
  },

  /**
   * 更新用户档案（昵称、头像、性别、年龄）
   */
  updateProfile(data) {
    return request.put('/user-profile/' + data.userId, data)
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
