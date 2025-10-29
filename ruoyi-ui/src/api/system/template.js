import request from '@/utils/request'

// 绑定模板
export function bindTemplate(data) {
  return request({
    url: '/sms/template',
    method: 'post',
    data: data
  })
}

// 解析模板（向上查找父机构）
export function resolveTemplate(orgCode, boardType, year) {
  return request({
    url: '/sms/template/resolve',
    method: 'get',
    params: {
      orgCode,
      boardType,
      year
    }
  })
}

// 检查模板是否存在
export function checkTemplate(orgCode, boardType, year) {
  return request({
    url: '/sms/template/exists',
    method: 'get',
    params: {
      orgCode,
      boardType,
      year
    }
  })
}