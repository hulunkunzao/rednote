<template>
  <div class="explore-view">
     <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="推荐" name="first"></el-tab-pane>
        <el-tab-pane label="学习成长" name="second">Config</el-tab-pane>
        <el-tab-pane label="美食探店" name="third">Role</el-tab-pane>
        <el-tab-pane label="穿搭美妆" name="fourth">Task</el-tab-pane>
        <el-tab-pane label="娱乐兴趣" name="fifth">Task</el-tab-pane>
        <el-tab-pane label="使用工具" name="sixth">Task</el-tab-pane>
    </el-tabs>
    <el-scrollbar ref="scrollbarRef" height="400px">
      <p v-for="item in num" :key="item" class="scrollbar-demo-item">
        {{ item }}
      </p>
    </el-scrollbar>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'

import type { TabsPaneContext } from 'element-plus'

const num = ref(30)
const scrollbarRef = ref()

const handleScroll = () => {
  if (scrollbarRef.value) {
    const wrapEl = scrollbarRef.value.wrapRef
    if (wrapEl) {
      const { scrollTop, clientHeight, scrollHeight } = wrapEl
      if (scrollTop + clientHeight >= scrollHeight - 10) {
        loadMore()
      }
    }
  }
}

const loadMore = () => {
  num.value += 5
}

const activeName = ref('first')

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event)
}

onMounted(() => {
  // 监听滚动事件
  if (scrollbarRef.value && scrollbarRef.value.wrapRef) {
    scrollbarRef.value.wrapRef.addEventListener('scroll', handleScroll)
  }
})
</script>

<style>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
.scrollbar-demo-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}
.el-slider {
  margin-top: 20px;
}
</style>