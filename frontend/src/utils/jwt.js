/**
 * JWT 解析工具
 * 用于从前端 localStorage 中解析 token 中的用户信息（userId, username, role）
 */

/**
 * 解析 JWT Token，返回 payload 对象
 * @param {string} token - JWT Token
 * @returns {object|null} 解析后的 payload，失败返回 null
 */
export function parseToken(token) {
  if (!token) return null
  try {
    // JWT 格式：header.payload.signature，取第二部分
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const payload = parts[1]
    // Base64 URL-safe 解码
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch (e) {
    console.error('JWT 解析失败:', e)
    return null
  }
}

/**
 * 从当前 token 中获取用户角色
 * @returns {number|null} 角色：1-普通用户，2-体育用户，3-赛事管理员，4-系统管理员
 */
export function getUserRole() {
  const token = localStorage.getItem('token')
  const payload = parseToken(token)
  return payload?.role ?? null
}

/**
 * 从当前 token 中获取用户 ID
 * @returns {number|null}
 */
export function getUserId() {
  const token = localStorage.getItem('token')
  const payload = parseToken(token)
  return payload?.sub ? Number(payload.sub) : null
}

/**
 * 从当前 token 中获取用户名
 * @returns {string|null}
 */
export function getUsername() {
  const token = localStorage.getItem('token')
  const payload = parseToken(token)
  return payload?.username ?? null
}

/**
 * 角色名称映射
 */
export const ROLE_MAP = {
  1: '普通用户',
  2: '体育用户',
  3: '赛事管理员',
  4: '系统管理员'
}

/**
 * 角色路由路径映射
 */
export const ROLE_ROUTE_MAP = {
  1: '/home/normal',
  2: '/home/sports',
  3: '/home/event-admin',
  4: '/home/system-admin'
}

/**
 * 根据角色获取首页路由路径
 * @param {number} role
 * @returns {string}
 */
export function getHomeRouteByRole(role) {
  return ROLE_ROUTE_MAP[role] || '/'
}
