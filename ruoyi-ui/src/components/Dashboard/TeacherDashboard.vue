<template>
  <div class="teacher-dashboard">
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

    <!-- 干部成绩头部 -->
    <div class="teacher-header">
      <div class="teacher-left">
      </div>
      <div class="teacher-right">
        <el-input
          v-model="searchText"
          size="small"
          clearable
          placeholder="搜索（姓名/编号/单位/指标等）"
          prefix-icon="el-icon-search"
        />
        <el-button type="primary" icon="el-icon-upload" size="small" @click="importDialogVisible = true">导入</el-button>
        <el-button icon="el-icon-download" size="small" @click="handleExportClick">导出</el-button>
        <el-button icon="el-icon-upload2" size="small" @click="openUploadTemplateDialog">上传模板</el-button>
        <el-button icon="el-icon-document" size="small" @click="resolveAndDownloadTemplate">下载模板</el-button>
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

    <!-- 干部成绩表格 -->
    <div class="tab-body">
      <dynamic-table
        :data="teacherTablePageData"
        board-type="teacher"
        :year="selectedYear"
        :org-code="currentOrgCode"
        :org-code-path="orgCodePath"
        :table-props="{
          border: true,
          size: 'small',
          style: 'width: 100%',
          'header-cell-style': { textAlign: 'center' }
        }"
        @config-loaded="onTableConfigLoaded"
        @config-error="onTableConfigError"
        ref="teacherTable"
      />

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

    <!-- 数据导入对话框 -->
    <el-dialog title="导入教师考核数据" :visible.sync="importDialogVisible" width="600px" :close-on-click-modal="false">
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
  </div>
</template>

<script>
import { getTeacherAssessmentData } from "@/api/dashboard"
import { deptTreeSelect } from "@/api/system/user"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate, getTemplate } from "@/api/system/template"
import { listTeacherAssessment, importTemplate, importData, exportTeacherAssessment } from "@/api/system/teacherAssessment"
import DynamicTable from "@/components/DynamicTable"

