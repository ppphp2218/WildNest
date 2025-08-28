import request from '@/utils/request'

// 酒品分类相关接口
export const categoryApi = {
  // 获取所有启用的分类
  getCategories: () => request.get('/categories'),
  
  // 获取顶级分类
  getTopCategories: () => request.get('/categories/top'),
  
  // 获取子分类
  getChildCategories: (parentId: number) => request.get(`/categories/children/${parentId}`),
  
  // 搜索分类
  searchCategories: (keyword: string) => request.get(`/categories/search?keyword=${keyword}`),
  
  // 获取分类树
  getCategoryTree: () => request.get('/categories/tree'),
  
  // 获取分类详情
  getCategoryDetail: (id: number) => request.get(`/categories/${id}`)
}

// 酒品相关接口
export const drinkApi = {
  // 获取酒品列表（分页）
  getDrinks: (params: {
    page?: number
    size?: number
    categoryId?: number
    keyword?: string
  }) => request.get('/drinks', { params }),
  
  // 获取酒品详情
  getDrinkDetail: (id: number) => request.get(`/drinks/${id}`),
  
  // 搜索酒品
  searchDrinks: (keyword: string, page = 1, size = 10) => 
    request.get(`/drinks/search?keyword=${keyword}&page=${page}&size=${size}`),
  
  // 获取推荐酒品
  getFeaturedDrinks: () => request.get('/drinks/featured'),
  
  // 获取热门酒品
  getPopularDrinks: () => request.get('/drinks/popular'),
  
  // 按标签获取酒品
  getDrinksByTag: (tag: string) => request.get(`/drinks/tag/${tag}`),
  
  // 按价格范围获取酒品
  getDrinksByPriceRange: (minPrice: number, maxPrice: number) => 
    request.get(`/drinks/price-range?minPrice=${minPrice}&maxPrice=${maxPrice}`),
  
  // 获取分类下的酒品数量
  getDrinkCountByCategory: (categoryId: number) => 
    request.get(`/drinks/count/category/${categoryId}`)
}

// 留言板相关接口
export const commentApi = {
  // 获取留言列表
  getComments: (params: {
    page?: number
    size?: number
    category?: string
  }) => request.get('/comments', { params }),
  
  // 提交留言
  submitComment: (data: {
    nickname: string
    content: string
    category?: string
    images?: string[]
  }) => request.post('/comments', data),
  
  // 点赞留言
  likeComment: (id: number) => request.post(`/comments/${id}/like`),
  
  // 回复留言
  replyComment: (id: number, data: {
    nickname: string
    content: string
  }) => request.post(`/comments/${id}/reply`, data),
  
  // 获取留言回复
  getCommentReplies: (id: number) => request.get(`/comments/${id}/replies`)
}

// 推荐服务相关接口
export const recommendApi = {
  // 获取推荐问题
  getQuestions: () => request.get('/recommend/questions'),
  
  // 提交推荐选项，获取推荐结果
  getRecommendation: (answers: number[]) => 
    request.post('/recommend/result', { answers })
}

// 管理员相关接口
export const adminApi = {
  // 管理员登录
  login: (data: {
    username: string
    password: string
  }) => request.post('/admin/login', data),
  
  // 管理员登出
  logout: () => request.post('/admin/logout'),
  
  // 获取管理员信息
  getAdminInfo: () => request.get('/admin/info')
}

// 文件上传相关接口
export const uploadApi = {
  // 上传图片到OSS
  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}