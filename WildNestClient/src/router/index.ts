import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { title: 'WildNest 酒吧' }
    },
    {
      path: '/drinks',
      name: 'drinks',
      component: () => import('../views/DrinksView.vue'),
      meta: { title: '酒单' }
    },
    {
      path: '/drinks/:id',
      name: 'drink-detail',
      component: () => import('../views/DrinkDetailView.vue'),
      meta: { title: '酒品详情' }
    },
    {
      path: '/comments',
      name: 'comments',
      component: () => import('../views/CommentsView.vue'),
      meta: { title: '留言板' }
    },
    {
      path: '/recommend',
      name: 'recommend',
      component: () => import('../views/RecommendView.vue'),
      meta: { title: '今天喝什么' }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { title: '关于酒吧' }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/AdminLoginView.vue'),
      meta: { title: '管理员登录' }
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('../views/admin/AdminDashboardView.vue'),
      meta: { title: '管理后台', requiresAuth: true }
    }
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAuth) {
    const adminToken = localStorage.getItem('admin_token')
    if (!adminToken) {
      next('/admin')
      return
    }
  }
  
  next()
})

export default router
