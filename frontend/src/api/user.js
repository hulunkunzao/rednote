import request from '@/utils/request'

export const loginApi = (data) => request.post('/users/login', data)
export const registerApi = (data) => request.post('/users/register', data)

export const getUserInfoApi = (userId) => request.get(`/users/${userId}`)

export const followUserApi = (followUserId) => request.post(`/users/follow/${followUserId}`)

export const isFollowedApi = (followUserId) => request.get(`/users/follow/${followUserId}`)

export const getBloggersApi = () => request.get(`/users/followers`)

export const getCurrentUserInfo = () => request.get(`/users/current/me`)