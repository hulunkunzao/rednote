<template>
  <div class="upload-image-page">
    <div class="upload-image-container">
      <!-- 初始上传界面 -->
      <div v-if="!isUploadStarted" class="initial-upload">
        <div class="upload-header">
          <h3>图片上传</h3>
          <p>支持 JPG、PNG、WEBP 格式，单张不超过 20MB，最多上传 9 张</p>
        </div>

        <el-upload
          class="image-upload-zone"
          action="/file/update"
          :file-list="imageFileList"
          :before-upload="beforeImageUpload"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          :on-remove="handleRemove"
          :auto-upload="false"
          accept="image/jpeg,image/png,image/webp"
          :limit="9"
          list-type="picture-card"
        >
          <div class="upload-box">
            <i class="el-icon-picture" />
            <div class="upload-text">点击或拖拽图片上传</div>
            <div class="upload-tip">仅支持 JPG/PNG/WEBP，≤20MB，最多 9 张</div>
          </div>
        </el-upload>
      </div>

      <div v-else class="publish-edit">
        

        <div class="main-content">
          <div class="edit-area">
            <div class="image-slider-section">
              <div class="image-slider-container">
                <div class="image-slider">
                  <div
                    class="image-item"
                    v-for="(item, index) in imageFileList"
                    :key="index"
                  >
                    <img :src="item.url" alt="预览图" />
                    <i class="el-icon-close" @click="handleRemove(item)" />
                  </div>

                  <el-upload
                    v-if="imageFileList.length < 9"
                    class="add-image-upload"
                    action="/api/upload/image"
                    :file-list="imageFileList"
                    :before-upload="beforeImageUpload"
                    :on-change="handleInPageChange"
                    :on-remove="handleRemove"
                    :on-exceed="handleExceed"
                    :auto-upload="false"
                    accept="image/jpeg,image/png,image/webp"
                    :limit="9"
                  >
                    <div class="add-btn">
                      <i class="el-icon-plus"></i>
                    </div>
                  </el-upload>
                </div>
              </div>
            </div>
            <div class="content-form">
              <el-input
                v-model="noteForm.title"
                class="title-input"
                placeholder="添加标题..."
              />
              <el-input
                v-model="noteForm.content"
                type="textarea"
                :rows="6"
                class="content-input"
                placeholder="添加正文描述..."
              />
              <div class="tag-section">
                <el-tag v-for="topic in topicList" 
                :key="topic.topicId" 
                :type="selectedTopics.includes(topic.topicId) ? 'primary' : 'info'"
                @click="toggleTopic(topic.topicId)"
                class="selectable-tag"
                >{{ topic.topicName }}
              </el-tag>
              </div>
            </div>

            <div class="action-buttons">
              <el-button type="default" @click="Draft">取消</el-button>
              <el-button type="primary" @click="publishImagesNote">发布</el-button>
            </div>
          </div>

          <div class="preview-area">
            <div class="phone-preview">
              <div class="phone-header">
                <span>9:41</span>
                <div class="status-icons">
                  <i class="el-icon-signal"></i>
                  <i class="el-icon-wifi"></i>
                </div>
              </div>
              <div class="phone-body">
                <img :src="imageFileList[0]?.url || defaultCover" class="preview-image" />
                <div class="preview-content">
                  <div class="preview-user">
                    <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="头像" />
                    <span>用户名</span>
                  </div>
                  <p class="preview-title">{{ noteForm.title || '请输入标题' }}</p>
                  <p class="preview-desc">{{ noteForm.content || '请输入描述' }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllTopicListApi } from '@/api/topic'
import { onMounted } from 'vue'
import { uploadApi } from '@/api/file'
const isUploadStarted = ref(false)
const imageFileList = ref([])
const defaultCover = 'https://p3-flow-imagex-download-sign.byteimg.com/tos-cn-i-a9rns2rl98/8d92c47022d34e5fae5a2fb147dfef88.png~tplv-a9rns2rl98-24:720:720.png'

const uploadPercent = ref(0)
const noteForm = reactive({ title: '', content: '' })

const topicList = ref([])
const isLoadingTopics = ref(true)
const selectedTopics = ref([])
const preloadTopics = async () => {
  try {
    isLoadingTopics.value = true
    const res = await getAllTopicListApi()
    topicList.value = res.data
  } catch (err) {
    ElMessage.error('加载话题失败')
  } finally {
    isLoadingTopics.value = false
  }
}

onMounted(() => {
  preloadTopics()
})

const toggleTopic = (topicId) => {
  const idx = selectedTopics.value.indexOf(topicId)
  idx > -1 ? selectedTopics.value.splice(idx, 1) : selectedTopics.value.push(topicId)
}
// 上传校验
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isImage) { ElMessage.error('只能上传图片'); return false }
  if (!isLt20M) { ElMessage.error('单张不超过20MB'); return false }
  return true
}

// 数量超限
const handleExceed = () => ElMessage.warning('最多上传9张')

// 删除图片
const handleRemove = (file) => {
  imageFileList.value = imageFileList.value.filter(item => item.uid !== file.uid)
}

