<template>
  <div class="upload-video-page">
    <div class="upload-video-container">
      <div v-if="!isUploadStarted" class="initial-upload">
        <div class="upload-header">
          <h3>视频上传</h3>
          <p>支持 MP4、MOV、AVI 格式，单个文件不超过 200MB</p>
        </div>

        <el-upload
          class="video-upload-zone"
          action="/files/update"
          :file-list="videoFileList"
          :before-upload="beforeVideoUpload"
          :on-change="handleFileChange"
          :auto-upload="false"
          accept="video/mp4,video/quicktime,video/x-msvideo,video/mov"
          :limit="1"
        >
          <div class="upload-box">
            <i class="el-icon-video-camera" />
            <div class="upload-text">点击或拖拽视频上传</div>
            <div class="upload-tip">仅支持 MP4/MOV/AVI，≤200MB</div>
          </div>
        </el-upload>
      </div>

      <div v-else class="publish-edit">
        <div class="top-bar">
          <div class="video-upload-status">
            <span class="file-name">{{ currentVideoName }}</span>
            <el-progress
              :percentage="uploadPercent"
              :stroke-width="6"
              class="upload-progress"
            />
            <div class="progress-info">
              <span>上传中 {{ uploadPercent }}%</span>
              <span>当前速度: {{ uploadSpeed }}</span>
              <span>剩余时间: {{ remainTime }}</span>
              <el-button type="text" class="cancel-upload" @click="cancelUpload">取消上传</el-button>
            </div>
          </div>
        </div>

        <div class="main-content">
          <div class="edit-area">
            <div class="cover-setting">
            <h4>设置封面</h4>
              <div class="cover-options">
                <div class="custom-cover">
                  <input
                    type="file"
                    ref="coverInput"
                    accept="image/jpeg,image/png,image/gif"
                    style="display: none"
                    @change="handleCoverChange"
                  />
                  <div class="cover-btn" @click="selectCover">
                    <i class="el-icon-picture"></i>
                    <span>设置封面</span>
                  </div>
                </div>
                <div v-if="coverImage" class="cover-preview">
                  <img :src="coverImage" alt="封面预览" class="cover-img" />
                  <el-button type="text" class="remove-cover" @click="removeCover">移除封面</el-button>
                </div>
              </div>
            </div>

            <div class="content-edit">
              <el-input
                v-model="noteForm.title"
                placeholder="填写标题会有更多赞哦"
                class="title-input"
              />
              <el-input
                v-model="noteForm.content"
                type="textarea"
                :rows="6"
                placeholder="输入正文描述，真诚有价值的分享予人温暖"
                class="content-textarea"
              />

              <!-- 标签区域 -->
              <div class="tag-area">
                <el-tag v-for="topic in topicList" 
                :key="topic.topicId" 
                :type="selectedTopics.includes(topic.topicId) ? 'primary' : 'info'"
                @click="toggleTopic(topic.topicId)"
                class="selectable-tag"
                >{{ topic.topicName }}
              </el-tag>
              </div>
            </div>

            <!-- 底部按钮 -->
            <div class="bottom-btns">
              <el-button type="default" class="save-btn" @click="saveDraft">暂存离开</el-button>
              <el-button type="primary" class="publish-btn" @click="publishVideoNote" :loading="isUploading">发布</el-button>
            </div>
          </div>

          <!-- 右侧预览区 -->
          <div class="preview-area">
            <div class="preview-tabs">
              <div class="tab-item active">笔记预览</div>
              <!-- <div class="tab-item">封面预览</div> -->
            </div>
            <div class="phone-preview">
              <div class="phone-header">
                <span class="time">9:41</span>
                <div class="status-icons">
                  <i class="el-icon-signal"></i>
                  <i class="el-icon-wifi"></i>
                  <i class="el-icon-charging"></i>
                </div>
              </div>
              <div class="phone-content">
                <div class="video-preview">
                  <img :src="coverImage || 'https://p3-flow-imagex-download-sign.byteimg.com/tos-cn-i-a9rns2rl98/8d92c47022d34e5fae5a2fb147dfef88.png~tplv-a9rns2rl98-24:720:720.png'" alt="视频封面" class="preview-img">
                  <i class="el-icon-video-play play-icon"></i>
                </div>
                <div class="note-info">
                  <div class="user-info">
                    <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="头像" class="avatar">
                    <span class="username">小红薯65386122 <i class="el-icon-star-on vip-icon"></i></span>
                  </div>
                  <p class="publish-time">编辑于 刚刚公开可见</p>
                  <div class="interact-icons">
                    <i class="el-icon-comment"></i>
                    <i class="el-icon-heart"></i>
                    <i class="el-icon-star-off"></i>
                    <i class="el-icon-share"></i>
                  </div>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllTopicListApi } from '@/api/topic'
