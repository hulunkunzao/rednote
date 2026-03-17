import request from '@/utils/request'

export const isLikedApi = (postId) => request.post(`/likes/isliked/${postId}` )
