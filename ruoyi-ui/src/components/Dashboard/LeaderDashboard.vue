<template>
  <div class="leader-dashboard">
    <!-- 上传模板对话框 -->
    <el-dialog title="上传模板 (Excel)" :visible.sync="uploadTemplateDialogVisible" width="600px" @open="loadExistingTemplate">
      <div>
        <!-- 已有模板信息显示 -->
        <div v-if="existingTemplate" class="existing-template-info" style="margin-bottom: 20px;">
          <el-alert
            title="当前已有模板"
            type="info"
            :closable="false"
            show-icon>
            <div slot="default">
              <p><strong>文件名：</strong>{{ existingTemplate.fileName }}</p>
              <p><strong>上传时间：</strong>{{ existingTemplate.createTime }}</p>
              <p><strong>文件大小：</strong>{{ formatFileSize(existingTemplate.fileSize) }}</p>
              <p style="color: #909399; font-size: 12px;">上传新模板将覆盖当前模板</p>
            </div>
          </el-alert>
        </div>
        
        <!-- 无模板提示 -->
        <div v-else-if="templateCheckCompleted" class="no-template-info" style="margin-bottom: 20px;">
          <el-alert
            title="暂无模板"
            type="warning"
            :closable="false"
            show-icon>
            <div slot="default">
              <p>当前组织暂无上传的模板，请上传新模板。</p>
            </div>
          </el-alert>
        </div>
        
        <!-- 加载中提示 -->
        <div v-else class="loading-template-info" style="margin-bottom: 20px;">
          <el-alert
            title="正在检查已有模板..."
            type="info"
            :closable="false"
            show-icon>
          </el-alert>
        </div>
        
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
            :description="importDescription"
            type="info"
            :closable="false"
            show-icon>
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
            :status="importProgress.status || undefined"
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
          </el-alert>
          <!-- 直接显示消息内容，不使用slot -->
          <div style="margin-top: 10px; padding: 15px; border: 1px solid #e6f7ff; background: #f6ffed; border-radius: 4px;" v-html="importResult.message"></div>
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
      <dynamic-table
        :data="leaderTablePageData"
        board-type="leader"
        :year="selectedYear"
        :org-code="currentOrgCode"
        :table-props="{
          border: true,
          size: 'small',
          style: 'width: 100%',
          'header-cell-style': { textAlign: 'center' }
        }"
        @config-loaded="onTableConfigLoaded"
        @config-error="onTableConfigError"
        ref="leaderTable"
      />

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
import { deptTreeSelect } from "@/api/system/user"
import { getToken } from "@/utils/auth"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate, getTemplate } from "@/api/system/template"
import DynamicTable from "@/components/DynamicTable"

