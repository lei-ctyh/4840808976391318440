<template>
  <div class="chart-dashboard">
    <div class="dashboard-header">
      <div class="title-line">
        <h3>图表看板</h3>
        <div class="toolbar">
          <span class="label">年度</span>
          <el-date-picker
            v-model="selectedYear"
            type="year"
            placeholder="选择年度"
            value-format="yyyy"
            :default-value="new Date()"
            size="small"
            @change="onYearChange"
          />
        </div>
      </div>
      <p class="description">展示当前单位的成绩、优秀率、合格率，以及下级单位对比</p>
    </div>

    <!-- 指标卡片 -->
    <div class="metric-cards">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric">
              <div class="metric-label">当前单位</div>
              <div class="metric-value current-name">{{ currentOrgName || '未选择单位' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric">
              <div class="metric-label">成绩（均分）</div>
              <div class="metric-value">{{ metrics.avgScore.toFixed(1) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric triplet">
              <div class="metric-item">
                <div class="metric-label">优秀率</div>
                <div class="metric-value good">{{ (metrics.excellentRate * 100).toFixed(1) }}%</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">合格率</div>
                <div class="metric-value pass">{{ (metrics.passRate * 100).toFixed(1) }}%</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">未合格</div>
                <div class="metric-value fail">{{ ((1 - metrics.passRate) * 100).toFixed(1) }}%</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区 -->
    <div class="charts">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">下级单位成绩对比</div>
            <div ref="scoresChartRef" class="chart-box"></div>
            <div v-if="!hasChildren" class="empty-tip">无下级单位数据</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">下级单位优秀率/合格率对比</div>
            <div ref="ratesChartRef" class="chart-box"></div>
            <div v-if="!hasChildren" class="empty-tip">无下级单位数据</div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="12" style="margin-top: 12px;">
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">当前单位优秀/合格/未合格占比</div>
            <div ref="pieChartRef" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">年度趋势：成绩、优秀率、合格率</div>
            <div ref="trendChartRef" class="chart-box"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
  
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ChartDashboard',
  props: {
    selectedDeptNode: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      selectedYear: String(new Date().getFullYear()),
      metrics: { avgScore: 0, excellentRate: 0, passRate: 0 },
      childrenComparisons: [],
      scoresChart: null,
      ratesChart: null,
      pieChart: null,
      trendChart: null,
      yearlyTrend: { years: [], avgScores: [], excellentRates: [], passRates: [] }
    }
  },
  computed: {
    currentOrgName() {
      if (!this.selectedDeptNode) return ''
      return this.selectedDeptNode.label || this.selectedDeptNode.name || this.selectedDeptNode.title || ''
    },
    hasChildren() {
      return this.childrenComparisons && this.childrenComparisons.length > 0
    }
  },
  watch: {
    selectedDeptNode: {
      handler() {
        this.generateDataAndRender()
      },
      immediate: true
    },
    selectedYear() {
      this.generateDataAndRender()
    }
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.scoresChart) this.scoresChart.dispose()
    if (this.ratesChart) this.ratesChart.dispose()
    if (this.pieChart) this.pieChart.dispose()
    if (this.trendChart) this.trendChart.dispose()
  },
  methods: {
    onYearChange() {
      // 年度变更触发刷新
      this.generateDataAndRender()
    },
    initCharts() {
      this.$nextTick(() => {
        if (!this.scoresChart && this.$refs.scoresChartRef) {
          this.scoresChart = echarts.init(this.$refs.scoresChartRef)
        }
        if (!this.ratesChart && this.$refs.ratesChartRef) {
          this.ratesChart = echarts.init(this.$refs.ratesChartRef)
        }
        if (!this.pieChart && this.$refs.pieChartRef) {
          this.pieChart = echarts.init(this.$refs.pieChartRef)
        }
        if (!this.trendChart && this.$refs.trendChartRef) {
          this.trendChart = echarts.init(this.$refs.trendChartRef)
        }
        this.generateDataAndRender()
      })
    },
    resizeCharts() {
      if (this.scoresChart) this.scoresChart.resize()
      if (this.ratesChart) this.ratesChart.resize()
      if (this.pieChart) this.pieChart.resize()
      if (this.trendChart) this.trendChart.resize()
    },
    // 生成可重复的模拟数据（基于orgCode与year）
    seedRandom(seedStr) {
      let seed = 0
      for (let i = 0; i < seedStr.length; i++) {
        seed = (seed * 31 + seedStr.charCodeAt(i)) >>> 0
      }
      return () => {
        seed ^= seed << 13
        seed ^= seed >>> 17
        seed ^= seed << 5
        return ((seed >>> 0) % 1000) / 1000
      }
    },
    generateMockMetrics(node, year) {
      if (!node) {
        return {
          metrics: { avgScore: 0, excellentRate: 0, passRate: 0 },
          childrenComparisons: []
        }
      }
      const orgCode = node.orgCode || node.org_code || node.id || node.label || 'default'
      const rand = this.seedRandom(String(orgCode) + '-' + String(year))

      // 当前单位指标
      const avgScore = 70 + Math.floor(rand() * 30) // 70-100
      const excellentRate = 0.2 + rand() * 0.5 // 20%-70%
      const passRate = Math.min(0.95, 0.6 + rand() * 0.35) // 60%-95%

      // 下级单位对比数据（最多取前8个孩子）
      const children = Array.isArray(node.children) ? node.children.slice(0, 8) : []
      const childrenComparisons = children.map((c, idx) => {
        const r = this.seedRandom(String(orgCode) + '-' + String(year) + '-' + String(idx))
        const cAvg = 65 + Math.floor(r() * 35)
        const cExcellent = 0.15 + r() * 0.6
        const cPass = Math.min(0.97, 0.55 + r() * 0.4)
        return {
          name: c.label || c.name || c.title || `下级单位${idx + 1}`,
          avgScore: cAvg,
          excellentRate: cExcellent,
          passRate: cPass
        }
      })

      return {
        metrics: { avgScore, excellentRate, passRate },
        childrenComparisons
      }
    },
    generateYearlyTrendData(node, year) {
      const baseYear = Number(year)
      const startYear = baseYear - 4
      const years = Array.from({ length: 5 }, (_, i) => String(startYear + i))
      const orgCode = (node && (node.orgCode || node.org_code || node.id || node.label)) || 'default'
      const avgScores = []
      const excellentRates = []
      const passRates = []

      // 基线受组织与年度影响，确保不同单位/年度可复现
      const baseRand = this.seedRandom(String(orgCode) + '-trend-year-base-' + String(year))
      let baseScore = 70 + Math.floor(baseRand() * 20) // 70-90
      let baseExcellent = 0.25 + baseRand() * 0.35    // 25%-60%
      let basePass = Math.min(0.97, 0.7 + baseRand() * 0.2) // 70%-97%

      years.forEach((y, idx) => {
        const r = this.seedRandom(String(orgCode) + '-trend-year-' + y + '-' + String(idx))
        const noiseScore = (r() - 0.5) * 8 // -4 ~ 4
        const score = Math.max(60, Math.min(100, baseScore + noiseScore))
        const eNoise = (r() - 0.5) * 0.08 // -4% ~ 4%
        const pNoise = (r() - 0.5) * 0.06 // -3% ~ 3%
        const eRate = Math.min(0.9, Math.max(0.1, baseExcellent + eNoise))
        const pRate = Math.min(0.98, Math.max(0.55, basePass + pNoise))
        avgScores.push(Number(score.toFixed(1)))
        excellentRates.push(Number((eRate * 100).toFixed(1)))
        passRates.push(Number((pRate * 100).toFixed(1)))
      })

      return { years, avgScores, excellentRates, passRates }
    },
    generateDataAndRender() {
      const { metrics, childrenComparisons } = this.generateMockMetrics(this.selectedDeptNode || {}, this.selectedYear)
      this.metrics = metrics
      this.childrenComparisons = childrenComparisons
      this.yearlyTrend = this.generateYearlyTrendData(this.selectedDeptNode || {}, this.selectedYear)
      this.updateCharts()
    },
    updateCharts() {
      if (!this.scoresChart || !this.ratesChart || !this.pieChart || !this.trendChart) return

      const names = this.childrenComparisons.map(c => c.name)
      const scoreValues = this.childrenComparisons.map(c => c.avgScore)
      const excellentValues = this.childrenComparisons.map(c => Number((c.excellentRate * 100).toFixed(1)))
      const passValues = this.childrenComparisons.map(c => Number((c.passRate * 100).toFixed(1)))

      // 成绩对比图
      this.scoresChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
        yAxis: { type: 'value', name: '成绩', min: 0, max: 100 },
        series: [
          {
            name: '平均成绩',
            type: 'bar',
            data: scoreValues,
            itemStyle: { color: '#409EFF' },
            label: { show: true, position: 'top' }
          }
        ]
      })

      // 优秀率 / 合格率 对比
      this.ratesChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['优秀率', '合格率'] },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
        yAxis: { type: 'value', name: '%', min: 0, max: 100 },
        series: [
          {
            name: '优秀率',
            type: 'bar',
            data: excellentValues,
            itemStyle: { color: '#67C23A' },
            label: { show: true, position: 'top', formatter: '{c}%' }
          },
          {
            name: '合格率',
            type: 'bar',
            data: passValues,
            itemStyle: { color: '#E6A23C' },
            label: { show: true, position: 'top', formatter: '{c}%' }
          }
        ]
      })

      // 当前单位占比饼图（优秀 / 合格但不优秀 / 未合格）
      const good = Number((this.metrics.excellentRate * 100).toFixed(1))
      const passOnly = Number(((Math.max(0, this.metrics.passRate - this.metrics.excellentRate)) * 100).toFixed(1))
      const fail = Number(((1 - this.metrics.passRate) * 100).toFixed(1))
      this.pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}% ({d}%)' },
        legend: { bottom: 0 },
        series: [
          {
            name: '占比',
            type: 'pie',
            radius: ['35%', '60%'],
            avoidLabelOverlap: true,
            label: { formatter: '{b}\n{c}%' },
            data: [
              { name: '优秀', value: good, itemStyle: { color: '#67C23A' } },
              { name: '合格(非优秀)', value: passOnly, itemStyle: { color: '#E6A23C' } },
              { name: '未合格', value: fail, itemStyle: { color: '#F56C6C' } }
            ]
          }
        ]
      })

      // 趋势图（年度折线）：成绩、优秀率、合格率
      this.trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['成绩', '优秀率', '合格率'] },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: this.yearlyTrend.years },
        yAxis: [
          { type: 'value', name: '成绩', min: 0, max: 100 },
          { type: 'value', name: '%', min: 0, max: 100, position: 'right' }
        ],
        series: [
          {
            name: '成绩', type: 'line', smooth: true,
            data: this.yearlyTrend.avgScores, yAxisIndex: 0,
            itemStyle: { color: '#409EFF' }, areaStyle: { opacity: 0.08 }
          },
          {
            name: '优秀率', type: 'line', smooth: true,
            data: this.yearlyTrend.excellentRates, yAxisIndex: 1,
            itemStyle: { color: '#67C23A' }, areaStyle: { opacity: 0.06 }
          },
          {
            name: '合格率', type: 'line', smooth: true,
            data: this.yearlyTrend.passRates, yAxisIndex: 1,
            itemStyle: { color: '#E6A23C' }, areaStyle: { opacity: 0.06 }
          }
        ]
      })
    }
  }
}
</script>

