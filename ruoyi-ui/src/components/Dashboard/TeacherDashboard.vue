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
        <el-table-column prop="title" label="衔级" width="100" />
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
import { deptTreeSelect } from "@/api/system/user"
import FileUpload from "@/components/FileUpload"
import { bindTemplate, resolveTemplate, getTemplate } from "@/api/system/template"
import { listTeacherAssessment, getTeacherAssessmentByUnitAndPeriod, importTemplate, importData, exportTeacherAssessment } from "@/api/system/teacherAssessment"

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
      baseApi: process.env.VUE_APP_BASE_API,
      // 部门树数据，用于构建单位显示名称
      deptTreeData: [],
      // 模板信息回显相关
      existingTemplate: null,
      templateCheckCompleted: false
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
    },
    currentOrgCode() {
      // 当组织节点切换时，重新加载数据
      this.loadTeacherData()
    }
  },
  created() {
    this.loadTeacherData()
    this.getDeptTreeData()
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
      // 创建文件输入元素
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = '.xlsx,.xls'
      input.onchange = (event) => {
        const file = event.target.files[0]
        if (file) {
          this.handleImportFile(file)
        }
      }
      input.click()
    },
    
    // 处理导入文件
    async handleImportFile(file) {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('updateSupport', true) // 支持更新已存在数据
      
      try {
        this.$loading({ text: '正在导入数据...' })
        const response = await importData(formData)
        
        if (response.code === 200) {
          this.$message.success(response.msg || '导入成功')
          // 重新加载数据
          this.loadTeacherData()
        } else {
          this.$message.error(response.msg || '导入失败')
          // 如果有错误详情，显示错误信息
          if (response.errors && response.errors.length > 0) {
            const errorMsg = response.errors.slice(0, 5).join('\n') // 只显示前5个错误
            this.$alert(errorMsg, '导入错误详情', {
              confirmButtonText: '确定',
              type: 'warning'
            })
          }
        }
      } catch (error) {
        console.error('导入失败:', error)
        this.$message.error('导入失败: ' + (error.message || '未知错误'))
      } finally {
        this.$loading().close()
      }
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
    async downloadTemplateFromServer() {
      try {
        this.$loading({ text: '正在下载模板...' })
        
        const response = await importTemplate()
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '教师考核导入模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.$message.success('模板下载成功')
      } catch (error) {
        console.error('模板下载失败:', error)
        this.$message.error('模板下载失败: ' + (error.message || '未知错误'))
      } finally {
        this.$loading().close()
      }
    },
    async loadTeacherData() {
      try {
        this.$loading({ text: '正在加载数据...' })
        
        // 构建查询参数
        const queryParams = {
          period: this.selectedYear,
          unitId: this.currentOrgCode
        }
        
        // 调用实际API获取数据
        const response = await getTeacherAssessmentByUnitAndPeriod(this.currentOrgCode, this.selectedYear)
        
        if (response.code === 200) {
          this.teacherTableData = response.data || []
        } else {
          console.warn('获取教师考核数据失败:', response.msg)
          // 如果API调用失败，使用模拟数据作为后备
          this.teacherTableData = getTeacherAssessmentData()
        }
      } catch (error) {
        console.error('加载教师考核数据失败:', error)
        // 如果API调用失败，使用模拟数据作为后备
        this.teacherTableData = getTeacherAssessmentData()
      } finally {
        this.$loading().close()
      }

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
