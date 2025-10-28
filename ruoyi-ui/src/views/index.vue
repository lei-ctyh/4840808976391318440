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
          <el-tabs v-model="activeTab" type="card">
            <el-tab-pane label="图表看板" name="charts">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane label="教师看板" name="teacher">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane label="学生看板" name="student">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane label="领导看板" name="leader">
              <div class="tab-body" />
            </el-tab-pane>
            <el-tab-pane label="单位看板" name="org">
              <div class="tab-body" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </pane>
    </splitpanes>
  </div>
  
</template>

<script>
import { deptTreeSelect } from "@/api/system/user"
import { Splitpanes, Pane } from "splitpanes"
import "splitpanes/dist/splitpanes.css"

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
      defaultExpandedKeys: []
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
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data || []
        this.$nextTick(() => {
          const root = (this.deptOptions && this.deptOptions.length > 0) ? this.deptOptions[0] : null
          if (root) {
            this.defaultExpandedKeys = [root.id]
            this.selectedDeptId = root.id
            if (this.$refs.tree) {
              this.$refs.tree.setCurrentKey(root.id)
            }
          }
        })
      })
    },
    filterNode(value, data) {
      if (!value) return true
      return (data.label || "").indexOf(value) !== -1
    },
    handleNodeClick(data) {
      this.selectedDeptId = data.id
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
</style>