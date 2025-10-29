<template>
  <div class="teacher-dashboard">
    <!-- 上传模板对话框 -->
    <el-dialog title="上传模板 (Excel)" :visible.sync="uploadTemplateDialogVisible" width="480px">
      <div>
        <p>请选择模板文件并上传到服务器。</p>
        <file-upload
          v-model="templateUrl"
          :limit="1"
          :file-type="['xls','xlsx']"
          :file-size="50"
          :is-show-tip="true"
          :action="'/common/upload'"
        />
        <p class="tip">上传后，"下载模板"将直接从服务器取回该文件。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadTemplateDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <div class="dashboard-header">
      <div class="header-left">
        <h3>教师看板</h3>
        <el-tag size="small" class="dept-tag">{{ selectedDeptNode && selectedDeptNode.label ? selectedDeptNode.label : '未选择组织' }}</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">查询</el-button>
        <el-button icon="el-icon-refresh" size="small" @click="handleRefresh">刷新</el-button>
        <el-button icon="el-icon-upload2" size="small" @click="handleImport">导入</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        <el-button icon="el-icon-upload" size="small" @click="handleUploadTemplate">上传模板</el-button>
        <el-button icon="el-icon-document" size="small" @click="handleDownloadTemplate">下载模板</el-button>
        <span class="label">学期</span>
        <el-select v-model="selectedSemester" placeholder="选择学期" size="small" @change="onSemesterChange">
          <el-option label="2024春季" value="2024-spring" />
          <el-option label="2024秋季" value="2024-autumn" />
          <el-option label="2025春季" value="2025-spring" />
        </el-select>
      </div>
    </div>
    
    <div class="dashboard-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon teacher-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ teacherStats.total }}</div>
            <div class="stat-label">教师总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ teacherStats.courses }}</div>
            <div class="stat-label">开设课程</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon evaluation-icon">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ teacherStats.avgRating }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon workload-icon">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ teacherStats.avgWorkload }}</div>
            <div class="stat-label">平均课时</div>
          </div>
        </div>
      </div>

      <!-- 教师列表 -->
      <div class="teacher-table">
        <el-table :data="teacherTablePageData" border size="small" style="width: 100%">
          <el-table-column prop="teacherId" label="工号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="department" label="所属部门" min-width="150" />
          <el-table-column prop="title" label="职称" width="120" />
          <el-table-column prop="subject" label="主讲科目" min-width="150" />
          <el-table-column prop="courseCount" label="课程数" width="80" />
          <el-table-column prop="studentCount" label="学生数" width="80" />
          <el-table-column prop="workload" label="课时" width="80" />
          <el-table-column prop="rating" label="评分" width="80">
            <template slot-scope="scope">
              <el-rate
                v-model="scope.row.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
                :max="5"
              />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="mini">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="viewTeacher(scope.row)">查看</el-button>
              <el-button type="text" size="mini" @click="editTeacher(scope.row)">编辑</el-button>
              <el-button type="text" size="mini" @click="viewCourses(scope.row)">课程</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="table-pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="teacherPagination.total"
            :page-size="teacherPagination.pageSize"
            :current-page="teacherPagination.currentPage"
            :page-sizes="[10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate } from "@/api/sms/template"

