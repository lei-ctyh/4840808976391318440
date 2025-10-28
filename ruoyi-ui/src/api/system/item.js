import request from '@/utils/request'

// 查询考试项管理列表
export function listItem(query) {
  return request({
    url: '/system/item/list',
    method: 'get',
    params: query
  })
}

// 查询考试项管理详细
export function getItem(itemId) {
  return request({
    url: '/system/item/' + itemId,
    method: 'get'
  })
}

// 新增考试项管理
export function addItem(data) {
  return request({
    url: '/system/item',
    method: 'post',
    data: data
  })
}

// 修改考试项管理
export function updateItem(data) {
  return request({
    url: '/system/item',
    method: 'put',
    data: data
  })
}

// 删除考试项管理
export function delItem(itemId) {
  return request({
    url: '/system/item/' + itemId,
    method: 'delete'
  })
}
