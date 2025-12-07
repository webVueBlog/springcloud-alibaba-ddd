import request from './request'

export interface Topic {
  id?: number
  topicName: string
  topicCode?: string
  coverImage?: string
  description?: string
  articleCount?: number
  followerCount?: number
  accessLevel?: string
}

export const topicApi = {
  // 获取专题列表
  getTopicList: () => {
    return request.get<Topic[]>('/content/topic')
  },
  // 获取专题详情
  getTopicById: (id: number) => {
    return request.get<Topic>(`/content/topic/${id}`)
  }
}

