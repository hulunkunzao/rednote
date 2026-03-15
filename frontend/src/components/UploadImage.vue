<template>
  <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
    <el-tab-pane label="上传视频" name="/video">
      <router-view v-if="activeTab === '/video'" />
    </el-tab-pane>
    <el-tab-pane label="上传图片" name="/image" v-if="activeTab === '/image'">
      <div class="upload-image-container">
        <h2>上传图文</h2>
        <el-form :model="imageForm" label-width="80px">
          <el-form-item label="图片">
            <el-upload
              class="image-uploader"
              action="#"
              :multiple="true"
              :on-change="handleImageChange"
              :auto-upload="false"
              list-type="picture-card"
            >
              <el-icon class="upload-icon"><Plus /></el-icon>
              <template :file="file">
                <div class="image-item">
                  <img :src="file.url" alt="" class="image-preview" />
                  <div class="image-actions">
                    <el-icon @click.stop="removeImage(file)" class="remove-icon"><Delete /></el-icon>
                  </div>
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="标题">
            <el-input v-model="imageForm.title" placeholder="请输入图文标题" maxlength="50" show-word-limit />
          </el-form-item>
          
          <el-form-item label="描述">
            <el-input
              v-model="imageForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入图文描述"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="标签">
            <el-tag
              v-for="tag in imageForm.tags"
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
            <el-button type="primary" @click="submitImage">发布图文</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-upload
        v-model:file-list="fileList"
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
      >
        <el-icon><Plus /></el-icon>
      </el-upload>
      <el-dialog v-model="dialogVisible">
        <img w-full :src="dialogImageUrl" alt="Preview Image" />
      </el-dialog>
    </el-tab-pane>
    <el-tab-pane label="上传文字" name="/article">
      <router-view v-if="activeTab === '/article'" />
    </el-tab-pane>
  </el-tabs>
  
</template>

<script lang="ts" setup>
import { Plus } from '@element-plus/icons-vue'

import type { UploadProps, UploadUserFile} from 'element-plus'
import{ElMessage} from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router';
import { ref, watch } from 'vue';

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
// 图文上传逻辑begin
const imageForm = ref({
  images: [],
  title: '',
  description: '',
  tags: []
});
 const file = ref({
  url:''
 });
const newTag = ref('');
 
const handleImageChange = (file, fileList) => {
  // 这里可以处理图片文件的上传逻辑
  // 示例：使用FileReader读取本地图片预览
  fileList.forEach(fileItem => {
    if (!fileItem.url) {
      const reader = new FileReader();
      reader.onload = (e) => {
        fileItem.url = e.target.result;
      };
      reader.readAsDataURL(fileItem.raw);
    }
  });
  imageForm.value.images = fileList;
};
 
const removeImage = (file) => {
  imageForm.value.images = imageForm.value.images.filter(item => item.uid !== file.uid);
};
 
const addTag = () => {
  if (newTag.value && !imageForm.value.tags.includes(newTag.value)) {
    imageForm.value.tags.push(newTag.value);
    newTag.value = '';
  }
};
 
const removeTag = (tag) => {
  imageForm.value.tags = imageForm.value.tags.filter(t => t !== tag);
};
 
const submitImage = () => {
  // 这里可以处理图文提交逻辑
  if (imageForm.value.images.length === 0) {
    ElMessage.warning('请上传图片');
    return;
  }
  if (!imageForm.value.title) {
    ElMessage.warning('请输入图文标题');
    return;
  }
  
  // 模拟提交成功
  ElMessage.success('图文发布成功');
  resetForm();
};
 
const resetForm = () => {
  imageForm.value = {
    images: [],
    title: '',
    description: '',
    tags: []
  };
  newTag.value = '';
};
// 图文上传逻辑end

const fileList = ref<UploadUserFile[]>([
  {
    name: 'food.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
  {
    name: 'plant-1.png',
    url: '/images/plant-1.png',
  },
  {
    name: 'food.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
  {
    name: 'plant-2.png',
    url: '/images/plant-2.png',
  },
  {
    name: 'food.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
  {
    name: 'figure-1.png',
    url: '/images/figure-1.png',
  },
  {
    name: 'food.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
  {
    name: 'figure-2.png',
    url: '/images/figure-2.png',
  },
])

const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
}

const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url!
  dialogVisible.value = true
}
</script>


<style scoped>
.upload-image-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
 
h2 {
  margin-bottom: 20px;
  color: #303133;
}
 
.image-uploader {
  margin-bottom: 20px;
}
 
.upload-icon {
  font-size: 28px;
  color: #8c939d;
}
 
.image-item {
  position: relative;
  width: 100px;
  height: 100px;
}
 
.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
 
.image-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 30px;
  opacity: 0;
  transition: opacity 0.3s;
}
 
.image-item:hover .image-actions {
  opacity: 1;
}
 
.remove-icon {
  cursor: pointer;
  font-size: 16px;
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