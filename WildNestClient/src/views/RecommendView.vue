<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { recommendApi } from '@/api'
import { showToast } from 'vant'

const questions = ref([])
const currentStep = ref(0)
const answers = ref<number[]>([])
const recommendations = ref([])
const loading = ref(false)
const showResult = ref(false)

const loadQuestions = async () => {
  try {
    loading.value = true
    const data = await recommendApi.getQuestions()
    questions.value = data || []
  } catch (error) {
    console.error('加载问题失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const selectOption = (optionId: number) => {
  answers.value[currentStep.value] = optionId
  
  if (currentStep.value < questions.value.length - 1) {
    // 下一题
    currentStep.value++
  } else {
    // 获取推荐结果
    getRecommendation()
  }
}

const getRecommendation = async () => {
  try {
    loading.value = true
    const data = await recommendApi.getRecommendation(answers.value)
    recommendations.value = data || []
    showResult.value = true
  } catch (error) {
    console.error('获取推荐失败:', error)
    showToast('获取推荐失败')
  } finally {
    loading.value = false
  }
}

const goToPrevious = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const restart = () => {
  currentStep.value = 0
  answers.value = []
  recommendations.value = []
  showResult.value = false
}

const goToDrinkDetail = (drinkId: number) => {
  $router.push(`/drinks/${drinkId}`)
}

onMounted(() => {
  loadQuestions()
})
</script>

<template>
  <div class="recommend">
    <!-- 头部导航 -->
    <van-nav-bar title="今天喝什么" fixed />

    <div class="content">
      <van-loading v-if="loading" class="loading" />
      
      <!-- 问答流程 -->
      <div v-else-if="!showResult && questions.length" class="question-flow">
        <!-- 进度条 -->
        <div class="progress">
          <van-progress 
            :percentage="((currentStep + 1) / questions.length) * 100" 
            color="#667eea"
          />
          <p class="progress-text">
            第 {{ currentStep + 1 }} / {{ questions.length }} 步
          </p>
        </div>

        <!-- 当前问题 -->
        <div class="question-card">
          <h2 class="question-title">
            {{ questions[currentStep]?.title }}
          </h2>
          <p class="question-desc">
            {{ questions[currentStep]?.description }}
          </p>
          
          <!-- 选项列表 -->
          <div class="options">
            <div 
              v-for="option in questions[currentStep]?.options" 
              :key="option.id"
              class="option-card"
              :class="{ active: answers[currentStep] === option.id }"
              @click="selectOption(option.id)"
            >
              <div class="option-content">
                <span class="option-text">{{ option.content }}</span>
                <van-icon 
                  v-if="answers[currentStep] === option.id"
                  name="success" 
                  color="#667eea"
                />
              </div>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="actions">
            <van-button 
              v-if="currentStep > 0"
              type="default"
              @click="goToPrevious"
            >
              上一步
            </van-button>
          </div>
        </div>
      </div>
      
      <!-- 推荐结果 -->
      <div v-else-if="showResult" class="result">
        <div class="result-header">
          <van-icon name="star" color="#667eea" size="32" />
          <h2 class="result-title">为您推荐</h2>
          <p class="result-desc">根据您的喜好，我们为您精选了以下酒品</p>
        </div>
        
        <div v-if="recommendations.length" class="recommendations">
          <div 
            v-for="item in recommendations" 
            :key="item.drink.id" 
            class="recommendation-card"
            @click="goToDrinkDetail(item.drink.id)"
          >
            <div class="drink-image">
              <img 
                :src="item.drink.imageUrl || '/default-drink.jpg'" 
                :alt="item.drink.name"
              />
            </div>
            <div class="drink-info">
              <h3 class="drink-name">{{ item.drink.name }}</h3>
              <p class="drink-reason">{{ item.reason }}</p>
              <div class="drink-meta">
                <span class="price">¥{{ item.drink.price }}</span>
                <span class="alcohol">{{ item.drink.alcoholContent }}°</span>
              </div>
            </div>
          </div>
        </div>
        
        <van-empty v-else description="暂无推荐" />
        
        <div class="result-actions">
          <van-button type="primary" @click="restart">
            重新测试
          </van-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <van-empty v-else description="暂无问题" />
    </div>

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
      activeTab: 3
    }
  }
}
</script>

<style scoped>
.recommend {
  min-height: 100vh;
  background: #f8f8f8;
  padding-top: 46px;
  padding-bottom: 50px;
}

.content {
  padding: 16px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.progress {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  text-align: center;
}

.progress-text {
  margin: 12px 0 0 0;
  font-size: 14px;
  color: #666;
}

.question-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.question-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #333;
  text-align: center;
}

.question-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 24px 0;
  text-align: center;
  line-height: 1.5;
}

.options {
  margin-bottom: 24px;
}

.option-card {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-card:hover {
  border-color: #667eea;
}

.option-card.active {
  border-color: #667eea;
  background: #f8f9ff;
}

.option-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.option-text {
  font-size: 16px;
  color: #333;
}

.actions {
  display: flex;
  justify-content: center;
}

.result {
  text-align: center;
}

.result-header {
  background: white;
  padding: 32px 24px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.result-title {
  font-size: 24px;
  font-weight: bold;
  margin: 12px 0 8px 0;
  color: #333;
}

.result-desc {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.recommendations {
  margin-bottom: 24px;
}

.recommendation-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.recommendation-card:active {
  transform: scale(0.98);
}

.drink-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
}

.drink-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.drink-info {
  flex: 1;
  text-align: left;
}

.drink-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.drink-reason {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.drink-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.alcohol {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.result-actions {
  padding: 0 24px;
}
</style>