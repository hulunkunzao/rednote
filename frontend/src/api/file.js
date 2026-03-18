import request from '@/utils/request'

export const uploadApi = (file) => request.post('/files/update', file)