import request from '@/utils/request'

// 查询领导班子考核列表
export function listLeaderAssessment(query) {
  return request({
    url: '/system/leaderAssessment/list',
    method: 'get',
    params: query
  })
}

// 根据人员ID和考核周期查询考核记录
export function getByPersonIdAndPeriod(personId, period) {
  return request({
    url: '/system/leaderAssessment/getByPersonIdAndPeriod',
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
    url: '/system/leaderAssessment/getByUnitIdAndPeriod',
    method: 'get',
    params: {
      unitId,
      period
    }
  })
}

// 导入领导班子考核数据
export function importData(data) {
  return request({
    url: '/system/leaderAssessment/importData',
    method: 'post',
    data: data
  })
}

// 下载导入模板
export function downloadImportTemplate() {
  return request({
    url: '/system/leaderAssessment/importTemplate',
    method: 'post',
    responseType: 'blob'
  })
}