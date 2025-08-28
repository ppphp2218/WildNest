<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const adminInfo = ref<any>({})
const stats = ref({
  totalDrinks: 0,
  totalComments: 0,
  todayComments: 0,
  totalViews: 0
})

const loadAdminInfo = async () => {
  try {
    const data = await adminApi.getAdminInfo()
    adminInfo.value = data || {}
  } catch (error) {
    console.error('获取管理员信息失败:', error)
  }
}

const logout = async () => {
  try {
    await showConfirmDialog({
      title: '确认退出',
      message: '确定要退出管理后台吗？'
    })
    
    await adminApi.logout()
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
    
    showToast('已退出登录')
    router.push('/admin')
  } catch (error) {
    // 用户取消或其他错误
  }
}

const navigateTo = (path: string) => {
  showToast('功能开发中，敬请期待！')
}

onMounted(() => {
  loadAdminInfo()
})
</script>

<template>
  <div class="admin-dashboard">
    <!-- 头部导航 -->
    <van-nav-bar title="管理后台" fixed>
      <template #right>
        <van-icon name="exit" @click="logout" />
      </template>
    </van-nav-bar>

    <div class="content">
      <!-- 管理员信息 -->
      <div class="admin-info">
        <div class="info-card">
          <div class="avatar">
            <van-image
              :src="adminInfo.avatarUrl || '/default-avatar.png'"
              round
              width="60"
              height="60"
            />
          </div>
          <div class="info">
            <h3 class="name">{{ adminInfo.realName || adminInfo.username }}</h3>
            <p class="role">{{ adminInfo.role === 'admin' ? '管理员' : '超级管理员' }}</p>
            <p class="login-info">
              登录次数: {{ adminInfo.loginCount || 0 }} 次
            </p>
          </div>
        </div>
      </div>

      <!-- 数据统计 -->
      <div class="stats-section">
        <van-divider>数据概览</van-divider>
        <van-grid :column-num="2" :border="false">
          <van-grid-item>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalDrinks }}</div>
              <div class="stat-label">酒品总数</div>
            </div>
          </van-grid-item>
          
          <van-grid-item>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalComments }}</div>
              <div class="stat-label">留言总数</div>
            </div>
          </van-grid-item>
          
          <van-grid-item>
            <div class="stat-item">
              <div class="stat-number">{{ stats.todayComments }}</div>
              <div class="stat-label">今日留言</div>
            </div>
          </van-grid-item>
          
          <van-grid-item>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalViews }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
          </van-grid-item>
        </van-grid>
      </div>

      <!-- 管理功能 -->
      <div class="management-section">
        <van-divider>管理功能</van-divider>
        
        <van-cell-group>
          <van-cell
            title="酒单管理"
            value="管理酒品和分类"
            is-link
            @click="navigateTo('/admin/drinks')"
          >
            <template #icon>
              <van-icon name="shop-o" color="#667eea" />
            </template>
          </van-cell>
          
          <van-cell
            title="留言板管理"
            value="管理用户留言"
            is-link
            @click="navigateTo('/admin/comments')"
          >
            <template #icon>
              <van-icon name="chat-o" color="#52c41a" />
            </template>
          </van-cell>
          
          <van-cell
            title="推荐服务管理"
            value="管理推荐规则"
            is-link
            @click="navigateTo('/admin/recommend')"
          >
            <template #icon>
              <van-icon name="star-o" color="#faad14" />
            </template>
          </van-cell>
          
          <van-cell
            title="系统设置"
            value="系统配置管理"
            is-link
            @click="navigateTo('/admin/settings')"
          >
            <template #icon>
              <van-icon name="setting-o" color="#722ed1" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <van-divider>快捷操作</van-divider>
        
        <div class="action-buttons">
          <van-button
            type="primary"
            size="small"
            @click="navigateTo('/admin/drinks/add')"
          >
            <van-icon name="plus" />
            添加酒品
          </van-button>
          
          <van-button
            type="success"
            size="small"
            @click="navigateTo('/admin/comments/pending')"
          >
            <van-icon name="eye-o" />
            审核留言
          </van-button>
          
          <van-button
            type="warning"
            size="small"
            @click="navigateTo('/admin/backup')"
          >
            <van-icon name="down" />
            数据备份
          </van-button>
        </div>
      </div>

      <!-- 系统信息 -->
      <div class="system-info">
        <van-divider>系统信息</van-divider>
        
        <van-cell-group>
          <van-cell title="系统版本" value="v1.0.0" />
          <van-cell title="最后更新" value="2024-01-01" />
          <van-cell title="在线状态" value="正常运行" label-class="status-normal" />
        </van-cell-group>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: #f8f8f8;
  padding-top: 46px;
}

.content {
  padding: 16px;
}

.admin-info {
  margin-bottom: 20px;
}

.info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  color: white;
}

.avatar {
  margin-right: 16px;
}

.info {
  flex: 1;
}

.name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 4px 0;
}

.role {
  font-size: 14px;
  margin: 0 0 4px 0;
  opacity: 0.9;
}

.login-info {
  font-size: 12px;
  margin: 0;
  opacity: 0.8;
}

.stats-section,
.management-section,
.quick-actions,
.system-info {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding: 0 16px;
  flex-wrap: wrap;
}

.action-buttons .van-button {
  flex: 1;
  min-width: 100px;
}

:deep(.status-normal) {
  color: #52c41a;
}
</style>