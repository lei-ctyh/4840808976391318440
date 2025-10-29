<template>
  <div class="leader-dashboard">
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

    <!-- 领导看板头部 -->
    <div class="leader-header">
      <div class="leader-left">
        <span class="leader-title">领导班子年度考核</span>
        <el-tag type="success" size="small">{{ orgTypeText }}</el-tag>
        <el-tag size="small" class="leader-dept">{{ organizationPath }}</el-tag>
      </div>
      <div class="leader-right">
        <el-button type="primary" icon="el-icon-upload" size="small" @click="handleImportClick">导入</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExportClick">导出</el-button>
        <el-button icon="el-icon-upload2" size="small" @click="openUploadTemplateDialog">上传模板</el-button>
        <el-button icon="el-icon-document" size="small" @click="downloadTemplateFromServer">下载模板</el-button>
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
    
    <!-- 领导看板表格 -->
    <div class="tab-body">
      <el-table :data="leaderTablePageData" border size="small" style="width: 100%" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="personId" label="人员编号" width="110" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="unitPath" label="单位(展示单位名称XX/XX/XX)" min-width="220" />
        <el-table-column prop="birthdate" label="出生年月" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="cycle" label="评定周期" width="120" />

        <el-table-column label="基础科目 20%">
          <el-table-column prop="baseBasicKnowledge" label="基本知识 20%" width="140" />
          <el-table-column label="体育 30%">
            <el-table-column prop="baseSportsTrack" label="田径" width="100" />
            <el-table-column prop="baseSportsRope" label="跳绳" width="100" />
            <el-table-column prop="baseSportsLongJump" label="跳远" width="100" />
          </el-table-column>
          <el-table-column prop="baseGroupA" label="共同A 25%" width="120" />
          <el-table-column prop="baseGroupB" label="共同B 25%" width="120" />
          <el-table-column prop="baseTotal" label="成绩" width="100" />
        </el-table-column>

        <el-table-column label="共同科目30%">
          <el-table-column prop="commonSubject1" label="课目1" width="100" />
          <el-table-column prop="commonSubject2" label="课目2" width="100" />
          <el-table-column prop="commonSubject3" label="课目3" width="100" />
          <el-table-column prop="commonSubject4" label="课目4" width="100" />
          <el-table-column prop="commonSubject5" label="课目5" width="100" />
          <el-table-column prop="commonSubject6" label="课目6" width="100" />
          <el-table-column prop="commonSubject7" label="课目7" width="100" />
          <el-table-column prop="commonSubject8" label="课目8" width="100" />
          <el-table-column prop="commonTotal" label="成绩" width="100" />
        </el-table-column>

        <el-table-column prop="jobBusiness" label="岗位业务 50%" width="130" />

        <el-table-column label="综合成绩">
          <el-table-column prop="comprehensivePercent" label="百分制" width="120" />
          <el-table-column prop="comprehensiveLevel" label="四级制 不合格，合格，良好，优秀" width="180" />
        </el-table-column>

        <el-table-column prop="remark" label="备注" min-width="120" />
        <el-table-column prop="description" label="说明" min-width="120" />
      </el-table>
      
      <!-- 分页 -->
      <div class="table-pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="leaderPagination.total"
          :page-size="leaderPagination.pageSize"
          :current-page="leaderPagination.currentPage"
          :page-sizes="[10, 20, 50]"
          @size-change="handleLeaderSizeChange"
          @current-change="handleLeaderCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getLeaderAssessmentData } from "@/mock/mockData"
import FileUpload from "@/components/FileUpload"

export default {
  name: "LeaderDashboard",
  components: { FileUpload },
  props: {
    selectedDeptNode: {
      type: Object,
      default: () => null
    },
    orgTypeText: {
      type: String,
      default: '其他组织'
    },
    organizationPath: {
      type: String,
      default: '未选择组织'
    }
  },
  data() {
    return {
      selectedYear: String(new Date().getFullYear()),
      leaderTableData: [],
      leaderPagination: { currentPage: 1, pageSize: 10, total: 0 },
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API
    }
  },
  computed: {
    leaderTablePageData() {
      const start = (this.leaderPagination.currentPage - 1) * this.leaderPagination.pageSize
      const end = start + this.leaderPagination.pageSize
      return this.leaderTableData.slice(start, end)
    },

  },
  watch: {
    templateUrl(val) {
      if (val) {
        this.templateFileName = String(val).split(',')[0].split('/').pop() || '领导考核模板.xlsx'
        try {
          localStorage.setItem('leaderTemplateUrl', val)
          localStorage.setItem('leaderTemplateFileName', this.templateFileName)
        } catch (e) {}
        this.uploadTemplateDialogVisible = false
        this.$message.success('模板上传成功')
      }
    },
    selectedYear() {
      this.loadLeaderData()
    }
  },
  created() {
    this.loadLeaderData()
    // 读取本地存储的模板信息
    try {
      const url = localStorage.getItem('leaderTemplateUrl')
      const name = localStorage.getItem('leaderTemplateFileName')
      if (url) this.templateUrl = url
      if (name) this.templateFileName = name
    } catch (e) {}
  },
  methods: {
    onYearChange() {
      this.loadLeaderData()
    },
    handleImportClick() {
      this.$message.info('导入由后端处理，前端不再解析文件')
    },
    handleExportClick() {
      this.$message.info('导出由后端生成文件，前端不再导出')
    },
    openUploadTemplateDialog() {
      this.uploadTemplateDialogVisible = true
    },
    downloadTemplateFromServer() {
      if (!this.templateUrl) {
        this.$message.warning('暂无已上传模板，请先点击"上传模板"上传')
        return
      }
      const isAbsolute = /^(https?:)?\/\//.test(this.templateUrl)
      const href = isAbsolute ? this.templateUrl : (this.baseApi + this.templateUrl)
      const a = document.createElement('a')
      a.href = href
      a.download = this.templateFileName || '领导考核模板.xlsx'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
    },
    loadLeaderData() {
      this.leaderTableData = getLeaderAssessmentData(this.selectedYear)
      this.leaderPagination.total = this.leaderTableData.length
      this.leaderPagination.currentPage = 1
    },
    handleLeaderSizeChange(size) {
      this.leaderPagination.pageSize = size
      this.leaderPagination.currentPage = 1
    },
    handleLeaderCurrentChange(page) {
      this.leaderPagination.currentPage = page
    }
  }
}
</script>

<style scoped>
.leader-dashboard {
  width: 100%;
}

.tab-body {
  min-height: 400px;
}

.leader-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
}

.leader-left {
  display: flex;
  align-items: center;
}

.leader-title {
  font-weight: 600;
  margin-right: 10px;
}

.leader-dept {
  margin-left: 8px;
}

.leader-right .label {
  margin-right: 8px;
  color: #606266;
}

.leader-right .el-button {
  margin-right: 8px;
}

.table-pagination {
  margin-top: 12px;
  text-align: right;
}

.tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style>