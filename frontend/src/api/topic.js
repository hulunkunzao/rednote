import request from '@/utils/request'
export const getAllTopicListApi = () => request.get('/topics/list')