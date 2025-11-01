<template>
  <div class="announcement-board">
    <div class="board-header">
      <h2>
        <i class="el-icon-bell"></i>
        系统公告
      </h2>
      <p class="subtitle">欢迎使用考核管理系统</p>
    </div>

    <div class="announcement-list">
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
      <div class="detail-content">
        {{ currentAnnouncement.fullContent }}
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AnnouncementBoard',
  data() {
    return {
      dialogVisible: false,
      currentAnnouncement: {},
      announcements: [
        {
          id: 1,
          title: '关于开展2025年度考核工作的通知',
          content: '各单位：为做好2025年度考核工作，现将有关事项通知如下。请各单位高度重视，认真组织实施...',
          fullContent: '各单位：\n\n为做好2025年度考核工作，现将有关事项通知如下：\n\n一、考核时间安排\n1. 自评阶段：2025年1月1日-1月15日\n2. 互评阶段：2025年1月16日-1月31日\n3. 总结阶段：2025年2月1日-2月15日\n\n二、考核要求\n1. 各单位要高度重视，认真组织实施\n2. 确保考核数据真实准确\n3. 按时完成各阶段工作\n\n请各单位认真落实，如有问题请及时联系考核办公室。',
          time: '2025-01-05 10:30',
          author: '系统管理员',
          important: true,
          isNew: true
        },
        {
          id: 2,
          title: '考核系统操作指南已更新',
          content: '为帮助各位用户更好地使用考核系统，我们更新了操作指南。新增了图表看板使用说明、数据导出功能介绍等内容...',
          fullContent: '为帮助各位用户更好地使用考核系统，我们更新了操作指南。\n\n本次更新内容包括：\n1. 图表看板使用说明\n2. 数据导出功能介绍\n3. 常见问题解答\n4. 快捷操作指引\n\n请各位用户查阅操作指南，提高工作效率。如遇到问题可联系技术支持。',
          time: '2025-01-03 14:20',
          author: '技术支持',
          important: false,
          isNew: true
        },
        {
          id: 3,
          title: '系统维护通知',
          content: '为提升系统性能和用户体验，计划于1月10日22:00-24:00进行系统维护升级，届时系统将暂停服务...',
          fullContent: '各位用户：\n\n为提升系统性能和用户体验，计划进行系统维护升级。\n\n维护时间：2025年1月10日 22:00 - 24:00\n\n维护内容：\n1. 数据库性能优化\n2. 界面交互体验提升\n3. 新功能上线准备\n\n维护期间系统将暂停服务，请各位用户提前做好工作安排。给您带来的不便敬请谅解！',
          time: '2025-01-02 09:15',
          author: '运维团队',
          important: false,
          isNew: false
        },
        {
          id: 4,
          title: '2024年度优秀单位表彰名单公示',
          content: '经过综合评定，现将2024年度优秀单位名单予以公示。公示期为2025年1月5日至1月12日...',
          fullContent: '各单位：\n\n经过综合评定，现将2024年度优秀单位名单予以公示。\n\n优秀单位名单：\n1. XX教学组织\n2. XX领导班子\n3. XX管理部门\n\n公示期：2025年1月5日至1月12日\n\n如有异议，请在公示期内向考核办公室反馈。联系电话：XXXX-XXXXXXXX',
          time: '2024-12-28 16:45',
          author: '考核办公室',
          important: true,
          isNew: false
        }
      ]
    }
  },
  methods: {
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
  white-space: pre-wrap;
  min-height: 200px;
  padding: 15px;
  background: #f9fafb;
  border-radius: 6px;
}
</style>
