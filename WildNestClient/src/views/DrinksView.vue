<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { categoryApi, drinkApi } from '@/api'

const categories = ref([])
const drinks = ref([])
const loading = ref(false)
const activeCategory = ref(0)
const searchKeyword = ref('')
const showSearch = ref(false)

const loadCategories = async () => {
  try {
    const data = await categoryApi.getCategories()
    categories.value = [{ id: 0, name: '全部' }, ...(data || [])]
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadDrinks = async (categoryId = 0, keyword = '') => {
  try {
    loading.value = true
    const params: any = { page: 1, size: 20 }
    if (categoryId > 0) params.categoryId = categoryId
    if (keyword) params.keyword = keyword
    
    const data = keyword 
      ? await drinkApi.searchDrinks(keyword)
      : await drinkApi.getDrinks(params)
    
    drinks.value = data?.records || data || []
  } catch (error) {
    console.error('加载酒品失败:', error)
  } finally {
    loading.value = false
  }
}

const onCategoryChange = (categoryId: number) => {
  activeCategory.value = categoryId
  loadDrinks(categoryId)
}

const onSearch = () => {
  if (searchKeyword.value.trim()) {
    loadDrinks(0, searchKeyword.value.trim())
  } else {
    loadDrinks(activeCategory.value)
  }
}

const onSearchCancel = () => {
  searchKeyword.value = ''
  showSearch.value = false
  loadDrinks(activeCategory.value)
}

onMounted(() => {
  loadCategories()
  loadDrinks()
})
</script>

<template>
  <div class="drinks">
    <!-- 头部搜索栏 -->
    <van-nav-bar title="酒单" fixed>
      <template #right>
        <van-icon name="search" @click="showSearch = true" />
      </template>
    </van-nav-bar>

    <!-- 搜索框 -->
    <van-search
      v-show="showSearch"
      v-model="searchKeyword"
      placeholder="搜索酒品"
      show-action
      @search="onSearch"
      @cancel="onSearchCancel"
    />

    <div class="content">
      <!-- 分类标签 -->
      <div class="categories">
        <van-tabs v-model:active="activeCategory" @change="onCategoryChange">
          <van-tab
            v-for="category in categories"
            :key="category.id"
            :title="category.name"
            :name="category.id"
          />
        </van-tabs>
      </div>

      <!-- 酒品列表 -->
      <div class="drinks-list">
        <van-loading v-if="loading" class="loading" />
        
        <div v-else-if="drinks.length" class="drinks-grid">
          <div 
            v-for="drink in drinks" 
            :key="drink.id" 
            class="drink-card"
            @click="$router.push(`/drinks/${drink.id}`)"
          >
            <div class="drink-image">
              <img :src="drink.imageUrl || '/default-drink.jpg'" :alt="drink.name" />
              <div v-if="drink.featured" class="featured-badge">推荐</div>
            </div>
            <div class="drink-info">
              <h3 class="drink-name">{{ drink.name }}</h3>
              <p class="drink-desc">{{ drink.description }}</p>
              <div class="drink-meta">
                <span class="price">¥{{ drink.price }}</span>
                <span class="alcohol">{{ drink.alcoholContent }}°</span>
              </div>
            </div>
          </div>
        </div>
        
        <van-empty v-else description="暂无酒品" />
      </div>
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
      activeTab: 1
    }
  }
}
</script>

<style scoped>
.drinks {
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

.drinks-list {
  padding: 16px;
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
  position: relative;
  width: 100%;
  height: 140px;
  overflow: hidden;
}

.drink-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.featured-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff6b6b;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.drink-info {
  padding: 12px;
}

.drink-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.drink-desc {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
</style>