import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    activeTab: 'home',
    activeCategory: '推荐',
    user: {
      avatar: '',
      nickname: '小蓝同学',
      signature: '分享美好生活',
      followers: 128,
      following: 56,
      likes: 2340
    },
    categories: [
      '推荐', '关注', '附近', '穿搭', '美食', '旅行', '家居', '健身', '美妆', '数码', '读书', '宠物'
    ],
    notes: [],
      messages: [],
      loading: false,
      showDetailModal: false,
      detailNoteId: null,
      showLoginModal: false,
      cardRect: null,
      homeScrollTop: 0
  }),
  getters: {
    currentCategoryNotes: (state) => {
      if (state.activeCategory === '推荐') {
        return state.notes
      }
      return state.notes.filter(note => note.category === state.activeCategory)
    }
  },
  actions: {
    setActiveTab(tab) {
      this.activeTab = tab
    },
    setActiveCategory(category) {
      this.activeCategory = category
    },
    fetchNotes() {
      this.loading = true
      const mockNotes = [
        { id: 1, title: '夏日清新蓝色连衣裙穿搭分享，超显白的颜色搭配', cover: 'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=40&h=40&fit=crop', nickname: '时尚达人', likes: 1234, category: '穿搭' },
        { id: 2, title: '三亚旅行攻略｜五天四晚人均2000玩转海岛', cover: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=40&h=40&fit=crop', nickname: '旅行日记', likes: 2345, category: '旅行' },
        { id: 3, title: '超好吃的蓝莓芝士蛋糕做法，新手也能轻松搞定', cover: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=40&h=40&fit=crop', nickname: '美食博主', likes: 3456, category: '美食' },
        { id: 4, title: '北欧风家居布置｜让家充满蓝色调的清新感', cover: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop', nickname: '家居生活', likes: 4567, category: '家居' },
        { id: 5, title: '每日健身打卡｜30天减脂塑形计划第15天', cover: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=40&h=40&fit=crop', nickname: '健身教练', likes: 5678, category: '健身' },
        { id: 6, title: '蓝色系美妆教程｜夏日清透妆这样画超好看', cover: 'https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=40&h=40&fit=crop', nickname: '美妆新手', likes: 6789, category: '美妆' },
        { id: 7, title: '最新数码产品开箱｜蓝色款平板真的太美了', cover: 'https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=40&h=40&fit=crop', nickname: '数码控', likes: 7890, category: '数码' },
        { id: 8, title: '本月书单推荐｜五本让你爱不释手的好书', cover: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=40&h=40&fit=crop', nickname: '读书人', likes: 8901, category: '读书' },
        { id: 9, title: '超萌布偶猫日常｜养猫新手必看的注意事项', cover: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1593104547489-5cfb3839a3b5?w=40&h=40&fit=crop', nickname: '宠物达人', likes: 9012, category: '宠物' },
        { id: 10, title: '杭州探店｜隐藏在西湖边的蓝色咖啡馆', cover: 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=40&h=40&fit=crop', nickname: '咖啡控', likes: 1123, category: '美食' },
        { id: 11, title: '春季穿搭必备｜蓝色衬衫的N种搭配方法', cover: 'https://images.unsplash.com/photo-1485968579580-b6d095142e6e?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=40&h=40&fit=crop', nickname: '穿搭分享', likes: 2234, category: '穿搭' },
        { id: 12, title: '京都赏枫攻略｜秋天最美的十个打卡地', cover: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=400&h=500&fit=crop', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=40&h=40&fit=crop', nickname: '环球旅行', likes: 3345, category: '旅行' }
      ]
      this.notes = mockNotes
      this.loading = false
    },
    fetchMessages() {
      this.messages = [
        { id: 1, type: 'like', title: '赞和收藏', content: '时尚达人 赞了你的笔记', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=48&h=48&fit=crop', time: '刚刚', unread: true },
        { id: 2, type: 'comment', title: '评论', content: '美食博主 评论了你的笔记：太好看了！', avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=48&h=48&fit=crop', time: '5分钟前', unread: true },
        { id: 3, type: 'follow', title: '关注', content: '旅行日记 关注了你', avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=48&h=48&fit=crop', time: '10分钟前', unread: false },
        { id: 4, type: 'at', title: '@我', content: '家居生活 @了你', avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=48&h=48&fit=crop', time: '30分钟前', unread: false },
        { id: 5, type: 'comment', title: '评论', content: '咖啡控 评论了你的笔记', avatar: 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=48&h=48&fit=crop', time: '1小时前', unread: false },
        { id: 6, type: 'like', title: '赞和收藏', content: '数码控 收藏了你的笔记', avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=48&h=48&fit=crop', time: '2小时前', unread: false }
      ]
    }
  }
})
