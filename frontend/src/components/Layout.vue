<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <app-menu />
      </el-aside>
      <el-container>
        <el-header>Header</el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import AppMenu from './AppMenu.vue';
</script>

<style scoped>
.common-layout {
  height: 100vh;
  width: 100%;
}
.common-layout:deep(.el-container){
  height: 100%;
}
</style>