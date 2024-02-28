### 2024.2.15
1. 更新计划 ✔
2. 使用某个计划 ✔
### 2.16
1. index 页面展示某个计划的详细信息 ✔
2. plan-detail 更新操作时，一旦新增一个计划项，按钮就会从“更新”-》"添加" --> 完成 ✔
- 主要是因为plan-detail页面中的isAdd字段被重复使用了，一个是该页面是更新页面还是添加页面；另一个字段是更新计划项还是修改更新项
### 2.28
1. 给项目添加缓存，减少数据库的压力


#### 添加缓存
1. 针对于 planObjectDto
- 当获取计划项时添加缓存，并在方法开始时尝试获取缓存数据，方法结束后添加缓存 ✔
- 当计划发生修改时删除缓存 ✔
- 当计划发生删除时删除缓存 ✔

出现的问题：redis 自动转换对象com.alibaba.fastjson.JSONObject cannot be cast to icu.shiyixi.dailybackend.dto.plan.PlanObjectDto
原因在于：fastJSON 无法自动将 json 字符串自动转换为 Java 对象
解决办法：
- 设置 redis 的序列化器为 java 默认的
- 手动在存储和获取 redis 数据时对 java 对象处理

2. 针对于 planlist
- 获取计划列表时添加 ✔
- 删除计划时清除缓存 ✔
- 添加计划时清除缓存 ✔
- 使用计划时清除缓存 ✔
- 更新计划时清除缓存

3. 针对于 planDetails
- 获取时添加 ✔
- 开始计划时清除缓存 ✔

4. 针对 planHistory 
- 获取时添加 ✔
- 打卡时清除缓存 ✔