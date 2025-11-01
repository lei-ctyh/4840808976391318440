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
      <p class="description">展示当前单位的成绩、优秀率、及格率，以及下级单位对比</p>
    </div>

    <!-- 指标卡片 -->
    <div class="metric-cards">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric">
              <div class="metric-label">综合评定</div>
              <div class="metric-value" :class="gradeLevelClass">{{ gradeLevel }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric">
              <div class="metric-label">综合成绩</div>
              <div class="metric-value">{{ metrics.avgScore.toFixed(1) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="never" class="metric-card">
            <div class="metric quadruplet">
              <div class="metric-item">
                <div class="metric-label">优秀率</div>
                <div class="metric-value excellent">{{ (metrics.excellentRate * 100).toFixed(1) }}%</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">良好率</div>
                <div class="metric-value good">{{ ((metrics.goodRate - metrics.excellentRate) * 100).toFixed(1) }}%</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">及格率</div>
                <div class="metric-value pass">{{ ((metrics.passRate - metrics.goodRate) * 100).toFixed(1) }}%</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">不及格</div>
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
            <div class="chart-title">下级单位四级评价对比</div>
            <div ref="ratesChartRef" class="chart-box"></div>
            <div v-if="!hasChildren" class="empty-tip">无下级单位数据</div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="12" style="margin-top: 12px;">
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">当前单位优秀/良好/及格/不及格占比</div>
            <div ref="pieChartRef" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="never">
            <div class="chart-title">年度趋势：成绩、优秀率、良好率、及格率、不及格率</div>
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
      metrics: { avgScore: 0, excellentRate: 0, goodRate: 0, passRate: 0 },
      childrenComparisons: [],
      scoresChart: null,
      ratesChart: null,
      pieChart: null,
      trendChart: null,
      yearlyTrend: { years: [], avgScores: [], excellentRates: [], goodRates: [], passRates: [], failRates: [] }
    }
  },
  computed: {
    currentOrgName() {
      if (!this.selectedDeptNode) return ''
      return this.selectedDeptNode.label || this.selectedDeptNode.name || this.selectedDeptNode.title || ''
    },
    hasChildren() {
      return this.childrenComparisons && this.childrenComparisons.length > 0
    },
    // 根据综合成绩计算评级
    gradeLevel() {
      const score = this.metrics.avgScore
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 60) return '及格'
      return '不及格'
    },
    // 根据评级返回对应的CSS class
    gradeLevelClass() {
      const score = this.metrics.avgScore
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 60) return 'pass'
      return 'fail'
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
          metrics: { avgScore: 0, excellentRate: 0, goodRate: 0, passRate: 0 },
          childrenComparisons: []
        }
      }
      const orgCode = node.orgCode || node.org_code || node.id || node.label || 'default'
      const rand = this.seedRandom(String(orgCode) + '-' + String(year))

      // 当前单位指标
      const avgScore = 70 + Math.floor(rand() * 30) // 70-100
      const excellentRate = 0.15 + rand() * 0.25 // 15%-40% 优秀
      const goodRate = excellentRate + 0.2 + rand() * 0.25 // 良好（优秀基础上+20%-45%）
      const passRate = Math.min(0.95, goodRate + 0.15 + rand() * 0.2) // 及格（良好基础上+15%-35%）

      // 下级单位对比数据（最多取前8个孩子）
      const children = Array.isArray(node.children) ? node.children.slice(0, 8) : []
      const childrenComparisons = children.map((c, idx) => {
        const r = this.seedRandom(String(orgCode) + '-' + String(year) + '-' + String(idx))
        const cAvg = 65 + Math.floor(r() * 35)
        const cExcellent = 0.1 + r() * 0.3
        const cGood = cExcellent + 0.15 + r() * 0.3
        const cPass = Math.min(0.97, cGood + 0.1 + r() * 0.25)
        return {
          name: c.label || c.name || c.title || `下级单位${idx + 1}`,
          avgScore: cAvg,
          excellentRate: cExcellent,
          goodRate: cGood,
          passRate: cPass
        }
      })

      return {
        metrics: { avgScore, excellentRate, goodRate, passRate },
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
      const goodRates = []
      const passRates = []
      const failRates = []

      // 基线受组织与年度影响，确保不同单位/年度可复现
      const baseRand = this.seedRandom(String(orgCode) + '-trend-year-base-' + String(year))
      let baseScore = 70 + Math.floor(baseRand() * 20) // 70-90
      let baseExcellent = 0.15 + baseRand() * 0.25    // 15%-40%
      let baseGood = baseExcellent + 0.2 + baseRand() * 0.25  // 良好
      let basePass = Math.min(0.97, baseGood + 0.15 + baseRand() * 0.2) // 及格

      years.forEach((y, idx) => {
        const r = this.seedRandom(String(orgCode) + '-trend-year-' + y + '-' + String(idx))
        const noiseScore = (r() - 0.5) * 8 // -4 ~ 4
        const score = Math.max(60, Math.min(100, baseScore + noiseScore))
        const eNoise = (r() - 0.5) * 0.08 // -4% ~ 4%
        const gNoise = (r() - 0.5) * 0.08 // -4% ~ 4%
        const pNoise = (r() - 0.5) * 0.06 // -3% ~ 3%
        const eRate = Math.min(0.9, Math.max(0.1, baseExcellent + eNoise))
        const gRate = Math.min(0.95, Math.max(eRate + 0.1, baseGood + gNoise))
        const pRate = Math.min(0.98, Math.max(gRate + 0.05, basePass + pNoise))
        const fRate = 1 - pRate
        avgScores.push(Number(score.toFixed(1)))
        excellentRates.push(Number((eRate * 100).toFixed(1)))
        goodRates.push(Number((gRate * 100).toFixed(1)))
        passRates.push(Number((pRate * 100).toFixed(1)))
        failRates.push(Number((fRate * 100).toFixed(1)))
      })

      return { years, avgScores, excellentRates, goodRates, passRates, failRates }
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
      const goodValues = this.childrenComparisons.map(c => Number((c.goodRate * 100).toFixed(1)))
      const passValues = this.childrenComparisons.map(c => Number((c.passRate * 100).toFixed(1)))
      const failValues = this.childrenComparisons.map(c => Number(((1 - c.passRate) * 100).toFixed(1)))

      // 成绩对比图
      this.scoresChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(64, 158, 255, 0.1)'
            }
          }
        },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
        yAxis: { type: 'value', name: '成绩', min: 0, max: 100 },
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        series: [
          {
            name: '平均成绩',
            type: 'bar',
            data: scoreValues,
            itemStyle: {
              color: '#409EFF',
              borderRadius: [4, 4, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: '#66B1FF',
                shadowBlur: 10,
                shadowColor: 'rgba(64, 158, 255, 0.5)'
              }
            },
            label: { show: true, position: 'top' }
          }
        ]
      })

      // 四级评价对比
      this.ratesChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 0, 0, 0.1)'
            }
          }
        },
        legend: { data: ['优秀率', '良好率', '及格率', '不及格率'] },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
        yAxis: { type: 'value', name: '%', min: 0, max: 100 },
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        animationDelay: (idx) => idx * 50,
        series: [
          {
            name: '优秀率',
            type: 'bar',
            data: excellentValues,
            itemStyle: {
              color: '#67C23A',
              borderRadius: [4, 4, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: '#85CE61',
                shadowBlur: 10,
                shadowColor: 'rgba(103, 194, 58, 0.5)'
              }
            },
            label: { show: true, position: 'top', formatter: '{c}%' }
          },
          {
            name: '良好率',
            type: 'bar',
            data: goodValues,
            itemStyle: {
              color: '#95D475',
              borderRadius: [4, 4, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: '#A8E28A',
                shadowBlur: 10,
                shadowColor: 'rgba(149, 212, 117, 0.5)'
              }
            },
            label: { show: true, position: 'top', formatter: '{c}%' }
          },
          {
            name: '及格率',
            type: 'bar',
            data: passValues,
            itemStyle: {
              color: '#E6A23C',
              borderRadius: [4, 4, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: '#EBB563',
                shadowBlur: 10,
                shadowColor: 'rgba(230, 162, 60, 0.5)'
              }
            },
            label: { show: true, position: 'top', formatter: '{c}%' }
          },
          {
            name: '不及格率',
            type: 'bar',
            data: failValues,
            itemStyle: {
              color: '#F56C6C',
              borderRadius: [4, 4, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: '#F78989',
                shadowBlur: 10,
                shadowColor: 'rgba(245, 108, 108, 0.5)'
              }
            },
            label: { show: true, position: 'top', formatter: '{c}%' }
          }
        ]
      })

      // 当前单位占比饼图（优秀 / 良好 / 及格 / 不及格）
      const excellent = Number((this.metrics.excellentRate * 100).toFixed(1))
      const good = Number(((this.metrics.goodRate - this.metrics.excellentRate) * 100).toFixed(1))
      const pass = Number(((this.metrics.passRate - this.metrics.goodRate) * 100).toFixed(1))
      const fail = Number(((1 - this.metrics.passRate) * 100).toFixed(1))
      this.pieChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}% ({d}%)',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#333',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          bottom: 0,
          itemGap: 15,
          textStyle: {
            fontSize: 12
          }
        },
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        series: [
          {
            name: '占比',
            type: 'pie',
            radius: ['35%', '60%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              formatter: '{b}\n{c}%',
              fontSize: 12
            },
            emphasis: {
              scale: true,
              scaleSize: 10,
              itemStyle: {
                shadowBlur: 20,
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              },
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: [
              { name: '优秀', value: excellent, itemStyle: { color: '#67C23A' } },
              { name: '良好', value: good, itemStyle: { color: '#95D475' } },
              { name: '及格', value: pass, itemStyle: { color: '#E6A23C' } },
              { name: '不及格', value: fail, itemStyle: { color: '#F56C6C' } }
            ]
          }
        ]
      })

      // 趋势图（年度折线）：成绩、优秀率、良好率、及格率、不及格率
      this.trendChart.setOption({
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#333',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            },
            lineStyle: {
              type: 'dashed'
            }
          }
        },
        legend: {
          data: ['成绩', '优秀率', '良好率', '及格率', '不及格率'],
          itemGap: 15,
          textStyle: {
            fontSize: 12
          }
        },
        grid: { left: 40, right: 40, top: 40, bottom: 40 },
        xAxis: {
          type: 'category',
          data: this.yearlyTrend.years,
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: '#999'
            }
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '成绩',
            min: 0,
            max: 100,
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            }
          },
          {
            type: 'value',
            name: '%',
            min: 0,
            max: 100,
            position: 'right',
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            }
          }
        ],
        animation: true,
        animationDuration: 1500,
        animationEasing: 'cubicOut',
        series: [
          {
            name: '成绩',
            type: 'line',
            smooth: true,
            data: this.yearlyTrend.avgScores,
            yAxisIndex: 0,
            itemStyle: { color: '#409EFF' },
            lineStyle: {
              width: 3
            },
            emphasis: {
              focus: 'series',
              lineStyle: {
                width: 4
              },
              itemStyle: {
                borderWidth: 3,
                borderColor: '#409EFF',
                shadowBlur: 10,
                shadowColor: 'rgba(64, 158, 255, 0.5)'
              }
            },
            areaStyle: {
              opacity: 0.1,
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                ]
              }
            },
            symbolSize: 8,
            symbol: 'circle'
          },
          {
            name: '优秀率',
            type: 'line',
            smooth: true,
            data: this.yearlyTrend.excellentRates,
            yAxisIndex: 1,
            itemStyle: { color: '#67C23A' },
            lineStyle: {
              width: 3
            },
            emphasis: {
              focus: 'series',
              lineStyle: {
                width: 4
              },
              itemStyle: {
                borderWidth: 3,
                borderColor: '#67C23A',
                shadowBlur: 10,
                shadowColor: 'rgba(103, 194, 58, 0.5)'
              }
            },
            areaStyle: {
              opacity: 0.1,
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                  { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
                ]
              }
            },
            symbolSize: 8,
            symbol: 'circle'
          },
          {
            name: '良好率',
            type: 'line',
            smooth: true,
            data: this.yearlyTrend.goodRates,
            yAxisIndex: 1,
            itemStyle: { color: '#95D475' },
            lineStyle: {
              width: 3
            },
            emphasis: {
              focus: 'series',
              lineStyle: {
                width: 4
              },
              itemStyle: {
                borderWidth: 3,
                borderColor: '#95D475',
                shadowBlur: 10,
                shadowColor: 'rgba(149, 212, 117, 0.5)'
              }
            },
            areaStyle: {
              opacity: 0.1,
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(149, 212, 117, 0.3)' },
                  { offset: 1, color: 'rgba(149, 212, 117, 0.05)' }
                ]
              }
            },
            symbolSize: 8,
            symbol: 'circle'
          },
          {
            name: '及格率',
            type: 'line',
            smooth: true,
            data: this.yearlyTrend.passRates,
            yAxisIndex: 1,
            itemStyle: { color: '#E6A23C' },
            lineStyle: {
              width: 3
            },
            emphasis: {
              focus: 'series',
              lineStyle: {
                width: 4
              },
              itemStyle: {
                borderWidth: 3,
                borderColor: '#E6A23C',
                shadowBlur: 10,
                shadowColor: 'rgba(230, 162, 60, 0.5)'
              }
            },
            areaStyle: {
              opacity: 0.1,
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
                  { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
                ]
              }
            },
            symbolSize: 8,
            symbol: 'circle'
          },
          {
            name: '不及格率',
            type: 'line',
            smooth: true,
            data: this.yearlyTrend.failRates,
            yAxisIndex: 1,
            itemStyle: { color: '#F56C6C' },
            lineStyle: {
              width: 3
            },
            emphasis: {
              focus: 'series',
              lineStyle: {
                width: 4
              },
              itemStyle: {
                borderWidth: 3,
                borderColor: '#F56C6C',
                shadowBlur: 10,
                shadowColor: 'rgba(245, 108, 108, 0.5)'
              }
            },
            areaStyle: {
              opacity: 0.1,
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
                  { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
                ]
              }
            },
            symbolSize: 8,
            symbol: 'circle'
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
  height: 100%;
}

.metric-card ::v-deep .el-card__body {
  height: 100%;
  min-height: 80px;
  display: flex;
  align-items: center;
}

.metric {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
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

.metric.quadruplet {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}

.metric.quadruplet .metric-item {
  margin-right: 0;
  flex: 0 0 auto;
}

.current-name {
  font-size: 16px;
  font-weight: normal;
}

.metric-value.excellent { color: #67C23A; }
.metric-value.good { color: #95D475; }
.metric-value.pass { color: #E6A23C; }
.metric-value.fail { color: #F56C6C; }

.charts .el-card { min-height: 360px; }
.chart-title { font-weight: 500; color: #606266; margin-bottom: 8px; }
.chart-box { width: 100%; height: 300px; }
.empty-tip { text-align: center; color: #C0C4CC; padding: 20px 0; }
</style>