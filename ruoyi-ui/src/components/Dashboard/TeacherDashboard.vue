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
          @input="handleTemplateUpload"
        />
        <p class="tip">上传后，"下载模板"将直接从服务器取回该文件。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadTemplateDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 教师看板头部 -->
    <div class="teacher-header">
      <div class="teacher-left">
        <span class="teacher-title">教师年度考核</span>
        <el-tag type="success" size="small">{{ orgTypeText }}</el-tag>
        <el-tag size="small" class="teacher-dept">{{ organizationPath }}</el-tag>
      </div>
      <div class="teacher-right">
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
    
    <!-- 教师看板表格 -->
    <div class="tab-body">
      <el-table :data="teacherTablePageData" border size="small" style="width: 100%" :header-cell-style="{ textAlign: 'center' }">
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
          :total="teacherPagination.total"
          :page-size="teacherPagination.pageSize"
          :current-page="teacherPagination.currentPage"
          :page-sizes="[10, 20, 50]"
          @size-change="handleTeacherSizeChange"
          @current-change="handleTeacherCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getTeacherAssessmentData } from "@/mock/mockData"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate } from "@/api/sms/template"

export default {
  name: "TeacherDashboard",
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
      teacherTableData: [],
      teacherPagination: { currentPage: 1, pageSize: 10, total: 0 },
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
    // 获取当前选中组织的orgCode
    currentOrgCode() {
      return this.selectedDeptNode && this.selectedDeptNode.orgCode ? this.selectedDeptNode.orgCode : null
    },
    // 看板类型固定为teacher
    boardType() {
      return 'teacher'
    }
  },
  watch: {
    selectedYear() {
      this.loadTeacherData()
    }
  },
  created() {
    this.loadTeacherData()
    // 读取本地存储的模板信息
    try {
      const url = localStorage.getItem('teacherTemplateUrl')
      const name = localStorage.getItem('teacherTemplateFileName')
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
        this.templateFileName = String(fileUrl).split(',')[0].split('/').pop() || '教师考核模板.xlsx'

        // 直接执行模板绑定
        if (this.currentOrgCode) {
          this.bindTemplateToOrg(fileUrl)
        } else {
          this.$message.warning('请先选择组织后再上传模板')
        }
      }
    },
    onYearChange() {
      this.loadTeacherData()
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
    loadTeacherData() {
      // 模拟教师考核数据，使用与领导看板相同的数据结构
      this.teacherTableData = [
        {
          personId: 'T001',
          name: '王教授',
          unitPath: '计算机学院/软件工程系/教学组',
          birthdate: '1975-03',
          age: 49,
          title: '教授',
          cycle: '2024年度',
          baseBasicKnowledge: 85,
          baseSportsTrack: 80,
          baseSportsRope: 85,
          baseSportsLongJump: 78,
          baseGroupA: 88,
          baseGroupB: 82,
          baseTotal: 83.6,
          commonSubject1: 90,
          commonSubject2: 88,
          commonSubject3: 85,
          commonSubject4: 87,
          commonSubject5: 89,
          commonSubject6: 86,
          commonSubject7: 88,
          commonSubject8: 90,
          commonTotal: 87.9,
          jobBusiness: 92,
          comprehensivePercent: 88.5,
          comprehensiveLevel: '良好',
          remark: '教学认真负责',
          description: '专业课教学优秀'
        },
        {
          personId: 'T002',
          name: '李副教授',
          unitPath: '计算机学院/计算机科学系/教学组',
          birthdate: '1980-07',
          age: 44,
          title: '副教授',
          cycle: '2024年度',
          baseBasicKnowledge: 82,
          baseSportsTrack: 75,
          baseSportsRope: 80,
          baseSportsLongJump: 72,
          baseGroupA: 85,
          baseGroupB: 78,
          baseTotal: 78.8,
          commonSubject1: 85,
          commonSubject2: 83,
          commonSubject3: 80,
          commonSubject4: 82,
          commonSubject5: 84,
          commonSubject6: 81,
          commonSubject7: 83,
          commonSubject8: 85,
          commonTotal: 82.9,
          jobBusiness: 88,
          comprehensivePercent: 85.2,
          comprehensiveLevel: '良好',
          remark: '科研能力强',
          description: '理论基础扎实'
        },
        {
          personId: 'T003',
          name: '张讲师',
          unitPath: '软件学院/软件工程系/教学组',
          birthdate: '1985-12',
          age: 39,
          title: '讲师',
          cycle: '2024年度',
          baseBasicKnowledge: 78,
          baseSportsTrack: 82,
          baseSportsRope: 85,
          baseSportsLongJump: 80,
          baseGroupA: 80,
          baseGroupB: 75,
          baseTotal: 80.0,
          commonSubject1: 82,
          commonSubject2: 80,
          commonSubject3: 78,
          commonSubject4: 79,
          commonSubject5: 81,
          commonSubject6: 77,
          commonSubject7: 80,
          commonSubject8: 82,
          commonTotal: 79.9,
          jobBusiness: 85,
          comprehensivePercent: 82.1,
          comprehensiveLevel: '合格',
          remark: '工作积极主动',
          description: '实践能力较强'
        }
      ]
      
      this.teacherPagination.total = this.teacherTableData.length
      this.teacherPagination.currentPage = 1
    },
    handleTeacherSizeChange(size) {
      this.teacherPagination.pageSize = size
      this.teacherPagination.currentPage = 1
    },
    handleTeacherCurrentChange(page) {
      this.teacherPagination.currentPage = page
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
          localStorage.setItem('teacherTemplateUrl', filePath)
          localStorage.setItem('teacherTemplateFileName', this.templateFileName)
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
.teacher-dashboard {
  width: 100%;
}

.teacher-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
}

.teacher-left {
  display: flex;
  align-items: center;
}

.teacher-title {
  font-weight: 600;
  margin-right: 10px;
}

.teacher-dept {
  margin-left: 8px;
}

.teacher-right {
  display: flex;
  align-items: center;
}

.teacher-right .label {
  margin-right: 8px;
  color: #606266;
}

.teacher-right .el-button,
.teacher-right .el-date-picker {
  margin-right: 8px;
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