export default {
  name: "TeacherDashboard",
  components: { FileUpload, DynamicTable },
  props: {
    selectedDeptNode: {
      type: Object,
      default: () => null
    },
    orgCodePath: {
      type: Array,
      default: () => []
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
      searchText: '',
      teacherTableData: [],
      teacherPagination: { currentPage: 1, pageSize: 10, total: 0 },
      uploadTemplateDialogVisible: false,
      templateUrl: '',
      templateFileName: '',
      baseApi: process.env.VUE_APP_BASE_API,
      // 部门树数据，用于构建单位显示名称
      deptTreeData: [],
      // 模板信息回显相关
      existingTemplate: null,
      templateCheckCompleted: false,
      // 导入相关
      importDialogVisible: false,
      importing: false,
      updateSupport: true,
      hasImportFile: false,
      uploadHeaders: { Authorization: "Bearer " + this.$store.getters.token },
      uploadData: {},
      importProgress: {
        show: false,
        percentage: 0,
        status: null,
        text: ''
      },
      importResult: {
        show: false,
        title: '',
        type: 'success',
        message: ''
      },
      uploadImportUrl: process.env.VUE_APP_BASE_API + '/system/teacher-assessment/importData'
    }
  },
  computed: {
    teacherTableFiltered() {
      const text = (this.searchText || '').trim().toLowerCase()
      if (!text) return this.teacherTableData
      return this.teacherTableData.filter(row => {
        const joined = Object.values(row).map(v => String(v ?? '')).join(' ').toLowerCase()
        return joined.includes(text)
      })
    },
    teacherTablePageData() {
      const list = this.teacherTableFiltered
      const start = (this.teacherPagination.currentPage - 1) * this.teacherPagination.pageSize
      const end = start + this.teacherPagination.pageSize
      return list.slice(start, end)
    },
    // 获取当前选中组织的orgCode
    currentOrgCode() {
      return this.selectedDeptNode && this.selectedDeptNode.orgCode ? this.selectedDeptNode.orgCode : null
    },
    // 看板类型固定为teacher
    boardType() {
      return 'teacher'
    },
    // 导入说明
    importDescription() {
      return [
        '1. 支持 .xls / .xlsx 文件，大小不超过 10MB',
        '2. 必填列（表头需与下列同义词之一一致）：',
        '   - 人员编号/编号（personId）',
        '   - 姓名（personName）',
        '   - 单位/单位编号（unitId，需符合机构编码规则：00/01开头，后续最多10位数字）',
        '   - 评定周期（period，年度，格式为 yyyy）',
        '3. 可选列：总成绩、总评定、备注、状态',
        '4. 指标列：除以上表头外的任何列会按列序映射为 metric001、metric002…，用于自定义指标',
        '5. 覆盖策略：同一人员+年度存在记录时，可选择是否覆盖'
      ].join('\n')
    }
  },
  watch: {
    selectedYear() {
      this.loadTeacherData()
    },
    currentOrgCode() {
      // 当组织节点切换时，重新加载数据
      this.loadTeacherData()
    },
    searchText() {
      this.teacherPagination.currentPage = 1
      this.teacherPagination.total = this.teacherTableFiltered.length
    }
  },
  created() {
    this.loadTeacherData()
    this.getDeptTreeData()
  },
  methods: {
    // 动态表格配置加载成功回调
    onTableConfigLoaded(config) {
      console.log('干部成绩表格配置加载成功:', config)
    },

    // 动态表格配置加载失败回调
    onTableConfigError(error) {
      console.error('干部成绩表格配置加载失败:', error)
      this.$message.warning('表格配置加载失败，已使用默认配置')
    },

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

    // 导入相关方法
    /** 下载模板操作 */
    importTemplate() {
      importTemplate().then(response => {
        this.$download.excel(response, '教师考核数据导入模板.xlsx');
      });
    },

    // 重置导入状态
    resetImportState() {
      this.hasImportFile = false
      this.importing = false
      this.importProgress = {
        show: false,
        percentage: 0,
        status: null,
        text: ''
      }
      this.importResult = {
        show: false,
        title: '',
        type: 'success',
        message: ''
      }
    },

    // 设置上传头部信息
    setupUploadHeaders() {
      this.uploadHeaders = {
        Authorization: "Bearer " + this.$store.getters.token
      }
      this.uploadData = {
        updateSupport: this.updateSupport,
        year: this.selectedYear
      }
    },

    // 文件选择变化处理
    handleFileChange(file, fileList) {
      this.hasImportFile = fileList.length > 0
    },

    // 上传前验证
    beforeImportUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.type === 'application/vnd.ms-excel'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isExcel) {
        this.$message.error('只能上传 Excel 格式文件!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!')
        return false
      }

      // 更新上传数据
      this.uploadData.updateSupport = this.updateSupport

      return true
    },

    // 导入进度处理
    handleImportProgress(event, file, fileList) {
      this.importing = true
      this.importProgress = {
        show: true,
        percentage: Math.round(event.percent),
        status: null,
        text: '正在上传文件...'
      }
    },

    // 导入成功处理
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
        this.loadTeacherData()
      } else {
        this.importResult.show = true
        this.importResult.title = '导入失败'
        this.importResult.type = 'error'
        this.importResult.message = `<p>${response.msg || '导入过程中发生错误'}</p>`
      }

      // 清空文件列表
      this.$refs.importUpload.clearFiles()
      this.hasImportFile = false
    },

    // 导入错误处理
    handleImportError(error, file, fileList) {
      this.importing = false
      this.importProgress.show = false
      this.importResult.show = true
      this.importResult.title = '导入失败'
      this.importResult.type = 'error'
      this.importResult.message = `<p>文件上传失败: ${error.message || '未知错误'}</p>`

      // 清空文件列表
      this.$refs.importUpload.clearFiles()
      this.hasImportFile = false
    },

    // 提交导入
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

    // 取消导入
    cancelImport() {
      this.importDialogVisible = false
      this.resetImportState()
    },

    // 处理导入点击
    handleImportClick() {
      this.resetImportState()
      this.importDialogVisible = true
      this.setupUploadHeaders()
    },

    async handleExportClick() {
      try {
        this.$loading({ text: '正在导出数据...' })

        // 构建查询参数
        const queryParams = {
          period: this.selectedYear,
          unitId: this.currentOrgCode
        }

        const response = await exportTeacherAssessment(queryParams)

        // 创建下载链接
        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `教师考核数据_${this.selectedYear}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        this.$message.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败: ' + (error.message || '未知错误'))
      } finally {
        this.$loading().close()
      }
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
    async loadTeacherData() {
      try {
        this.$loading({ text: '正在加载数据...' })
        const response = await getTeacherAssessmentData(this.selectedYear, this.currentOrgCode)

        if (response.code === 200) {
          const rawData = response.rows || response.data || []
          this.teacherTableData = rawData.map(item => this.mapBackendDataToFrontend(item))
          this.teacherPagination.total = response.total || this.teacherTableData.length
          this.teacherPagination.currentPage = 1
        } else {
          this.$message.error(response.msg || '获取数据失败')
          this.teacherTableData = []
          this.teacherPagination.total = 0
        }
      } catch (error) {
        console.error('加载教师考核数据失败:', error)
        this.$message.error('加载数据失败，请稍后重试')
        this.teacherTableData = []
        this.teacherPagination.total = 0
      } finally {
        this.$loading().close()
      }
    },
    // 将后端数据映射为前端表格数据（对齐教师表头配置）
    mapBackendDataToFrontend(backendData) {
      return {
        personId: backendData.personId,
        personName: backendData.personName,
        unitId: backendData.unitId,
        unitPath: this.getUnitDisplayName(backendData.unitId),
        birthDate: backendData.birthDate,
        age: backendData.age,
        title: backendData.title,
        period: backendData.period,

        // 基础科目 20% - 映射到metric001-metric006
        baseBasicKnowledge: backendData.metric001 || '0',
        baseSportsTrack: backendData.metric002 || '0',
        baseSportsRope: backendData.metric003 || '0',
        baseSportsLongJump: backendData.metric004 || '0',
        baseGroupA: backendData.metric005 || '0',
        baseGroupB: backendData.metric006 || '0',
        baseTotal: this.calculateBaseTotal(backendData),

        // 共同科目 30% - 映射到metric007-metric014
        commonSubject1: backendData.metric007 || '0',
        commonSubject2: backendData.metric008 || '0',
        commonSubject3: backendData.metric009 || '0',
        commonSubject4: backendData.metric010 || '0',
        commonSubject5: backendData.metric011 || '0',
        commonSubject6: backendData.metric012 || '0',
        commonSubject7: backendData.metric013 || '0',
        commonSubject8: backendData.metric014 || '0',
        commonTotal: this.calculateCommonTotal(backendData),

        // 岗位业务 50% - 映射到metric015
        jobBusiness: backendData.metric015 || '0',

        // 综合成绩与评定
        totalScore: backendData.totalScore || '0',
        totalRating: backendData.totalRating || '及格',
        remark: backendData.remark || '',
        description: backendData.status || ''
      }
    },
    // 计算基础科目总成绩
    calculateBaseTotal(data) {
      const basicKnowledge = parseFloat(data.metric001 || 0) * 0.2
      const sportsAvg = (parseFloat(data.metric002 || 0) + parseFloat(data.metric003 || 0) + parseFloat(data.metric004 || 0)) / 3
      const sports = sportsAvg * 0.3
      const groupA = parseFloat(data.metric005 || 0) * 0.25
      const groupB = parseFloat(data.metric006 || 0) * 0.25
      return (basicKnowledge + sports + groupA + groupB).toFixed(1)
    },
    // 计算共同科目总成绩
    calculateCommonTotal(data) {
      let total = 0
      let count = 0
      for (let i = 7; i <= 14; i++) {
        const key = `metric${i.toString().padStart(3, '0')}`
        if (data[key]) {
          total += parseFloat(data[key])
          count++
        }
      }
      return count > 0 ? (total / count).toFixed(1) : '0'
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
    // 获取部门树数据
    async getDeptTreeData() {
      try {
        const response = await deptTreeSelect()
        this.deptTreeData = response.data || []
      } catch (error) {
        console.error('获取部门树数据失败:', error)
      }
    },
    // 根据单位ID获取单位显示名称
    getUnitDisplayName(unitId) {
      if (!unitId || !this.deptTreeData.length) {
        return unitId || ''
      }

      // 递归查找部门节点
      const findDeptNode = (nodes, targetId) => {
        for (const node of nodes) {
          if (node.id === targetId || node.orgCode === targetId) {
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
      const buildPath = (nodeId) => {
        const node = findDeptNode(this.deptTreeData, nodeId)
        if (!node) return unitId

        const path = [node.label]
        let currentNode = node

        // 向上查找父节点
        while (currentNode.parentId) {
          const parentNode = findDeptNode(this.deptTreeData, currentNode.parentId)
          if (parentNode) {
            path.unshift(parentNode.label)
            currentNode = parentNode
          } else {
            break
          }
        }

        return path.join('/')
      }

      return buildPath(unitId)
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
  flex-wrap: nowrap;
}

.teacher-right .label {
  margin-right: 8px;
  color: #606266;
}

.teacher-right .el-button,
.teacher-right .el-date-picker {
  margin-right: 8px;
}

.teacher-right .el-input {
  margin-right: 8px;
  width: 220px;
  flex: 0 0 220px;
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
