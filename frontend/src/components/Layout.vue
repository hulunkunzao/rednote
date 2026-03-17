<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <app-menu />
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <img src="/logo.png" alt="logo" class="logo">
            <h2>欢迎使用轻量版小宏书</h2>
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
  gap: 10px;
}

.header-content h2 {
  font-style: italic;
  margin: 0;
  color: red;
}
</style>