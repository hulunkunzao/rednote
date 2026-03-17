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
        <el-card :body-style="{ padding: '0px' }" @click="showDetail(post)">
          <div class="post-image">
            <img 
              :src="post.coverImage" 
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
              <img class="avatar" :src="bloggerInfo.bloggerAvatar" alt="头像" />
              <span class="username">{{ bloggerInfo.bloggerName }}</span>
              <button class="follow-btn" @click="handleFollow">{{ followed ? '取消关注' : '关注' }}</button>
            </div>

            <!-- 正文内容 -->
            <div class="post-content">
              <p class="post-title">{{ post.title }}</p>
              <p class="post-desc">
                {{ post.content }}
              </p>
              <p class="post-meta">发布时间{{ post.createTime }}</p>
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
              <button class="like-btn" @click="handleLike">{{ isLiked ? '取消点赞' : '点赞' }}</button>
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
import { isLikedApi,likedApi } from '@/api/like'
import { ElMessage } from 'element-plus' 
import { followUserApi,isFollowedApi } from '@/api/user'
import '@/assets/baseHome.css'
export default {
    data() {
        return {
          postList: [],
          post: {},
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
          bloggerInfo: {
            bloggerId: 0,
            bloggerName: '',
            bloggerAvatar: '',
          },
          followed: false,
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
          }
        },
        async handleFollow(){
          try {
            const response = await followUserApi(this.bloggerInfo.bloggerId)
            this.followed = response.data
            if (response.code === 500){
              ElMessage.error('不能关注自己')
              return
            }
            if (this.followed) {
              ElMessage.success('关注成功')
            } else {
              ElMessage.success('已经取消关注')
            }
          } catch (error) {
            ElMessage.error('操作失败，请稍后重试')
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
        },
        async handleLike(){
          try {
              const response = await likedApi(this.currentPostId)
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
        async isLike(postId){
          try {
            const response = await isLikedApi(postId)
            this.isLiked = response.data
          } catch (error) {
            ElMessage.error('操作失败，请稍后重试')
          }
        },
        handleTabClick(tab) {
          const topicId = parseInt(tab.paneName) 
          this.topicId = topicId
          this.fetchPosts()
        },
        async isFollowed(bloggerId){
          try {
            const response = await isFollowedApi(bloggerId)
            this.followed = response.data
          } catch (error) {
            ElMessage.error('操作失败，请稍后重试')
          }
        },
        showDetail(post) {
          this.post = post
          this.currentPostId = post.postId
          this.commentInfo.postId = post.postId
          this.bloggerInfo.bloggerId = post.userId
          this.bloggerInfo.bloggerName = post.userName
          this.bloggerInfo.bloggerAvatar = post.userAvatar
          this.currentImageIndex = 0 
          this.getPostDetail(post.postId)
          this.fetchPostImages(post.postId)
          this.fetchCommentList(post.postId)
          this.isFollowed(post.userId)
          this.isLike(post.postId)
          this.detailVisible = true
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