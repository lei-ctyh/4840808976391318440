<template>
  <div class="app-container">
    <splitpanes class="default-theme">
      <pane size="18">
        <div class="left-pane">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入机构名称"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              @keyup.enter.native="() => $refs.tree && $refs.tree.filter(deptName)"
            />
          </div>
          <el-tree
            ref="tree"
            :data="deptOptions"
            :props="defaultProps"
            node-key="id"
            :default-expanded-keys="defaultExpandedKeys"
            highlight-current
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
          />
        </div>
      </pane>
      <pane size="82">
        <div class="right-pane">
          <el-dialog title="导入考核数据 (Excel)" :visible.sync="importDialogVisible" width="500px">
            <div>
              <p>请选择符合模板的 Excel 文件以导入数据。</p>
              <input type="file" accept=".xlsx,.xls" @change="onImportFileSelected" />
              <p class="tip">建议先“下载模板”，填好后再导入。</p>
            </div>
            <span slot="footer" class="dialog-footer">
              <el-button @click="importDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="confirmImport">导入</el-button>
            </span>
          </el-dialog>
          <el-tabs v-if="visibleTabKeys.length > 0" v-model="activeTab" type="card">
            <el-tab-pane v-if="visibleTabKeys.includes('charts')" label="图表看板" name="charts">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('teacher')" label="教师看板" name="teacher">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('student')" label="学生看板" name="student">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('leader')" label="领导看板" name="leader">
              <div class="leader-header">
                <div class="leader-left">
                  <span class="leader-title">领导班子年度考核</span>
                  <el-tag type="success" size="small">{{ orgTypeText }}</el-tag>
                  <el-tag size="small" class="leader-dept">{{ selectedDeptNode && selectedDeptNode.label ? selectedDeptNode.label : '未选择组织' }}</el-tag>
                </div>
                <div class="leader-right">
                  <el-button type="primary" icon="el-icon-upload" size="small" @click="openImportDialog">导入</el-button>
                  <el-button icon="el-icon-download" size="small" @click="exportLeaderExcel">导出</el-button>
                  <el-button icon="el-icon-document" size="small" @click="downloadLeaderExcelTemplate">下载模板</el-button>
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
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('org')" label="单位看板" name="org">
              <div class="tab-body" />
            </el-tab-pane>
          </el-tabs>
          <div v-else class="empty-state">
            <i class="el-icon-info" />
            <span>当前组织类型暂无可用看板，请选择“领导班子”或“教学组织”节点。</span>
          </div>
        </div>
      </pane>
    </splitpanes>
  </div>
  
</template>

<script>
import { deptTreeSelect } from "@/api/system/user"
import { Splitpanes, Pane } from "splitpanes"
import "splitpanes/dist/splitpanes.css"
import { getLeaderAssessmentData } from "@/mock/mockData"

