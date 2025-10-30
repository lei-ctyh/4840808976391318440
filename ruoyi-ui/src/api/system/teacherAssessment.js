import request from '@/utils/request'

// 查询教师考核列表
export function listTeacherAssessment(query) {
  return request({
    url: '/system/teacher-assessment/list',
    method: 'get',
    params: query
  })
}

// 根据人员ID和考核周期查询考核记录
export function getTeacherAssessmentByPersonAndPeriod(personId, period) {
  return request({
    url: `/system/teacher-assessment/person/${personId}/${period}`,
    method: 'get'
  })
}

// 根据单位ID和考核周期查询考核记录列表
export function getTeacherAssessmentByUnitAndPeriod(unitId, period) {
  return request({
    url: `/system/teacher-assessment/unit/${unitId}/${period}`,
    method: 'get'
  })
}

// 导出教师考核列表
export function exportTeacherAssessment(query) {
  return request({
    url: '/system/teacher-assessment/export',
    method: 'post',
    data: query
  })
}

// 获取教师考核导入模板
export function importTemplate() {
  return request({
    url: '/system/teacher-assessment/importTemplate',
    method: 'post'
  })
}

// 导入教师考核数据
export function importData(data) {
  return request({
    url: '/system/teacher-assessment/importData',
    method: 'post',
    data: data
  })
}