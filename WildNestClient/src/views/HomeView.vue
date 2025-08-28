<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { drinkApi } from '@/api'

const featuredDrinks = ref([])
const loading = ref(false)

const loadFeaturedDrinks = async () => {
  try {
    loading.value = true
    const data = await drinkApi.getFeaturedDrinks()
    featuredDrinks.value = data || []
  } catch (error) {
    console.error('加载推荐酒品失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFeaturedDrinks()
})
</script>

<template>
  <div class="home">
    <!-- 头部横幅 -->
    <div class="header-banner">
      <div class="banner-content">
        <h1 class="title">WildNest</h1>
        <p class="subtitle">精品酒吧 · 品味生活</p>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <van-grid :column-num="4" :border="false">
        <van-grid-item
          icon="shop-o"
          text="酒单"
          to="/drinks"
        />
        <van-grid-item
          icon="chat-o"
          text="留言板"
          to="/comments"
        />
        <van-grid-item
          icon="star-o"
          text="今天喝什么"
          to="/recommend"
        />
        <van-grid-item
          icon="info-o"
          text="关于我们"
          to="/about"
        />
      </van-grid>
    </div>

    <!-- 推荐酒品 -->
    <div class="featured-section">
      <van-divider>精选推荐</van-divider>
      
      <van-loading v-if="loading" class="loading" />
      
      <div v-else-if="featuredDrinks.length" class="drinks-grid">
        <div 
          v-for="drink in featuredDrinks" 
          :key="drink.id" 
          class="drink-card"
          @click="$router.push(`/drinks/${drink.id}`)"
        >
          <div class="drink-image">
            <img :src="drink.imageUrl || '/default-drink.jpg'" :alt="drink.name" />
          </div>
          <div class="drink-info">
            <h3 class="drink-name">{{ drink.name }}</h3>
            <p class="drink-price">¥{{ drink.price }}</p>
          </div>
        </div>
      </div>
      
      <van-empty v-else description="暂无推荐酒品" />
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
      activeTab: 0
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f8f8f8;
  padding-bottom: 50px;
}

.header-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 20px;
  text-align: center;
}

.title {
  font-size: 32px;
  font-weight: bold;
  margin: 0;
}

.subtitle {
  font-size: 16px;
  margin: 8px 0 0 0;
  opacity: 0.9;
}

.quick-actions {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.featured-section {
  margin: 16px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.drinks-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 0 16px;
}

.drink-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.drink-card:active {
  transform: scale(0.98);
}

.drink-image {
  width: 100%;
  height: 120px;
  overflow: hidden;
}

.drink-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.drink-info {
  padding: 12px;
}

.drink-name {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.drink-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
  margin: 0;
}
</style>
