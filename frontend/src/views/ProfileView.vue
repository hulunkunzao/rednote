<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="profile-avatar">
        <el-avatar size="100" :src="user.avatar">{{ user.name.charAt(0) }}</el-avatar>
      </div>
      <div class="profile-info">
        <h2>{{ user.name }}</h2>
        <p>{{ user.bio }}</p>
        <div class="profile-stats">
          <div class="stat-item">
            <span class="stat-value">{{ user.stats.posts }}</span>
            <span class="stat-label">笔记</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ user.stats.followers }}</span>
            <span class="stat-label">关注者</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ user.stats.following }}</span>
            <span class="stat-label">关注</span>
          </div>
        </div>
      </div>
    </div>
    <div class="profile-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="笔记" name="posts">
          <div class="posts-grid">
            <div v-for="post in user.posts" :key="post.id" class="post-item">
              <el-card :body-style="{ padding: '0px' }">
                <img :src="post.cover" class="post-cover" />
                <div class="post-content">
                  <h3>{{ post.title }}</h3>
                  <p>{{ post.excerpt }}</p>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收藏" name="favorites">
          <div class="empty-state">
            <el-empty description="暂无收藏" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="关注" name="following">
          <div class="empty-state">
            <el-empty description="暂无关注" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 模拟用户数据
const user = ref({
  name: '用户名称',
  bio: '这是个人简介',
  avatar: '',
  stats: {
    posts: 12,
    followers: 56,
    following: 34,
  },
  posts: [
    {
      id: 1,
      title: '我的第一篇笔记',
      excerpt: '这是笔记的摘要内容...',
      cover:
        'https://neeko-copilot.bytedance.net/api/text2image?prompt=Vue%203%20note%20cover&size=800x600',
    },
    {
      id: 2,
      title: '学习 Vue 3 的心得',
      excerpt: 'Vue 3 真的很棒...',
      cover:
        'https://neeko-copilot.bytedance.net/api/text2image?prompt=Vue%203%20learning&size=800x600',
    },
    {
      id: 3,
      title: '前端开发最佳实践',
      excerpt: '分享一些前端开发的最佳实践...',
      cover:
        'https://neeko-copilot.bytedance.net/api/text2image?prompt=Frontend%20best%20practices&size=800x600',
    },
  ],
})

const activeTab = ref('posts')
</script>

<style scoped>
.profile-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.profile-header {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-avatar {
  margin-right: 30px;
}

.profile-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.profile-info p {
  margin: 0 0 20px 0;
  color: #666;
}

.profile-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.profile-content {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.post-item {
  transition: transform 0.3s ease;
}

.post-item:hover {
  transform: translateY(-5px);
}

.post-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.post-content {
  padding: 15px;
}

.post-content h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
}

.post-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.empty-state {
  padding: 60px 0;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  .profile-avatar {
    margin-right: 0;
    margin-bottom: 20px;
  }

  .profile-stats {
    justify-content: center;
  }

  .posts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
