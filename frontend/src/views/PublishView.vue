<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu default-active="$route.path" router class="el-menu-vertical-demo menu-container">
            <el-dropdown @command="handleDropdownCommand">
              <span class="el-dropdown-link publish-dropdown">
                <el-icon class="publish-icon"><CirclePlus /></el-icon>
                  <span class="publish-text">发布笔记</span>
                    <el-icon class="el-icon--right publish-arrow">
                      <arrow-down />
                    </el-icon>
                  </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="/video"><el-icon><VideoCamera /></el-icon>上传视屏</el-dropdown-item>
                  <el-dropdown-item command="/image"><el-icon><Picture /></el-icon>上传图文</el-dropdown-item>
                  <el-dropdown-item command="/article"><el-icon><EditPen /></el-icon>写长文</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
        </el-header>
        <el-main >
            <!-- <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
              <el-tab-pane label="上传视频" name="/video">
                <router-view v-if="activeTab === '/video'" />
              </el-tab-pane>
              <el-tab-pane label="上传图片" name="/image">
                <router-view v-if="activeTab === '/image'" />
              </el-tab-pane>
              <el-tab-pane label="上传文字" name="/article">
                <router-view v-if="activeTab === '/article'" />
              </el-tab-pane>
            </el-tabs> -->
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { ref, watch } from 'vue';

const router = useRouter();
const route = useRoute();
const activeTab = ref(route.path);

watch(() => route.path, (newPath) => {
  activeTab.value = newPath;
});

const handleDropdownCommand = (command) => {
  router.push(command);
};

const handleTabClick = (tab) => {
  router.push(tab.props.name);
};
</script>

<style>

.menu-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 发布笔记下拉菜单样式 */
.publish-dropdown {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  background-color: #409EFF;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
}

.publish-dropdown:hover {
  background-color: #66B1FF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.publish-icon {
  font-size: 18px;
  margin-right: 8px;
}

.publish-text {
  font-size: 16px;
  margin-right: 8px;
}

.publish-arrow {
  font-size: 14px;
}
</style>