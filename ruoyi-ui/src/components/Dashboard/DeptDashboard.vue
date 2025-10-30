<template>
  <div class="dept-dashboard">
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
        <h3>单位看板</h3>
        <el-tag size="small" class="dept-tag">{{ selectedDeptNode && selectedDeptNode.label ? selectedDeptNode.label : '未选择组织' }}</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">查询</el-button>
        <el-button icon="el-icon-refresh" size="small" @click="handleRefresh">刷新</el-button>
        <el-button icon="el-icon-upload2" size="small" @click="handleImport">导入</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        <el-button icon="el-icon-upload" size="small" @click="handleUploadTemplate">上传模板</el-button>
        <el-button icon="el-icon-document" size="small" @click="handleDownloadTemplate">下载模板</el-button>
        <span class="label">年度</span>
        <el-date-picker
          v-model="selectedYear"
          type="year"
          placeholder="选择年度"
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
          <div class="stat-icon dept-icon">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ deptStats.total }}</div>
            <div class="stat-label">下级单位</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon staff-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ deptStats.staff }}</div>
            <div class="stat-label">总人数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon performance-icon">
            <i class="el-icon-trophy"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ deptStats.avgScore }}</div>
            <div class="stat-label">平均绩效</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon budget-icon">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ deptStats.budget }}万</div>
            <div class="stat-label">年度预算</div>
          </div>
        </div>
      </div>

      <!-- 单位列表 -->
      <div class="dept-table">
        <el-table :data="deptTablePageData" border size="small" style="width: 100%" row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
          <el-table-column prop="deptName" label="单位名称" min-width="200" />
          <el-table-column prop="deptCode" label="单位编码" width="120" />
          <el-table-column prop="leader" label="负责人" width="100" />
          <el-table-column prop="staffCount" label="人员数量" width="100" />
          <el-table-column prop="performance" label="绩效评分" width="100">
            <template slot-scope="scope">
              <el-progress
                :percentage="scope.row.performance"
                :color="getPerformanceColor(scope.row.performance)"
                :show-text="true"
                :format="format => `${format}分`"
              />
            </template>
          </el-table-column>
          <el-table-column prop="budget" label="预算(万元)" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="mini">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="成立时间" width="120" />
          <el-table-column label="操作" width="180">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="viewDept(scope.row)">查看</el-button>
              <el-button type="text" size="mini" @click="editDept(scope.row)">编辑</el-button>
              <el-button type="text" size="mini" @click="viewStaff(scope.row)">人员</el-button>
              <el-button type="text" size="mini" @click="viewReport(scope.row)">报告</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="table-pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="deptPagination.total"
            :page-size="deptPagination.pageSize"
            :current-page="deptPagination.currentPage"
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
import { bindTemplate, resolveTemplate } from "@/api/system/template"

export default {
  name: "DeptDashboard",
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
      deptStats: {
        total: 0,
        staff: 0,
        avgScore: 0,
        budget: 0
      },
      deptTableData: [],
      deptPagination: { currentPage: 1, pageSize: 10, total: 0 },
      // 模板相关数据
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API
    }
  },
  computed: {
    deptTablePageData() {
      const start = (this.deptPagination.currentPage - 1) * this.deptPagination.pageSize
      const end = start + this.deptPagination.pageSize
      return this.deptTableData.slice(start, end)
    },
    currentOrgCode() {
      return this.selectedDeptNode?.orgCode || ''
    },
    boardType() {
      return 'dept'
    }
  },
  watch: {
    selectedYear() {
      this.loadDeptData()
    },
    currentOrgCode() {
      // 当组织节点切换时，重新加载数据
      this.loadDeptData()
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
    this.loadDeptData()
  },
  methods: {
    onYearChange() {
      this.loadDeptData()
    },
    handleSearch() {
      this.loadDeptData()
    },
    handleRefresh() {
      this.loadDeptData()
    },
    handleExport() {
      this.$message.info('导出单位数据')
    },
    loadDeptData() {
      // 模拟单位统计数据
      this.deptStats = {
        total: 12,
        staff: 856,
        avgScore: 85.6,
        budget: 2580
      }

      // 模拟单位表格数据（树形结构）
      this.deptTableData = [
        {
          id: 1,
          deptName: '计算机学院',
          deptCode: 'CS001',
          leader: '张院长',
          staffCount: 156,
          performance: 88,
          budget: 580,
          status: '正常',
          createTime: '2010-09',
          children: [
            {
              id: 11,
              deptName: '软件工程系',
              deptCode: 'CS001-01',
              leader: '李主任',
              staffCount: 45,
              performance: 90,
              budget: 180,
              status: '正常',
              createTime: '2012-03'
            },
            {
              id: 12,
              deptName: '计算机科学系',
              deptCode: 'CS001-02',
              leader: '王主任',
              staffCount: 52,
              performance: 86,
              budget: 200,
              status: '正常',
              createTime: '2011-09'
            }
          ]
        },
        {
          id: 2,
          deptName: '数学学院',
          deptCode: 'MATH001',
          leader: '赵院长',
          staffCount: 89,
          performance: 82,
          budget: 320,
          status: '正常',
          createTime: '2008-09',
          children: [
            {
              id: 21,
              deptName: '应用数学系',
              deptCode: 'MATH001-01',
              leader: '陈主任',
              staffCount: 35,
              performance: 85,
              budget: 150,
              status: '正常',
              createTime: '2010-03'
            }
          ]
        },
        {
          id: 3,
          deptName: '物理学院',
          deptCode: 'PHY001',
          leader: '刘院长',
          staffCount: 76,
          performance: 79,
          budget: 280,
          status: '正常',
          createTime: '2009-09'
        }
      ]

      this.deptPagination.total = this.deptTableData.length
      this.deptPagination.currentPage = 1
    },
    getPerformanceColor(performance) {
      if (performance >= 90) return '#67c23a'
      if (performance >= 80) return '#e6a23c'
      if (performance >= 70) return '#f56c6c'
      return '#909399'
    },
    getStatusType(status) {
      const typeMap = {
        '正常': 'success',
        '整改': 'warning',
        '停办': 'danger'
      }
      return typeMap[status] || 'info'
    },
    viewDept(row) {
      this.$message.info(`查看单位：${row.deptName}`)
    },
    editDept(row) {
      this.$message.info(`编辑单位：${row.deptName}`)
    },
    viewStaff(row) {
      this.$message.info(`查看${row.deptName}的人员`)
    },
    viewReport(row) {
      this.$message.info(`查看${row.deptName}的报告`)
    },
    handleSizeChange(size) {
      this.deptPagination.pageSize = size
      this.deptPagination.currentPage = 1
    },
    handleCurrentChange(page) {
      this.deptPagination.currentPage = page
    },
    // 模板相关方法
    handleImport() {
      this.$message.info('导入功能待实现')
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
.dept-dashboard {
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

.dept-icon {
  background: #409eff;
}

.staff-icon {
  background: #67c23a;
}

.performance-icon {
  background: #e6a23c;
}

.budget-icon {
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

.dept-table {
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
