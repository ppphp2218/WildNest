<script setup lang="ts">
import { ref } from 'vue'

const barInfo = ref({
  name: 'WildNest 酒吧',
  description: '专业精品酒吧，为您提供优质的酒品和服务',
  address: '北京市朝阳区xxx街道xxx号',
  phone: '400-123-4567',
  hours: '18:00 - 02:00',
  story: 'WildNest 成立于2020年，致力于为客人提供最优质的酒品体验。我们拥有专业的调酒师团队，精选全球优质酒品，为每一位客人调制独特的鸡尾酒。'
})

const team = ref([
  {
    name: '张师傅',
    position: '首席调酒师',
    experience: '10年经验',
    specialty: '经典鸡尾酒'
  },
  {
    name: '李师傅',
    position: '调酒师',
    experience: '5年经验',
    specialty: '创意调酒'
  }
])

const images = ref([
  '/bar-1.jpg',
  '/bar-2.jpg',
  '/bar-3.jpg'
])

const callPhone = () => {
  window.location.href = `tel:${barInfo.value.phone}`
}

const openMap = () => {
  // 这里可以集成地图API
  alert('地图功能开发中')
}
</script>

<template>
  <div class="about">
    <!-- 头部导航 -->
    <van-nav-bar title="关于酒吧" fixed />

    <div class="content">
      <!-- 酒吧图片轮播 -->
      <div class="banner">
        <van-swipe :autoplay="3000" indicator-color="white">
          <van-swipe-item v-for="(image, index) in images" :key="index">
            <img :src="image" :alt="`酒吧环境 ${index + 1}`" class="banner-image" />
          </van-swipe-item>
        </van-swipe>
      </div>

      <!-- 酒吧介绍 -->
      <div class="intro-section">
        <van-cell-group>
          <van-cell :title="barInfo.name" :value="barInfo.description" />
        </van-cell-group>
      </div>

      <!-- 联系信息 -->
      <div class="contact-section">
        <van-divider>联系我们</van-divider>
        <van-cell-group>
          <van-cell 
            title="地址" 
            :value="barInfo.address" 
            is-link
            @click="openMap"
          >
            <template #icon>
              <van-icon name="location-o" />
            </template>
          </van-cell>
          
          <van-cell 
            title="电话" 
            :value="barInfo.phone" 
            is-link
            @click="callPhone"
          >
            <template #icon>
              <van-icon name="phone-o" />
            </template>
          </van-cell>
          
          <van-cell title="营业时间" :value="barInfo.hours">
            <template #icon>
              <van-icon name="clock-o" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 品牌故事 -->
      <div class="story-section">
        <van-divider>品牌故事</van-divider>
        <div class="story-content">
          <p class="story-text">{{ barInfo.story }}</p>
        </div>
      </div>

      <!-- 团队介绍 -->
      <div class="team-section">
        <van-divider>调酒师团队</van-divider>
        <div class="team-list">
          <div v-for="member in team" :key="member.name" class="team-card">
            <div class="member-avatar">
              <van-image
                :src="`/team-${member.name}.jpg`"
                :alt="member.name"
                round
                width="60"
                height="60"
              />
            </div>
            <div class="member-info">
              <h3 class="member-name">{{ member.name }}</h3>
              <p class="member-position">{{ member.position }}</p>
              <div class="member-details">
                <van-tag type="primary" size="small">{{ member.experience }}</van-tag>
                <van-tag type="success" size="small">{{ member.specialty }}</van-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 特色服务 -->
      <div class="services-section">
        <van-divider>特色服务</van-divider>
        <van-grid :column-num="2" :border="false">
          <van-grid-item
            icon="star-o"
            text="个性化调酒"
          />
          <van-grid-item
            icon="gift-o"
            text="生日庆祝"
          />
          <van-grid-item
            icon="friends-o"
            text="聚会包场"
          />
          <van-grid-item
            icon="music-o"
            text="现场音乐"
          />
        </van-grid>
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
      activeTab: 3
    }
  }
}
</script>

<style scoped>
.about {
  min-height: 100vh;
  background: #f8f8f8;
  padding-top: 46px;
  padding-bottom: 50px;
}

.content {
  padding-bottom: 16px;
}

.banner {
  height: 200px;
  margin-bottom: 16px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.intro-section,
.contact-section,
.story-section,
.team-section,
.services-section {
  margin-bottom: 16px;
}

.story-content {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin: 0 16px;
}

.story-text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin: 0;
}

.team-list {
  padding: 0 16px;
}

.team-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.member-avatar {
  margin-right: 16px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px 0;
  color: #333;
}

.member-position {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.member-details {
  display: flex;
  gap: 8px;
}

.services-section {
  background: white;
  margin: 0 16px;
  border-radius: 12px;
  padding: 16px;
}
</style>
