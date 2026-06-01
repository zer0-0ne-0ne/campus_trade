// 图片工具模块，提供图片占位符和图片路径处理功能

// 商品图片占位符（400x300）
const PLACEHOLDER_IMG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'%3E%3Crect fill='%23f0f0f0' width='400' height='300'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' fill='%23bbb' font-size='20'%3E%E6%97%A0%E5%9B%BE%E7%89%87%3C/text%3E%3C/svg%3E"

// 用户头像占位符（100x100）
const PLACEHOLDER_AVATAR = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Crect fill='%23e8e8e8' width='100' height='100'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' fill='%23bbb' font-size='14'%3E%E5%A4%B4%E5%83%8F%3C/text%3E%3C/svg%3E"

// 获取商品图片路径，如果图片路径为空则返回占位符
export function getProductImage(imagePath, pid) {
  if (imagePath) return imagePath
  return PLACEHOLDER_IMG
}

// 获取用户头像路径，如果头像路径为空则返回占位符
export function getAvatar(avatarPath) {
  if (avatarPath) return avatarPath
  return PLACEHOLDER_AVATAR
}

export { PLACEHOLDER_IMG, PLACEHOLDER_AVATAR }
