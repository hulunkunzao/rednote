<template>

  <div class="post-grid">
    <el-tabs @tab-click="handleTabClick">
        <el-tab-pane label="推荐" name="0"></el-tab-pane>
        <el-tab-pane v-for="topic in topicList" 
          :key="topic.topicId" :name="topic.topicId.toString()">
          <template #label>
            <el-tooltip :content="topic.topicDescription" placement="top">
              <span>{{ topic.topicName }}</span>
            </el-tooltip>
          </template>
        </el-tab-pane>
    </el-tabs>

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

    <div v-if="detailVisible">
      <!-- 遮罩层 -->
      <div class="modal-mask" @click="handleClose"></div>
      <div class="modal-content-wrap">
        <div class="left-card">
          <div class="image-container" v-if="imageUrlList.length > 0">
            <img 
                :src="imageUrlList[currentImageIndex]" 
                :alt="`Image ${currentImageIndex + 1}`"
                class="post-image"
            >
            <!-- 翻页按钮 -->
            <button 
                class="nav-btn prev-btn" 
                @click="prevImage"
                :disabled="currentImageIndex === 0"
            >
                &lt;
            </button>
            <button 
                class="nav-btn next-btn" 
                @click="nextImage"
                :disabled="currentImageIndex === imageUrlList.length - 1"
            >
                &gt;
            </button>
            <!-- 图片指示器 -->
            <div class="image-indicator">
                {{ currentImageIndex + 1 }} / {{ imageUrlList.length }}
            </div>
          </div>
          <div class="no-image" v-else>
              <p>暂无图片</p>
          </div>
        </div>
        
        <div class="right-card">
          <!-- 顶部固定区域：用户信息 + 帖子内容 -->
          <div class="right-card-header">
            <!-- 顶部用户信息 -->
            <div class="user-header">
              <img class="avatar" :src="bloggerInfo.avatar" alt="头像" />
              <span class="username">{{ bloggerInfo.username }}</span>
              <button class="follow-btn">关注</button>
            </div>

            <!-- 正文内容 -->
            <div class="post-content">
              <p class="post-title">{{ postDetail.title }}</p>
              <p class="post-desc">
                {{ postDetail.content }}
              </p>
              <p class="post-meta">发布时间{{ postDetail.createdTime }}</p>
            </div>
          </div>

          <!-- 中间可滚动评论区 -->
          <div class="comment-scroll-area">
            <div class="comment-section">
              <p class="comment-count">共{{ commentList.length }}条评论</p>
              <div class="comment-item" v-for="comment in commentList" :key="comment.commentId">
                <img class="comment-avatar" :src="comment.userAvatar" alt="评论头像" />
                <div class="comment-content">
                  <span class="comment-username">{{ comment.userName }}</span>
                  <p class="comment-text">{{ comment.content }}</p>
                  <p class="comment-meta">{{ comment.createTime }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部固定区域：评论输入 + 互动按钮 -->
          <div class="right-card-footer">
            <div class="bottom-bar">
              <input type="text" placeholder="说点什么..." 
              class="comment-input" 
              v-model="commentInfo.content" 
              @keyup.enter="handleSendComment"
              />
              <button class="send-btn" @click="handleSendComment">发送</button>
              <button class="like-btn" @click="handleLike">点赞</button>
              <span>{{ postDetail.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 关闭按钮 --> 
      <button class="close-btn" @click="handleClose">×</button>
    </div>
  </div>

</template>

<script>
import { getPostListApi,getPostDetailApi,getTopicListApi,getPostImagesApi } from '@/api/post'
import { getCommentListApi,addCommentApi } from '@/api/comment'
import { getUserInfoApi } from '@/api/user'
import { isLikedApi } from '@/api/like'
import { ElMessage } from 'element-plus' 
import '@/assets/baseHome.css'
export default {
    data() {
        return {
            postList: [],
            page: 1,
            size: 50,
            postDetail: {},
            detailVisible: false,
            topicList: [],
            topicId: 0,
            imageUrlList: [],
            currentImageIndex: 0,
            commentList: [],
            commentInfo: {
              postId: 0,
              content: '',
            },
            currentPostId: 0,
            isLiked: false,
            bloggerInfo: {},
        }
    },  
    methods: {
        async fetchPosts() {
          this.loading = true
          try {
              const response = await getPostListApi({
                topicId: this.topicId,
              })
              this.postList = response.data
          } catch (error) {
              console.error('获取帖子列表失败:', error)
          } finally {
              
          }
        },
        async fetchUserInfo(){
          try {
              const response = await getUserInfoApi(this.postDetail.userId)
              this.bloggerInfo = response.data
          } catch (error) {
              console.error('获取用户信息失败:', error)
          }
        },
        async handleSendComment() {
          if (!this.commentInfo.content.trim()) {
            ElMessage.warning('请输入评论内容')
            return
          }
          try {
            await addCommentApi(this.commentInfo)
            ElMessage.success('评论成功')
            this.commentInfo.content = ''
            this.fetchCommentList(this.currentPostId)
          } catch (error) {
            ElMessage.error('评论失败')
          }
        },
        async fetchPostImages(postId) {
          try {
              const response = await getPostImagesApi(postId)
              this.imageUrlList = response.data
          } catch (error) {
              console.error('获取帖子图片失败:', error)
          }
        },
        async fetchTopics() {
          try {
              const response = await getTopicListApi()
              this.topicList = response.data
          } catch (error) {
              console.error('获取话题列表失败:', error)
          }
        },
        async fetchCommentList(postId){
          try {
              const response = await getCommentListApi(postId)
              this.commentList = response.data
          } catch (error) {
              console.error('获取评论列表失败:', error)
          }
        },
        async getPostDetail(postId){
            try {
                const response = await getPostDetailApi(postId)
                this.postDetail = response.data
            } catch (error) {
                console.error('获取帖子详情失败:', error)
            }
            this.fetchUserInfo()
        },
        async handleLike(){
          try {
              const response = await isLikedApi(this.currentPostId)
              this.isLiked = response.data
              if(this.isLiked){
                ElMessage.success('点赞成功')
                this.postDetail.likeCount++
              }else{
                ElMessage.success('取消点赞成功')
                this.postDetail.likeCount--
              }
          } catch (error) {
              console.error('点赞失败:', error)
          }
        },
        handleTabClick(tab) {
          const topicId = parseInt(tab.paneName) 
          this.topicId = topicId
          this.fetchPosts()
        },
        showDetail(postId) {
          this.currentPostId = postId
          this.commentInfo.postId = postId
          console.log(postId)
          this.detailVisible = true
          this.currentImageIndex = 0 
          this.getPostDetail(postId)
          this.fetchPostImages(postId)
          this.fetchCommentList(postId)
          
        },
        handleClose() {
            this.detailVisible = false
            this.currentImageIndex = 0 
        },
        prevImage() {
          if (this.currentImageIndex > 0) {
            this.currentImageIndex--
          }
        },
        nextImage() {
          if (this.currentImageIndex < this.imageUrlList.length - 1) {
            this.currentImageIndex++
          }
        }
    },
    mounted() {
        this.fetchPosts()
        this.fetchTopics()
    }
}
</script>