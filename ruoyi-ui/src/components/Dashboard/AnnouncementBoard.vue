<template>
  <div class="announcement-board">
    <div class="board-header">
      <h2>
        <i class="el-icon-bell"></i>
        系统公告
      </h2>
      <p class="subtitle">欢迎使用考核管理系统</p>
    </div>

    <div class="announcement-list" v-loading="loading">
      <template v-if="!loading && announcements.length > 0">
        <el-card
          v-for="item in announcements"
          :key="item.id"
          class="announcement-card"
          shadow="hover"
        >
          <div class="card-header">
            <div class="title-row">
              <el-tag v-if="item.important" type="danger" size="mini" effect="dark">
                重要
              </el-tag>
              <el-tag v-else-if="item.isNew" type="warning" size="mini">
                最新
              </el-tag>
              <h3 class="announcement-title">{{ item.title }}</h3>
            </div>
            <span class="announcement-time">{{ item.time }}</span>
          </div>
          <div class="announcement-content">
            {{ item.content }}
          </div>
          <div class="announcement-footer">
            <span class="author">
              <i class="el-icon-user"></i>
              {{ item.author }}
            </span>
            <el-button type="text" size="small" @click="viewDetail(item)">
              查看详情 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
        </el-card>
      </template>

      <!-- 空数据提示 -->
      <el-empty
        v-if="!loading && announcements.length === 0"
        description="暂无公告信息"
        :image-size="120"
      >
      </el-empty>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      :title="currentAnnouncement.title"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="detail-header">
        <div class="detail-meta">
          <span class="meta-item">
            <i class="el-icon-user"></i>
            发布人：{{ currentAnnouncement.author }}
          </span>
          <span class="meta-item">
            <i class="el-icon-time"></i>
            发布时间：{{ currentAnnouncement.time }}
          </span>
        </div>
      </div>
      <div class="detail-content" v-html="currentAnnouncement.fullContent"></div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice } from '@/api/system/notice'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AnnouncementBoard',
  data() {
    return {
      dialogVisible: false,
      currentAnnouncement: {},
      announcements: [],
      loading: false
    }
  },
  created() {
    this.loadAnnouncements()
  },
  methods: {
    // 加载公告列表
    loadAnnouncements() {
      this.loading = true
      listNotice({
        status: '0', // 只查询正常状态的公告
        pageNum: 1,
        pageSize: 10 // 获取最新的10条公告
      }).then(response => {
        // 将后端数据转换为前端需要的格式
        this.announcements = response.rows.map(notice => {
          return this.formatNotice(notice)
        })
      }).catch(error => {
        console.error('加载公告失败:', error)
        this.$message.error('加载公告失败')
      }).finally(() => {
        this.loading = false
      })
    },

    // 去除HTML标签，获取纯文本
    stripHtmlTags(html) {
      if (!html) return ''
      // 创建一个临时div元素来解析HTML
      const tmp = document.createElement('div')
      tmp.innerHTML = html
      return tmp.textContent || tmp.innerText || ''
    },

    // 格式化公告数据
    formatNotice(notice) {
      const fullContent = notice.noticeContent || ''
      // 从富文本中提取纯文本用于摘要显示
      const plainText = this.stripHtmlTags(fullContent)
      // 生成摘要内容（取前100个字符）
      const content = plainText.length > 100
        ? plainText.substring(0, 100) + '...'
        : plainText

      // 判断是否为最新公告（3天内创建的）
      const createTime = new Date(notice.createTime)
      const now = new Date()
      const diffDays = (now - createTime) / (1000 * 60 * 60 * 24)
      const isNew = diffDays <= 3

      // 判断是否为重要公告（noticeType为1表示通知，通常比较重要）
      const important = notice.noticeType === '1'

      return {
        id: notice.noticeId,
        title: notice.noticeTitle,
        content: content,
        fullContent: fullContent, // 保留原始HTML内容用于详情展示
        time: parseTime(notice.createTime, '{y}-{m}-{d} {h}:{i}'),
        author: notice.createBy || '系统管理员',
        important: important,
        isNew: isNew
      }
    },

    viewDetail(item) {
      this.currentAnnouncement = item
      this.dialogVisible = true
    }
  }
}
</script>

<style scoped>
.announcement-board {
  padding: 20px;
  background: #f5f7fa;
  min-height: 600px;
}

.board-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.board-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.board-header h2 i {
  font-size: 32px;
  animation: ring 2s ease-in-out infinite;
}

@keyframes ring {
  0%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(-10deg); }
  20%, 40% { transform: rotate(10deg); }
}

.subtitle {
  margin: 10px 0 0 0;
  font-size: 14px;
  opacity: 0.9;
}

.announcement-list {
  max-width: 1000px;
  margin: 0 auto;
}

.announcement-card {
  margin-bottom: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
}

.announcement-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.announcement-card ::v-deep .el-card__body {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.announcement-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.announcement-time {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
  margin-left: 16px;
}

.announcement-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.author {
  color: #909399;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.author i {
  font-size: 14px;
}

/* 详情对话框样式 */
.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item i {
  color: #909399;
}

.detail-content {
  color: #303133;
  font-size: 14px;
  line-height: 2;
  min-height: 200px;
  padding: 15px;
  background: #f9fafb;
  border-radius: 6px;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

/* 富文本内容样式优化 */
.detail-content ::v-deep img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 10px 0;
}

.detail-content ::v-deep p {
  margin: 8px 0;
}

.detail-content ::v-deep h1,
.detail-content ::v-deep h2,
.detail-content ::v-deep h3,
.detail-content ::v-deep h4,
.detail-content ::v-deep h5,
.detail-content ::v-deep h6 {
  margin: 12px 0 8px;
  font-weight: 600;
}

.detail-content ::v-deep ul,
.detail-content ::v-deep ol {
  padding-left: 20px;
  margin: 8px 0;
}

.detail-content ::v-deep table {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
}

.detail-content ::v-deep table td,
.detail-content ::v-deep table th {
  border: 1px solid #ddd;
  padding: 8px;
}

.detail-content ::v-deep table th {
  background-color: #f2f2f2;
  font-weight: 600;
}

.detail-content ::v-deep blockquote {
  border-left: 4px solid #ddd;
  padding-left: 15px;
  margin: 10px 0;
  color: #666;
}

.detail-content ::v-deep pre {
  background: #f4f4f4;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

.detail-content ::v-deep code {
  background: #f4f4f4;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}
</style>
