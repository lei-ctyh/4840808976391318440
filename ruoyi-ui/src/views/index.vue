<template>
  <div class="app-container">
    <splitpanes class="default-theme">
      <pane size="18">
        <div class="left-pane">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入单位名称"
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
          <el-tabs v-if="visibleTabKeys.length > 0" v-model="activeTab" type="card">
            <el-tab-pane v-if="visibleTabKeys.includes('charts')" label="成绩对比" name="charts">
              <chart-dashboard :selectedDeptNode="selectedDeptNode" />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('teacher')" label="干部成绩" name="teacher">
              <teacher-dashboard
                :selectedDeptNode="selectedDeptNode"
                :orgTypeText="orgTypeText"
                :organizationPath="organizationPath"
              />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('student')" label="战士成绩" name="student">
              <student-dashboard
                :selectedDeptNode="selectedDeptNode"
                :orgTypeText="orgTypeText"
                :organizationPath="organizationPath"
              />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('leader')" label="个人成绩" name="leader">
              <leader-dashboard
                :selectedDeptNode="selectedDeptNode"
                :orgTypeText="orgTypeText"
                :organizationPath="organizationPath"
              />
            </el-tab-pane>
            <el-tab-pane v-if="visibleTabKeys.includes('dept')" label="单位看板" name="dept">
              <dept-dashboard   :selectedDeptNode="selectedDeptNode"
                                :orgTypeText="orgTypeText"
                                :organizationPath="organizationPath" />
            </el-tab-pane>
          </el-tabs>
          <div v-else class="announcement-container">
            <announcement-board />
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

// 导入看板组件
import LeaderDashboard from "@/components/Dashboard/LeaderDashboard.vue"
import ChartDashboard from "@/components/Dashboard/ChartDashboard.vue"
import StudentDashboard from "@/components/Dashboard/StudentDashboard.vue"
import TeacherDashboard from "@/components/Dashboard/TeacherDashboard.vue"
import DeptDashboard from "@/components/Dashboard/DeptDashboard.vue"
import AnnouncementBoard from "@/components/Dashboard/AnnouncementBoard.vue"

export default {
  name: "Index",
  components: {
    Splitpanes,
    Pane,
    LeaderDashboard,
    ChartDashboard,
    StudentDashboard,
    TeacherDashboard,
    DeptDashboard,
    AnnouncementBoard
  },
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
      visibleTabKeys: ["charts", "teacher", "student", "leader", "dept"]
    }
  },
  computed: {
    orgTypeText() {
      const type = this.resolveOrgType(this.selectedElNode)
      if (type === 'leader') return '领导班子'
      if (type === 'teaching') return '教学组织'
      return '其他组织'
    },
    organizationPath() {
      if (!this.selectedElNode || !this.selectedDeptNode) {
        return '未选择组织'
      }

      // 构建完整的组织路径
      const buildPath = (node) => {
        const path = []
        let current = node

        while (current) {
          if (current.data && current.data.label) {
            path.unshift(current.data.label)
          } else if (current.label) {
            path.unshift(current.label)
          }
          current = current.parent
        }

        return path.length > 0 ? path.join(' / ') : '未知组织'
      }

      return buildPath(this.selectedElNode)
    }
  },
  watch: {
    deptName(val) {
      if (this.$refs.tree) this.$refs.tree.filter(val)
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
        this.visibleTabKeys = ["leader"]
        if (!["leader"].includes(this.activeTab)) this.activeTab = "leader"
        return
      }
      if (type === 'teaching') {
        this.visibleTabKeys = ["charts", "teacher", "student", "dept"]
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
.announcement-container {
  min-height: 400px;
}
</style>
