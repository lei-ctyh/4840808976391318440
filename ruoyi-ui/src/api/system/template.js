import request from '@/utils/request'

// 查询模板列表
export function listTemplate(query) {
  return request({
    url: '/system/template/list',
    method: 'get',
    params: query
  })
}

// 查询模板详细
export function getTemplate(templateId) {
  return request({
    url: '/system/template/' + templateId,
    method: 'get'
  })
}

// 新增模板
export function addTemplate(data) {
  return request({
    url: '/system/template',
    method: 'post',
    data: data
  })
}

// 修改模板
export function updateTemplate(data) {
  return request({
    url: '/system/template',
    method: 'put',
    data: data
  })
}

// 删除模板
export function delTemplate(templateId) {
  return request({
    url: '/system/template/' + templateId,
    method: 'delete'
  })
}

// 绑定模板
export function bindTemplate(data) {
  return request({
    url: '/system/template/bind',
    method: 'post',
    data: data
  })
}

// 解析模板（查找可用模板）
export function resolveTemplate(params) {
  return request({
    url: '/system/template/resolve',
    method: 'get',
    params: params
  })
}

// 检查模板是否存在
export function checkTemplate(params) {
  return request({
    url: '/system/template/exists',
    method: 'get',
    params: params
  })
}