// 初始上传
const handleFileChange = (file, fileList) => {
  if (file.status === 'ready') {
    file.url = URL.createObjectURL(file.raw)
    imageFileList.value = fileList
    isUploadStarted.value = true
    uploadSingleImage(file)
  }
}

// 编辑页内添加
const handleInPageChange = (file, fileList) => {
  if (file.status === 'ready') {
    file.url = URL.createObjectURL(file.raw)
    imageFileList.value = fileList
    uploadSingleImage(file)
  }
}

const uploadSingleImage = async (UploadFile) => {
  try {
    const formData = new FormData()
    formData.append('file', UploadFile.raw)
    
    const response = await uploadApi(formData)
    
    const index = imageFileList.value.findIndex(item => item.uid === UploadFile.uid)
    if (index !== -1) {
      // 将接口返回的URL添加到imageFileList中，用于显示图片
      imageFileList.value[index].url = response.data
      // 保存服务器返回的URL到serverUrl字段
      imageFileList.value[index].serverUrl = response.data
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    ElMessage.error('图片上传失败：' + (error.message || '未知错误'))
    console.error('上传失败:', error)
  }
}
const postDTO = ref({
  title: '',
  content: '',
  coverImage: '',
  topicIds: [],
  fileUrls: []
})
const Draft = () => {
  videoFileList.value = []
  selectedFile.value = null
  uploadedVideoUrl.value = ''
  coverImage.value = null
  coverUrl.value = ''
  noteForm.title = ''
  noteForm.content = ''
  selectedTopics.value = []
  isUploading.value = false
  postDTO.value = {
    title: '',
    content: '',
    coverImage: '',
    topicIds: [],
    fileUrls: []  
  }
}
const publishImagesNote = async () => {
   if (!noteForm.title) {
    ElMessage.warning('请填写标题')
    return
  }
  if (!uploadedVideoUrl.value) {
    ElMessage.warning('视频未上传完成')
    return
  }
  postDTO.value = {
    title: noteForm.title,
    content: noteForm.content,
    coverImage: coverUrl.value,
    topicIds: selectedTopics.value,
    fileUrls: [uploadedVideoUrl.value]
  }
  
  isUploading.value = true
  const res = await createPostApi(postDTO.value)
  setTimeout(() => {
    if(res.code === 200){
      ElMessage.success('发布成功！')
    }else{
      ElMessage.error('发布失败：' + res.message)
    }
  }, 3)
  
    uploadedVideoUrl.value = ''
    coverImage.value = null
    coverUrl.value = ''
    noteForm.title = ''
    noteForm.content = ''
    selectedTopics.value = []
    isUploading.value = false
    postDTO.value = {
      title: '',
      content: '',
      coverImage: '',
      topicIds: [],
      fileUrls: []  
    }
}
</script>

<style scoped>
.upload-image-page {
  width: 100%;
  min-height: 100vh;
  background: #f8f8f8;
  padding: 20px;
  box-sizing: border-box;
}
.upload-image-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

/* 初始上传 */
.initial-upload .upload-header {
  margin-bottom: 24px;
}
.upload-header h3 {
  font-size: 20px;
  margin: 0 0 8px 0;
}
.upload-header p {
  color: #999;
  margin: 0;
}
.upload-box {
  height: 220px;
  border: 2px dashed #eee;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}
.upload-box i {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 12px;
}

/* 发布编辑页 */
.top-bar {
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}
.upload-progress {
  max-width: 600px;
  margin: 8px 0;
}
.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}
.cancel-upload {
  color: #f56c6c !important;
}

.main-content {
  display: flex;
  gap: 40px;
}
.edit-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 横向图片列表（效果图核心） */
.image-slider-section {
  width: 100%;
}
.image-slider-container {
  width: 100%;
  overflow-x: auto;
  padding-bottom: 8px;
}
.image-slider {
  display: flex;
  gap: 12px;
  min-width: 100%;
}
.image-item {
  width: 160px;
  height: 160px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  background: #f5f5f5;
}
.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.image-item .el-icon-close {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 20px;
  height: 20px;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
}

/* 添加按钮 */
.add-btn {
  width: 160px;
  height: 160px;
  border-radius: 12px;
  border: 2px dashed #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  cursor: pointer;
}
.add-btn i {
  font-size: 24px;
  color: #999;
}

/* 表单 */
.content-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.title-input {
  font-size: 16px;
  padding: 12px;
  border-radius: 8px;
}
.content-input {
  border-radius: 8px;
  resize: none;
}
.tag-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.add-tag-btn {
  font-size: 12px;
  color: #409eff;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

/* 预览 */
.preview-area {
  width: 300px;
  flex-shrink: 0;
}
.phone-preview {
  border: 2px solid #eee;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
}
.phone-header {
  padding: 12px;
  background: #f5f5f5;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}
.phone-body {
  padding: 16px;
}
.preview-image {
  width: 100%;
  border-radius: 12px;
  margin-bottom: 12px;
}
.preview-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.preview-user img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}
.preview-title {
  font-weight: bold;
  margin: 0 0 4px 0;
}
.preview-desc {
  color: #666;
  font-size: 12px;
  margin: 0;
}
</style>