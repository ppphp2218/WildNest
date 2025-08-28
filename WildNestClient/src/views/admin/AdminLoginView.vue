<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const onSubmit = async () => {
  if (!loginForm.value.username.trim()) {
    showToast('请输入用户名')
    return
  }
  
  if (!loginForm.value.password.trim()) {
    showToast('请输入密码')
    return
  }
  
  try {
    loading.value = true
    const data = await adminApi.login(loginForm.value)
    
    // 保存登录信息
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_info', JSON.stringify(data))
    
    showToast('登录成功')
    router.push('/admin/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/')
}
</script>

<template>
  <div class="admin-login">
    <!-- 头部导航 -->
    <van-nav-bar
      title="管理员登录"
      left-arrow
      @click-left="goBack"
    />

    <div class="login-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo">
          <van-icon name="manager-o" size="64" color="#667eea" />
        </div>
        <h1 class="title">WildNest 管理后台</h1>
        <p class="subtitle">请使用管理员账号登录</p>
      </div>

      <!-- 登录表单 -->
      <div class="form-section">
        <van-form @submit="onSubmit">
          <van-cell-group inset>
            <van-field
              v-model="loginForm.username"
              name="username"
              label="用户名"
              placeholder="请输入用户名"
              :rules="[{ required: true, message: '请输入用户名' }]"
            >
              <template #left-icon>
                <van-icon name="manager-o" />
              </template>
            </van-field>
            
            <van-field
              v-model="loginForm.password"
              type="password"
              name="password"
              label="密码"
              placeholder="请输入密码"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <template #left-icon>
                <van-icon name="lock" />
              </template>
            </van-field>
          </van-cell-group>
          
          <div class="submit-section">
            <van-button
              round
              block
              type="primary"
              native-type="submit"
              :loading="loading"
              loading-text="登录中..."
            >
              登录
            </van-button>
          </div>
        </van-form>
      </div>

      <!-- 提示信息 -->
      <div class="tips">
        <van-notice-bar
          left-icon="info-o"
          text="默认账号: admin, 密码: admin123"
          background="#f8f9ff"
          color="#667eea"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 46px;
}

.login-container {
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 46px);
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo {
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: white;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.form-section {
  margin-bottom: 20px;
}

.submit-section {
  padding: 16px;
}

.tips {
  margin-top: auto;
}
</style>