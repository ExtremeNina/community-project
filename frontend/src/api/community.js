// ============================================================
// 移除了原先项目的接口请求（已迁移至 hongshu-web）
// ============================================================

// import request from './request'

// const communityApi = {
//   checkLogin() {
//     return request.get('/community/isLogin')
//   },

//   getUserAvatar() {
//     return request.get('/community/user/avatar')
//   },

//   getRecommend() {
//     return request.get('/community/Recommend')
//   },

//   getUnreadMessage(userId) {
//     return request.get('/community/unreadMessage', { params: { userId } })
//   },

//   getArticleDetail(articleId) {
//     return request.get(`/articles/${articleId}`)
//   },

//   getCommentUserProfile() {
//     return request.get('/comment/userProfile')
//   },

//   publishComment(data) {
//     return request.post('/comment/publish', data)
//   },

//   getCommentList(articleId) {
//     return request.get(`/comment/${articleId}`)
//   }
// }

// export default communityApi