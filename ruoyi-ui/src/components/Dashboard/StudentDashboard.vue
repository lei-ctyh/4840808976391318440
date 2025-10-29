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
          @input="handleTemplateUpload"
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
        <el-tag size="small" type="info" class="org-tag">{{ orgTypeText }}</el-tag>
        <el-tag size="small" class="dept-tag">{{ organizationPath }}</el-tag>
      </div>
      <div class="header-right">
        <el-button icon="el-icon-upload" size="small" @click="handleImportClick">导入</el-button>
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

    <div class="dashboard-content">
      <!-- 学生考核表格 -->
      <div class="student-table">
        <el-table :data="studentTablePageData" border size="small" style="width: 100%">
          <el-table-column prop="personId" label="人员编号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="unitPath" label="单位" min-width="200" />
          <el-table-column prop="birthdate" label="出生年月" width="100" />
          <el-table-column prop="age" label="年龄" width="80" />
          <el-table-column prop="title" label="职称" width="100" />
          <el-table-column prop="cycle" label="评定周期" width="100" />

          <!-- 基础科目 20% -->
          <el-table-column label="基础科目 20%" align="center">
            <el-table-column prop="baseBasicKnowledge" label="基本知识 20%" width="100" />
            <el-table-column label="体育 30%" align="center">
              <el-table-column prop="baseSportsTrack" label="田径" width="80" />
              <el-table-column prop="baseSportsRope" label="跳绳" width="80" />
              <el-table-column prop="baseSportsLongJump" label="跳远" width="80" />
            </el-table-column>
            <el-table-column prop="baseGroupA" label="共同A 25%" width="100" />
            <el-table-column prop="baseGroupB" label="共同B 25%" width="100" />
            <el-table-column prop="baseTotal" label="成绩" width="80" />
          </el-table-column>

          <!-- 共同科目 30% -->
          <el-table-column label="共同科目30%" align="center">
            <el-table-column prop="commonSubject1" label="课目1" width="80" />
            <el-table-column prop="commonSubject2" label="课目2" width="80" />
            <el-table-column prop="commonSubject3" label="课目3" width="80" />
            <el-table-column prop="commonSubject4" label="课目4" width="80" />
            <el-table-column prop="commonSubject5" label="课目5" width="80" />
            <el-table-column prop="commonSubject6" label="课目6" width="80" />
            <el-table-column prop="commonSubject7" label="课目7" width="80" />
            <el-table-column prop="commonSubject8" label="课目8" width="80" />
            <el-table-column prop="commonTotal" label="成绩" width="80" />
          </el-table-column>

          <!-- 岗位业务 50% -->
          <el-table-column prop="jobBusiness" label="岗位业务 50%" width="120" />

          <!-- 综合成绩 -->
          <el-table-column label="综合成绩" align="center">
            <el-table-column prop="comprehensivePercent" label="百分制" width="100" />
            <el-table-column prop="comprehensiveLevel" label="四级制" width="100">
              <template slot-scope="scope">
                <el-tag :type="getPerformanceColor(scope.row.comprehensiveLevel)" size="mini">
                  {{ scope.row.comprehensiveLevel }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table-column>

          <el-table-column prop="remark" label="备注" min-width="120" />
          <el-table-column prop="description" label="说明" min-width="120" />
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
            @size-change="handleStudentSizeChange"
            @current-change="handleStudentCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getStudentAssessmentData } from "@/mock/mockData"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate } from "@/api/system/template"

export default {
  name: "StudentDashboard",
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
      studentTableData: [],
      studentPagination: { currentPage: 1, pageSize: 10, total: 0 },
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
    // 获取当前选中组织的orgCode
    currentOrgCode() {
      return this.selectedDeptNode && this.selectedDeptNode.orgCode ? this.selectedDeptNode.orgCode : null
    },
    // 看板类型固定为student
    boardType() {
      return 'student'
    }
  },
  watch: {
    selectedYear() {
      this.loadStudentData()
    }
  },
  created() {
    this.loadStudentData()
    // 读取本地存储的模板信息
    try {
      const url = localStorage.getItem('studentTemplateUrl')
      const name = localStorage.getItem('studentTemplateFileName')
      if (url) this.templateUrl = url
      if (name) this.templateFileName = name
    } catch (e) {
      console.warn('读取本地存储失败:', e)
    }
  },
  methods: {
    // 处理模板上传成功
    handleTemplateUpload(fileUrl) {
      if (fileUrl) {
        // 设置文件名
        this.templateFileName = String(fileUrl).split(',')[0].split('/').pop() || '学生考核模板.xlsx'

        // 直接执行模板绑定
        if (this.currentOrgCode) {
          this.bindTemplateToOrg(fileUrl)
        } else {
          this.$message.warning('请先选择组织后再上传模板')
        }
      }
    },
    onYearChange() {
      this.loadStudentData()
    },
    handleImportClick() {
      this.$message.info('导入由后端处理，前端不再解析文件')
    },
    handleExportClick() {
      this.$message.info('导出由后端生成文件，前端不再导出')
    },
    openUploadTemplateDialog() {
      // 重置上传状态
      this.templateUrl = ''
      this.templateFileName = ''
      this.uploadTemplateDialogVisible = true
    },
    downloadTemplateFromServer() {
      // 使用resolveTemplate查找可用模板
      this.resolveAndDownloadTemplate()
    },
    loadStudentData() {
      // 模拟学生考核数据，使用与领导看板相同的数据结构
      this.studentTableData = [
        {
          personId: 'S001',
          name: '张三',
          unitPath: '计算机学院/软件工程系/2023级1班',
          birthdate: '2005-03',
          age: 19,
          title: '学生',
          cycle: '2024年度',
          baseBasicKnowledge: 88,
          baseSportsTrack: 85,
          baseSportsRope: 90,
          baseSportsLongJump: 82,
          baseGroupA: 86,
          baseGroupB: 89,
          baseTotal: 86.8,
          commonSubject1: 92,
          commonSubject2: 89,
          commonSubject3: 87,
          commonSubject4: 90,
          commonSubject5: 88,
          commonSubject6: 91,
          commonSubject7: 89,
          commonSubject8: 93,
          commonTotal: 89.9,
          jobBusiness: 90,
          comprehensivePercent: 89.2,
          comprehensiveLevel: '优秀',
          remark: '学习成绩优异',
          description: '综合素质全面发展'
        },
        {
          personId: 'S002',
          name: '李四',
          unitPath: '计算机学院/计算机科学系/2023级2班',
          birthdate: '2005-07',
          age: 19,
          title: '学生',
          cycle: '2024年度',
          baseBasicKnowledge: 82,
          baseSportsTrack: 78,
          baseSportsRope: 85,
          baseSportsLongJump: 75,
          baseGroupA: 80,
          baseGroupB: 83,
          baseTotal: 80.6,
          commonSubject1: 85,
          commonSubject2: 82,
          commonSubject3: 80,
          commonSubject4: 84,
          commonSubject5: 81,
          commonSubject6: 83,
          commonSubject7: 82,
          commonSubject8: 86,
          commonTotal: 82.9,
          jobBusiness: 85,
          comprehensivePercent: 83.8,
          comprehensiveLevel: '良好',
          remark: '表现良好',
          description: '有进步空间'
        },
        {
          personId: 'S003',
          name: '王五',
          unitPath: '软件学院/软件工程系/2023级3班',
          birthdate: '2005-12',
          age: 18,
          title: '学生',
          cycle: '2024年度',
          baseBasicKnowledge: 75,
          baseSportsTrack: 80,
          baseSportsRope: 78,
          baseSportsLongJump: 72,
          baseGroupA: 76,
          baseGroupB: 74,
          baseTotal: 75.0,
          commonSubject1: 78,
          commonSubject2: 76,
          commonSubject3: 74,
          commonSubject4: 77,
          commonSubject5: 75,
          commonSubject6: 79,
          commonSubject7: 76,
          commonSubject8: 80,
          commonTotal: 76.9,
          jobBusiness: 78,
          comprehensivePercent: 76.8,
          comprehensiveLevel: '合格',
          remark: '需要努力',
          description: '基础有待加强'
        }
      ]

      this.studentPagination.total = this.studentTableData.length
      this.studentPagination.currentPage = 1
    },
    getPerformanceColor(level) {
      const colorMap = {
        '优秀': 'success',
        '良好': 'primary',
        '合格': 'warning',
        '不合格': 'danger'
      }
      return colorMap[level] || 'info'
    },
    handleStudentSizeChange(size) {
      this.studentPagination.pageSize = size
      this.studentPagination.currentPage = 1
    },
    handleStudentCurrentChange(page) {
      this.studentPagination.currentPage = page
    },
    // 绑定模板到当前组织
    async bindTemplateToOrg(filePath) {
      if (!this.currentOrgCode) {
        this.$message.error('无法获取当前组织编码，请重新选择组织')
        return
      }

      try {
        const templateData = {
          orgCode: this.currentOrgCode,
          boardType: this.boardType,
          year: parseInt(this.selectedYear),
          filePath: filePath,
          fileName: this.templateFileName,
          fileExt: this.templateFileName.split('.').pop() || 'xlsx',
          status: '1'
        }

        await bindTemplate(templateData)
        this.uploadTemplateDialogVisible = false
        this.$message.success('模板绑定成功')

        // 更新本地存储（保持兼容性）
        try {
          localStorage.setItem('studentTemplateUrl', filePath)
          localStorage.setItem('studentTemplateFileName', this.templateFileName)
        } catch (e) {}

      } catch (error) {
        console.error('绑定模板失败:', error)
        this.$message.error('模板绑定失败: ' + (error.msg || '未知错误'))
      }
    },
    // 解析并下载模板
    async resolveAndDownloadTemplate() {
      if (!this.currentOrgCode) {
        this.$message.error('无法获取当前组织编码，请重新选择组织')
        return
      }

      try {
        const response = await resolveTemplate(
          this.currentOrgCode,
          this.boardType,
          parseInt(this.selectedYear)
        )
        console.log('resolveTemplate response:', response)

        if (response.code === 200 && response.data) {
          // 找到模板，开始下载
          const template = response.data
          const isAbsolute = /^(https?:)?\/\//.test(template.filePath)
          const href = isAbsolute ? template.filePath : (this.baseApi + template.filePath)

          const a = document.createElement('a')
          a.href = href
          a.download = template.fileName
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)

          this.$message.success('模板下载开始')
        } else {
          // 未找到模板
          this.$message.warning('未在组织链找到可用模板，请先上传模板或联系上级组织')
        }

      } catch (error) {
        console.error('解析模板失败:', error)
        this.$message.error('查找模板失败: ' + (error.msg || '未知错误'))
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

.org-tag {
  margin-left: 8px;
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

/* 表格样式优化 */
.el-table {
  font-size: 12px;
}

.el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.el-table td {
  padding: 8px 0;
}

/* 嵌套表头样式 */
.el-table .el-table__header th {
  text-align: center;
  border-right: 1px solid #ebeef5;
}

.el-table .el-table__body td {
  text-align: center;
  border-right: 1px solid #ebeef5;
}

/* 成绩列样式 */
.el-table .el-table__body td:nth-child(8),
.el-table .el-table__body td:nth-child(18),
.el-table .el-table__body td:nth-child(20),
.el-table .el-table__body td:nth-child(21) {
  font-weight: 600;
  color: #409eff;
}

/* 综合成绩样式 */
.el-table .el-table__body td:nth-child(20),
.el-table .el-table__body td:nth-child(21) {
  background-color: #f0f9ff;
}
</style>
