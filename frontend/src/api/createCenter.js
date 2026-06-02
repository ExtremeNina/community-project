// ============================================================
// 移除了原先项目的接口请求（已迁移至 hongshu-web）
// ============================================================

// import request from './request'

// const createCenterApi = {
//   publish(data) {
//     return request.post('/create-center', data)
//   },

//   uploadImage(file) {
//     const formData = new FormData()
//     formData.append('image', file)
//     return request.post('/create-center/upload/image', formData, {
//       headers: { 'Content-Type': 'multipart/form-data' }
//     })
//   },

//   saveDraft(data) {
//     return request.post('/create-center/draft', data)
//   },

//   getDrafts() {
//     return request.get('/create-center/drafts')
//   },

//   getPublished() {
//     return request.get('/create-center/published')
//   },

//   getPending() {
//     return request.get('/create-center/pending')
//   },

//   updateDraft(draftId) {
//     return request.put(`/create-center/drafts/${draftId}`)
//   },

//   deleteDraft(draftId) {
//     return request.delete(`/create-center/drafts/${draftId}`)
//   },

//   deleteArticle(articleId) {
//     return request.delete(`/create-center/${articleId}`)
//   },

//   getArticle(articleId) {
//     return request.get(`/create-center/${articleId}`)
//   },

//   searchByTitle(title, status) {
//     return request.get('/create-center/by-title', { params: { title, status } })
//   },

//   searchByDate(date, status) {
//     return request.get('/create-center/by-date', { params: { date, status } })
//   }
// }

// export default createCenterApi