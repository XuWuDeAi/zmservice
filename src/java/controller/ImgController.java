package controller;


import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import unit.AfQRCode;
import unit.VerifyCodeUtils;
import unit.ZmQrAnaService;
import unit.ZmThumbnailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;


//图片相关
@Controller
public class ImgController {

    //生成服务器二维码图片
    @RequestMapping(value = "/creQrcode.do", produces = "application/json; charset=utf-8")
    public void creQrcode(@RequestBody String str, HttpServletResponse response) throws IOException {
        try {

            JSONObject req = new JSONObject(URLDecoder.decode(str, "UTF-8"));
            String content = req.getString("content");
            int size = req.getInt("size");
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            OutputStream outStream = response.getOutputStream();
            new AfQRCode(size).generate(content, outStream);
            outStream.close();
        } catch (Exception e) {
            response.sendError(500, "内部出错: " + e.getMessage());
        }
    }

    //解析二维码图片
    @RequestMapping(value = "/anaQrcode.do", produces = "application/json; charset=utf-8")
    public void anaQrcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new ZmQrAnaService(request, response).doPost();
    }

    //生成验证码图片
    @RequestMapping("/verification.do")
    public void valicode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        //存入会话session
        HttpSession session = request.getSession(true);
        session.setAttribute("rand", verifyCode.toLowerCase());
        int w = 200, h = 80;
        VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
    }

    //生成略缩图
    @RequestMapping("/creThumbnails.do")
    public void creThumbnails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        new ZmThumbnailsService(request, response).doPost();

    }

}
