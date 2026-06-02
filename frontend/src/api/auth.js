// ============================================================
// 移除了原先项目的接口请求（已迁移至 hongshu-web）
// ============================================================

// import request from './request'

// const authApi = {
//   login(username, password) {
//     return request.post('/auth/login', { username, password })
//   },

//   register(data) {
//     return request.post('/auth/register', data)
//   },

//   sendQQEmail(email) {
//     return request.post('/auth/QQ/sendCode', { email })
//   },

//   qqLogin(mail, code) {
//     return request.post('/auth/QQ/login', { mail, code })
//   },

//   sendVerifyCode(data) {
//     return request.post('/auth/verify-code', data)
//   },

//   githubLogin() {
//     return request.post('/oauth/github/login')
//   },

//   refreshToken() {
//     return request.post('/auth/refresh')
//   },

//   logout() {
//     return request.post('/auth/loginOut')
//   }
// }

// export default authApi