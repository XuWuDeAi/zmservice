package unit;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ZmThumbnailsService {

    HttpServletRequest request;
    HttpServletResponse response;

    public ZmThumbnailsService(HttpServletRequest request,
                               HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void doPost() throws Exception {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart)
//            throw new Exception("请求编码必须为: multipart/form-data !");

        request.setCharacterEncoding("UTF-8");

        // ServletFileUpload ： commons包里提供的工具类
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter;
        iter = upload.getItemIterator(request);
      //  InputStream in = null;
        int height = 200;
        int width = 200;

        while (iter.hasNext()) {
            // 表单域
            FileItemStream item = iter.next();
            String fieldName = item.getFieldName();
            InputStream fieldStream = item.openStream();
            if (item.isFormField()) {
                //   普通表单域: 直接读取值
                String fieldValue = Streams.asString(fieldStream, "UTF-8");
                if (fieldName.equals("height")) {
                    height = Integer.parseInt(fieldValue);
                } else if (fieldName.equals("width")) {
                    width = Integer.parseInt(fieldValue);
                }
                printLog("表单域:" + fieldName + "=" + fieldValue);
            } else {
                printLog("文件开始解析");
                Decode(fieldStream, height, width);

            }
        }

        return;
    }


    public void Decode(InputStream in, int hetght, int width) throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(in);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        OutputStream os = response.getOutputStream();
        Thumbnails.of(image)
                .size(hetght, width)
                .outputFormat("png")
                .toOutputStream(os);
        os.close();
        in.close();
        return;
    }

    //////////////////////////////////////////////////

    private void printLog(String message) {
        System.out.println(message);
    }


}
