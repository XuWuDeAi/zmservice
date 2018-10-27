package controller;


import entity.JsonRespBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import unit.HttpUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//爬虫相关
@Controller
public class ReptileControl {

    @ResponseBody
    @RequestMapping(value = "/hello", produces = "text/plain; charset=utf-8")
    public String valicode(HttpServletRequest request, HttpServletResponse response) {
        try {
            return JsonRespBean.success("测试");
        } catch (Exception e) {
            return JsonRespBean.erro(e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/jianshu", produces = "text/plain; charset=utf-8")
    public String jianshu(HttpServletRequest request, HttpServletResponse response) {
        try {

            String value;
            value = HttpUnit.get("https://www.jianshu.com/");
            Document doc = Jsoup.parse(value);
            return JsonRespBean.success(doc);
        } catch (Exception e) {
            return JsonRespBean.erro(e);
        }
    }


}
