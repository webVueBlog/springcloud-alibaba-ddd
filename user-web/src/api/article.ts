import request from './request'

export interface Article {
  id?: number
  userId?: number
  title: string
  summary?: string
  coverImage?: string
  content: string
  contentType?: string
  categoryId?: number
  publishStatus?: string
  publishTime?: string | Date
  accessLevel?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
  collectCount?: number
  shareCount?: number
  isTop?: number
  isHot?: number
  isRecommend?: number
  tagIds?: number[]
  tagNames?: string[]
}

export const articleApi = {
  // 获取文章列表
  getArticleList: (params?: {
    categoryId?: number
    publishStatus?: string
    title?: string
    pageNum?: number
    pageSize?: number
  }) => {
    return request.get<Article[]>('/content/article', { params })
  },
  // 获取文章详情
  getArticleById: (id: number) => {
    return request.get<Article>(`/content/article/${id}`)
  },
  // 创建文章
  createArticle: (article: Article) => {
    return request.post<Article>('/content/article', article)
  },
  // 更新文章
  updateArticle: (id: number, article: Article) => {
    return request.put<Article>(`/content/article/${id}`, article)
  },
  // 发布文章
  publishArticle: (id: number) => {
    return request.post(`/content/article/${id}/publish`)
  },
  // 点赞文章
  likeArticle: (id: number) => {
    return request.post(`/content/article/${id}/like`)
  },
  // 取消点赞
  unlikeArticle: (id: number) => {
    return request.post(`/content/article/${id}/unlike`)
  }
}

