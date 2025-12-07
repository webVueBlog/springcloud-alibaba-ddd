/**
 * 文本处理工具函数
 */

/**
 * 截断文本
 */
export function truncate(text: string, maxLength: number, suffix: string = '...'): string {
  if (!text || text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + suffix
}

/**
 * 提取文本摘要（去除HTML标签）
 */
export function extractSummary(html: string, maxLength: number = 150): string {
  if (!html) return ''
  
  // 移除HTML标签
  const text = html.replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim()
  return truncate(text, maxLength)
}

/**
 * 高亮搜索关键词
 */
export function highlightKeyword(text: string, keyword: string): string {
  if (!keyword || !text) return text
  
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}

