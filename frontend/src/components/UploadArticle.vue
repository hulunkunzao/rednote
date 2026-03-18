<template>
  <div class="upload-article-page">
    <div class="upload-article-container">
      <div v-if="!isStartEdit" class="initial-upload">
        <div class="upload-header">
          <h3>文章发布</h3>
          <p>编写原创文章，分享你的知识与见解</p>
        </div>
        <el-button type="primary" size="large" @click="startEdit">开始编写文章</el-button>
      </div>
      <div v-else class="publish-edit">
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
                    <i class="el-icon-plus"></i>
                    <span>上传封面</span>
                  </div>
                  <div v-if="coverImage" class="cover-preview">
                    <img :src="coverImage" alt="封面预览" class="preview-img">
                    <button class="remove-btn" @click="removeCover">删除</button>
                  </div>
                </div>
              </div>
            </div>
            <el-upload
              ref="uploadCover"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              action="/api/upload"
              style="display:none;"
            >
              <el-button></el-button>
            </el-upload>
            <div class="content-edit">
              <el-input v-model="articleForm.title" placeholder="请输入文章标题" class="title-input" />
              <el-input v-model="articleForm.content" type="textarea" :rows="18" placeholder="请输入文章内容" class="content-textarea" />

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

            <div class="bottom-btns">
              <el-button type="default" @click="cancelEdit">取消</el-button>
              <el-button type="primary" @click="publishArticleNote">发布文章</el-button>
            </div>
          </div>
          <div class="preview-area">
            <div class="preview-tabs">
              <div class="tab-item active">文章预览</div>
            </div>
            <div class="phone-preview">
              <div class="phone-header">
                <span class="time">{{  currentTime }}</span>
                <div class="status-icons">
                  <i class="el-icon-signal"></i>
                  <i class="el-icon-wifi"></i>
                </div>
              </div>
              <div class="phone-content">
                <h3 class="article-title">{{ articleForm.title || '文章标题' }}</h3>
                <div class="article-content">{{ articleForm.content || '文章正文预览...' }}</div>
                <div class="note-info">
                  <div class="user-info">
                    <img :src="userInfo.avatar" alt="用户头像" class="avatar">
                    <span class="username"> {{ userInfo.username }}</span>
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllTopicListApi } from '@/api/topic'
import { onMounted } from 'vue'
import { uploadApi } from '@/api/file'
import {createPostApi} from '@/api/post'
import { getCurrentUserInfo } from '@/api/user'
const isStartEdit = ref(false)
const articleForm = reactive({
  title: '',
  content: ''
})
const userInfo = ref({})
const currentTime = ref(new Date().toLocaleTimeString('zh-CN', { timeZone: 'Asia/Shanghai' }))

//写一个函数，用于更新当前时间
const updateCurrentTime = () => {
  currentTime.value = new Date().toLocaleTimeString('zh-CN', { timeZone: 'Asia/Shanghai' });
}
// 每秒钟更新一次当前时间
setInterval(updateCurrentTime, 1000)


const coverImage = ref(null)
const coverUrl = ref('')
const coverUploading = ref(false)
const coverInput = ref(null)
const topicList = ref([])
const selectedTopics = ref([])


const postDTO = ref({
  title: '',
  content: '',
  coverImage: '',
  topicIds: [],
  fileUrls: []
})
const toggleTopic = (topicId) => {
  const idx = selectedTopics.value.indexOf(topicId)
  idx > -1 ? selectedTopics.value.splice(idx, 1) : selectedTopics.value.push(topicId)
}
onMounted(async () => {
  try {
    await fetchUserInfo()
    const res = await getAllTopicListApi()
    topicList.value = res.data
  } catch (err) {
    ElMessage.error('加载话题失败')
  }
})

const startEdit = () => {
  isStartEdit.value = true
}

const cancelEdit = () => {
  isStartEdit.value = false
  topicList = []
  coverImage.value = null
  coverUrl.value = ''
  articleForm.title = ''
  articleForm.content = ''
  selectedTopics.value = []
  postDTO.value = {
    title: '',
    title: '',
    content: '',
    coverImage: '',
    topicIds: [],
    fileUrls: []  
  }
  ElMessage.info('已取消编辑')
}

