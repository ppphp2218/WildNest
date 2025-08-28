<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { drinkApi } from '@/api'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const drink = ref<any>({})
const loading = ref(false)
const showImagePreview = ref(false)
const isFavorite = ref(false)

const loadDrinkDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const data = await drinkApi.getDrinkDetail(id)
    drink.value = data || {}
    
    // 检查是否已收藏
    const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
    isFavorite.value = favorites.includes(id)
  } catch (error) {
    console.error('加载酒品详情失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const toggleFavorite = () => {
  const id = Number(route.params.id)
  let favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
  
  if (isFavorite.value) {
    favorites = favorites.filter((fId: number) => fId !== id)
    showToast('已取消收藏')
  } else {
    favorites.push(id)
    showToast('已添加收藏')
  }
  
  localStorage.setItem('favorites', JSON.stringify(favorites))
  isFavorite.value = !isFavorite.value
}

const onImageClick = () => {
  if (drink.value.imageUrl) {
    showImagePreview.value = true
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadDrinkDetail()
})
</script>

<template>
  <div class="drink-detail">
    <!-- 头部导航 -->
    <van-nav-bar
      :title="drink.name || '酒品详情'"
      left-arrow
      fixed
      @click-left="goBack"
    >
      <template #right>
        <van-icon
          :name="isFavorite ? 'star' : 'star-o'"
          :color="isFavorite ? '#ff6b6b' : '#999'"
          @click="toggleFavorite"
        />
      </template>
    </van-nav-bar>

    <div class="content">
      <van-loading v-if="loading" class="loading" />
      
      <div v-else-if="drink.id" class="drink-info">
        <!-- 酒品图片 -->
        <div class="drink-image" @click="onImageClick">
          <img 
            :src="drink.imageUrl || '/default-drink.jpg'" 
            :alt="drink.name"
          />
          <div v-if="drink.featured" class="featured-badge">推荐</div>
        </div>

        <!-- 基本信息 -->
        <div class="basic-info">
          <h1 class="drink-name">{{ drink.name }}</h1>
          <div class="drink-meta">
            <span class="price">¥{{ drink.price }}</span>
            <span class="alcohol">酒精度: {{ drink.alcoholContent }}°</span>
          </div>
          <div class="tags" v-if="drink.tags">
            <van-tag 
              v-for="tag in drink.tags.split(',')"
              :key="tag"
              type="primary"
              size="small"
            >
              {{ tag.trim() }}
            </van-tag>
          </div>
        </div>

        <!-- 详细描述 -->
        <div class="description">
          <van-cell-group>
            <van-cell title="酒品描述" :value="drink.description" />
            <van-cell title="原料成分" :value="drink.ingredients || '暂无信息'" />
            <van-cell title="口感特点" :value="drink.taste || '暂无信息'" />
            <van-cell title="适宜场合" :value="drink.occasion || '暂无信息'" />
            <van-cell title="浏览次数" :value="`${drink.viewCount || 0} 次`" />
          </van-cell-group>
        </div>

        <!-- 推荐搭配 -->
        <div v-if="drink.pairing" class="pairing">
          <van-divider>推荐搭配</van-divider>
          <p class="pairing-text">{{ drink.pairing }}</p>
        </div>
      </div>
      
      <van-empty v-else description="酒品不存在" />
    </div>

    <!-- 图片预览 -->
    <van-image-preview
      v-model:show="showImagePreview"
      :images="[drink.imageUrl || '/default-drink.jpg']"
    />
  </div>
</template>

<style scoped>
.drink-detail {
  min-height: 100vh;
  background: #f8f8f8;
  padding-top: 46px;
}

.content {
  padding: 16px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.drink-image {
  position: relative;
  width: 100%;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  cursor: pointer;
}

.drink-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.featured-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #ff6b6b;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.basic-info {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.drink-name {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 12px 0;
  color: #333;
}

.drink-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price {
  font-size: 28px;
  font-weight: bold;
  color: #ff6b6b;
}

.alcohol {
  font-size: 14px;
  color: #666;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 6px;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.description {
  margin-bottom: 16px;
}

.pairing {
  background: white;
  padding: 20px;
  border-radius: 12px;
}

.pairing-text {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin: 0;
}
</style>