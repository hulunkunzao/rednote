<template>

  <div class="post-grid">
    <el-row :gutter="20">
      <el-col 
        v-for="post in postList" 
        :key="post.postId" 
        :xs="24" 
        :sm="12" 
        :md="8" 
        :lg="6" 
        class="post-col"
      >
        <el-card :body-style="{ padding: '0px' }" @click="showDetail(post.postId)">
          <div class="post-image">
            <img 
              :src="post.coverUrl" 
              :alt="post.title"
            >
          </div>
          <div class="post-content">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-description">{{ post.content }}</p>
            <div class="post-user">
              <img 
                :src="post.userAvatar" 
                :alt="post.createTime" 
                class="avatar"
              >
              <span>{{ post.userName }}</span>
              <span class="time">{{ post.createTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-drawer
      v-model="detailVisible"
      direction="rtl"
      size="50%"
      :before-close="handleClose"
      title="帖子详情"
    >
      <div class="post-detail">
        <h2 class="detail-title">{{ postDetail.title }}</h2>
        <div class="detail-user">
          <!-- <img :src="postDetail.userAvatar || 'https://via.placeholder.com/40'" :alt="postDetail.userName" class="detail-avatar"> -->
          <!-- <span class="detail-username">{{ postDetail.userName }}</span>
          <span class="detail-time">{{ postDetail.createTime }}</span> -->
          <span> 点赞量：{{ postDetail.likeCount }}</span>
          <img :src="postDetail.coverImage" :alt="postDetail.title">
          {{ postDetail.coverImage }}
          <p class="detail-text">{{ postDetail.content }}</p>
        </div>
      </div>
    </el-drawer>
  </div>

</template>

<script>
import { getPostListApi,getPostDetailApi } from '@/api/post'
export default {
    data() {
        return {
            postList: [],
            page: 1,
            size: 50,
            postDetail: {},
            detailVisible: false,
        }
    },  
    methods: {
        async fetchPosts() {
            this.loading = true
            try {
                const response = await getPostListApi({
                page: this.page,
                size: this.size
                })
                this.postList = response.data
            } catch (error) {
                console.error('获取帖子列表失败:', error)
            } finally {
                
            }
        },
        async getPostDetail(postId){
            try {
                const response = await getPostDetailApi(postId)
                this.postDetail = response.data
            } catch (error) {
                console.error('获取帖子详情失败:', error)
            }
        },
        showDetail(postId) {
            console.log(postId)
            this.detailVisible = true
            console.log(this.detailVisible)
            this.getPostDetail(postId)
        },
        handleClose() {
            this.detailVisible = false
        }
    },
    mounted() {
        this.fetchPosts()
    }
}
</script>

<style>
.post-grid {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.post-col {
  margin-bottom: 20px;
}
/* 卡片图片样式 */
.post-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}
.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保证图片比例不变，填充区域 */
  transition: transform 0.3s;
}
.post-image img:hover {
  transform: scale(1.05);
}
/* 卡片内容样式 */
.post-content {
  padding: 15px;
}
.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  line-height: 1.4;
  /* 超出两行省略 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.post-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  /* 超出两行省略 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.post-user {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
}
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 8px;
  object-fit: cover;
}
.el-card {
  border-radius: 12px; 
  overflow: hidden;
  transition: box-shadow 0.3s;
}
.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.time {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}
</style>