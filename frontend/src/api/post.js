import request from '@/utils/request'

export const getPostListApi = () => request.get('/posts/list' )

export const getPostDetailApi = (postId) => request.get(`/postsDetails/${postId}`)