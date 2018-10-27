package unit;

import java.io.File;

/* 记录上传文件的信息
 * 
 */

public class AfFileUploadInfo
{
	public String realName ;// 原始文件名
	public String suffix ; // 后缀
	public String tmpFileName ; // 服务器临时文件名
	public File tmpFile ;
	public long fileSize ; // 文件大小	
}
