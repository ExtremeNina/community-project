// ============================================================
// 移除了原先项目的接口请求（已迁移至 hongshu-web）
// ============================================================

// import axios from 'axios'

// const request = axios.create({
//   baseURL: '/api',
//   timeout: 15000,
//   headers: {
//     'Content-Type': 'application/json'
//   }
// })

// let isRefreshing = false
// let failedQueue = []

// function processQueue(error, token = null) {
//   failedQueue.forEach(prom => {
//     if (error) {
//       prom.reject(error)
//     } else {
//       prom.resolve(token)
//     }
//   })
//   failedQueue = []
// }

// const refreshAxios = axios.create({
//   baseURL: '/api',
//   timeout: 15000,
//   headers: { 'Content-Type': 'application/json' },
//   withCredentials: true
// })

// request.interceptors.request.use(
//   config => {
//     const token = sessionStorage.getItem('token')
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`
//     }
//     return config
//   },
//   error => Promise.reject(error)
// )

// request.interceptors.response.use(
//   response => {
//     const res = response.data
//     if (res.code !== undefined && res.code !== 1) {
//       if (res.msg) {
//         console.error('API Error:', res.msg)
//       }
//       return Promise.reject(new Error(res.msg || '请求失败'))
//     }
//     return res
//   },
//   error => {
//     const originalRequest = error.config
//     if (error.response && error.response.status === 401 && !originalRequest._retry) {
//       if (originalRequest.url === '/auth/refresh') {
//         sessionStorage.removeItem('token')
//         failedQueue = []
//         isRefreshing = false
//         window.location.href = '/login'
//         return Promise.reject(error)
//       }
//       if (isRefreshing) {
//         return new Promise((resolve, reject) => {
//           failedQueue.push({ resolve, reject })
//         }).then(token => {
//           originalRequest.headers.Authorization = `Bearer ${token}`
//           return request(originalRequest)
//         })
//       }
//       originalRequest._retry = true
//       isRefreshing = true
//       return new Promise((resolve, reject) => {
//         refreshAxios.post('/auth/refresh')
//           .then(res => {
//             const body = res.data
//             if (body && body.code === 1 && body.data) {
//               const newToken = body.data
//               sessionStorage.setItem('token', newToken)
//               processQueue(null, newToken)
//               originalRequest.headers.Authorization = `Bearer ${newToken}`
//               resolve(request(originalRequest))
//             } else {
//               processQueue(new Error('Refresh failed'))
//               sessionStorage.removeItem('token')
//               window.location.href = '/login'
//               reject(error)
//             }
//           })
//           .catch(err => {
//             processQueue(err)
//             sessionStorage.removeItem('token')
//             isRefreshing = false
//             failedQueue = []
//             window.location.href = '/login'
//             reject(err)
//           })
//           .finally(() => {
//             isRefreshing = false
//           })
//       })
//     }
//     return Promise.reject(error)
//   }
// )

// export default request