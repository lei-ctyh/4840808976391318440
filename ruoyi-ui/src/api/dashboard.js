import request from '@/utils/request'

// 获取领导班子考核看板数据 - 模拟数据实现
export function getLeaderAssessmentData(year, unitId = null) {
  // 返回模拟的领导班子考核数据
  const mockData = [
    {
      personId: '001',
      personName: '张三',
      unitId: '0001',
      unitName: '领导班子/校长办公室/办公室',
      birthDate: '1975-03-15',
      age: 49,
      title: '校长',
      period: year,
      // 基础科目 20%
      basicKnowledge: '85',
      sportsTrack: '90',
      sportsRope: '88',
      sportsJump: '92',
      baseGroupA: '87',
      baseGroupB: '89',
      baseTotal: '88.2',
      // 共同科目 30%
      commonSubject1: '90',
      commonSubject2: '88',
      commonSubject3: '92',
      commonSubject4: '85',
      commonSubject5: '89',
      commonSubject6: '91',
      commonSubject7: '87',
      commonSubject8: '93',
      commonTotal: '89.4',
      // 岗位业务 50%
      jobBusiness: '92',
      // 综合成绩
      comprehensivePercent: '90.2',
      comprehensiveLevel: '优秀',
      remark: '表现优异',
      description: '各项指标均达到优秀标准'
    },
    {
      personId: '002',
      personName: '李四',
      unitId: '0002',
      unitName: '领导班子/副校长办公室/教务处',
      birthDate: '1978-07-22',
      age: 46,
      title: '副校长',
      period: year,
      // 基础科目 20%
      basicKnowledge: '82',
      sportsTrack: '85',
      sportsRope: '83',
      sportsJump: '87',
      baseGroupA: '84',
      baseGroupB: '86',
      baseTotal: '84.5',
      // 共同科目 30%
      commonSubject1: '87',
      commonSubject2: '85',
      commonSubject3: '89',
      commonSubject4: '82',
      commonSubject5: '86',
      commonSubject6: '88',
      commonSubject7: '84',
      commonSubject8: '90',
      commonTotal: '86.4',
      // 岗位业务 50%
      jobBusiness: '88',
      // 综合成绩
      comprehensivePercent: '86.8',
      comprehensiveLevel: '良好',
      remark: '工作认真负责',
      description: '在教学管理方面表现突出'
    },
    {
      personId: '003',
      personName: '王五',
      unitId: '0003',
      unitName: '领导班子/党委办公室/组织部',
      birthDate: '1980-11-08',
      age: 44,
      title: '党委书记',
      period: year,
      // 基础科目 20%
      basicKnowledge: '88',
      sportsTrack: '92',
      sportsRope: '90',
      sportsJump: '94',
      baseGroupA: '89',
      baseGroupB: '91',
      baseTotal: '90.7',
      // 共同科目 30%
      commonSubject1: '93',
      commonSubject2: '91',
      commonSubject3: '95',
      commonSubject4: '88',
      commonSubject5: '92',
      commonSubject6: '94',
      commonSubject7: '90',
      commonSubject8: '96',
      commonTotal: '92.4',
      // 岗位业务 50%
      jobBusiness: '95',
      // 综合成绩
      comprehensivePercent: '93.1',
      comprehensiveLevel: '优秀',
      remark: '党建工作突出',
      description: '在组织建设和思想政治工作方面成绩显著'
    }
  ]
  
  return mockData
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
    url: '/system/studentAssessment/dashboard',
    method: 'get',
    params: {
      year,
      unitId
    }
  })
}

// 获取组织考核统计数据
export function getOrgAssessmentStats(year, unitId = null) {
  return request({
    url: '/system/assessment/orgStats',
    method: 'get',
    params: {
      year,
      unitId
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