import {
  mockNotes,
  mockCategories,
  mockComments,
  mockUsers
} from './data'

function findMockNoteById(noteId: string) {
  return mockNotes.find(n => n.id === noteId) || mockNotes[0]
}

export function getMockData(url: string, params?: any) {
  // 首页推荐笔记
  if (url.includes('/api/community/Recommend') || url.includes('/api/search/articles')) {
    return {
      code: 1,
      msg: 'success',
      data: {
        records: [...mockNotes],
        total: mockNotes.length,
        current: 1,
        size: 20
      }
    }
  }

  // 分类列表
  if (url.includes('/api/category/tree') || url.includes('/api/category')) {
    return {
      code: 1,
      msg: 'success',
      data: [...mockCategories]
    }
  }

  // 笔记详情
  if (url.includes('/api/articles/')) {
    const noteId = url.split('/api/articles/')[1]?.split('?')[0] || ''
    const note = findMockNoteById(noteId)
    return {
      code: 1,
      msg: 'success',
      data: {
        id: Number(note.id),
        title: note.title,
        content: note.content,
        coverImageUrl: note.noteCover,
        author: note.username,
        userProfileVO: {
          userId: Number(note.uid),
          username: note.username,
          icon: note.avatar
        },
        tagList: note.tagList.map(t => ({ title: t.title })),
        likeCount: note.likeCount,
        commentCount: note.commentCount,
        isLike: false,
        isCollection: false
      }
    }
  }

  // 笔记详情 (通过 ID)
  if (url.includes('/api/create-center/')) {
    const noteId = url.split('/api/create-center/')[1]?.split('?')[0] || params?.id || ''
    const note = findMockNoteById(noteId)
    return {
      code: 1,
      msg: 'success',
      data: {
        id: Number(note.id),
        title: note.title,
        content: note.content,
        coverImageUrl: note.noteCover,
        author: note.username,
        userProfileVO: {
          userId: Number(note.uid),
          username: note.username,
          icon: note.avatar
        },
        tagList: note.tagList.map(t => ({ title: t.title })),
        likeCount: note.likeCount,
        commentCount: note.commentCount,
        isLike: false,
        isCollection: false
      }
    }
  }

  // 评论列表
  if (url.includes('/api/comment/')) {
    return {
      code: 1,
      msg: 'success',
      data: {
        records: [...mockComments],
        total: mockComments.length
      }
    }
  }

  // 用户信息
  if (url.includes('/api/users/me') || url.includes('/api/user/profile')) {
    const mockUser = mockUsers['2001']
    return {
      code: 1,
      msg: 'success',
      data: mockUser
    }
  }

  // 搜索记录
  if (url.includes('/api/search/record')) {
    return {
      code: 1,
      msg: 'success',
      data: ['咖啡', '穿搭', '减脂餐', '护肤推荐', '家居改造']
    }
  }

  // 热门搜索
  if (url.includes('/api/search/hot')) {
    return {
      code: 1,
      msg: 'success',
      data: ['周末去哪儿', 'OOTD', '减脂餐', '护肤', '猫咪日常']
    }
  }

  // 关注动态
  if (url.includes('/api/follower/getFollowTrend')) {
    return {
      code: 1,
      msg: 'success',
      data: {
        records: mockNotes.slice(0, 5),
        total: 5
      }
    }
  }

  // 消息数量
  if (url.includes('/api/im/countMessage') || url.includes('/api/im/count')) {
    return {
      code: 1,
      msg: 'success',
      data: {
        chatCount: 3,
        likeOrCollectionCount: 12,
        commentCount: 5,
        followCount: 2
      }
    }
  }

  // 聊天用户列表
  if (url.includes('/api/im/chatUserList')) {
    return {
      code: 1,
      msg: 'success',
      data: [
        { userId: 2002, username: '穿搭薯队长', icon: 'https://picsum.photos/seed/avatar2/100/100', count: 2, lastMessage: '这件衣服很适合你！' },
        { userId: 2003, username: '轻食生活家', icon: 'https://picsum.photos/seed/avatar3/100/100', count: 1, lastMessage: '今天的减脂餐做了吗？' }
      ]
    }
  }

  // 创作中心 - 全部文章
  if (url.includes('/api/create-center/all')) {
    const allNotes = [
      ...mockNotes.slice(0, 21).map(n => ({ ...n, status: 3 })),
      ...mockNotes.slice(21, 30).map(n => ({ ...n, status: 2 })),
      ...mockNotes.slice(30).map(n => ({ ...n, status: 0 })),
    ]
    return {
      code: 1,
      msg: 'success',
      data: allNotes
    }
  }

  // 创作中心 - 已发布文章 (status=3)
  if (url.includes('/api/create-center/published')) {
    return {
      code: 1,
      msg: 'success',
      data: mockNotes.slice(0, 21).map(n => ({
        ...n,
        status: 3
      }))
    }
  }

  // 创作中心 - 待审核文章 (status=2)
  if (url.includes('/api/create-center/pending')) {
    return {
      code: 1,
      msg: 'success',
      data: mockNotes.slice(21, 30).map(n => ({
        ...n,
        status: 2
      }))
    }
  }

  // 创作中心 - 未通过文章 (status=0)
  if (url.includes('/api/create-center/rejected')) {
    return {
      code: 1,
      msg: 'success',
      data: mockNotes.slice(30).map(n => ({
        ...n,
        status: 0
      }))
    }
  }

  // 创作中心 - 草稿 (status=1)
  if (url.includes('/api/create-center/drafts')) {
    return {
      code: 1,
      msg: 'success',
      data: mockNotes.slice(0, 0).map(n => ({
        ...n,
        status: 1
      }))
    }
  }

  return null
}