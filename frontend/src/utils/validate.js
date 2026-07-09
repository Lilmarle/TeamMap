/**
 * 校验工具
 */

// 手机号正则
export const isMobile = (value) => /^1[3-9]\d{9}$/.test(value)

// 邮箱正则
export const isEmail = (value) => /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(value)

// URL 正则
export const isUrl = (value) => /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/.test(value)
