package com.liu.xyz.search.constant;

import com.liu.xyz.search.service.MallSearchService;
import com.liu.xyz.search.vo.SearchParam;
import com.liu.xyz.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * create liu 2022-10-20
 */
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;
    @RequestMapping("/list.html")
    public String list(SearchParam searchParam, Model model, HttpServletRequest request){
        //获取对应 查询路径 ?开始的
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult searchResult =mallSearchService.search(searchParam);
        model.addAttribute("result",searchResult);
        return "list";
    }
}
