---
模拟数据库的python实现
---
[toc]
># ==选题综述==
模拟数据库设计，主要完成需求分析、概念设计与逻辑设计**


## 课题要求
>>**项目要求**：自由一组，经阅读文献了解知识后，设计需求，设计数据库，==根据项目实际描述进行分析、设计==
>>**课题要求**：
（1）设计方案要合理；
（2）能基于该方案完成系统要求的功能；
（3）设计方案有一定的合理性分析。

# 一、工具依赖

1. pycharm

2. wps office

# 二、技术依赖

1. python
2. xlrt和xlrd python库

# 三、分析实现
## 0. 问题分析
- 需要实现数据库创建、创建表、数据库表增删改查、建立索引并比较效果等
## 1、数据库设计实现
### 1.0 初步分析设计
>根据如上分析，需要设计至少如下表，得出如下结论

1. 将数据库转换为.xlsx存储
2. 将数据库表转换为sheet存储
3. 每个数据库都有一个“系统表”，用于存储数据库基本信息
4. 每个数据库表sheet，第一行存储数据库表项数+字段数---->便于增删改查操作

![image-20220523135740050](E:\Tang_programe\Python_learn\pycharm\simulateDatabase\callImg\README.asset\image-20220523135740050.png)



**当前缺陷：**

1. 时间原因，暂时只实现：创建数据库、选择数据库、添加数据库表、插入数据、删除数据

## 2.系统逻辑设计
### 2.1 实现语句

实现通过识别sql语句实现，实现如下语句：

1. create database [database_name] 创建数据库，例：

   ```mysql
   create database test
   ```

   

2. use [database_name] 使用数据库，例如：

   ```mysql
   use test
   ```

   

3. create table [table_name] (values) 创建数据库表,例如:

   ```mysql
   create table test
   ```

   

4. delete from [table_name] {where [condition]} 删除数据库表数据，包括删除整个表和选定条目删除，例如：

   ```mysql
   delete from test          /     delete from test where a=1
   ```

   

5. insert into [table_name] (values) 向数据库表插入数据，插入数目与字段数一致，例如：

   ```mysql
   insert into test (1,2,3,4,5,6)
   ```

   

# 四、技术实现
## 1. 综述

## 2. 实现效果

![image-20220523135915022](E:\Tang_programe\Python_learn\pycharm\simulateDatabase\callImg\README.asset\image-20220523135915022.png)

![image-20220523135939254](E:\Tang_programe\Python_learn\pycharm\simulateDatabase\callImg\README.asset\image-20220523135939254.png)

# 五、问题综述

1. 时间原因，暂时只实现：创建数据库、选择数据库、添加数据库表、插入数据、删除数据

# 六、项目总结

暂无

# 七、参考文献

暂无

# 附录

附录一：初始化数据库所用脚本

