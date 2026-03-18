import request from '@/utils/request'

export const uploadFileApi = (data) => request.post('/files/update', data)
