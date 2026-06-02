import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: sessionStorage.getItem('token') || null,
    user: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userAvatar: (state) => state.user?.icon || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=36&h=36&fit=crop',
    userNickname: (state) => state.user?.username || '未登录用户'
  },
  actions: {
    setToken(token) {
      this.token = token
      if (token) {
        sessionStorage.setItem('token', token)
      } else {
        sessionStorage.removeItem('token')
      }
    },
    setUser(user) {
      this.user = user
    },
    logout() {
      this.token = null
      this.user = null
      sessionStorage.removeItem('token')
    }
  }
})