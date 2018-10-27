package unit;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;


public class ZmQrAnaService {

    HttpServletRequest request;
    HttpServletResponse response;

    public ZmQrAnaService(HttpServletRequest request,
                          HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }


    public void doPost() throws IOException {
        JSONObject jresp = new JSONObject();
        try {
            Object data = doUpload();
            jresp.put("error", 0);
            jresp.put("reason", "OK");
            if (data != null)
                jresp.put("data", data);
        } catch (Exception e) {
            jresp.put("error", -1);
            jresp.put("reason", e.getMessage());
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.write(jresp.toString(2));
        out.close();
    }

    public Object doUpload() throws Exception {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart)
//            throw new Exception("请求编码必须为: multipart/form-data !");

        request.setCharacterEncoding("UTF-8");

        // ServletFileUpload ： commons包里提供的工具类
        ServletFileUpload upload = new ServletFileUpload();
        AfFileUploadInfo info = new AfFileUploadInfo();

        FileItemIterator iter;
        iter = upload.getItemIterator(request);


        while (iter.hasNext()) {
            // 表单域
            FileItemStream item = iter.next();
            String fieldName = item.getFieldName();
            InputStream fieldStream = item.openStream();
            if (item.isFormField()) {
                // 普通表单域: 直接读取值
                String fieldValue = Streams.asString(fieldStream, "UTF-8");
                printLog("表单域:" + fieldName + "=" + fieldValue);
            } else {
                printLog("文件开始解析");
               return Decode(fieldStream);
            }
            break;
        }
        return "解析失败";
    }


    public String Decode(InputStream in) throws IOException {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        BufferedImage image = null;
        try {
            image = ImageIO.read(in);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        // 定义二维码的参数
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            Result result = multiFormatReader.decode(binaryBitmap, hints);
            return result.toString();
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        in.close();
        return "解析失败";
    }

    //////////////////////////////////////////////////

    private void printLog(String message) {
        System.out.println(message);
    }


}
