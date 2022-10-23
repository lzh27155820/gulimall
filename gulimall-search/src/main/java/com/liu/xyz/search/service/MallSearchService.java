package com.liu.xyz.search.service;

import com.liu.xyz.search.vo.SearchParam;
import com.liu.xyz.search.vo.SearchResult;

/**
 * create liu 2022-10-20
 */
public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
