<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="父考试项ID" prop="parentId">
        <el-input
          v-model="queryParams.parentId"
          placeholder="请输入父考试项ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="考试项名称" prop="itemName">
        <el-input
          v-model="queryParams.itemName"
          placeholder="请输入考试项名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="显示顺序" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入显示顺序"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sms_exam_item_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="核定方式" prop="scoringMethod">
        <el-select v-model="queryParams.scoringMethod" placeholder="请选择核定方式" clearable>
          <el-option
            v-for="dict in dict.type.sms_scoring_method"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="权重" prop="weightPercent">
        <el-input
          v-model="queryParams.weightPercent"
          placeholder="请输入权重"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
	    <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:item:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="itemList"
      row-key="itemId"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="父考试项ID" prop="parentId" />
      <el-table-column label="考试项名称" align="center" prop="itemName" />
      <el-table-column label="显示顺序" align="center" prop="orderNum" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sms_exam_item_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="核定方式" align="center" prop="scoringMethod">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sms_scoring_method" :value="scope.row.scoringMethod"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="权重" align="center" prop="weightPercent" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:item:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['system:item:add']"
          >新增</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:item:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改考试项管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="父考试项ID" prop="parentId">
          <treeselect v-model="form.parentId" :options="itemOptions" :normalizer="normalizer" placeholder="请选择父考试项ID" />
        </el-form-item>
        <el-form-item label="考试项名称" prop="itemName">
          <el-input v-model="form.itemName" placeholder="请输入考试项名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input v-model="form.orderNum" placeholder="请输入显示顺序" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sms_exam_item_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="核定方式" prop="scoringMethod">
          <el-select v-model="form.scoringMethod" placeholder="请选择核定方式">
            <el-option
              v-for="dict in dict.type.sms_scoring_method"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="权重" prop="weightPercent">
          <el-input v-model="form.weightPercent" placeholder="请输入权重" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listItem, getItem, delItem, addItem, updateItem } from "@/api/system/item"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "Item",
  dicts: ['sms_scoring_method', 'sms_exam_item_status'],
  components: {
    Treeselect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 考试项管理表格数据
      itemList: [],
      // 考试项管理树选项
      itemOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        parentId: null,
        itemName: null,
        orderNum: null,
        status: null,
        scoringMethod: null,
        weightPercent: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        itemName: [
          { required: true, message: "考试项名称不能为空", trigger: "blur" }
        ],
        weightPercent: [
          { required: true, message: "权重不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询考试项管理列表 */
    getList() {
      this.loading = true
      listItem(this.queryParams).then(response => {
        this.itemList = this.handleTree(response.data, "itemId", "parentId")
        this.loading = false
      })
    },
    /** 转换考试项管理数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.itemId,
        label: node.itemName,
        children: node.children
      }
    },
	/** 查询考试项管理下拉树结构 */
    getTreeselect() {
      listItem().then(response => {
        this.itemOptions = []
        const data = { itemId: 0, itemName: '顶级节点', children: [] }
        data.children = this.handleTree(response.data, "itemId", "parentId")
        this.itemOptions.push(data)
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        itemId: null,
        parentId: null,
        itemName: null,
        orderNum: null,
        status: null,
        scoringMethod: null,
        remark: null,
        weightPercent: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset()
      this.getTreeselect()
      if (row != null && row.itemId) {
        this.form.parentId = row.itemId
      } else {
        this.form.parentId = 0
      }
      this.open = true
      this.title = "添加考试项管理"
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false
      this.isExpandAll = !this.isExpandAll
      this.$nextTick(() => {
        this.refreshTable = true
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.getTreeselect()
      if (row != null) {
        this.form.parentId = row.parentId
      }
      getItem(row.itemId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改考试项管理"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.itemId != null) {
            updateItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addItem(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除考试项管理编号为"' + row.itemId + '"的数据项？').then(function() {
        return delItem(row.itemId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