export default {
  name: "LeaderDashboard",
  components: { FileUpload, DynamicTable },
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
        status: null, // 使用null而不是空字符串
        text: ''
      },
      importResult: {
        show: false,
        title: '',
        type: '',
        message: ''
      },
      // 部门树数据，用于构建单位显示名称
      deptTreeData: [],
      // 模板信息回显相关
      existingTemplate: null,
      templateCheckCompleted: false
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
    },
    // 导入说明描述
    importDescription() {
      return '1. 支持.xls和.xlsx格式的Excel文件\n2. 必填字段：人员编号、姓名、评定周期\n3. 如果存在相同人员和年度的记录，可选择是否覆盖'
    }
  },
  watch: {
    selectedYear() {
      this.loadLeaderData()
    },
    currentOrgCode() {
      // 当组织节点切换时，重新加载数据
      this.loadLeaderData()
    }
  },
  created() {
    this.loadLeaderData()
    this.getDeptTreeData()
  },
  methods: {
    // 动态表格配置加载成功回调
    onTableConfigLoaded(config) {
      console.log('领导看板表格配置加载成功:', config)
    },
    
    // 动态表格配置加载失败回调
    onTableConfigError(error) {
      console.error('领导看板表格配置加载失败:', error)
      this.$message.warning('表格配置加载失败，已使用默认配置')
    },
    
    // 获取部门树数据
    getDeptTreeData() {
      deptTreeSelect().then(response => {
        this.deptTreeData = response.data || []
      }).catch(error => {
        console.warn('获取部门树数据失败:', error)
        this.deptTreeData = []
      })
    },
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
      // 加载已有模板信息
      this.loadExistingTemplate()
    },
    downloadTemplateFromServer() {
      // 使用resolveTemplate查找可用模板
      this.resolveAndDownloadTemplate()
    },
    async loadLeaderData() {
      try {
        this.loading = true
        const response = await getLeaderAssessmentData(this.selectedYear, this.currentOrgCode)

        if (response.code === 200) {
          // 处理后端返回的数据格式，将metric字段映射为前端表格字段
          const rawData = response.rows || response.data || []
          this.leaderTableData = rawData.map(item => this.mapBackendDataToFrontend(item))
          this.leaderPagination.total = response.total || this.leaderTableData.length
          this.leaderPagination.currentPage = 1
        } else {
          this.$message.error(response.msg || '获取数据失败')
          this.leaderTableData = []
          this.leaderPagination.total = 0
        }
      } catch (error) {
        console.error('加载领导考核数据失败:', error)
        this.$message.error('加载数据失败，请稍后重试')
        this.leaderTableData = []
        this.leaderPagination.total = 0
      } finally {
        this.loading = false
      }
    },

    // 将后端数据映射为前端表格数据
    mapBackendDataToFrontend(backendData) {
      return {
        personId: backendData.personId,
        personName: backendData.personName,
        unitId: backendData.unitId,
        unitName: this.getUnitDisplayName(backendData.unitId), // 需要根据unitId获取单位名称
        birthDate: backendData.birthDate,
        age: backendData.age,
        title: backendData.title,
        period: backendData.period,

        // 基础科目 20% - 映射到metric001-metric006
        basicKnowledge: backendData.metric001 || '0', // 基本知识 20%
        sportsTrack: backendData.metric002 || '0',    // 田径
        sportsRope: backendData.metric003 || '0',     // 跳绳
        sportsJump: backendData.metric004 || '0',     // 跳远
        baseGroupA: backendData.metric005 || '0',     // 共同A 25%
        baseGroupB: backendData.metric006 || '0',     // 共同B 25%
        baseTotal: this.calculateBaseTotal(backendData), // 基础科目总成绩

        // 共同科目 30% - 映射到metric007-metric014
        commonSubject1: backendData.metric007 || '0',
        commonSubject2: backendData.metric008 || '0',
        commonSubject3: backendData.metric009 || '0',
        commonSubject4: backendData.metric010 || '0',
        commonSubject5: backendData.metric011 || '0',
        commonSubject6: backendData.metric012 || '0',
        commonSubject7: backendData.metric013 || '0',
        commonSubject8: backendData.metric014 || '0',
        commonTotal: this.calculateCommonTotal(backendData), // 共同科目总成绩

        // 岗位业务 50% - 映射到metric015
        jobBusiness: backendData.metric015 || '0',

        // 综合成绩
        comprehensivePercent: backendData.totalScore || '0',
        comprehensiveLevel: backendData.totalRating || '及格',
        remark: backendData.remark || '',
        description: backendData.status || ''
      }
    },

    // 计算基础科目总成绩
    calculateBaseTotal(data) {
      const basicKnowledge = parseFloat(data.metric001 || 0) * 0.2  // 基本知识 20%
      const sports = (parseFloat(data.metric002 || 0) + parseFloat(data.metric003 || 0) + parseFloat(data.metric004 || 0)) / 3 * 0.3 // 体育 30%
      const groupA = parseFloat(data.metric005 || 0) * 0.25  // 共同A 25%
      const groupB = parseFloat(data.metric006 || 0) * 0.25  // 共同B 25%
      return (basicKnowledge + sports + groupA + groupB).toFixed(1)
    },

    // 计算共同科目总成绩
    calculateCommonTotal(data) {
      let total = 0
      let count = 0
      for (let i = 7; i <= 14; i++) {
        const metricKey = `metric${i.toString().padStart(3, '0')}`
        if (data[metricKey]) {
          total += parseFloat(data[metricKey])
          count++
        }
      }
      return count > 0 ? (total / count).toFixed(1) : '0'
    },

    // 获取单位显示名称 - 通过deptTreeSelect数据构建层级路径
    getUnitDisplayName(unitId) {
      if (!unitId || !this.deptTreeData) {
        return unitId || ''
      }

      // 递归查找部门节点
      const findDeptNode = (nodes, targetId) => {
        for (const node of nodes) {
          if (node.id === targetId || node.orgCode === unitId) {
            return node
          }
          if (node.children && node.children.length > 0) {
            const found = findDeptNode(node.children, targetId)
            if (found) return found
          }
        }
        return null
      }

      // 构建层级路径
      const buildDeptPath = (node, allNodes) => {
        const path = []
        let current = node

        // 向上查找父级节点
        while (current) {
          path.unshift(current.label)
          if (!current.parentId) break

          // 查找父节点
          current = findDeptNode(allNodes, current.parentId)
        }

        return path.join('/')
      }

      const deptNode = findDeptNode(this.deptTreeData, unitId)
      if (deptNode) {
        return buildDeptPath(deptNode, this.deptTreeData)
      }

      return unitId || ''
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
      this.importProgress.status = null // 使用null而不是空字符串
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
      console.log('导入响应:', response)
      this.importing = false
      this.importProgress.show = false

      if (response.code === 200) {
        const result = response.data || {}
        console.log('导入结果数据:', result)

        let message = `<p><strong>导入完成！</strong>总计处理 ${result.total || 0} 条记录</p>`
        message += `<div style="margin: 10px 0; padding: 10px; background: #f5f7fa; border-radius: 4px;">`

        // 显示所有统计信息，包括为0的情况
        message += `<p style="margin: 5px 0;">✅ 成功导入：${result.success || 0} 条</p>`
        message += `<p style="margin: 5px 0;">🔄 更新记录：${result.update || 0} 条</p>`
        message += `<p style="margin: 5px 0;">⏭️ 跳过记录：${result.skip || 0} 条</p>`
        message += `<p style="margin: 5px 0; color: ${result.error > 0 ? '#f56c6c' : '#67c23a'};">❌ 失败记录：${result.error || 0} 条</p>`
        message += `</div>`

        if (result.errorMessages && result.errorMessages.length > 0) {
          message += `<p style="color: #f56c6c; margin-top: 10px;"><strong>错误详情：</strong></p>`
          result.errorMessages.slice(0, 5).forEach(error => {
            message += `<p style="color: #f56c6c; font-size: 12px; margin-left: 10px;">• ${error}</p>`
          })
          if (result.errorMessages.length > 5) {
            message += `<p style="color: #f56c6c; font-size: 12px; margin-left: 10px;">... 还有 ${result.errorMessages.length - 5} 个错误</p>`
          }
        }

        console.log('构建的消息:', message)

        // 设置导入结果显示
        this.importResult.show = true
        this.importResult.title = '导入完成'
        this.importResult.type = result.error > 0 ? 'warning' : 'success'
        this.importResult.message = message

        console.log('importResult状态:', this.importResult)

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
    },
    
    // 加载已有模板信息
    async loadExistingTemplate() {
      this.existingTemplate = null
      this.templateCheckCompleted = false
      
      // 验证必需参数
      if (!this.currentOrgCode || !this.boardType || !this.selectedYear) {
        console.log('参数不完整，跳过模板查询:', {
          currentOrgCode: this.currentOrgCode,
          boardType: this.boardType,
          selectedYear: this.selectedYear
        })
        this.templateCheckCompleted = true
        return
      }
      
      try {
        const response = await getTemplate({
          orgCode: this.currentOrgCode,
          boardType: this.boardType,
          year: this.selectedYear
        })
        
        if (response.code === 200 && response.data) {
          this.existingTemplate = response.data
          // 设置模板文件名和URL，用于显示当前模板信息
          this.templateFileName = response.data.fileName || ''
          this.templateUrl = response.data.fileUrl || ''
        }
      } catch (error) {
        console.error('查询已有模板失败:', error)
      } finally {
        this.templateCheckCompleted = true
      }
    },
    
    // 格式化文件大小
    formatFileSize(size) {
      if (!size) return '未知'
      
      const units = ['B', 'KB', 'MB', 'GB']
      let index = 0
      let fileSize = parseFloat(size)
      
      while (fileSize >= 1024 && index < units.length - 1) {
        fileSize /= 1024
        index++
      }
      
      return fileSize.toFixed(2) + ' ' + units[index]
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
