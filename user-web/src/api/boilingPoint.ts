import request from './request'

export interface BoilingPoint {
  id?: number
  userId?: number
  content: string
  imageUrls?: string
  likeCount?: number
  commentCount?: number
  createTime?: string | Date
}

export const boilingPointApi = {
  // 获取沸点列表
  getBoilingPointList: (params?: { pageNum?: number; pageSize?: number }) => {
    return request.get<BoilingPoint[]>('/content/boiling-point', { params })
  },
  // 发布沸点
  createBoilingPoint: (boilingPoint: BoilingPoint) => {
    return request.post<BoilingPoint>('/content/boiling-point', boilingPoint)
  },
  // 点赞沸点
  likeBoilingPoint: (id: number) => {
    return request.post(`/content/boiling-point/${id}/like`)
  }
}

