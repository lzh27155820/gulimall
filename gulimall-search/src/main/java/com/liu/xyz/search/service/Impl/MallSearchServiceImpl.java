package com.liu.xyz.search.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.liu.xyz.common.to.es.SkuESModel;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.search.constant.EsConstant;
import com.liu.xyz.search.feign.ProductFeignService;
import com.liu.xyz.search.service.MallSearchService;
import com.liu.xyz.search.vo.AttrResponseVo;
import com.liu.xyz.search.vo.SearchParam;
import com.liu.xyz.search.vo.SearchResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create liu 2022-10-20
 */
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ProductFeignService productFeignService;
    @SneakyThrows
    @Override
    public SearchResult search(SearchParam searchParam) {

        SearchResult result=null;
        SearchRequest searchRequest =BuidldSearchRequest(searchParam);


        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT.toBuilder().build());
            result = buildSearchResult(response, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * ??????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????,??????????????????
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {

        SearchResult result = new SearchResult();

        //1????????????????????????????????????
        SearchHits hits = response.getHits();

        List<SkuESModel> esModels = new ArrayList<>();
        //????????????????????????
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuESModel  esModel = JSON.parseObject(sourceAsString, SkuESModel.class);

                //????????????????????????????????????????????????????????????????????????
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    //??????????????????????????????
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleValue = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleValue);
                }
                esModels.add(esModel);
            }
        }
        result.setProduct(esModels);

        //2?????????????????????????????????????????????
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        //???????????????????????????
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1??????????????????id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //2????????????????????????
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3???????????????????????????
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }

        result.setAttrs(attrVos);

        //3?????????????????????????????????????????????
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        //????????????????????????
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

            //1??????????????????id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);

            //2????????????????????????
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            //3????????????????????????
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //4?????????????????????????????????????????????
        //????????????????????????
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //????????????id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //???????????????
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }

        result.setCatalogs(catalogVos);
        //===============????????????????????????????????????====================//
        //5???????????????-??????
        result.setPageNum(param.getPageNum());
        //5???1???????????????????????????
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        //5???2????????????-?????????-??????
        int totalPages = (int)total % EsConstant.PRODUCT_PAGESIZE == 0 ?
                (int)total / EsConstant.PRODUCT_PAGESIZE : ((int)total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);


        //6????????????????????????
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                //1??????????????????attrs?????????????????????
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                  //  Object attr1 = r.get("attr");
                    AttrResponseVo data =(AttrResponseVo) r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });

                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                //2???????????????????????????????????????????????????????????????????????????????????????url?????????????????????
                //??????????????????????????????????????????
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr,"UTF-8");
                    encode.replace("+","%20");  //??????????????????????????????Java???????????????????????????
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = param.get_queryString().replace("&attrs=" + attr, "");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(collect);
        }


        return result;
    }



    private SearchRequest BuidldSearchRequest(SearchParam param) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * must????????????
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        /**
         * bool -filter
         */
        if(param.getCatalog3Id()!=null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        if(param.getBrandId()!=null){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //

        if(param.getAttrs()!=null&&param.getAttrs().size()>0){


            for (String attrStr: param.getAttrs()){

                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId=s[0];
                String[] attrValue=s[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrId",attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                //
                NestedQueryBuilder nestedQuery =
                        QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }

            //QueryBuilders.nestedQuery("attrs",null, ScoreMode.None);
        }
        if(null != param.getHasStock()){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }
        //boolQuery.filter(QueryBuilders.termsQuery("hastStock",param.getHasStock()==1));

        if (!StringUtils.isEmpty(param.getSkuPrice())){
            //skuPrice????????????1_500???_500???500_
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] price = param.getSkuPrice().split("_");
            if(price.length==2){
                rangeQuery.gte(price[0]).lte(price[1]);
            }else if(price.length == 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(price[1]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(price[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        /**
         * ?????? ?????????????????????
         */
        //??????
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0],order);
        }
        //?????? from =
        searchSourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //??????
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");//????????????
            builder.postTags("</b>");//????????????
            searchSourceBuilder.highlighter(builder);
        }
        /**
         * ????????????
         */
        /**
         * ????????????
         */
        //1. ????????????????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);


        //1.1 ??????????????????-???????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg")
                .field("brandName").size(1));
        //1.2 ??????????????????-??????????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg")
                .field("brandImg").size(1));

        searchSourceBuilder.aggregation(brand_agg);

        //2. ??????????????????????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);

        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        searchSourceBuilder.aggregation(catalog_agg);

        //2. ??????????????????????????????
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //2.1 ????????????ID????????????
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_agg.subAggregation(attr_id_agg);
        //2.1.1 ???????????????ID?????????????????????????????????
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //2.1.1 ???????????????ID?????????????????????????????????
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        searchSourceBuilder.aggregation(attr_agg);

        log.debug("?????????DSL?????? {}",searchSourceBuilder.toString());

        searchSourceBuilder.query(boolQuery);
        String s = searchSourceBuilder.toString();
        System.out.println(s);
        SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},searchSourceBuilder);
        return request;
    }
}
