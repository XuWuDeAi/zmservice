package controller;


import entity.JsonRespBean;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import unit.HttpUnit;

import javax.lang.model.element.Element;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.List;

@Controller
public class ReptileControl {

    @ResponseBody
    @RequestMapping(value = "/hello", produces = "text/plain; charset=utf-8")
    public String valicode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return JsonRespBean.success("测试");
        } catch (Exception e) {
            return JsonRespBean.erro(e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/jianshu", produces = "text/plain; charset=utf-8")
    public String jianshu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String value;
            value = HttpUnit.get("https://www.jianshu.com/");
            Document doc = Jsoup.parse(value);
            return JsonRespBean.success(doc.toString());
        } catch (Exception e) {
            return JsonRespBean.erro(e);
        }
    }


}
