<template>
  <div class="dynamic-table-test">
    <h2>动态表格组件测试</h2>

    <div class="test-section">
      <h3>教师成绩表格测试</h3>
      <div class="controls">
        <label>年份：</label>
        <el-select v-model="teacherYear" @change="onTeacherYearChange">
          <el-option label="2024" value="2024"></el-option>
          <el-option label="2023" value="2023"></el-option>
        </el-select>

        <label style="margin-left: 20px;">机构编码：</label>
        <el-select v-model="teacherOrgCode" @change="onTeacherOrgChange">
          <el-option label="0001 (有自定义配置)" value="0001"></el-option>
          <el-option label="0002 (使用默认配置)" value="0002"></el-option>
        </el-select>
      </div>

      <dynamic-table
        :data="teacherTestData"
        board-type="teacher"
        :year="teacherYear"
        :org-code="teacherOrgCode"
        :table-props="{
          border: true,
          size: 'small',
          style: 'width: 100%',
          'header-cell-style': { textAlign: 'center' }
        }"
        @config-loaded="onTeacherConfigLoaded"
        @config-error="onTeacherConfigError"
        ref="teacherTable"
      />
    </div>

    <div class="test-section" style="margin-top: 40px;">
      <h3>个人成绩表格测试</h3>
      <div class="controls">
        <label>年份：</label>
        <el-select v-model="leaderYear" @change="onLeaderYearChange">
          <el-option label="2024" value="2024"></el-option>
          <el-option label="2023" value="2023"></el-option>
        </el-select>

        <label style="margin-left: 20px;">机构编码：</label>
        <el-select v-model="leaderOrgCode" @change="onLeaderOrgChange">
          <el-option label="0001 (使用默认配置)" value="0001"></el-option>
          <el-option label="0002 (使用默认配置)" value="0002"></el-option>
        </el-select>
      </div>

      <dynamic-table
        :data="leaderTestData"
        board-type="leader"
        :year="leaderYear"
        :org-code="leaderOrgCode"
        :table-props="{
          border: true,
          size: 'small',
          style: 'width: 100%',
          'header-cell-style': { textAlign: 'center' }
        }"
        @config-loaded="onLeaderConfigLoaded"
        @config-error="onLeaderConfigError"
        ref="leaderTable"
      />
    </div>
  </div>
</template>

<script>
import DynamicTable from "@/components/DynamicTable"

export default {
  name: "DynamicTableTest",
  components: { DynamicTable },
  data() {
    return {
      teacherYear: "2024",
      teacherOrgCode: "0001",
      leaderYear: "2024",
      leaderOrgCode: "0001",
      teacherTestData: [
        {
          personId: "T001",
          name: "张三",
          unitPath: "教学组织/计算机系/软件工程班",
          birthdate: "1985-06-15",
          age: 38,
          title: "副教授",
          cycle: "2024年度",
          baseBasicKnowledge: 85,
          baseSportsTrack: 90,
          baseSportsRope: 88,
          baseSportsLongJump: 92,
          baseGroupA: 87,
          baseGroupB: 89,
          baseTotal: 88.2,
          commonSubject1: 85,
          commonSubject2: 90,
          commonSubject3: 88,
          commonSubject4: 92,
          commonSubject5: 87,
          commonSubject6: 89,
          commonSubject7: 91,
          commonSubject8: 86,
          commonTotal: 88.5,
          jobBusiness: 92,
          comprehensivePercent: 89.6,
          comprehensiveLevel: "良好",
          remark: "表现优秀",
          description: "综合素质较高"
        },
        {
          personId: "T002",
          name: "李四",
          unitPath: "教学组织/数学系/应用数学班",
          birthdate: "1982-03-20",
          age: 41,
          title: "教授",
          cycle: "2024年度",
          baseBasicKnowledge: 92,
          baseSportsTrack: 85,
          baseSportsRope: 87,
          baseSportsLongJump: 89,
          baseGroupA: 91,
          baseGroupB: 93,
          baseTotal: 89.5,
          commonSubject1: 94,
          commonSubject2: 88,
          commonSubject3: 91,
          commonSubject4: 89,
          commonSubject5: 92,
          commonSubject6: 90,
          commonSubject7: 87,
          commonSubject8: 93,
          commonTotal: 90.5,
          jobBusiness: 95,
          comprehensivePercent: 92.1,
          comprehensiveLevel: "优秀",
          remark: "学术带头人",
          description: "教学科研并重"
        }
      ],
      leaderTestData: [
        {
          personId: "L001",
          personName: "王五",
          unitPath: "领导班子/校长办公室",
          birthDate: "1975-08-10",
          age: 48,
          title: "校长",
          cycle: "2024年度",
          baseBasicKnowledge: 95,
          baseSportsTrack: 88,
          baseSportsRope: 90,
          baseSportsLongJump: 87,
          baseGroupA: 94,
          baseGroupB: 96,
          baseTotal: 91.7,
          commonSubject1: 96,
          commonSubject2: 94,
          commonSubject3: 95,
          commonSubject4: 93,
          commonSubject5: 97,
          commonSubject6: 92,
          commonSubject7: 94,
          commonSubject8: 95,
          commonTotal: 94.5,
          jobBusiness: 98,
          comprehensivePercent: 95.2,
          comprehensiveLevel: "优秀",
          remark: "领导能力突出",
          description: "管理水平优异"
        }
      ]
    }
  },
  methods: {
    onTeacherYearChange() {
      console.log('教师年份变更:', this.teacherYear)
    },
    onTeacherOrgChange() {
      console.log('教师机构变更:', this.teacherOrgCode)
    },
    onLeaderYearChange() {
      console.log('领导年份变更:', this.leaderYear)
    },
    onLeaderOrgChange() {
      console.log('领导机构变更:', this.leaderOrgCode)
    },
    onTeacherConfigLoaded(config) {
      console.log('教师表格配置加载成功:', config)
      this.$message.success('教师表格配置加载成功')
    },
    onTeacherConfigError(error) {
      console.error('教师表格配置加载失败:', error)
      this.$message.warning('教师表格配置加载失败，使用默认配置')
    },
    onLeaderConfigLoaded(config) {
      console.log('领导表格配置加载成功:', config)
      this.$message.success('领导表格配置加载成功')
    },
    onLeaderConfigError(error) {
      console.error('领导表格配置加载失败:', error)
      this.$message.warning('领导表格配置加载失败，使用默认配置')
    }
  }
}
</script>

<style scoped>
.dynamic-table-test {
  padding: 20px;
}

.test-section {
  margin-bottom: 30px;
}

.controls {
  margin: 15px 0;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.controls label {
  font-weight: bold;
  margin-right: 10px;
}

h2 {
  color: #409EFF;
  margin-bottom: 20px;
}

h3 {
  color: #606266;
  margin-bottom: 15px;
}
</style>
