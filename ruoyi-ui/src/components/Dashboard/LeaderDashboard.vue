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
          @input="handleTemplateUpload"
        />
        <p class="tip">上传后，"下载模板"将直接从服务器取回该文件。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadTemplateDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 数据导入对话框 -->
    <el-dialog title="导入领导班子考核数据" :visible.sync="importDialogVisible" width="600px" :close-on-click-modal="false">
      <div>
        <div class="import-tips">
          <el-alert
            title="导入说明"
            type="info"
            :closable="false"
            show-icon>
            <div slot="description">
              <p>1. 支持.xls和.xlsx格式的Excel文件</p>
              <p>2. 必填字段：人员编号、姓名、评定周期</p>
              <p>3. 如果存在相同人员和年度的记录，可选择是否覆盖</p>
            </div>
          </el-alert>
        </div>

        <div class="import-actions" style="margin: 20px 0;">
          <el-checkbox v-model="updateSupport">覆盖已存在的数据</el-checkbox>
        </div>

        <div class="import-upload">
          <el-upload
            ref="importUpload"
            :limit="1"
            accept=".xlsx,.xls"
            :headers="uploadHeaders"
            :action="uploadImportUrl"
            :data="uploadData"
            :on-change="handleFileChange"
            :on-progress="handleImportProgress"
            :on-success="handleImportSuccess"
            :on-error="handleImportError"
            :before-upload="beforeImportUpload"
            :auto-upload="false"
            drag>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传xlsx/xls文件，且不超过10MB</div>
          </el-upload>
        </div>

        <!-- 导入进度 -->
        <div v-if="importProgress.show" class="import-progress" style="margin-top: 20px;">
          <el-progress
            :percentage="importProgress.percentage"
            :status="importProgress.status"
            :stroke-width="18">
          </el-progress>
          <p class="progress-text">{{ importProgress.text }}</p>
        </div>

        <!-- 导入结果 -->
        <div v-if="importResult.show" class="import-result" style="margin-top: 20px;">
          <el-alert
            :title="importResult.title"
            :type="importResult.type"
            :closable="false"
            show-icon>
            <div slot="description" v-html="importResult.message"></div>
          </el-alert>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelImport">取消</el-button>
        <el-button type="primary" @click="submitImport" :loading="importing" :disabled="!hasImportFile">开始导入</el-button>
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
import { getLeaderAssessmentData } from "@/api/dashboard"
import { getToken } from "@/utils/auth"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate } from "@/api/system/template"

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
      selectedYear: new Date().getFullYear().toString(),
      leaderTableData: [],
      leaderPagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API,
      // 数据导入相关
      importDialogVisible: false,
      importing: false,
      updateSupport: false,
      hasImportFile: false,
      uploadHeaders: {},
      uploadImportUrl: process.env.VUE_APP_BASE_API + '/system/leaderAssessment/importData',
      uploadData: {},
      importProgress: {
        show: false,
        percentage: 0,
        status: '',
        text: ''
      },
      importResult: {
        show: false,
        title: '',
        type: '',
        message: ''
      }
    }
  },
  computed: {
    leaderTablePageData() {
      const start = (this.leaderPagination.currentPage - 1) * this.leaderPagination.pageSize
      const end = start + this.leaderPagination.pageSize
      return this.leaderTableData.slice(start, end)
    },
    // 获取当前选中组织的orgCode
    currentOrgCode() {
      return this.selectedDeptNode && this.selectedDeptNode.orgCode ? this.selectedDeptNode.orgCode : null
    },
    // 看板类型固定为leader
    boardType() {
      return 'leader'
    }
  },
  watch: {
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
    } catch (e) {
      console.warn('读取本地存储失败:', e)
    }
  },
  methods: {
    // 处理模板上传成功
    handleTemplateUpload(fileUrl) {
      if (fileUrl) {
        // 设置文件名
        this.templateFileName = String(fileUrl).split(',')[0].split('/').pop() || '领导考核模板.xlsx'

        // 直接执行模板绑定
        if (this.currentOrgCode) {
          this.bindTemplateToOrg(fileUrl)
        } else {
          this.$message.warning('请先选择组织后再上传模板')
        }
      }
    },
    onYearChange() {
      this.loadLeaderData()
    },
    handleImportClick() {
      this.resetImportState()
      this.importDialogVisible = true
      this.setupUploadHeaders()
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
          localStorage.setItem('leaderTemplateUrl', filePath)
          localStorage.setItem('leaderTemplateFileName', this.templateFileName)
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
    },

    // 数据导入相关方法
    resetImportState() {
      this.importing = false
      this.hasImportFile = false
      this.updateSupport = false
      this.importProgress.show = false
      this.importProgress.percentage = 0
      this.importProgress.status = ''
      this.importProgress.text = ''
      this.importResult.show = false
      this.uploadData = {}
      if (this.$refs.importUpload) {
        this.$refs.importUpload.clearFiles()
      }
    },

    setupUploadHeaders() {
      this.uploadHeaders = {
        Authorization: 'Bearer ' + getToken()
      }
      this.uploadData = {
        updateSupport: this.updateSupport,
        year: this.selectedYear
      }
    },

    handleFileChange(file, fileList) {
      // 文件选择后立即检查并更新状态
      if (fileList.length > 0) {
        const selectedFile = fileList[0].raw
        const isExcel = selectedFile.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                       selectedFile.type === 'application/vnd.ms-excel'
        const isLt10M = selectedFile.size / 1024 / 1024 < 10

        if (isExcel && isLt10M) {
          this.hasImportFile = true
        } else {
          this.hasImportFile = false
          if (!isExcel) {
            this.$message.error('只能上传Excel文件!')
          }
          if (!isLt10M) {
            this.$message.error('上传文件大小不能超过10MB!')
          }
        }
      } else {
        this.hasImportFile = false
      }
    },

    beforeImportUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.type === 'application/vnd.ms-excel'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isExcel) {
        this.$message.error('只能上传Excel文件!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过10MB!')
        return false
      }

      this.hasImportFile = true
      this.uploadData.updateSupport = this.updateSupport
      this.uploadData.year = this.selectedYear
      return true
    },

    handleImportProgress(event, file, fileList) {
      this.importProgress.show = true
      this.importProgress.percentage = Math.round(event.percent)
      this.importProgress.status = 'active'
      this.importProgress.text = '正在上传文件...'
    },

    handleImportSuccess(response, file, fileList) {
      this.importing = false
      this.importProgress.show = false

      if (response.code === 200) {
        this.importResult.show = true
        this.importResult.title = '导入成功'
        this.importResult.type = 'success'

        const result = response.data || {}
        let message = `<p>导入完成！</p>`
        if (result.success > 0) {
          message += `<p>成功导入 ${result.success} 条记录</p>`
        }
        if (result.update > 0) {
          message += `<p>更新 ${result.update} 条记录</p>`
        }
        if (result.skip > 0) {
          message += `<p>跳过 ${result.skip} 条记录</p>`
        }
        if (result.error > 0) {
          message += `<p style="color: #f56c6c;">失败 ${result.error} 条记录</p>`
        }
        if (result.errorMessages && result.errorMessages.length > 0) {
          message += `<p style="color: #f56c6c;">错误详情：</p>`
          result.errorMessages.slice(0, 5).forEach(error => {
            message += `<p style="color: #f56c6c; font-size: 12px;">• ${error}</p>`
          })
          if (result.errorMessages.length > 5) {
            message += `<p style="color: #f56c6c; font-size: 12px;">... 还有 ${result.errorMessages.length - 5} 个错误</p>`
          }
        }

        this.importResult.message = message

        // 刷新数据
        this.loadLeaderData()
      } else {
        this.importResult.show = true
        this.importResult.title = '导入失败'
        this.importResult.type = 'error'
        this.importResult.message = `<p>${response.msg || '导入过程中发生错误'}</p>`
      }
    },

    handleImportError(error, file, fileList) {
      this.importing = false
      this.importProgress.show = false
      this.importResult.show = true
      this.importResult.title = '导入失败'
      this.importResult.type = 'error'
      this.importResult.message = `<p>文件上传失败: ${error.message || '未知错误'}</p>`
    },

    submitImport() {
      if (!this.hasImportFile) {
        this.$message.warning('请先选择要导入的文件')
        return
      }

      this.importing = true
      this.importResult.show = false
      this.uploadData.updateSupport = this.updateSupport
      this.uploadData.year = this.selectedYear

      this.$refs.importUpload.submit()
    },

    cancelImport() {
      this.importDialogVisible = false
      this.resetImportState()
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

/* 导入对话框样式 */
.import-tips {
  margin-bottom: 20px;
}

.import-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.import-upload {
  margin: 20px 0;
}

.import-progress {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.progress-text {
  text-align: center;
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.import-result {
  padding: 15px;
  border-radius: 4px;
}

.import-result .el-alert__description p {
  margin: 5px 0;
}
</style>
