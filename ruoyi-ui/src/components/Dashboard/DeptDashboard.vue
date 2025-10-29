<template>
  <div class="dept-dashboard">
    <div class="dashboard-header">
      <div class="header-left">
        <h3>单位看板</h3>
        <el-tag size="small" class="dept-tag">{{ selectedDeptNode && selectedDeptNode.label ? selectedDeptNode.label : '未选择组织' }}</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">查询</el-button>
        <el-button icon="el-icon-refresh" size="small" @click="handleRefresh">刷新</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
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
export default {
  name: "DeptDashboard",
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
      deptPagination: { currentPage: 1, pageSize: 10, total: 0 }
    }
  },
  computed: {
    deptTablePageData() {
      const start = (this.deptPagination.currentPage - 1) * this.deptPagination.pageSize
      const end = start + this.deptPagination.pageSize
      return this.deptTableData.slice(start, end)
    }
  },
  watch: {
    selectedYear() {
      this.loadDeptData()
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