export const STATUS_MAP = {
  0: 'draft',
  1: 'reviewing',
  2: 'published'
}

export const STATUS_LABEL_MAP = {
  draft: '草稿',
  reviewing: '审核中',
  published: '已发布'
}

export function mapArticleVO(item) {
  const statusKey = STATUS_MAP[item.status] || 'draft'
  return {
    id: item.id,
    title: item.title || '',
    cover: item.coverImageUrl || '',
    summary: item.summary || '',
    views: item.viewCount || 0,
    likes: item.loveCount || 0,
    comments: item.comments || 0,
    date: item.createTime || '',
    status: statusKey,
    statusLabel: STATUS_LABEL_MAP[statusKey] || '未知'
  }
}

export function mapArticleList(list) {
  return (list || []).map(mapArticleVO)
}