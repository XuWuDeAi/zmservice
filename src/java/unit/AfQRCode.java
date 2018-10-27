package unit;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/* 对 libZXing封装的一个类
 * 用于生成二级码
 */

public class AfQRCode
{	
	int fgColor = 0xFF000000;  // 前景色
	int bgColor = 0xFFFFFFFF;  // 背景色
	int size = 300; // 图片大小
	
	public AfQRCode()
	{		
	}
	
	public AfQRCode(int size)
	{
		this.size = size;
	}

	public void generate(String text, File jpgfile) throws Exception
	{		
		FileOutputStream outputStream = new FileOutputStream(jpgfile);
		try{
			generate ( text, outputStream);
		}finally
		{
			try{ outputStream.close();} catch(Exception e){}
		}
	}
	
	public void generate(String text, OutputStream stream) throws Exception
	{
		// 二维码的图片格式
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.MARGIN, 1);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
				BarcodeFormat.QR_CODE, size, size, hints);
		
		// 生成二维码
		String imageFormat = "jpg";
		BufferedImage image = toImage(bitMatrix);
		if (!ImageIO.write(image, imageFormat, stream))
		{
			throw new IOException("不能将二维码写入图片文件："	+ imageFormat);
		}	
	}	
	
	private BufferedImage toImage(BitMatrix matrix)
	{
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				image.setRGB(x, y, matrix.get(x, y) ? fgColor : bgColor);

			}
		}
		return image;
	}	
	
	public static void main(String[] args) throws Exception
	{
		String text = "http://afanihao.cn";
		File jpg = new File("test_qr.jpg");
		new AfQRCode().generate(text, jpg);
		System.out.println("完成" + jpg.getAbsolutePath());
	}

}
