import request from '@/utils/request'

// 获取领导班子考核看板数据 - 调用后端API
export function getLeaderAssessmentData(year, unitId = null) {
  return request({
    url: '/system/leaderAssessment/list',
    method: 'get',
    params: {
      period: year,
      unitId: unitId
    }
  })
}

// 获取教师考核看板数据
export function getTeacherAssessmentData(year, unitId = null) {
  return request({
    url: '/system/teacherAssessment/dashboard',
    method: 'get',
    params: {
      year,
      unitId
    }
  })
}

// 获取学生考核看板数据
export function getStudentAssessmentData(year, unitId = null) {
  return request({
    url: '/system/studentAssessment/list',
    method: 'get',
    params: {
      year,
      unitId
    }
  })
}

// 获取组织考核统计数据
export function getDeptAssessmentData(year, unitId = null) {
  return request({
    url: '/system/deptAssessment/list',
    method: 'get',
    params: {
      period: year,
      unitId: unitId
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
