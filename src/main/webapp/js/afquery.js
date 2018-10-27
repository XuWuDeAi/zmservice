
var Af = {};

/* 输出日志 */
Af.log = function(msg)
{
	 try {   console.log(msg);     } catch (err) {}
};

/* RESTful 调用的封装 
 *  示例  Af.rest("uri_of_rest_service", req, function(ans){
 * 	
 * });
 */

// rest返回200 OK，但是error值不为0
Af.restErrHandler = function(error, reason)
{	
	alert(reason);
};
// rest没有返回200 ( 可能返回500等错误，或者网络不可连接)
Af.httpErrHandler = function()
{	
	alert("服务器HTTP错误");
};
// serviceUri 服务名, req 请求参象 , dataHandler 应答数据处理函数
Af.rest = function (serviceUri, req, dataHandler)
{
	jQuery.ajax({				
		url: serviceUri, 			
		method: "POST", 
		processData: false,	
		data: JSON.stringify(req), 
		dataType: 'json',
		success: function(ans){
			if(ans.error != 0)
				Af.restErrHandler( ans.error, ans.reason);
			else
				dataHandler(ans.data);
		},
		error: function( jqXHR, textStatus, errorThrown){
			Af.httpErrHandler();
		}
	});	
}

/* JSONP 调用 */
Af.jsonp = function(URI, req, resultHanlder)
{
	jQuery.ajax({				
			url: URI,	
			method: "GET", // get方式
			dataType: "jsonp", // 1: jsonp 
			//jsonpCallback: "callback",
			data: req, // 参数
			success: resultHanlder
	});	
}

/* 动态加载页面 */
Af.loadPage = function( container, url)
{
	// container可以是选择器字符串
	if(container.constructor == String)	 
		container = $(container);
		
	$.get(url, function(content){
		container.html( content);
	});
}

/*  用法: 
 *   var template = $(".template").html();
 *   var tmpl = new AfTemplate(template) ;
 *   var data = { ... }
 *   var html = tmpl.replace(data);
 * 
 */
function AfTemplate(template)
{
	this.map = null;;
	this.template = template;
	
	this.compile = function()
	{
		this.map = {};
		
		var r = new RegExp("\\{#.*?\\}", "g");

		var result;
		while ((result = r.exec( this.template )) != null)  
		{
			//Af.log( result[0] );
			var match = result[0]; // {#xxx}
			var key = match.substr(2, match.length - 3); // 不允许带空格  
			//var key = key1.trim();
			this.map[key] = new RegExp("\\{#" + key + "\\}", "g");
			//Af.log(varName);
		}
		
		return this;
	};
	
	this.replace= function( data )
	{
		// 第一次运行时编译
		if(this.map == null) this.compile();
		
		var html = this.template;
		for( var key in this.map)
		{
			var regex = this.map[key];
			var value = data[ key];
			if(value != null)
				html = html.replace( regex, value);
		}
		return html;
	};
}

/* map 定义 */
/* 可以按ID查询的表 */
function AfMap()
{
	this.array = [];
	
	this.put = function(id, obj)
	{
		/* 检查重复, 如果已经存在直接替换 */
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			if(e.id == id) 
			{
				e.obj = obj;
				return;
			}
		}
		/* 添加新的项 */
		var e = {};
		e.id = id;
		e.obj = obj;
		this.array.push( e );
	};
	
	this.get = function(id)
	{
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			if(e.id == id) 
				return e.obj;
		}
		return null;
	};
	
	/* 遍历 : callback: 如果要continue就return true, 否则返回false */
	this.each = function ( callback )
	{
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			if(false == callback (e.id, e.obj ) ) break;
		}
	};
	
	this.remove = function ( id )
	{
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			if(e.id == id)
				this.array.splice(i, 1);
		}
	};
	
	this.size = function()
	{
		return this.array.length;
	};
	
	this.clear = function()
	{
		this.array = [];
	};
	
	this.values = function()
	{
		var values = [];
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			values.push(e.obj);
		}
		return values;
	};
	this.ids = function()
	{
		var result = [];
		for(var i=0; i<this.array.length;i++)
		{
			var e = this.array[i];
			result.push(e.id);
		}
		return result;
	};
}

/* 以逗号分隔的ID列表 */
function AfIdList ()
{
	this.ids = [];	
	
	this.aa = function (str)
	{
		if(str==null || str.length==0) return this;
		var sss = str.split(",");
		for(var i=0; i<sss.length; i++)
		{
			var it = sss[i];
			if(it.length > 0 && ! this.contains( it ))
			{				
				this.ids.push(it);
			}
		}
		return this;
	};
	this.at = function(index)
	{
		if(this.ids.length == 0) return null;
		return this.ids[index];
	};
	this.contains = function (id)
	{
		for(var i=0; i<this.ids.length; i++)
		{
			if( id == this.ids[i]) return true;
		}
		return false;
	};
	this.size = function ()
	{
		return this.ids.length;
	};
	this.toString = function()
	{
		return this.ids.join(",");
	}
}

/* 可以传一个 jQuery对象，也可以传一个 selector字符串 */
Af.showDialog = function(selector)
{
	var dlg = selector;
	if(selector.constructor == String)	 
		dlg = $(selector);
	
	// 点击关闭时，关闭对话框
	var closeButton = $('[role="close"]', dlg);
	if(closeButton != null);
	{
		closeButton.click(function(){
			dlg.hide();
		});
	}
	
	dlg.show();
}

/*
 * 上传工具类 
 */
