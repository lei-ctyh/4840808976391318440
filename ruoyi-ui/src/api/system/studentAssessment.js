import request from '@/utils/request'

// 查询领导班子考核列表
export function liststudentAssessment(query) {
  return request({
    url: '/system/studentAssessment/list',
    method: 'get',
    params: query
  })
}

// 根据人员ID和考核周期查询考核记录
export function getByPersonIdAndPeriod(personId, period) {
  return request({
    url: '/system/studentAssessment/getByPersonIdAndPeriod',
    method: 'get',
    params: {
      personId,
      period
    }
  })
}

// 根据单位ID和考核周期查询考核记录列表
export function getByUnitIdAndPeriod(unitId, period) {
  return request({
    url: '/system/studentAssessment/getByUnitIdAndPeriod',
    method: 'get',
    params: {
      unitId,
      period
    }
  })
}

// 导入战士成绩考核数据（multipart/form-data），支持传递 unitId、year、updateSupport
export function importData(formData, params = {}) {
  return request({
    url: '/system/studentAssessment/importData',
    method: 'post',
    data: formData,
    params: params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载导入模板
export function downloadImportTemplate() {
  return request({
    url: '/system/studentAssessment/importTemplate',
    method: 'post',
    responseType: 'blob'
  })
}