<style scoped>
.chart-dashboard {
  width: 100%;
}

.dashboard-header {
  margin-bottom: 12px;
}

.title-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dashboard-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.toolbar {
  display: flex;
  align-items: center;
}

.toolbar .label {
  margin-right: 8px;
  color: #606266;
}

.description {
  margin: 8px 0 0 0;
  color: #909399;
  font-size: 13px;
}

.metric-cards {
  margin-bottom: 12px;
}

.metric-card {
  border: 1px solid #ebeef5;
}

.metric {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.metric-label {
  color: #606266;
}

.metric-value {
  font-size: 20px;
  font-weight: 600;
}

.metric .metric-item {
  display: inline-flex;
  flex-direction: column;
  margin-right: 16px;
}

.metric.triplet {
  display: flex;
  align-items: baseline;
}

.current-name {
  font-size: 16px;
  font-weight: normal;
}

.metric-value.good { color: #67C23A; }
.metric-value.pass { color: #E6A23C; }
.metric-value.fail { color: #F56C6C; }

.charts .el-card { min-height: 360px; }
.chart-title { font-weight: 500; color: #606266; margin-bottom: 8px; }
.chart-box { width: 100%; height: 300px; }
.empty-tip { text-align: center; color: #C0C4CC; padding: 20px 0; }
</style>