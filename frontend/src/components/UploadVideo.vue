<template>
  <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
    <el-tab-pane label="上传视频" name="/video" v-if="activeTab === '/video'">
      <div class="upload-video-container">
        <h2>上传视频</h2>
        <el-form :model="videoForm" label-width="80px">
          <el-form-item label="视频">
            <el-upload
              class="video-uploader"
              action="#"
              :show-file-list="false"
              :on-change="handleVideoChange"
              :auto-upload="false"
              accept="video/*"
            >
              <div v-if="videoForm.videoUrl" class="video-preview">
                <video :src="videoForm.videoUrl" controls class="video-player"></video>
                <el-button type="danger" size="small" @click="removeVideo">移除视频</el-button>
              </div>
              <el-icon v-else class="video-uploader-icon"><VideoCamera /></el-icon>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="标题">
            <el-input v-model="videoForm.title" placeholder="请输入视频标题" maxlength="50" show-word-limit />
          </el-form-item>
          
          <el-form-item label="描述">
            <el-input
              v-model="videoForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入视频描述"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="标签">
            <el-tag
              v-for="tag in videoForm.tags"
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
            <el-button type="primary" @click="submitVideo">发布视频</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-upload
        class="upload-demo"
        drag
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
        multiple
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽视频至此 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            jpg/png files with a size less than 500kb
          </div>
        </template>
      </el-upload>
    </el-tab-pane>
    <el-tab-pane label="上传图片" name="/image">
      <router-view v-if="activeTab === '/image'" />
    </el-tab-pane>
    <el-tab-pane label="上传文字" name="/article">
      <router-view v-if="activeTab === '/article'" />
    </el-tab-pane>
  </el-tabs>
  
</template>

<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router';
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const activeTab = ref(route.path);
// tag标签跳转begin
watch(() => route.path, (newPath) => {
  activeTab.value = newPath;
});

const handleDropdownCommand = (command) => {
  router.push(command);
};

const handleTabClick = (tab) => {
  router.push(tab.props.name);
};
// tag标签跳转end
const videoForm = ref({
  videoUrl: '',
  title: '',
  description: '',
  tags: []
});
 
const newTag = ref('');
 
const handleVideoChange = (file) => {
  // 这里可以处理视频文件的上传逻辑
  // 示例：使用FileReader读取本地视频预览
  const reader = new FileReader();
  reader.onload = (e) => {
    videoForm.value.videoUrl = e.target.result as string;
  };
  reader.readAsDataURL(file.raw);
};
 
const removeVideo = () => {
  videoForm.value.videoUrl = '';
};
 
const addTag = () => {
  if (newTag.value && !videoForm.value.tags.includes(newTag.value)) {
    videoForm.value.tags.push(newTag.value);
    newTag.value = '';
  }
};
 
const removeTag = (tag) => {
  videoForm.value.tags = videoForm.value.tags.filter(t => t !== tag);
};
 
const submitVideo = () => {
  // 这里可以处理视频提交逻辑
  if (!videoForm.value.videoUrl) {
    ElMessage.warning('请上传视频');
    return;
  }
  if (!videoForm.value.title) {
    ElMessage.warning('请输入视频标题');
    return;
  }
  
  // 模拟提交成功
  ElMessage.success('视频发布成功');
  resetForm();
};
 
const resetForm = () => {
  videoForm.value = {
    videoUrl: '',
    title: '',
    description: '',
    tags: []
  };
  newTag.value = '';
};
</script>

<style scoped>
.upload-video-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
 
h2 {
  margin-bottom: 20px;
  color: #303133;
}
 
.video-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
 
.video-uploader:hover {
  border-color: #409EFF;
}
 
.video-uploader-icon {
  font-size: 48px;
  color: #8c939d;
}
 
.video-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
 
.video-player {
  max-width: 100%;
  max-height: 250px;
  margin-bottom: 10px;
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