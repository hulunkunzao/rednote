import request from '@/utils/request'

export const getPostListApi = (params) => request.get('/posts/list', {params})

export const getPostDetailApi = (postId) => request.get(`/postsDetails/${postId}`)

export const getTopicListApi = () => request.get('/topics/list')

export const getPostImagesApi = (postId) => request.get(`/post-images/${postId}`)

export const getBloggerPostsApi = (bloggerId) => request.get(`/posts/list/${bloggerId}`)

export const createPostApi = (postDTO) => request.post('/posts/insert', postDTO)