import { uploadApi } from '@/api/file'
import axios from 'axios'
import { createPostApi } from '@/api/post'
// 核心状态
const isUploadStarted = ref(false)
const videoFileList = ref([])
const currentVideoName = ref('')
const uploadPercent = ref(0)
const uploadSpeed = ref('0KB/s')
const remainTime = ref('0s')
const uploadStartTimestamp = ref(0)
const uploadedVideoUrl = ref('')
const isUploading = ref(false)

// 笔记数据
const noteForm = reactive({
  title: '',
  content: ''
})
// 封面相关状态
const coverImage = ref(null)
const coverUrl = ref('')
const coverUploading = ref(false)

// 封面文件输入框引用
const coverInput = ref(null)
// 话题
const topicList = ref([])
const isLoadingTopics = ref(true)
const selectedTopics = ref([])

// 保存当前选中的文件对象
const selectedFile = ref(null)

// ==============================================
// 上传前校验
// ==============================================
const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt200M = file.size / 1024 / 1024 < 200

  if (!isVideo) {
    ElMessage.error('只能上传视频文件！')
    return false
  }
  if (!isLt200M) {
    ElMessage.error('视频大小不能超过 200MB！')
    return false
  }
  return true
}

// ==============================================
// 加载话题
// ==============================================
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

// ==============================================
// 文件选择后：切换界面 + 保存文件
// ==============================================
const handleFileChange = (uploadFile) => {
  if (uploadFile.status === 'ready') {
    const valid = beforeVideoUpload(uploadFile.raw)
    if (!valid) {
      videoFileList.value = []
      return
    }

    // 关键：保存真实文件对象
    selectedFile.value = uploadFile.raw
    currentVideoName.value = uploadFile.name
    isUploadStarted.value = true
    
    // 立即开始真实上传
    uploadVideo(uploadFile.raw)
  }
}

// ==============================================
// 真实上传函数
// ==============================================
const uploadVideo = async (uploadfile) => {
  if (!uploadfile) return
  
  isUploading.value = true
  uploadPercent.value = 0
  uploadStartTimestamp.value = Date.now()

  try {
    // 1. 构建正确的 FormData —— 后端必须用 file 接收
    const file = new FormData()
    file.append('file', uploadfile)

    // 2. 发送请求（自动 multipart/form-data）
    const res = await uploadApi(file, {
      onUploadProgress: (progressEvent) => {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        uploadPercent.value = percent

        // 计算速度和剩余时间
        const elapsed = (Date.now() - uploadStartTimestamp.value) / 1000 // 秒
        const speed = progressEvent.loaded / elapsed // bytes/s
        uploadSpeed.value = (speed / 1024).toFixed(2) + 'KB/s'
        const remainingBytes = progressEvent.total - progressEvent.loaded
        remainTime.value = speed > 0 ? Math.round(remainingBytes / speed) + 's' : '计算中...'
      }
    })

    // 成功
    uploadPercent.value = 100
    console.log("url: ", res.data)
    uploadedVideoUrl.value = res.data
    ElMessage.success('视频上传完成！')
    
  } catch (err) {
    console.error('上传失败', err)
    ElMessage.error('上传失败：' + (err.message || '请重试'))
    cancelUpload()
  } finally {
    isUploading.value = false
  }
}

// ==============================================
// 取消上传
// ==============================================
const cancelUpload = () => {
  ElMessageBox.confirm('确定取消上传吗？', '提示').then(() => {
    isUploadStarted.value = false
    videoFileList.value = []
    selectedFile.value = null
    currentVideoName.value = ''
    uploadPercent.value = 0
    uploadedVideoUrl.value = ''
    ElMessage.info('已取消')
  })
}
// ==============================================
// 封面相关函数
// ==============================================
const selectCover = () => {
  // 触发隐藏的文件输入框
  coverInput.value.click()
}

const handleCoverChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB！')
    return
  }
  const reader = new FileReader()
  reader.onload = (e) => {
    coverImage.value = e.target.result
  }
  reader.readAsDataURL(file)
  await uploadCover(file)
}

const uploadCover = async (file) => {
  if (!file) return

  coverUploading.value = true

  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadApi(formData)
    coverUrl.value = res.data
    ElMessage.success('封面上传成功！')
  } catch (err) {
    console.error('封面上传失败', err)
    ElMessage.error('封面上传失败：' + (err.message || '请重试'))
    coverImage.value = null
  } finally {
    coverUploading.value = false
  }
}

