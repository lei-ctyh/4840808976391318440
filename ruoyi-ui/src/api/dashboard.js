import request from '@/utils/request'

// 获取领导班子考核看板数据 - 调用后端API（支持分页）
export function getLeaderAssessmentData(year, unitId = null, pageNum = 1, pageSize = 10) {
  return request({
    url: '/system/leaderAssessment/list',
    method: 'get',
    params: {
      period: year,
      unitId: unitId,
      pageNum,
      pageSize
    }
  })
}

// 获取教师考核看板数据（支持分页）
export function getTeacherAssessmentData(year, unitId = null, pageNum = 1, pageSize = 10) {
  return request({
    url: '/system/teacher-assessment/list',
    method: 'get',
    params: {
      period: year,
      unitId: unitId,
      pageNum,
      pageSize
    }
  })
}

// 获取学生考核看板数据（支持分页，参数名统一为 period）
export function getStudentAssessmentData(year, unitId = null, pageNum = 1, pageSize = 10) {
  return request({
    url: '/system/studentAssessment/list',
    method: 'get',
    params: {
      period: year,
      unitId,
      pageNum,
      pageSize
    }
  })
}

// 获取单位考核看板数据（支持分页）
export function getDeptAssessmentData(year, unitId = null, pageNum = 1, pageSize = 10) {
  return request({
    url: '/system/deptAssessment/list',
    method: 'get',
    params: {
      period: year,
      unitId: unitId,
      pageNum,
      pageSize
    }
  })
}

// 获取考核数据图表统计
export function getAssessmentChartData(year, unitId = null, type = 'all') {
  return request({
    url: '/system/assessment/chartData',
    method: 'get',
    params: {
      year,
      unitId,
      type
    }
  })
}

// 获取看板统计数据（新接口）
export function getDashboardStatistics(orgCode, year) {
  return request({
    url: '/system/assessment/dashboard/statistics',
    method: 'get',
    params: {
      orgCode,
      year
    }
  })
}
