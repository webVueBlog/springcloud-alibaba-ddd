import request from './request'

export interface Comment {
  id?: number
  userId?: number
  username?: string
  avatar?: string
  targetType?: 'ARTICLE' | 'COMMENT'
  targetId?: number
  parentId?: number
  content: string
  likeCount?: number
  replyCount?: number
  auditStatus?: string
  status?: number
  createTime?: string | Date
  replies?: Comment[]
}

export interface CreateCommentParams {
  targetType: 'ARTICLE' | 'COMMENT'
  targetId: number
  parentId?: number
  content: string
}

export const commentApi = {
  // 获取评论列表
  getCommentList: (targetType: string, targetId: number, params?: { pageNum?: number; pageSize?: number }) => {
    return request.get<Comment[]>(`/content/comment`, {
      params: {
        targetType,
        targetId,
        ...params
      }
    })
  },
  // 创建评论
  createComment: (params: CreateCommentParams) => {
    return request.post<Comment>('/content/comment', params)
  },
  // 点赞评论
  likeComment: (id: number) => {
    return request.post(`/content/comment/${id}/like`)
  },
  // 删除评论
  deleteComment: (id: number) => {
    return request.delete(`/content/comment/${id}`)
  }
}

