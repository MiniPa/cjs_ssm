package chengjs.controller;

import com.chengjs.lucene.LuceneIndex;
import com.chengjs.pojo.Content;
import com.chengjs.pojo.PageEntity;
import com.chengjs.pojo.User;
import com.chengjs.service.ContentService;
import com.chengjs.service.IUserService;
import com.chengjs.util.PageUtil;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {
    @Resource
    private IUserService userService;

    @Resource
    private ContentService contentService ;

    @RequestMapping("/index")
    public String index(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        HttpServletRequest request, Model model) {
        PageEntity pageEntity = new PageEntity(page, 10);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pageEntity.getStart());
        map.put("size", pageEntity.getPageSize());


        List<User> users = userService.list(map);
        Long total = userService.getTotal(map);
        model.addAttribute("users", users);
        StringBuffer param = new StringBuffer();



        String pageHtml = PageUtil.genPagination(request.getContextPath() + "/index", total, page, 10, param.toString());
        model.addAttribute("pageHtml", pageHtml);
        return "index";
    }

    @RequestMapping("/turnToWebSocketIndex")
    public String turnToWebSocketIndex(){
        return  "websocket/websocket" ;
    }

    /**
     * 加载聊天记录
     * @param response
     */
    @RequestMapping("/content_load")
    public void content_load(HttpServletResponse response){
        JSONObject jsonObject = new JSONObject() ;
        try {
            JSONObject jo = new JSONObject() ;
            List<Content> list = contentService.findContentList();
            jo.put("contents",list) ;
            jsonObject = CommonUtil.parseJson("1","操作成功",jo);
        }catch (Exception e){
            e.printStackTrace();
            CommonUtil.parseJson("2","操作异常","");
        }
        CommonUtil.responseBuildJson(response,jsonObject);
    }



    @RequestMapping("/q")
    public String search(@RequestParam(value = "q", required = false,defaultValue = "") String q,
                         @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                         Model model,
                         HttpServletRequest request) throws Exception {
        LuceneIndex luceneIndex = new LuceneIndex() ;
        List<User> userList = luceneIndex.searchBlog(q);
        /**
         * 关于查询之后的分页我采用的是每次分页发起的请求都是将所有的数据查询出来，
         * 具体是第几页再截取对应页数的数据，典型的拿空间换时间的做法，如果各位有什么
         * 高招欢迎受教。
         */
        Integer toIndex = userList.size() >= Integer.parseInt(page) * 5 ? Integer.parseInt(page) * 5 : userList.size();
        List<User> newList = userList.subList((Integer.parseInt(page) - 1) * 5, toIndex);
        model.addAttribute("userList",newList) ;
        String s = this.genUpAndDownPageCode(Integer.parseInt(page), userList.size(), q, 5, "");
        model.addAttribute("pageHtml",s) ;
        model.addAttribute("q",q) ;
        model.addAttribute("resultTotal",userList.size()) ;
        model.addAttribute("pageTitle","搜索关键字'" + q + "'结果页面") ;

        return "queryResult";
    }

    @RequestMapping(value = "/jsonpInfo",method = { RequestMethod.GET })
    @ResponseBody
    public Object jsonpInfo(String callback,Integer userId) throws IOException {
        User user = userService.getUserById(userId);
        JSONPObject jsonpObject = new JSONPObject(callback,user) ;
        return jsonpObject ;
    }

    @RequestMapping("/createAllIndex")
    public void createAllIndex(HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject() ;
        try {
            JSONObject jo = new JSONObject() ;
            Map<String, Object> map = new HashMap<String, Object>();
            List<User> users = userService.list(map);
            for (User user : users) {
                //先删除原有的索引再创建新的
                LuceneIndex luceneIndex = new LuceneIndex();
                luceneIndex.deleteIndex(user.getUserId() + "");
            }
            for (User user : users) {
                //先删除原有的索引再创建新的
                LuceneIndex luceneIndex = new LuceneIndex();
                luceneIndex.addIndex(user);
            }
            jsonObject = CommonUtil.parseJson("1", "操作成功", jo);
        }catch (Exception e){
            e.printStackTrace();
            jsonObject = CommonUtil.parseJson("2", "操作异常", "");
        }
        //构建返回
        CommonUtil.responseBuildJson(response, jsonObject);
    }


    /**
     * 查询之后的分页
     * @param page
     * @param totalNum
     * @param q
     * @param pageSize
     * @param projectContext
     * @return
     */
    private String genUpAndDownPageCode(int page,Integer totalNum,String q,Integer pageSize,String projectContext){
        long totalPage=totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
        StringBuffer pageCode=new StringBuffer();
        if(totalPage==0){
            return "";
        }else{
            pageCode.append("<nav>");
            pageCode.append("<ul class='pager' >");
            if(page>1){
                pageCode.append("<li><a href='"+projectContext+"/q?page="+(page-1)+"&q="+q+"'>上一页</a></li>");
            }else{
                pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
            }
            if(page<totalPage){
                pageCode.append("<li><a href='"+projectContext+"/q?page="+(page+1)+"&q="+q+"'>下一页</a></li>");
            }else{
                pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
            }
            pageCode.append("</ul>");
            pageCode.append("</nav>");
        }
        return pageCode.toString();
    }
}
