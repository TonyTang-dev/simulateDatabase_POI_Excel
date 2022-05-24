/* 
	全局变量
 */

//男女数量
var sexList=[          // 数据数组，name 为数据项名称，value 为数据项值
			{value:0, name:'数据库'},
			{value:0, name:'数据表'},
		];
var roleNameList=[];
var roleCountList=[0,0];

/* sql语句框 */
var $sqlInput = $("#operation-input");
// 操作效果
var $sqlResult = $("#opeResult");
// 上一操作
var $lastOpe = $("#lastOpe");

/* 操作记录表 */
$table = $("#opeTab");
// var opeList = [
// 	{
// 		"opeID":1,
// 		"opeSql":"create database sys",
// 		"opeTime":"10ms"
// 	}
// ];

// 操作ID
var opeCountID = -1;
// 待添加语句
var waitAppendSql = null;


/* 将语句发送到后端 */
function sendSql(sql){
	$.ajax({
		method: "POST",
		url:'/simulateDatabase_mvc/operateDatabaseController.do',
		timeout: 50000,
		context:this,
		data:{
		    operation: sql
		},
		success:function(data,textStatus){
		    if(data != "undefined" && data !="0" && data!=null){
				$sqlResult.text("操作成功");
		    }
		    else{
				$sqlResult.text("操作失败");
		    }
		},
		error:function(data){
			 window.location.href="/simulateDatabase_mvc/show404.html";
		},
		complete:function(){
		    $sqlInput.val("");  //无论成功与否，都需要再次将输入框清空，原因是采用回车出发的情况下，是识别不到
		                        //回车的，也就是说输入框会保留回车，因此会对下一次操作产生影响
		                        //而每次执行之后，再次清空，确保了输入框不会残留冗余数据，保证操作的正确性
		}
	});
}

/* 执行sql语句函数 */
function excuteSql(){
	var x;
	if(window.event) // IE8 及更早IE版本
	{
		x=event.keyCode;
	}
	else if(event.which) // IE9/Firefox/Chrome/Opera/Safari
	{
		x=event.which;
	}
	if(x == 13){
        var operation = $sqlInput.val();
		if (operation != null && operation != ""){
			sendSql(operation);
		}
		else{
			Toast("请先输入正确的Sql语句再执行");
			return;
		}
		$lastOpe.text(operation);
		$sqlInput.val("");
		waitAppendSql={
			"opeID":opeCountID+1,
			"opeSql":operation,
			"opeTime":Math.random()*100+"ms"
		};
		$table.bootstrapTable('prepend', waitAppendSql);
		opeCountID += 1;
	}
}

/* 
	类似于构造函数，自动执行，基于jquery实现，包括函数绑定和界面初始化
 */
$(function($){
	/* 执行sql */
    $("#executeSql").click(function(){
        var operation = $sqlInput.val();
        if (operation != null && operation != ""){
        	sendSql(operation);
        }
        else{
			Toast("请先输入正确的Sql语句再执行");
        	return;
        }
		$sqlInput.val("");
		$lastOpe.text(operation);
		waitAppendSql={
			"opeID":opeCountID+1,
			"opeSql":operation,
			"opeTime":Math.random()*100+"ms"
		};
		$table.bootstrapTable('prepend', waitAppendSql);
		opeCountID += 1;
    });
	/* 清空 */
	$("#resetSql").click(function(){
		$sqlInput.val("");
	});
	/* 回退 */
	$("#rollbackSql").click(function(){
		$sqlInput.val($lastOpe.text())
	});

	// 初始化选中状态，首先选中控制台
	$("#console").css("background-color","#ff5500");
	
	// 点击个人头像的弹出提示
	$("#head-photo").click(function(){
		Toast("点击了个人头像");
	});
	
	/* 
		图形可视化，将用户信息基于图标展现---基于echarts.js
	 */
	// 基于准备好的dom，初始化echarts实例
	var myChart = echarts.init($('#pieGraph')[0]);

	// 指定图表的配置项和数据
	var option = {
		title: {
			text: '数据库/表统计'
		},
		color: ["#ff5500","#0055ff"],
		tooltip: {},
		legend: {
			data:['数据库']
		},
		series : [
			{
				name: '库/表',
				type: 'pie',    // 设置图表类型为饼图
				radius: '55%',  // 饼图的半径，外半径为可视区尺寸（容器高宽中较小一项）的 55% 长度。
				data: sexList
			}
		]
	};
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
	
	
	// 基于准备好的dom，初始化echarts实例----学校分布
	var barChart = echarts.init($("#barGraph")[0]);
	// 指定图表的配置项和数据
	var optionBar = {
		title: {
			text: '数据库表分布情况'
		},
		color: ["#ff5500","#55ff00","#0000ff"],
		tooltip: {},
		legend: {
			data:['表数目']
		},
		xAxis: {
			data: ['test','sys']//schoolNameList
		},
		yAxis: {},
		series: [{
			name: '数据库',
			type: 'bar',
			data: roleCountList
		}]
	};
	// 使用刚指定的配置项和数据显示图表。
	barChart.setOption(optionBar);

	
	/* 
		解析url，获取登录状态
	*/
	var url = decodeURI(window.location.href);
	var userName="";
	var pwd="";
	try{
		// 如果能够获得链接数据，则代表用户是登录过来的
		var params = url .split("?name=")[1].split("pwd=");
		userName=params[0];
		pwd=params[1];
		
		$(".nick").text("昵称："+userName);
		
		$("#login").text("退出登录");
	}catch(exception){
		// 获取不到链接数据，说明用户是游客登录进来的
		userName="";
		pwd="";
	}
	
	/* 
		初始化表格属性
	 */
	$table.bootstrapTable({
		pagination: true,
		striped:true,
		uniqueId:"opeID",

		columns:[
					{
						field:'select',
						checkbox:true,
						align:'center',
						valign:"middle"
					},{
						title:"操作编号",
						field:"opeID",
						align:"center",
						valign:"middle"
					},{
						title:"操作语句",
						field:"opeSql",
						align:"center",
						valign:"middle"
					},{
						title:"操作时间",
						field:"opeTime",
						align:"center",
						valign:"middle"
					}
				],

		// data: opeList,
		/* 
			成功加载数据
		 */
		onLoadSuccess:function(data){
			
		},
		onRefresh(){
			Toast("表格数据已刷新");
		},
		onClickRow(row,$element){
			
		},
		onCheckAll(rows){
		},
		onCheck(row){
			
		}
	});
});