const removeCover = () => {
  coverImage.value = null
  coverUrl.value = ''
  if (coverInput.value) {
    coverInput.value.value = ''
  }
}
// ==============================================
// 草稿 / 发布
// ==============================================
const saveDraft = () => {
  if (!noteForm.title && !noteForm.content) {
    ElMessage.warning('请填写标题或内容')
    return
  }
  localStorage.setItem('videoNoteDraft', JSON.stringify({
    ...noteForm,
    selectedTopics: selectedTopics.value,
    videoUrl: uploadedVideoUrl.value,
    coverUrl: coverUrl.value
  }))
  ElMessage.success('草稿已保存')
}
const postDTO = ref({
  title: '',
  content: '',
  coverImage: '',
  topicIds: [],
  fileUrls: []
})
const publishVideoNote = async () => {
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
  
    isUploadStarted.value = false
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

</script>

<style scoped>
/* 样式完全不变，省略以节省空间 */
.upload-video-page {
  width: 100%;
  min-height: calc(100vh - 60px);
  background: #f8f8f8;
  padding: 20px;
  box-sizing: border-box;
}
.upload-video-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
}
.initial-upload .upload-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}
.initial-upload .upload-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
}
.initial-upload .upload-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}
.video-upload-zone {
  width: 100%;
}
:deep(.initial-upload .el-upload) {
  width: 100%;
}
.upload-box {
  width: 100%;
  height: 260px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #fafafa;
  transition: all 0.3s;
}
.upload-box:hover {
  border-color: #409eff;
  background: #f0f7ff;
}
.upload-box i {
  font-size: 50px;
  color: #409eff;
}
.upload-text {
  font-size: 16px;
  color: #666;
  margin: 15px 0 8px 0;
}
.upload-tip {
  font-size: 12px;
  color: #999;
}
.publish-edit .top-bar {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}
.video-upload-status {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.file-name {
  font-size: 14px;
  color: #333;
  font-weight: 50;
}
.upload-progress {
  width: 100%;
  max-width: 800px;
}
.progress-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
  color: #666;
}
.cancel-upload {
  color: #f56c6c !important;
  margin-left: auto;
}
.main-content {
  display: flex;
  gap: 30px;
}
.edit-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 25px;
}
.cover-setting h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.cover-options {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.custom-cover .cover-btn {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
}
.custom-cover .cover-btn:hover {
  border-color: #409eff;
  background: #f0f7ff;
}
.custom-cover .cover-btn i {
  font-size: 24px;
  color: #409eff;
}
.custom-cover .cover-btn span {
  font-size: 12px;
  color: #666;
}


.ai-tip {
  font-size: 12px;
  color: #666;
  margin: 0 0 10px 0;
}

.content-edit .title-input {
  font-size: 16px;
  padding: 10px;
  border-radius: 4px;
}
.content-textarea {
  font-size: 14px;
  padding: 10px;
  border-radius: 4px;
  resize: none;
}
.tag-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}
.selectable-tag {
  cursor: pointer;
  user-select: none;
}
.selectable-tag:hover {
  opacity: 0.8;
}
.bottom-btns {
  display: flex;
  gap: 15px;
  margin-top: auto;
}
.save-btn {
  padding: 8px 20px;
}
.publish-btn {
  padding: 8px 20px;
}
.preview-area {
  width: 320px;
  flex-shrink: 0;
}
.preview-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}
.tab-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding-bottom: 5px;
  border-bottom: 2px solid transparent;
}
.tab-item.active {
  color: #409eff;
  border-bottom-color: #409eff;
}
.phone-preview {
  width: 100%;
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
}
.phone-header {
  padding: 10px 15px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.time {
  font-size: 12px;
  color: #333;
  font-weight: 50;
}
.status-icons {
  display: flex;
  gap: 5px;
  font-size: 12px;
  color: #333;
}
.phone-content {
  padding: 15px;
}
.video-preview {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  margin-bottom: 15px;
}
.preview-img {
  width: 100%;
  display: block;
}
.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 30px;
  color: #fff;
  opacity: 0.8;
}
.note-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}
.username {
  font-size: 14px;
  color: #333;
}
.vip-icon {
  font-size: 12px;
  color: #ffd700;
  margin-left: 5px;
}
.publish-time {
  font-size: 12px;
  color: #999;
}
.interact-icons {
  display: flex;
  gap: 15px;
  font-size: 16px;
  color: #666;
  margin-top: 5px;
}
.cover-preview {
  position: relative;
}
.cover-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
}
.remove-cover {
  position: absolute;
  bottom: -20px;
  left: 0;
  color: #f56c6c !important;
  font-size: 12px;
}
</style>