function AfFileUploader()
{
	this.fileButton = null; // 原生DOM
	this.file = null; // 要上传的文件
	this.uploadUrl = null; 
	this.status = 0; // 0, 1, 100, -1(失败), -2(canceled)
	this.progress = 0; // 0-100
	this.response = {}; // 上传完毕后服务器的返回值
	this.observer = null;   // 调用者通知
	this.enableLog = true; // 是否显示内部打印
	
	// 输入参数可以是:Selector / jQuery对象 / 原生DOM
	this.setButton = function(fileButton)
	{
		// 如果输入参数是 Selector
		if(fileButton.constructor == String) fileButton = $(fileButton);
		
		// 如果输入参数是jQuery对象, 则转成DOM
		if(fileButton.constructor == jQuery) fileButton = fileButton[0];
		
		// 先看原来有没有绑定, 如果已经绑定了一个uploader，则返回原有对象
		if(fileButton.uploader != null)  return fileButton.uploader;
		
		// 创建新的uploader
		this.fileButton = fileButton;
		
		// 把上下文存放到DOM元素里
		this.fileButton.uploader = this;
		
		// 添加回调，确保只添加一次
//			this.fileButton.removeEventListener("change", this.onFileChanged);
//			this.fileButton.addEventListener("change", this.onFileChanged);
		this.fileButton.addEventListener("change", function(){
			var ctx = this.uploader;
			var fileButton = this;
			if(fileButton.files.length == 0) return;		
			
			var file = fileButton.files[0];
			ctx.log("select file: " + file.name);
			
			ctx.setFile(file);
			ctx.startUpload( );
		});
		
		return this;
	};
	
	this.setUploadUrl = function(url)
	{
		this.uploadUrl = url;
		return this;
	};
	
	// observer要实现2个回调方法
	// uploadHandleEvent : function( msg , uploader) {}
	// uploadTestFile : function ( upload) {}
	this.setObserver = function ( observer )
	{
		this.observer = observer;
		return this;
	};
	
	this.setLogEnabled  = function( enabled)
	{
		this.enableLog = enabled;
		return this;
	};
	
	// 设置文件 (并不立即启动)
	this.setFile = function(file)
	{
		this.file = file;
		return this;
	};
		
	// 打开文件对话框，让用户选择文件
	this.openFileDialog = function()
	{
		if(this.fileButton == null) throw ("尚未初始化file button! 请调用 setButton() 进行设置!");

		$(this.fileButton).click();	
	};

	// 外部可以直接送进来一个 File 对象
	this.startUpload = function( )
	{
		if(this.uploadUrl == null) throw ("尚未设置uploadUrl,无法上传! 请调用 setUploadUrl()进行设置!");
		if(this.file == null) throw ("尚未设置file! 请调用openFileDialog()打开文件选择对话框!或者调用setFile()传一个File对象!");
		
		var file = this.file;
		// 上传测试: 是否允许上传
		if(this.observer != null && this.observer.uploadTestFile != null)
		{
			if( ! this.observer.uploadTestFile(this))
			{
				this.log("不满足上传条件 ! " + file.name);
				return;
			}
		}
		
		this.log("开始上传: " + file.name);

	   	var vFD = new FormData();
		vFD.append('file', file); // 'file' 为HTTP Post里的字段名, file 对浏览器里的File对象
				    
	    var oXHR = new XMLHttpRequest();
	    oXHR.ctx = this;
	    oXHR.upload.ctx = this;
	    
	    oXHR.upload.addEventListener("progress", this.evt_upload_progress, false);
	    oXHR.addEventListener("load", this.evt_upload_complete, false);
	    oXHR.addEventListener("error", this.evt_upload_failed, false);
	    oXHR.addEventListener("abort", this.evt_upload_cancel, false);		
	
		this.notify('start');
		oXHR.open("POST", this.uploadUrl );
	    oXHR.send(vFD);
	    
	    this.vFD = vFD;
	    this.oXHR = oXHR; /* 保存这个上传者对象, 用于调用其abort()函数 */
	   	this.status = 1;		   		   	
	   	return this;
	};
	
	// 取消上传
	this.cancelUpload = function()
	{		
		if(this.oXHR != null)
		{
			try{
				this.oXHR.abort(); 
    			this.oXHR = null;
    			this.status = -2;
			}catch(err)
			{
				Af.log("取消上传时出错 ：" + err);
			}
    	}
	};
	
	// 通知调用者: 'start' 'progress' 'complete' 'error' 'abort'
	this.notify = function(msg) 
	{
		if(this.observer != null && this.observer.uploadHandleEvent != null)
		{
			this.observer.uploadHandleEvent(msg, this);
		}
	};
	
	this.log = function(msg)
	{
		if(!this.enableLog) return;
		try {   console.log(msg);     } catch (err) {}
	};
	
	//////////////////////////////
	
	this.evt_upload_progress = function (evt) 
	{
		var ctx = this.ctx;
	    if (evt.lengthComputable)
	    {
	    	ctx.progress = Math.round(evt.loaded * 100 / evt.total);		    	
	    	ctx.log ("上传进度: " + ctx.progress);		
	    	ctx.notify('progress');
	    }	        
	};
	this.evt_upload_complete = function (evt)
	{
		var ctx = this.ctx;
		if(evt.loaded == 0)
		{
			ctx.status = -1;
			ctx.log ("上传失败!" + ctx.file.name);
			ctx.notify('error');
		}
		else
		{
			ctx.status = 100;
	    	ctx.response = JSON.parse(evt.target.responseText);
	   		ctx.log (ctx.response); 
	   		ctx.notify('complete');
		}			
	};		 
	this.evt_upload_failed = function (evt) 
	{			
		var ctx = this.ctx;
		ctx.status = -1;
		ctx.log ("上传出错"); 
		ctx.notify('error');
	};
	this.evt_upload_cancel = function (evt) 
	{
		var ctx = this.ctx;
		ctx.status = -2;
		ctx.log( "上传中止!");	
		ctx.notify('abort');
	};
}