/* 
	弹窗提示，函数封装---基于动态添加dom节点
 */
function Toast(msg){  
	duration=1000;  
	var m = document.createElement('div');
	if(msg==""){
		m.innerHTML="请先输入内容哦！";
	}
	else{
		m.innerHTML = msg; 
	}
	m.style.cssText="z-index: 99; font-size: 18px;font-weight:bold;font-family:'宋体';color: rgb(255, 255, 255);background-color: rgba(0, 0, 0, 0.6);padding: 10px 15px;margin: 0 0 0 -60px;border-radius: 4px;position: fixed;    top: 50%;left: 50%;width: 200px;text-align: center;";
	document.body.appendChild(m);  
	setTimeout(function() {  
		var d = 0.5;
		m.style.opacity = '0';  
		
		document.body.removeChild(m)
	}, duration);  
}

/* 
	由于我们需要动态hover效果和标签切换效果，所以基于js对效果进行
	绑定，切换之后需要修改背景色、悬停效果等
 */
var curIndex = 2;
function changePage(index){
	if(index == curIndex){
		return;
	}
	var $page = null;
	var $selectTag=null;
	switch(index){
		case 1:
			$page = $("#myData");
			$selectTag = $("#data");
			break;
		case 2:
			$page = $("#myConsole");
			$selectTag = $("#console");
			break;
		case 3:
			$page = $("#myAdd");
			$selectTag = $("#add");
			break;
		case 4:
			$page = $("#myQuery");
			$selectTag = $("#query");
			break;
		case 5:
			$page = $("#myUpdate");
			$selectTag = $("#update");
			break;
		case 6:
			$page = $("#myDelete");
			$selectTag = $("#delete");
			break;
		case 7:
			$page = $("#myUserList");
			$selectTag = $("#listUser");
			break;
		case 8:
			$page = $("#mySetting");
			$selectTag = $("#setting");
			break;
	}
	var $cur_page = null;
	var $cur_tag = null;
	switch(curIndex){
		case 1:
			$cur_page = $("#myData");
			$cur_tag = $("#data");
			curIndex = index;
			break;
		case 2:
			$cur_page = $("#myConsole");
			$cur_tag = $("#console");
			curIndex = index;
			break;
		case 3:
			$cur_page = $("#myAdd");
			$cur_tag = $("#add");
			curIndex = index;
			break;
		case 4:
			$cur_page = $("#myQuery");
			$cur_tag = $("#query");
			curIndex = index;
			break;
		case 5:
			$cur_page = $("#myUpdate");
			$cur_tag = $("#update");
			curIndex = index;
			break;
		case 6:
			$cur_page = $("#myDelete");
			$cur_tag = $("#delete");
			curIndex = index;
			break;
		case 7:
			$cur_page = $("#myUserList");
			$cur_tag = $("#listUser");
			curIndex = index;
			break;
		case 8:
			$cur_page = $("#mySetting");
			$cur_tag = $("#setting");
			curIndex = index;
			break;
	}
	// 隐藏旧页面，展示新页面
	$cur_page.hide();
	$page.show();
	
	// 为选中标签着色
	$cur_tag.css("background-color","rgba(0,0,0,0)");
	
	// 由于设置CSS之后原先的设计失效，故动态添加悬浮指针效果
	$cur_tag.hover(function(){
		$cur_tag.css("background-color","#ff5500");
	},function(){
		$cur_tag.css("background-color","rgba(0,0,0,0)");
	});
	$selectTag.css("background-color","#ff5500");
	$selectTag.hover(function(){
		$selectTag.css("background-color","#ff5500");
	},function(){
		$selectTag.css("background-color","#ff5500");
	});
}