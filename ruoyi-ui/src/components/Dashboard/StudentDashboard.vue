<template>
  <div class="student-dashboard">
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
        <h3>学生看板</h3>
        <el-tag size="small" class="dept-tag">{{ selectedDeptNode && selectedDeptNode.label ? selectedDeptNode.label : '未选择组织' }}</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">查询</el-button>
        <el-button icon="el-icon-refresh" size="small" @click="handleRefresh">刷新</el-button>
        <el-button icon="el-icon-upload" size="small" @click="handleImport">导入</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        <el-button icon="el-icon-upload2" size="small" @click="openUploadTemplateDialog">上传模板</el-button>
        <el-button icon="el-icon-document" size="small" @click="downloadTemplate">下载模板</el-button>
        <span class="label">学年</span>
        <el-date-picker
          v-model="selectedYear"
          type="year"
          placeholder="选择学年"
          value-format="yyyy"
          :default-value="new Date()"
          @change="onYearChange"
          size="small"
        />
      </div>
    </div>
    
    <div class="dashboard-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon student-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ studentStats.total }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon grade-icon">
            <i class="el-icon-medal"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ studentStats.excellent }}</div>
            <div class="stat-label">优秀学生</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon class-icon">
            <i class="el-icon-school"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ studentStats.classes }}</div>
            <div class="stat-label">班级数量</div>
          </div>
        </div>
      </div>

      <!-- 学生列表 -->
      <div class="student-table">
        <el-table :data="studentTablePageData" border size="small" style="width: 100%">
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="grade" label="年级" width="100" />
          <el-table-column prop="major" label="专业" min-width="150" />
          <el-table-column prop="totalScore" label="总成绩" width="100" />
          <el-table-column prop="ranking" label="排名" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="mini">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="viewStudent(scope.row)">查看</el-button>
              <el-button type="text" size="mini" @click="editStudent(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="table-pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="studentPagination.total"
            :page-size="studentPagination.pageSize"
            :current-page="studentPagination.currentPage"
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
  name: "StudentDashboard",
  components: { FileUpload },
  props: {
    selectedDeptNode: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      selectedYear: String(new Date().getFullYear()),
      studentStats: {
        total: 0,
        excellent: 0,
        classes: 0
      },
      studentTableData: [],
      studentPagination: { currentPage: 1, pageSize: 10, total: 0 },
      // 模板相关数据
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API
    }
  },
  computed: {
    studentTablePageData() {
      const start = (this.studentPagination.currentPage - 1) * this.studentPagination.pageSize
      const end = start + this.studentPagination.pageSize
      return this.studentTableData.slice(start, end)
    },
    currentOrgCode() {
      return this.selectedDeptNode?.orgCode || ''
    },
    boardType() {
      return 'student'
    }
  },
  watch: {
    selectedYear() {
      this.loadStudentData()
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
    this.loadStudentData()
  },
  methods: {
    onYearChange() {
      this.loadStudentData()
    },
    handleSearch() {
      this.loadStudentData()
    },
    handleRefresh() {
      this.loadStudentData()
    },
    loadStudentData() {
      // 模拟学生数据
      this.studentStats = {
        total: 1250,
        excellent: 186,
        classes: 42
      }
      
      // 模拟学生表格数据
      this.studentTableData = [
        {
          studentId: '2023001',
          name: '张三',
          className: '计算机1班',
          grade: '2023级',
          major: '计算机科学与技术',
          totalScore: 92.5,
          ranking: 1,
          status: '优秀'
        },
        {
          studentId: '2023002',
          name: '李四',
          className: '计算机1班',
          grade: '2023级',
          major: '计算机科学与技术',
          totalScore: 88.0,
          ranking: 5,
          status: '良好'
        }
        // 可以添加更多模拟数据
      ]
      
      this.studentPagination.total = this.studentTableData.length
      this.studentPagination.currentPage = 1
    },
    getStatusType(status) {
      const typeMap = {
        '优秀': 'success',
        '良好': 'primary',
        '合格': 'info',
        '不合格': 'danger'
      }
      return typeMap[status] || 'info'
    },
    viewStudent(row) {
      this.$message.info(`查看学生：${row.name}`)
    },
    editStudent(row) {
      this.$message.info(`编辑学生：${row.name}`)
    },
    handleSizeChange(size) {
      this.studentPagination.pageSize = size
      this.studentPagination.currentPage = 1
    },
    handleCurrentChange(page) {
      this.studentPagination.currentPage = page
    },
    // 模板相关方法
    handleImport() {
      this.$message.info('导入功能待实现')
    },
    handleExport() {
      this.$message.info('导出功能待实现')
    },
    handleUploadTemplate() {
      // 重置上传状态
      this.templateUrl = ''
      this.templateFileName = ''
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
          this.selectedYear
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
          year: this.selectedYear,
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
.student-dashboard {
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

.header-right .el-button {
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

.student-icon {
  background: #409eff;
}

.grade-icon {
  background: #67c23a;
}

.class-icon {
  background: #e6a23c;
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

.student-table {
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