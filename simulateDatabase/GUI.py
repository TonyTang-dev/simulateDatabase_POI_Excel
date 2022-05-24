# -*- coding: utf-8 -*-

import wx
import win32api
import sys
import os
import wx.lib.agw.aui as aui
from wx.adv import Animation, AnimationCtrl
import glob
import fitz
import time
import xlwt, xlrd
from xlutils.copy import copy as xl_copy

# 模块
from word2pdf import doc2pdf
from globalVar import globalVar


class mainFrame(wx.Frame):
    '''程序主窗口类，继承自wx.Frame'''

    id_open = 1
    id_help = 2

    id_word2pdf = 3
    id_pdf2word = 4
    id_mergePdf = 5
    id_cutPdf = 6
    id_img2pdf = 7
    id_pdf2img = 8
    id_author = 9

    fileName = ""

    def __init__(self, parent):
        '''构造函数'''

        wx.Frame.__init__(self, parent, -1, globalVar.APP_TITLE)
        self.SetBackgroundColour(wx.Colour(224, 224, 224))
        self.SetSize((620, 400))
        self.SetMaxSize((620, 400))
        self.Center()

        if hasattr(sys, "frozen") and getattr(sys, "frozen") == "windows_exe":
            exeName = win32api.GetModuleFileName(win32api.GetModuleHandle(None))
            icon = wx.Icon(exeName, wx.BITMAP_TYPE_ICO)
        else:
            icon = wx.Icon(globalVar.APP_ICON, wx.BITMAP_TYPE_ICO)
        self.SetIcon(icon)

        self.tb1 = self._CreateToolBar('F')
        self.tb2 = self._CreateToolBar()
        # self.tbv = self._CreateToolBar('V')

        p_left = wx.Panel(self, -1)
        # p_left.SetBackgroundColour("#90d7ec")
        p_center0 = wx.Panel(self, -1)

        image_file = ''
        with open("imgPath.txt", 'r', encoding="UTF-8") as f:
            image_file = f.readline()
        if not os.path.exists(image_file):
            image_file = globalVar.bgimgPath
        to_bmp_image = wx.Image(image_file, wx.BITMAP_TYPE_ANY).ConvertToBitmap()
        self.bitmap = wx.StaticBitmap(p_center0, -1, to_bmp_image, (0, 0), (400, 200))
        globalVar.bgimg = self.bitmap

        p_center1 = wx.Panel(self, -1)
        p_bottom = wx.Panel(self, -1)
        p_center0.SetBackgroundColour("White")
        p_bottom.SetBackgroundColour("White")
        # image_file = 'res/addfile.png'
        # to_bmp_image = wx.Image(image_file, wx.BITMAP_TYPE_ANY).ConvertToBitmap()
        # self.bitmap = wx.StaticBitmap(p_bottom, -1, to_bmp_image, (0, 0), (400, 140))
        self.text_input = wx.TextCtrl(p_bottom, -1, size=(400, 150),
                                style=wx.TE_LEFT | wx.TE_NOHIDESEL | wx.TE_MULTILINE)
        # 现将字体颜色设置为浅灰色
        # self.text_input.SetDefaultStyle(wx.TextAttr(wx.LIGHT_GREY))
        # 然后添加提示文本
        # self.text_input.AppendText("请输入要执行的sql语句")
        # 最后再把颜色改为黑色即可
        self.text_input.SetDefaultStyle(wx.TextAttr(wx.BLACK))

        statusText0 = wx.StaticText(p_left, -1, globalVar.STATUS, pos=(0, 10), size=(200, 180), style=wx.ALIGN_LEFT)

        filepathText0 = wx.StaticText(p_center0, -1, "", pos=(0, 21), size=(500, -1), style=wx.ALIGN_LEFT)
        globalVar.textDetail = filepathText0

        btn = wx.Button(p_left, -1, u'执行语句', pos=(30, 200), size=(100, -1))
        btn.Bind(wx.EVT_BUTTON, self.OnSwitch)
        btn.SetBackgroundColour('#f47920')
        btn = wx.Button(p_left, -1, u'更换背景', pos=(30, 260), size=(100, -1))
        btn.Bind(wx.EVT_BUTTON, self.changeBg)
        btn.SetBackgroundColour('white')

        text0 = wx.StaticText(p_center0, -1, u'当前操作：' + globalVar.status, pos=(0, 0), size=(400, 20), style=wx.ALIGN_CENTER)
        globalVar.textStatus = text0
        text0.SetFont(wx.Font(10, wx.FONTFAMILY_ROMAN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_BOLD))
        text0.SetBackgroundColour("#f36c21")
        line = wx.StaticText(p_center0, -1, u'', pos=(0, 20), size=(400, 1), style=wx.ALIGN_CENTER)
        line.SetBackgroundColour("black")

        self._mgr = aui.AuiManager()
        self._mgr.SetManagedWindow(self)

        self._mgr.AddPane(self.tb1,
                          aui.AuiPaneInfo().Name("ToolBar1").Caption(u"工具条").ToolbarPane().Top().Row(0).Position(
                              0).Floatable(False)
                          )
        self._mgr.AddPane(self.tb2,
                          aui.AuiPaneInfo().Name("ToolBar2").Caption(u"工具条").ToolbarPane().Top().Row(0).Position(
                              1).Floatable(True)
                          )
        # self._mgr.AddPane(self.tbv, 
        # aui.AuiPaneInfo().Name("ToolBarV").Caption(u"工具条").ToolbarPane().Right().Floatable(True)
        # )

        self._mgr.AddPane(p_left,
                          aui.AuiPaneInfo().Name("LeftPanel").Left().Layer(1).MinSize((200, -1)).Caption(
                              u"操作区").MinimizeButton(True).MaximizeButton(True).CloseButton(False)
                          )

        self._mgr.AddPane(p_center0,
                          aui.AuiPaneInfo().Name("CenterPanel0").CenterPane().Show()
                          )

        self._mgr.AddPane(p_center1,
                          aui.AuiPaneInfo().Name("CenterPanel1").CenterPane().Hide()
                          )

        self._mgr.AddPane(p_bottom,
                          aui.AuiPaneInfo().Name("BottomPanel").Bottom().MinSize((-1, 100)).Caption(
                              u"消息区").CaptionVisible(False).Resizable(True)
                          )

        self._mgr.Update()


    def _CreateToolBar(self, d='H'):
        '''创建工具栏'''

        bmp_open = wx.Bitmap('res/file.png', wx.BITMAP_TYPE_ANY)
        bmp_save = wx.Bitmap('res/pdf2img.png', wx.BITMAP_TYPE_ANY)
        bmp_help = wx.Bitmap('res/trans.png', wx.BITMAP_TYPE_ANY)
        bmp_about = wx.Bitmap('res/mine2.png', wx.BITMAP_TYPE_ANY)
        bmp_trans = wx.Bitmap('res/trans3.png', wx.BITMAP_TYPE_ANY)
        bmp_trans2 = wx.Bitmap('res/trans4.png', wx.BITMAP_TYPE_ANY)
        bmp_trans3 = wx.Bitmap('res/trans5.png', wx.BITMAP_TYPE_ANY)
        bmp_trans4 = wx.Bitmap('res/img2pdf.png', wx.BITMAP_TYPE_ANY)

        if d.upper() in ['V', 'VERTICAL']:
            tb = aui.AuiToolBar(self, -1, wx.DefaultPosition, wx.DefaultSize,
                                agwStyle=aui.AUI_TB_TEXT | aui.AUI_TB_VERTICAL)
        else:
            tb = aui.AuiToolBar(self, -1, wx.DefaultPosition, wx.DefaultSize, agwStyle=aui.AUI_TB_TEXT)
        tb.SetToolBitmapSize(wx.Size(16, 16))

        if d.upper() != 'F':
            tb.AddSimpleTool(self.id_mergePdf, u'删除数据库', bmp_trans2, u'删除当前数据库')
            tb.AddSimpleTool(self.id_cutPdf, u'删除表', bmp_trans, u'删除数据库表')
            tb.AddSimpleTool(self.id_img2pdf, u'建索引', bmp_trans3, u'建立数据库索引')
            tb.AddSeparator()
            tb.AddSimpleTool(self.id_pdf2img, u'查询表', bmp_help, u'对数据库表进行查询操作')
            tb.AddSimpleTool(self.id_author, u'作者', bmp_about, u'关于作者')
            tb.Bind(wx.EVT_TOOL, self.dealFunction)
        else:
            tb.AddSimpleTool(self.id_open, u'File', bmp_open, u'打开文件')
            tb.AddSimpleTool(self.id_help, u'教程', bmp_help, u'使用教程')
            tb.AddSeparator()
            tb.AddSimpleTool(self.id_word2pdf, u'建数据库', bmp_save, u'新建一个数据库')
            tb.AddSimpleTool(self.id_pdf2word, u'建数据表', bmp_trans4, u'新建一个数据库表')

            tb.Bind(wx.EVT_TOOL, self.dealFunction)
        tb.Realize()
        return tb

    def openAuthor(self):
        globalVar.textDetail.SetLabel("作者：唐YF\n联系方式：3538182550@qq.com(邮箱)\n状态："
                                      "项目还在进一步维护中，敬请期待\n项目：本项目已开源，欢迎访问本人代码托管仓库\n"
                                      "仓库地址：\n"
                                      "gitee: https://gitee.com/TangGarlic/fileSystem.git\n"
                                      "github: https://github.com/TonyTang-dev/fileSystem.git\n"
                                      "写在最后：感谢您使用本软件，如软件有问题或您有新需求，记得联系我")
        return


    def openHelp(self):
        globalVar.textDetail.SetLabel("0、安装：将文件夹放到电脑中，为“音符文档助手.exe”建快捷方式即可\n"
                                      "1、首先在上方工具栏选择您需要进行的操作，状态栏会提示您当前状态\n"
                                      "2、若是对文件的操作，先选择文件，拖动文件到下方/点击File打开均可\n"
                                      "3、确定好文件之后点击左下角“开始输出”接口开始输出\n"
                                      "4、word转pdf功能目前需要电脑中已安装有office套件/wps\n"
                                      "5、选择功能-->选择文件-->点击转换"
                                      "注意：\n"
                                      "a. 拖动文件时可多个文件一起选中拖动到下方文件框\n"
                                      "b. 本软件不获取您的个人信息,如有卡顿指定是软件有bug，不必惊慌\n"
                                      "c. 如果您的一些操作导致软件卡死/闪退，那就是软件有问题--联系作者\n"
                                      "d. 如有疑问，请查看软件文件夹目录下的“音符文档助手使用手册.pdf”\n"
                                      "e. 如有需求或疑问请联系作者（点击“作者”可见/3538182550@qq.com）")

        return

    def dealFunction(self, event):
        index = event.GetId()
        if globalVar.operationId == 2 or globalVar.operationId == 9:
            globalVar.textDetail.SetLabel("")
            globalVar.fileList.clear()
        globalVar.operationId = index
        # id_open = 1 id_help = 2  id_word2pdf = 3 id_pdf2word = 4
        # id_mergePdf = 5 id_cutPdf = 6 id_img2pdf = 7 id_pdf2img = 8 id_author = 9
        if index == 1:
            globalVar.status = "打开本地文件"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
        elif index == 2:
            globalVar.status = "使用教程"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            self.openHelp()
        elif index == 3:
            globalVar.status = "建数据库"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.word2pdf()
        elif index == 4:
            globalVar.status = "建数据库表"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.pdf2word()
        elif index == 5:
            globalVar.status = "删除数据库"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.mergePdf()
        elif index == 6:
            globalVar.status = "删除数据库表"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.cutPdf()
        elif index == 7:
            globalVar.status = "建立索引"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.img2pdf()
        elif index == 8:
            globalVar.status = "查询数据库数据"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            # self.pdf2img()
        elif index == 9:
            globalVar.status = "关于作者"
            globalVar.textStatus.SetLabel("当前操作："+globalVar.status)
            self.openAuthor()


    def createDatabase(self, baseName):
        xls = xlwt.Workbook()
        sht1 = xls.add_sheet('系统表')
        # Font0 = xlwt.Font()
        # Font0.name = "Times New Roman"
        # Font0.colour_index = 2
        # Font0.bold = True  # 加粗
        style0 = xlwt.XFStyle()
        sht1.write(0, 0, "数据库名", style0)
        sht1.write(0, 1, baseName, style0)

        xls.save(baseName+'.xlsx')

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

    def selectTable(self, tableInfo):
        # open existing workbook
        rb = xlrd.open_workbook(globalVar.curDatabase + ".xlsx", formatting_info=True)
        # add sheet to workbook with existing sheets
        rdSht = rb.sheet_by_name(tableInfo[3])
        # 设置字体格式
        Font0 = xlwt.Font()
        Font0.name = "Times New Roman"
        Font0.colour_index = 2
        Font0.bold = True  # 加粗
        style0 = xlwt.XFStyle()

        info = ""
        rowNum = int(rdSht.cell_value(0, 1))
        filedNum = int(rdSht.cell_value(0, 3))
        for i in range(1, rowNum):
            if i==2:
                info += "- - - - - - - - -\n"
            for j in range(0, filedNum):
                info = info+(rdSht.cell_value(i, j))+" "
            info += "\n"

        globalVar.textDetail.SetLabel(info)
        return


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
            elif globalVar.sqlStatementMap[globalVar.curOperation[0]] == 3:
                self.selectTable(globalVar.curOperation)

        else:
            globalVar.status = "无效语句"
            globalVar.textStatus.SetLabel("当前操作：" + globalVar.status)

        self.text_input.Clear()
        globalVar.curOperation.clear()


    def changeBg(self, evt):
        # 打开开文件对话框
        dlg = wx.FileDialog(self, u"选择文件", style=wx.FD_OPEN)
        if dlg.ShowModal() == wx.ID_OK:
            # print(dlg.GetPath())  # 文件夹路径
            imgPath = dlg.GetPath()
            if imgPath[-3:] != "jpg" and imgPath[-3:] != "png":
                d = wx.MessageDialog(None, u"请选择jpg/png图片才行哦！", u"提示", wx.YES_NO | wx.ICON_QUESTION)
                if d.ShowModal() == wx.ID_OK:
                    d.Destroy()
                    dlg.Destroy()
                    return
            with open("imgPath.txt", "w+", encoding="UTF-8") as f:
                f.write(imgPath)

            img = wx.Image(imgPath, wx.BITMAP_TYPE_ANY).Rescale(400, 200).ConvertToBitmap()
            globalVar.bgimg.SetBitmap(wx.BitmapFromImage(img))

        dlg.Destroy()
        return


class mainApp(wx.App):
    def OnInit(self):
        self.SetAppName(globalVar.APP_TITLE)
        self.Frame = mainFrame(None)
        self.Frame.Show()
        return True


if __name__ == "__main__":
    app = mainApp()
    app.MainLoop()