export default {
  name: "TeacherDashboard",
  components: { FileUpload },
  props: {
    selectedDeptNode: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      selectedSemester: '2024-autumn',
      teacherStats: {
        total: 0,
        courses: 0,
        avgRating: 0,
        avgWorkload: 0
      },
      teacherTableData: [],
      teacherPagination: { currentPage: 1, pageSize: 10, total: 0 },
      // 模板相关数据
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API
    }
  },
  computed: {
    teacherTablePageData() {
      const start = (this.teacherPagination.currentPage - 1) * this.teacherPagination.pageSize
      const end = start + this.teacherPagination.pageSize
      return this.teacherTableData.slice(start, end)
    },
    currentOrgCode() {
      return this.selectedDeptNode?.orgCode || ''
    },
    boardType() {
      return 'teacher'
    }
  },
  watch: {
    selectedSemester() {
      this.loadTeacherData()
    },
    templateUrl(newVal) {
      if (newVal) {
        // 文件上传完成后自动绑定模板
        this.templateFileName = newVal.split('/').pop()
        this.bindTemplateToOrg()
      }
    }
  },
  created() {
    this.loadTeacherData()
  },
  methods: {
    onSemesterChange() {
      this.loadTeacherData()
    },
    handleSearch() {
      this.loadTeacherData()
    },
    handleRefresh() {
      this.loadTeacherData()
    },
    handleExport() {
      this.$message.info('导出教师数据')
    },
    loadTeacherData() {
      // 模拟教师统计数据
      this.teacherStats = {
        total: 156,
        courses: 89,
        avgRating: 4.2,
        avgWorkload: 16
      }
      
      // 模拟教师表格数据
      this.teacherTableData = [
        {
          teacherId: 'T001',
          name: '王教授',
          department: '计算机学院',
          title: '教授',
          subject: '数据结构与算法',
          courseCount: 3,
          studentCount: 120,
          workload: 18,
          rating: 4.8,
          status: '在职'
        },
        {
          teacherId: 'T002',
          name: '李副教授',
          department: '计算机学院',
          title: '副教授',
          subject: '操作系统',
          courseCount: 2,
          studentCount: 80,
          workload: 12,
          rating: 4.5,
          status: '在职'
        },
        {
          teacherId: 'T003',
          name: '张讲师',
          department: '软件学院',
          title: '讲师',
          subject: 'Web开发技术',
          courseCount: 4,
          studentCount: 160,
          workload: 20,
          rating: 4.2,
          status: '在职'
        }
        // 可以添加更多模拟数据
      ]
      
      this.teacherPagination.total = this.teacherTableData.length
      this.teacherPagination.currentPage = 1
    },
    getStatusType(status) {
      const typeMap = {
        '在职': 'success',
        '休假': 'warning',
        '离职': 'info'
      }
      return typeMap[status] || 'info'
    },
    viewTeacher(row) {
      this.$message.info(`查看教师：${row.name}`)
    },
    editTeacher(row) {
      this.$message.info(`编辑教师：${row.name}`)
    },
    viewCourses(row) {
      this.$message.info(`查看${row.name}的课程`)
    },
    handleSizeChange(size) {
      this.teacherPagination.pageSize = size
      this.teacherPagination.currentPage = 1
    },
    handleCurrentChange(page) {
      this.teacherPagination.currentPage = page
    },
    // 模板相关方法
    handleImport() {
      this.$message.info('导入功能待实现')
    },
    handleUploadTemplate() {
      this.uploadTemplateDialogVisible = true
    },
    async handleDownloadTemplate() {
      if (!this.currentOrgCode) {
        this.$message.warning('请先选择部门')
        return
      }
      
      try {
        const response = await resolveTemplate(
          this.currentOrgCode,
          this.boardType,
          this.selectedSemester.split('-')[0] // 从学期中提取年份
        )
        
        if (response.code === 200 && response.data) {
          // 使用返回的filePath下载文件
          const downloadUrl = this.baseApi + response.data.filePath
          const link = document.createElement('a')
          link.href = downloadUrl
          link.download = response.data.fileName || '模板.xlsx'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          this.$message.success('模板下载成功')
        } else {
          this.$message.warning('未在组织链找到可用模板')
        }
      } catch (error) {
        console.error('下载模板失败:', error)
        this.$message.error('下载模板失败')
      }
    },
    async bindTemplateToOrg() {
      if (!this.templateUrl || !this.currentOrgCode) {
        this.$message.warning('请确保已上传文件并选择了部门')
        return
      }
      
      try {
        const response = await bindTemplate({
          orgCode: this.currentOrgCode,
          boardType: this.boardType,
          year: this.selectedSemester.split('-')[0], // 从学期中提取年份
          filePath: this.templateUrl,
          fileName: this.templateFileName
        })
        
        if (response.code === 200) {
          this.$message.success('模板绑定成功')
          this.uploadTemplateDialogVisible = false
          this.templateUrl = ''
          this.templateFileName = ''
        } else {
          this.$message.error(response.msg || '模板绑定失败')
        }
      } catch (error) {
        console.error('绑定模板失败:', error)
        this.$message.error('绑定模板失败')
      }
    }
  }
}
</script>

<style scoped>
.teacher-dashboard {
  width: 100%;
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left h3 {
  margin: 0 12px 0 0;
  color: #303133;
  font-size: 16px;
}

.dept-tag {
  margin-left: 8px;
}

.header-right .label {
  margin-right: 8px;
  color: #606266;
}

.header-right .el-button,
.header-right .el-select {
  margin-right: 8px;
}

.dashboard-content {
  min-height: 400px;
}

.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: #fff;
}

.teacher-icon {
  background: #409eff;
}

.course-icon {
  background: #67c23a;
}

.evaluation-icon {
  background: #e6a23c;
}

.workload-icon {
  background: #f56c6c;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.teacher-table {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 16px;
}

.table-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>