const selectCover = () => {
  coverInput.value.click()
}

const handleCoverChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB')
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    coverImage.value = e.target.result
  }
  reader.readAsDataURL(file)
  await uploadCover(file)
}
const fetchUserInfo = async () => {
  try {
    const response = await getCurrentUserInfo()
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const uploadCover = async (fileUpload) => {
  if (!fileUpload) return

  coverUploading.value = true

  try {
    const file = new FormData()
    file.append('file', fileUpload)
    const res = await uploadApi(file)
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
const publishArticleNote = async () => {
 if (!articleForm.title) {
    ElMessage.warning('请填写标题')
    return
  }
  if (!articleForm.content) {
    ElMessage.warning('内容未上传完成')
    return
  }
  postDTO.value = {
    title: articleForm.title,
    content: articleForm.content,
    coverImage: coverUrl.value,
    topicIds: selectedTopics.value,
    fileUrls: [coverUrl.value]
  }
  
  const res = await createPostApi(postDTO.value)
  setTimeout(() => {
    if(res.code === 200){
      ElMessage.success('发布成功！')
    }else{
      ElMessage.error('发布失败：' + res.message)
    }
  }, 3)
  isStartEdit.value = false
  topicList = []
  coverImage.value = null
  coverUrl.value = ''
  articleForm.title = ''
  articleForm.content = ''
  selectedTopics.value = []
  postDTO.value = {
    title: '',
    title: '',
    content: '',
    coverImage: '',
    topicIds: [],
    fileUrls: []  
  }
}

</script>

<style scoped>
.upload-article-page {
  width: 100%;
  min-height: calc(100vh - 60px);
  background: #f8f8f8;
  padding: 20px;
  box-sizing: border-box;
}
.upload-article-container {
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
}
.initial-upload .upload-header p {
  color: #999;
  font-size: 14px;
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
.content-edit .title-input {
  font-size: 18px;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 15px;
}
.content-textarea {
  font-size: 15px;
  padding: 12px;
  border-radius: 4px;
  resize: none;
}
.tag-area {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}
.more-tag {
  font-size: 12px;
  color: #409eff;
}
.bottom-btns {
  display: flex;
  gap: 15px;
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
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  overflow: hidden;
}
.phone-header {
  padding: 10px 15px;
  background: #f5f5f5;
  display: flex;
  justify-content: space-between;
}
.time {
  font-size: 12px;
}
.status-icons {
  display: flex;
  gap: 5px;
  font-size: 12px;
}
.phone-content {
  padding: 15px;
}
.article-title {
  font-size: 16px;
  margin: 0 0 10px 0;
}
.article-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
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
}
.article-cover {
  margin: 20px 0;
}
.article-cover label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.cover-box {
  width: 100%;
}

.cover-upload {
  width: 100%;
  height: 200px;
  border: 1px dashed #ccc;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  color: #999;
  cursor: pointer;
  background: #fafafa;
}
.cover-upload i {
  font-size: 24px;
  margin-bottom: 8px;
}

/* 预览 */
.cover-preview {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 6px;
  overflow: hidden;
}
.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-tool {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,.5);
  padding: 6px;
  text-align: right;
}
.cover-tool button {
  background: #fff;
  border: none;
  padding: 3px 8px;
  margin-left: 6px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.cover-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
}
/* 封面设置区域 */
.cover-setting {
  margin-bottom: 20px;
}

.cover-setting h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

/* 封面选项容器 */
.cover-options {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

/* 自定义封面区域 */
.custom-cover {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 封面上传按钮 */
.cover-btn {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
  color: #909399;
}

.cover-btn:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
  color: #409eff;
}

.cover-btn i {
  font-size: 24px;
  margin-bottom: 8px;
}

.cover-btn span {
  font-size: 14px;
}

/* 封面预览容器 */
.cover-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

/* 预览图片 */
.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 删除按钮 */
.remove-btn {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  padding: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.remove-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

/* 标签样式 */
.tag-area {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selectable-tag {
  cursor: pointer;
  user-select: none;
}

.selectable-tag:hover {
  opacity: 0.8;
}
</style>