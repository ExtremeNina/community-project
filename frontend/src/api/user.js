// ============================================================
// 移除了原先项目的接口请求（已迁移至 hongshu-web）
// ============================================================

// import request from './request'

// const userApi = {
//   getProfile() {
//     return request.get('/users/me')
//   },

//   getProfileEdit() {
//     return request.get('/users/me/edit')
//   },

//   updateProfile(data) {
//     return request.put('/users/me', data)
//   },

//   uploadAvatar(file) {
//     const formData = new FormData()
//     formData.append('file', file)
//     return request.post('/users/me/upload/image', formData, {
//       headers: { 'Content-Type': 'multipart/form-data' }
//     })
//   }
// }

// export default userApi