<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { commentApi, uploadApi } from '@/api'
import { showToast, showConfirmDialog } from 'vant'

const comments = ref([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const showSubmitForm = ref(false)
const activeCategory = ref('all')

// 提交表单数据
const submitForm = ref({
  nickname: '',
  content: '',
  category: 'general',
  images: []
})

const categories = [
  { value: 'all', label: '全部' },
  { value: 'general', label: '一般留言' },
  { value: 'suggestion', label: '建议反馈' },
  { value: 'praise', label: '表扬赞美' }
]

const loadComments = async (isRefresh = false) => {
  try {
    if (isRefresh) {
      refreshing.value = true
    } else {
      loading.value = true
    }
    
    const params: any = { page: 1, size: 20 }
    if (activeCategory.value !== 'all') {
      params.category = activeCategory.value
    }
    
    const data = await commentApi.getComments(params)
    comments.value = data?.records || data || []
    finished.value = true
  } catch (error) {
    console.error('加载留言失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onCategoryChange = (category: string) => {
  activeCategory.value = category
  loadComments()
}

const onRefresh = () => {
  loadComments(true)
}

const likeComment = async (commentId: number) => {
  try {
    await commentApi.likeComment(commentId)
    showToast('点赞成功')
    // 更新本地数据
    const comment = comments.value.find((c: any) => c.id === commentId)
    if (comment) {
      comment.likeCount = (comment.likeCount || 0) + 1
      comment.isLiked = true
    }
  } catch (error) {
    showToast('点赞失败')
  }
}

const showReplyDialog = (comment: any) => {
  showConfirmDialog({
    title: '回复留言',
    message: '功能开发中，敬请期待！'
  })
}

const submitComment = async () => {
  if (!submitForm.value.nickname.trim()) {
    showToast('请输入昵称')
    return
  }
  
  if (!submitForm.value.content.trim()) {
    showToast('请输入留言内容')
    return
  }
  
  try {
    await commentApi.submitComment(submitForm.value)
    showToast('留言提交成功')
    showSubmitForm.value = false
    
    // 重置表单
    submitForm.value = {
      nickname: '',
      content: '',
      category: 'general',
      images: []
    }
    
    // 刷新列表
    loadComments(true)
  } catch (error) {
    showToast('提交失败')
  }
}

const uploadImage = async (file: File) => {
  try {
    const imageUrl = await uploadApi.uploadImage(file)
    submitForm.value.images.push(imageUrl)
    showToast('图片上传成功')
  } catch (error) {
    showToast('图片上传失败')
  }
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadComments()
})
</script>

<template>
  <div class="comments">
    <!-- 头部导航 -->
    <van-nav-bar title="留言板" fixed>
      <template #right>
        <van-icon name="edit" @click="showSubmitForm = true" />
      </template>
    </van-nav-bar>

    <div class="content">
      <!-- 分类标签 -->
      <div class="categories">
        <van-tabs v-model:active="activeCategory" @change="onCategoryChange">
          <van-tab
            v-for="category in categories"
            :key="category.value"
            :title="category.label"
            :name="category.value"
          />
        </van-tabs>
      </div>

      <!-- 留言列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
        >
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id" 
              class="comment-card"
            >
              <div class="comment-header">
                <div class="user-info">
                  <van-image
                    :src="comment.avatarUrl || '/default-avatar.png'"
                    class="avatar"
                    round
                  />
                  <div class="user-meta">
                    <span class="nickname">{{ comment.nickname }}</span>
                    <span class="time">{{ formatTime(comment.createdAt) }}</span>
                  </div>
                </div>
                <van-tag 
                  v-if="comment.category !== 'general'"
                  type="primary"
                  size="small"
                >
                  {{ categories.find(c => c.value === comment.category)?.label }}
                </van-tag>
              </div>
              
              <div class="comment-content">
                <p class="content-text">{{ comment.content }}</p>
                
                <!-- 图片展示 -->
                <div v-if="comment.images && comment.images.length" class="images">
                  <van-image
                    v-for="(image, index) in comment.images"
                    :key="index"
                    :src="image"
                    class="comment-image"
                    @click="$refs.imagePreview.show(comment.images, index)"
                  />
                </div>
              </div>
              
              <div class="comment-actions">
                <div class="action-buttons">
                  <van-button
                    size="small"
                    type="primary"
                    plain
                    :disabled="comment.isLiked"
                    @click="likeComment(comment.id)"
                  >
                    <van-icon name="good-job-o" />
                    {{ comment.likeCount || 0 }}
                  </van-button>
                  
                  <van-button
                    size="small"
                    type="default"
                    plain
                    @click="showReplyDialog(comment)"
                  >
                    <van-icon name="chat-o" />
                    回复
                  </van-button>
                </div>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <!-- 提交留言弹窗 -->
    <van-popup v-model:show="showSubmitForm" position="bottom" :style="{ height: '70%' }">
      <div class="submit-form">
        <van-nav-bar
          title="发表留言"
          left-text="取消"
          right-text="发布"
          @click-left="showSubmitForm = false"
          @click-right="submitComment"
        />
        
        <div class="form-content">
          <van-field
            v-model="submitForm.nickname"
            label="昵称"
            placeholder="请输入昵称"
            required
          />
          
          <van-field
            v-model="submitForm.category"
            label="分类"
            is-link
            readonly
            @click="$refs.categoryPicker.show()"
          />
          
          <van-field
            v-model="submitForm.content"
            type="textarea"
            label="内容"
            placeholder="请输入留言内容"
            rows="4"
            maxlength="500"
            show-word-limit
            required
          />
          
          <div class="image-upload">
            <van-uploader
              v-model="submitForm.images"
              :max-count="3"
              :after-read="uploadImage"
            />
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 分类选择器 -->
    <van-picker
      ref="categoryPicker"
      :columns="categories.filter(c => c.value !== 'all')"
      @confirm="(value) => { submitForm.category = value.value }"
    />

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" fixed>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="shop-o" to="/drinks">酒单</van-tabbar-item>
      <van-tabbar-item icon="chat-o" to="/comments">留言板</van-tabbar-item>
      <van-tabbar-item icon="star-o" to="/recommend">推荐</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 2
    }
  }
}
</script>

<style scoped>
.comments {
  min-height: 100vh;
  background: #f8f8f8;
  padding-top: 46px;
  padding-bottom: 50px;
}

.content {
  padding-top: 44px;
}

.categories {
  background: white;
  position: sticky;
  top: 46px;
  z-index: 99;
}

.comments-list {
  padding: 16px;
}

.comment-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.user-meta {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.content-text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin: 0 0 12px 0;
}

.images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.comment-image {
  width: 100%;
  height: 80px;
  border-radius: 6px;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.submit-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.form-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.image-upload {
  margin-top: 16px;
}
</style>