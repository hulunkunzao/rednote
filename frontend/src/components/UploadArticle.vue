<template>
  <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
    <el-tab-pane label="上传视频" name="/video">
      <router-view v-if="activeTab === '/video'" />
    </el-tab-pane>
    <el-tab-pane label="上传图片" name="/image" >
      <router-view v-if="activeTab === '/image'" />
    </el-tab-pane>
    <el-tab-pane label="上传文字" name="/article" v-if="activeTab === '/article'">
      <div class="upload-article-container">
        <h2>写长文</h2>
        <el-form :model="articleForm" label-width="80px">
          <el-form-item label="标题">
            <el-input v-model="articleForm.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
          </el-form-item>
          
          <el-form-item label="封面">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :on-change="handleCoverChange"
              :auto-upload="false"
            >
              <img v-if="articleForm.cover" :src="articleForm.cover" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="内容">
            <el-input
              v-model="articleForm.content"
              type="textarea"
              :rows="10"
              placeholder="请输入文章内容"
              maxlength="5000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="标签">
            <el-tag
              v-for="tag in articleForm.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-model="newTag"
              placeholder="输入标签后按回车添加"
              @keyup.enter="addTag"
              class="tag-input"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="submitArticle">发布文章</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue'
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

const articleForm = ref({
  title: '',
  cover: '',
  content: '',
  tags: []
});

const newTag = ref('');

const handleCoverChange = (file) => {
  // 这里可以处理封面图片的上传逻辑
  // 示例：使用FileReader读取本地图片预览
  const reader = new FileReader();
  reader.onload = (e) => {
    articleForm.value.cover = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const addTag = () => {
  if (newTag.value && !articleForm.value.tags.includes(newTag.value)) {
    articleForm.value.tags.push(newTag.value);
    newTag.value = '';
  }
};

const removeTag = (tag) => {
  articleForm.value.tags = articleForm.value.tags.filter(t => t !== tag);
};

const submitArticle = () => {
  // 这里可以处理文章提交逻辑
  if (!articleForm.value.title) {
    ElMessage.warning('请输入文章标题');
    return;
  }
  if (!articleForm.value.content) {
    ElMessage.warning('请输入文章内容');
    return;
  }
  
  // 模拟提交成功
  ElMessage.success('文章发布成功');
  resetForm();
};

const resetForm = () => {
  articleForm.value = {
    title: '',
    cover: '',
    content: '',
    tags: []
  };
  newTag.value = '';
};
</script>

<style scoped>
.upload-article-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #303133;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-input {
  width: 200px;
  margin-top: 8px;
}
</style>