export default {
  name: "Index",
  components: { Splitpanes, Pane },
  data() {
    return {
      deptName: undefined,
      deptOptions: [],
      defaultProps: { children: "children", label: "label" },
      activeTab: "charts",
      selectedDeptId: undefined,
      defaultExpandedKeys: [],
      selectedDeptNode: null,
      selectedElNode: null,
      visibleTabKeys: ["charts", "teacher", "student", "leader", "org"],
      selectedYear: String(new Date().getFullYear()),
      leaderTableData: [],
      leaderPagination: { currentPage: 1, pageSize: 10, total: 0 }
      ,importDialogVisible: false,
      importSelectedFile: null
    }
  },
  computed: {
    orgTypeText() {
      const type = this.resolveOrgType(this.selectedElNode)
      if (type === 'leader') return '领导班子'
      if (type === 'teaching') return '教学组织'
      return '其他组织'
    },
    leaderTablePageData() {
      const start = (this.leaderPagination.currentPage - 1) * this.leaderPagination.pageSize
      const end = start + this.leaderPagination.pageSize
      return this.leaderTableData.slice(start, end)
    }
  },
  watch: {
    deptName(val) {
      if (this.$refs.tree) this.$refs.tree.filter(val)
    },
    activeTab(val) {
      if (val === 'leader') {
        this.loadLeaderData()
      }
    },
    selectedYear() {
      if (this.activeTab === 'leader') {
        this.loadLeaderData()
      }
    }
  },
  created() {
    this.getDeptTree()
  },
  methods: {
    // 基于机构编码判定组织类型（优先），回退到名称
    resolveOrgType(node) {
      let n = node
      while (n) {
        const code = n && n.data && (n.data.orgCode || n.data.org_code)
        if (code && typeof code === 'string') {
          const prefix = code.slice(0, 2)
          if (prefix === '00') return 'leader'
          if (prefix === '01') return 'teaching'
        }
        n = n.parent
      }
      // 代码缺失时回退到名称判断
      n = node
      while (n) {
        const label = (n && (n.label || (n.data && n.data.label))) ? String(n.label || n.data.label) : ''
        if (label.includes('领导班子')) return 'leader'
        if (label.includes('教学组织')) return 'teaching'
        n = n.parent
      }
      return null
    },
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data || []
        this.$nextTick(() => {
          const root = (this.deptOptions && this.deptOptions.length > 0) ? this.deptOptions[0] : null
          if (root) {
            this.defaultExpandedKeys = [root.id]
            this.selectedDeptId = root.id
            this.selectedDeptNode = root
            if (this.$refs.tree) {
              this.$refs.tree.setCurrentKey(root.id)
            }
            const rootNode = this.$refs.tree ? this.$refs.tree.getNode(root.id) : null
            this.selectedElNode = rootNode
            this.computeVisibleTabs(rootNode)
          }
        })
      })
    },
    onYearChange() {
      // 年度切换时重新加载领导看板数据
      if (this.activeTab === 'leader') {
        this.loadLeaderData()
      }
    },
    openImportDialog() {
      this.importDialogVisible = true
      this.importSelectedFile = null
    },
    onImportFileSelected(e) {
      const files = e.target.files
      if (files && files[0]) {
        this.importSelectedFile = files[0]
      }
    },
    async confirmImport() {
      if (!this.importSelectedFile) {
        this.$message.warning('请先选择 Excel 文件')
        return
      }
      const XLSX = await this.ensureXLSX()
      try {
        const buf = await this.importSelectedFile.arrayBuffer()
        const wb = XLSX.read(new Uint8Array(buf), { type: 'array' })
        const sheetName = wb.SheetNames[0]
        const sheet = wb.Sheets[sheetName]
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 })
        if (!rows || rows.length === 0) {
          this.$message.error('Excel 内容为空')
          return
        }
        const hasGroupHeader = rows[0].some(v => ['基本信息','基础课目','共同课目','综合成绩'].includes(String(v || '').trim()))
        const header = hasGroupHeader ? (rows[1] || []) : (rows[0] || [])
        const dataRows = hasGroupHeader ? rows.slice(2) : rows.slice(1)
        const map = {
          '人员编号': 'personId',
          '姓名': 'name',
          '单位': 'unitPath',
          '出生年月': 'birthdate',
          '年龄': 'age',
          '职称': 'title',
          '评定周期': 'cycle',
          '基本知识': 'baseBasicKnowledge',
          '田径': 'baseSportsTrack',
          '跳绳': 'baseSportsRope',
          '跳远': 'baseSportsLongJump',
          '共同A': 'baseGroupA',
          '共同B': 'baseGroupB',
          '基础成绩': 'baseTotal',
          '课目1': 'commonSubject1',
          '课目2': 'commonSubject2',
          '课目3': 'commonSubject3',
          '课目4': 'commonSubject4',
          '课目5': 'commonSubject5',
          '课目6': 'commonSubject6',
          '课目7': 'commonSubject7',
          '课目8': 'commonSubject8',
          '共同成绩': 'commonTotal',
          '岗位业务': 'jobBusiness',
          '百分制': 'comprehensivePercent',
          '四级制': 'comprehensiveLevel',
          '备注': 'remark',
          '说明': 'description'
        }
        const keyIndex = header.map(h => map[String(h).trim()] || null)
        const data = dataRows
          .filter(r => Array.isArray(r) && r.some(c => c != null && String(c).trim() !== ''))
          .map(r => {
            const obj = {}
            keyIndex.forEach((key, idx) => {
              if (key) obj[key] = r[idx] !== undefined ? r[idx] : ''
            })
            if (!obj.cycle) obj.cycle = `${this.selectedYear}年度`
            return obj
          })
        this.leaderTableData = data
        this.leaderPagination.total = this.leaderTableData.length
        this.leaderPagination.currentPage = 1
        this.importDialogVisible = false
        this.$message.success('导入完成')
      } catch (err) {
        console.error(err)
        this.$message.error('Excel 解析失败，请检查文件格式')
      }
    },
    async exportLeaderExcel() {
      const XLSX = await this.ensureXLSX()
      const headerTop = [
        '基本信息','','','','','','',
        '基础课目','','','','','','',
        '共同课目','','','','','','','','',
        '岗位业务',
        '综合成绩','',
        '备注','说明'
      ]
      const headerLeaf = [
        '人员编号','姓名','单位','出生年月','年龄','职称','评定周期',
        '基本知识','田径','跳绳','跳远','共同A','共同B','基础成绩',
        '课目1','课目2','课目3','课目4','课目5','课目6','课目7','课目8','共同成绩',
        '岗位业务','百分制','四级制','备注','说明'
      ]
      const mapOrder = [
        'personId','name','unitPath','birthdate','age','title','cycle',
        'baseBasicKnowledge','baseSportsTrack','baseSportsRope','baseSportsLongJump','baseGroupA','baseGroupB','baseTotal',
        'commonSubject1','commonSubject2','commonSubject3','commonSubject4','commonSubject5','commonSubject6','commonSubject7','commonSubject8','commonTotal',
        'jobBusiness','comprehensivePercent','comprehensiveLevel','remark','description'
      ]
      const dataRows = this.leaderTableData.map(item => mapOrder.map(k => item[k] != null ? item[k] : ''))
      const aoa = [headerTop, headerLeaf, ...dataRows]
      const ws = XLSX.utils.aoa_to_sheet(aoa)
      ws['!merges'] = [
        // A1:G1 基本信息
        { s: { r: 0, c: 0 }, e: { r: 0, c: 6 } },
        // H1:N1 基础课目
        { s: { r: 0, c: 7 }, e: { r: 0, c: 13 } },
        // O1:V1 共同课目（8课目+共同成绩）
        { s: { r: 0, c: 14 }, e: { r: 0, c: 22 } },
        // W1:W1 岗位业务（不合并）—保留单元格即可
        // X1:Y1 综合成绩
        { s: { r: 0, c: 24 }, e: { r: 0, c: 25 } }
      ]
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '领导考核')
      XLSX.writeFile(wb, `领导考核_${this.selectedYear}.xlsx`)
    },
    async downloadLeaderExcelTemplate() {
      const XLSX = await this.ensureXLSX()
      const headerTop = [
        '基本信息','','','','','','',
        '基础课目','','','','','','',
        '共同课目','','','','','','','','',
        '岗位业务',
        '综合成绩','',
        '备注','说明'
      ]
      const headerLeaf = [
        '人员编号','姓名','单位','出生年月','年龄','职称','评定周期',
        '基本知识','田径','跳绳','跳远','共同A','共同B','基础成绩',
        '课目1','课目2','课目3','课目4','课目5','课目6','课目7','课目8','共同成绩',
        '岗位业务','百分制','四级制','备注','说明'
      ]
      const aoa = [headerTop, headerLeaf]
      const ws = XLSX.utils.aoa_to_sheet(aoa)
      ws['!merges'] = [
        { s: { r: 0, c: 0 }, e: { r: 0, c: 6 } },
        { s: { r: 0, c: 7 }, e: { r: 0, c: 13 } },
        { s: { r: 0, c: 14 }, e: { r: 0, c: 22 } },
        { s: { r: 0, c: 24 }, e: { r: 0, c: 25 } }
      ]
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '领导考核模板')
      XLSX.writeFile(wb, '领导考核模板.xlsx')
    },
    async ensureXLSX() {
      if (window && window.XLSX) return window.XLSX
      return new Promise((resolve, reject) => {
        const script = document.createElement('script')
        script.src = 'https://cdn.jsdelivr.net/npm/xlsx@0.18.5/dist/xlsx.full.min.js'
        script.onload = () => resolve(window.XLSX)
        script.onerror = () => reject(new Error('XLSX 加载失败'))
        document.head.appendChild(script)
      })
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
    filterNode(value, data) {
      if (!value) return true
      return (data.label || "").indexOf(value) !== -1
    },
    handleNodeClick(data, node) {
      this.selectedDeptId = data.id
      this.selectedDeptNode = data
      this.selectedElNode = node
      this.computeVisibleTabs(node)
    },
    computeVisibleTabs(node) {
      // 优先使用机构编码判断，失败则回退到名称
      const type = this.resolveOrgType(node)
      if (type === 'leader') {
        this.visibleTabKeys = ["charts", "leader"]
        if (!["charts", "leader"].includes(this.activeTab)) this.activeTab = "charts"
        return
      }
      if (type === 'teaching') {
        this.visibleTabKeys = ["charts", "teacher", "student", "org"]
        if (!this.visibleTabKeys.includes(this.activeTab)) this.activeTab = "charts"
        return
      }
      // 其他情况，显示空状态
      this.visibleTabKeys = []
      this.activeTab = "charts"
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.left-pane {
  padding-right: 12px;
}
.right-pane {
  padding-left: 12px;
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
.leader-alert {
  margin: 12px 0;
}
.table-pagination {
  margin-top: 12px;
  text-align: right;
}
.empty-state {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
.empty-state .el-icon-info {
  margin-right: 8px;
}
</style>