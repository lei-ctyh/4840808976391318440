# 多级降级表头配置说明

## 概述

系统现已支持多级降级的表头配置加载策略，可以根据机构层级自动查找合适的配置文件。

## 配置加载优先级

假设用户选择了机构：`某连队 (orgCode: 010101)`
- 父级机构：`某营 (orgCode: 0101)`
- 祖父级机构：`某团 (orgCode: 01)`
- 年份：`2025`
- 类型：`teacher`

### 完整的配置查找顺序

系统会按以下顺序尝试加载配置文件，找到第一个存在的配置即使用：

```
1. teacher-2025-010101.json  ← 当前机构+年份
2. teacher-010101.json       ← 当前机构（通用）
3. teacher-2025-0101.json    ← 父级机构+年份
4. teacher-0101.json         ← 父级机构（通用）
5. teacher-2025-01.json      ← 祖父级机构+年份
6. teacher-01.json           ← 祖父级机构（通用）
7. default-teacher.json      ← 默认配置（兜底）
```

## 配置文件命名规范

### 1. 带年份的配置
```
{boardType}-{year}-{orgCode}.json
```
**示例**：
- `teacher-2025-010101.json` - 2025年某连队干部成绩配置
- `student-2024-0101.json` - 2024年某营战士成绩配置

### 2. 通用配置（不限年份）
```
{boardType}-{orgCode}.json
```
**示例**：
- `teacher-01.json` - 某团干部成绩通用配置（所有年份共用）
- `leader-010101.json` - 某连队个人成绩通用配置

### 3. 默认配置
```
default-{boardType}.json
```
**示例**：
- `default-teacher.json` - 干部成绩默认配置
- `default-student.json` - 战士成绩默认配置

## 实际应用场景

### 场景1：团级统一配置
如果团级机构(orgCode: 01)希望为所有下属单位提供统一的配置：

```bash
# 创建团级通用配置
public/config/tableHeaders/teacher-01.json
```

这样，团级及其所有下属单位（营、连）都会使用这个配置，除非下属单位有自己的配置文件。

### 场景2：年度特殊配置
如果2025年度有特殊的考核项目：

```bash
# 创建2025年度配置
public/config/tableHeaders/teacher-2025-01.json
```

2025年会使用这个配置，其他年度降级到 `teacher-01.json` 或默认配置。

### 场景3：连队个性化配置
如果某个连队(orgCode: 010101)需要特殊配置：

```bash
# 创建连队专属配置
public/config/tableHeaders/teacher-010101.json
```

该连队使用自己的配置，其他连队使用营级或团级配置。

## 配置优先级示例

### 示例1：完整配置

```
public/config/tableHeaders/
├── teacher-2025-010101.json  ← 某连队2025年配置
├── teacher-010101.json       ← 某连队通用配置
├── teacher-2025-0101.json    ← 某营2025年配置
├── teacher-0101.json         ← 某营通用配置
├── teacher-01.json           ← 某团通用配置
└── default-teacher.json      ← 默认配置
```

**查询结果**：
- 用户选择`某连队`，年份`2025` → 使用 `teacher-2025-010101.json`
- 用户选择`某连队`，年份`2024` → 使用 `teacher-010101.json`
- 用户选择`某营`，年份`2025` → 使用 `teacher-2025-0101.json`
- 用户选择`某团`，年份`2025` → 使用 `teacher-01.json`

### 示例2：部分配置

```
public/config/tableHeaders/
├── teacher-0101.json         ← 某营通用配置
└── default-teacher.json      ← 默认配置
```

**查询结果**：
- 用户选择`某连队`，任意年份 → 使用 `teacher-0101.json`（向上找到营级配置）
- 用户选择`某营`，任意年份 → 使用 `teacher-0101.json`
- 用户选择`其他单位` → 使用 `default-teacher.json`

## 创建配置文件步骤

### 1. 确定配置文件名
根据需求确定：
- 看板类型：`teacher`/`student`/`leader`/`dept`
- 年份：是否限定特定年份
- 机构编码：从后端接口获取

### 2. 复制现有配置
```bash
# 复制默认配置作为模板
cp default-teacher.json teacher-0101.json
```

### 3. 修改配置内容
根据 `README.md` 中的说明修改 `columns` 字段。

### 4. 测试配置
- 打包项目：`npm run build:prod`
- 部署到服务器
- 用户选择对应机构查看效果

## 调试技巧

### 查看配置加载日志
打开浏览器控制台，可以看到配置加载的详细日志：

```
✓ 找到配置: teacher-2025-0101.json
```

或者

```
  未找到机构 010101 的配置，尝试上级机构...
  未找到机构 0101 的配置，尝试上级机构...
✓ 找到配置: teacher-01.json
```

### 清除缓存
如果修改了配置文件但未生效，可能是缓存问题：

```javascript
import { clearTableHeaderCache } from '@/utils/tableHeaderConfig'

// 清除所有配置缓存
clearTableHeaderCache()

// 刷新页面重新加载
```

## 注意事项

1. ✅ **机构编码必须一致**：配置文件名中的 orgCode 必须与数据库中的一致
2. ✅ **JSON格式正确**：确保配置文件是合法的 JSON 格式
3. ✅ **向上查找**：系统会自动向上级机构查找，无需配置所有层级
4. ⚠️ **文件位置**：配置文件必须放在 `public/config/tableHeaders/` 目录下
5. ⚠️ **文件名大小写**：Linux服务器区分大小写，保持文件名小写

## 优势

- 🚀 **灵活配置**：不同机构、不同年度可使用不同配置
- 🔄 **自动降级**：子机构没有配置时自动使用父级配置
- 💾 **性能优化**：配置自动缓存，减少重复加载
- 🛠️ **易于维护**：配置文件独立，修改后无需重新打包（直接在服务器上修改即可）

## 常见问题

**Q: 如何为所有下属单位设置统一配置？**
A: 在上级机构创建通用配置，如 `teacher-01.json`，所有下属单位会自动继承。

**Q: 某个单位需要特殊配置怎么办？**
A: 创建该单位的专属配置文件，如 `teacher-010101.json`，会优先使用。

**Q: 配置修改后不生效怎么办？**
A: 刷新浏览器清除缓存，或调用 `clearTableHeaderCache()` 方法。

**Q: 如何判断当前使用的是哪个配置文件？**
A: 打开浏览器控制台，查看日志输出，会显示实际加载的配置文件名。
