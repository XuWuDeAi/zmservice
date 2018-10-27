package unit;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@WebServlet("/FileUploadService")
public class AfFileUploadService extends HttpServlet
{
	// 文件上传的临时目录 
	File tmpDir ;
	
	@Override
	public void init() throws ServletException
	{
		File webroot = new File(getServletContext().getRealPath("/"));
		tmpDir = new File(webroot, "upload");
		tmpDir.mkdirs(); // 上传进来的文件存放在 WebRoot/upload/ 下
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		JSONObject jresp = new JSONObject();
		try
		{
			Object data = doUpload(request, response);
			jresp.put("error", 0);
			jresp.put("reason",  "OK");
			if(data != null)
				jresp.put("data", data);
			
		} catch (Exception e)
		{
			jresp.put("error", -1);
			jresp.put("reason", e.getMessage());
		}
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");			
		PrintWriter out = response.getWriter();
		out.write(jresp.toString(2));
		out.close();
	}
	
	private Object doUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart)
			throw new Exception("请求编码必须为: multipart/form-data !");
		
		request.setCharacterEncoding("UTF-8");
			
		// ServletFileUpload ： commons包里提供的工具类
		ServletFileUpload upload = new ServletFileUpload();
		AfFileUploadInfo info = new AfFileUploadInfo();
		
		FileItemIterator iter = upload.getItemIterator(request);
		while (iter.hasNext()) 
		{
			// 表单域 
		    FileItemStream item = iter.next();
		    String fieldName = item.getFieldName();
		    InputStream fieldStream = item.openStream();
		    if (item.isFormField())
		    {
		    	// 普通表单域: 直接读取值			    	
		    	String fieldValue = Streams.asString(fieldStream, "UTF-8");
		    	printLog("表单域:" + fieldName + "=" + fieldValue);			    	
		    } 
		    else 
		    {
		    	// 生成唯一的文件名
		    	info.realName = item.getName(); // 原始文件名
		    	info.suffix = fileSuffix(info.realName); // 后缀
		    	info.tmpFileName = createTmpFileName(info.suffix); // 服务器临时文件名
		    	info.tmpFile = new File(tmpDir, info.tmpFileName);
		    	info.fileSize = 0; // 文件大小			    	
		    	
		    	printLog("文件上传开始:" + info.realName + " >> " + info.tmpFile);
			        
		        // 从FieldStream读取数据, 保存到目标文件			        
		        info.tmpFile.getParentFile().mkdirs();		        
		        FileOutputStream fileStream = new FileOutputStream(info.tmpFile);
		        try{
		        	// 从请求里读取文件数据，保存到本地文件
		        	info.fileSize = copy(fieldStream, fileStream);
		        }finally{
		        	try{ fileStream.close();}catch(Exception e){}
		        	try{ fieldStream.close();}catch(Exception e){}
		        }
		        
		        printLog("文件上传完成:" + info.realName + ", 大小: " + info.fileSize);
		    }
		}	
		return info.tmpFileName;
	}
	
	private long copy(InputStream in, OutputStream out) throws Exception
	{
		long count = 0;
		byte[] buf = new byte[8192];
		while (true)
		{
			int n = in.read(buf);
			if (n < 0)
				break;
			if (n == 0)
				continue;
			out.write(buf, 0, n);

			count += n;
		}
		return count;
	}
	
	//////////////////////////////////////////////////
	
	private void printLog(String message)
	{
		System.out.println(message);
	}
	
	// 生成一个唯一的ID
	private String createUUID ()
	{
		 String s = UUID.randomUUID().toString(); 
	     String s2 = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
	     return s2.toUpperCase();
	}
	
	// 得到一个保证不重复的临时文件名
	private String createTmpFileName(String suffix)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String datestr = sdf.format(new Date());
		String name = datestr + "-" + createUUID() + "." + suffix;
		return name;
	}
	
	// 得到文件的后缀名
	public String fileSuffix(String fileName)
	{
		int p = fileName.lastIndexOf('.');
		if(p >= 0)
		{
			return fileName.substring(p+1).toLowerCase();
		}
		return "";
	}
}
