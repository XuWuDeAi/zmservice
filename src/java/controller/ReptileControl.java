package controller;


import entity.JsonRespBean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import unit.HttpUnit;
import unit.MainUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//爬虫相关
@Controller
public class ReptileControl {

    //简书首页爬虫
    @ResponseBody
    @RequestMapping(value = "/jianshu", produces = "text/plain; charset=utf-8")
    public String jianshu(HttpServletRequest request, HttpServletResponse response) {
        try {
            String value;
            value = HttpUnit.get("https://www.jianshu.com/");
            Document doc = Jsoup.parse(value);
            Elements content = doc.select("ul[class$=note-list]").first().select("li");

            JSONArray array = new JSONArray();
            for (int i = 0; i < content.size(); i++) {
                Element item = content.get(i);
                String type = item.selectFirst("a[class$=title]").text();
                String title = item.selectFirst("p[class$=abstract]").text();
              //  String img = item.selectFirst("img").attr("data-echo");
                JSONObject jsop=new JSONObject();
                jsop.put("type",type);
                jsop.put("title",title);
              //  jsop.put("img",img);
                array.put(jsop);
            }
            MainUnit.print(content.toString());
            return JsonRespBean.success(array);
        } catch (Exception e) {
            return JsonRespBean.erro(e);
        }
    }


}
