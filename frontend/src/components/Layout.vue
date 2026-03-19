<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <app-menu />
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <div class="header-left">
              <img src="/logo.png" alt="logo" class="logo">
              <h2>欢迎使用轻量版小宏书</h2>
            </div>
            <div class="header-right" v-if="userInfo.username">
              <span class="user-name">{{ userInfo.username }}</span>
              <img 
                :src="userInfo.avatar" 
                :alt="userInfo.username" 
                class="user-avatar"
              >
              <span>(粉丝共{{ userDetail.fansCount }}人)</span>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import AppMenu from './AppMenu.vue'
import { getCurrentUserInfo, getCurrentUserDetail } from '@/api/user'
import { ref, onMounted } from 'vue'

const userInfo = ref({})
const userDetail = ref({})

const fetchUserInfo = async () => {
  try {
    const response = await getCurrentUserInfo()
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}
const fetchUserDetail = async () => {
  try {
    const response = await getCurrentUserDetail()
    userDetail.value = response.data
  } catch (error) {
    console.error('获取用户详情失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchUserDetail()
})
</script>

<style scoped>
.common-layout {
  height: 100vh;
  width: 100%;
}
.common-layout:deep(.el-container) {
  height: 100%;
}
.logo {
  width: 30px;
  height: 30px;
}
.header {
  height: 5px;
}
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left h2 {
  font-style: italic;
  margin: 0;
  color: red;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
}
</style>