```python
    def createTable(self, tableInfo):
        # open existing workbook
        rb = xlrd.open_workbook(globalVar.curDatabase+".xlsx", formatting_info=True)
        # make a copy of it
        wb = xl_copy(rb)
        # add sheet to workbook with existing sheets
        sht1 = wb.add_sheet(tableInfo[2])
        # 设置字体格式
        Font0 = xlwt.Font()
        Font0.name = "Times New Roman"
        Font0.colour_index = 2
        Font0.bold = True  # 加粗
        style0 = xlwt.XFStyle()

        sht1.write(0, 0, "当前表数据项数目", style0)         # 存储当前表的项数
        sht1.write(0, 1, 2, style0)  # 存储当前表的项数
        sht1.write(0, 2, "当前表数据字段数目", style0)         # 存储当前表的项数
        sht1.write(0, 3, len(tableInfo[3][1:-1].split(',')), style0)  # 存储当前表的项数

        column = 0
        for i in tableInfo[3][1:-1].split(','):
            # 添加字段
            sht1.write(1, column, i, style0)
            column += 1
        wb.save(globalVar.curDatabase+".xlsx")

    # insert into table
    def insertTableItem(self, tableInfo):
        # open existing workbook
        rb = xlrd.open_workbook(globalVar.curDatabase + ".xlsx", formatting_info=True)
        # make a copy of it
        wb = xl_copy(rb)
        # add sheet to workbook with existing sheets
        sht1 = wb.get_sheet(tableInfo[2])
        rdSht = rb.sheet_by_name(tableInfo[2])
        # 设置字体格式
        Font0 = xlwt.Font()
        Font0.name = "Times New Roman"
        Font0.colour_index = 2
        Font0.bold = True  # 加粗
        style0 = xlwt.XFStyle()

        # 从表中修改当前行数
        sht1.write(0, 1, rdSht.cell_value(0, 1)+1, style0)
        rowNum = int(rdSht.cell_value(0, 1))

        column = 0
        for i in tableInfo[3][1:-1].split(','):
            # 添加字段
            sht1.write(rowNum, column, i, style0)
            column += 1
        wb.save(globalVar.curDatabase + ".xlsx")

    def deleteTable(self, tableInfo, func):
        # open existing workbook
        rb = xlrd.open_workbook(globalVar.curDatabase + ".xlsx", formatting_info=True)
        # make a copy of it
        wb = xl_copy(rb)
        # add sheet to workbook with existing sheets
        sht1 = wb.get_sheet(tableInfo[2])
        rdSht = rb.sheet_by_name(tableInfo[2])
        # 设置字体格式
        Font0 = xlwt.Font()
        Font0.name = "Times New Roman"
        Font0.colour_index = 2
        Font0.bold = True  # 加粗
        style0 = xlwt.XFStyle()

        if func == 1:
            # 从表中修改当前行数
            filedNum = int(rdSht.cell_value(0, 3))
            rowNum = int(rdSht.cell_value(0, 1))
            sht1.write(0, 1, 2, style0)

            for i in range(2, rowNum):
                # 添加字段
                for j in range(0, filedNum):
                    sht1.write(i, j, '', style0)
        else:
            # 从表中修改当前行数
            rowNum = int(rdSht.cell_value(0, 1))
            sht1.write(0, 1, rowNum-1, style0)

            filedNum = int(rdSht.cell_value(0, 3))

            filedColumn = -1
            for index in range(0, filedNum):
                if str(tableInfo[4].split('=')[0]) == str(rdSht.cell_value(1, index)):
                    filedColumn = index

            if filedColumn == -1:
                globalVar.status = "无效语句"
                globalVar.textStatus.SetLabel("当前操作：" + globalVar.status)
                return

            isMove = 0
            for i in range(2, rowNum):
                # 添加字段
                if str(tableInfo[4].split('=')[1]) == str(rdSht.cell_value(i, filedColumn)):
                    isMove = 1
                if isMove == 1:
                    if i == rowNum-1:
                        for j in range(0, filedNum):
                            sht1.write(i, j, '', style0)
                        break
                    for j in range(0, filedNum):
                        sht1.write(i, j, rdSht.cell_value(i+1, j), style0)
        wb.save(globalVar.curDatabase + ".xlsx")

    def OnSwitch(self, evt):
        print(self.text_input.GetValue())

        globalVar.curOperation = self.text_input.GetValue().split()

        if globalVar.curOperation[0] in globalVar.sqlStatement:
            # create
            if globalVar.sqlStatementMap[globalVar.curOperation[0]] == 0:
                if globalVar.curOperation[1] == "database":
                    self.createDatabase(globalVar.curOperation[2])
                elif globalVar.curOperation[1] == "table":
                    self.createTable(globalVar.curOperation)
            # insert
            elif globalVar.sqlStatementMap[globalVar.curOperation[0]] == 4:
                self.insertTableItem(globalVar.curOperation)
            # use
            elif globalVar.sqlStatementMap[globalVar.curOperation[0]] == 5:
                globalVar.curDatabase = globalVar.curOperation[1]
                globalVar.status = globalVar.curOperation[1]
                globalVar.textStatus.SetLabel("当前数据库："+globalVar.status)
            elif globalVar.sqlStatementMap[globalVar.curOperation[0]] == 1:
                if len(globalVar.curOperation) == 3:
                    self.deleteTable(globalVar.curOperation, 1)
                elif len(globalVar.curOperation) == 5:
                    self.deleteTable(globalVar.curOperation, 2)

        else:
            globalVar.status = "无效语句"
            globalVar.textStatus.SetLabel("当前操作：" + globalVar.status)

        self.text_input.Clear()
        globalVar.curOperation.clear()
```

