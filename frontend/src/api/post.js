import request from '@/utils/request'

export const getPostListApi = (params) => request.get('/posts/list', {params})

export const getPostDetailApi = (postId) => request.get(`/postsDetails/${postId}`)

export const getTopicListApi = () => request.get('/topics/list')

export const getPostImagesApi = (postId) => request.get(`/post-images/${postId}`)
