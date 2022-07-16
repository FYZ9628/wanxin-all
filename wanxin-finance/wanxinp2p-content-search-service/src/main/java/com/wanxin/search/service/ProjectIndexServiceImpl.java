package com.wanxin.search.service;

import com.wanxin.api.search.model.ProjectQueryParamsDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.PageVO;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 标的索引业务层实现类
 *
 * @Author Administrator
 * @Date 2022/6/25 0:55
 */
@Service
@RefreshScope
public class ProjectIndexServiceImpl implements ProjectIndexService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${wanxinp2p.es.index}")
    private String projectIndex;

//    @Autowired
//    private ProjectIndexMapper projectIndexMapper;

    @Override
    public PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO queryParamsDTO,
                                                Integer pageNo, Integer pageSize,
                                                String sortBy, String order) {

        //1、创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(projectIndex);

        //2、搜索条件
        //2.1、创建条件封装对象
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //2.2、非空判断并封装条件
        if (StringUtils.isNotBlank(queryParamsDTO.getName())) {
            queryBuilder.must(QueryBuilders.termQuery("name",queryParamsDTO.getName()));
        }

        if (queryParamsDTO.getStartPeriod() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery("period").gte(queryParamsDTO.getStartPeriod()));
        }

        if (queryParamsDTO.getEndPeriod() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery("period").lte(queryParamsDTO.getEndPeriod()));
        }

        // 默认为满标查询
//        queryBuilder.must(QueryBuilders.termQuery("projectstatus", "FULLY"));

//        // 精确匹配状态为满标
//        if (queryParamsDTO.getProjectStatus() != null) {
//            queryBuilder.must(QueryBuilders.termQuery("projectstatus", "FULLY"));
//        }

        queryBuilder.must(QueryBuilders.termQuery("projectstatus", "COLLECTING"));

        // 精确匹配状态为募集中
        if (queryParamsDTO.getProjectStatus() != null) {
            queryBuilder.must(QueryBuilders.termQuery("projectstatus", "COLLECTING"));
        }

        //3、创建SearchSourceBuilder对象--总的封装对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //3.1、封装条件
        searchSourceBuilder.query(queryBuilder);
        //3.2、设置排序参数
        if (StringUtils.isNotBlank(sortBy) && StringUtils.isNotBlank(order)) {
            if (order.toLowerCase().equals("asc")) {
                searchSourceBuilder.sort(sortBy, SortOrder.ASC);
            }

            if (order.toLowerCase().equals("desc")) {
                searchSourceBuilder.sort(sortBy, SortOrder.DESC);
            }
        } else {
            searchSourceBuilder.sort("createdate", SortOrder.DESC);
        }

        //3.3、设置分页参数
        searchSourceBuilder.from((pageNo - 1) * pageSize);
        searchSourceBuilder.size(pageSize);
        //4、完成封装
        searchRequest.source(searchSourceBuilder);

        List<ProjectDTO> list = new ArrayList<>();
        PageVO<ProjectDTO> pageVO = new PageVO<>();
        try {
            //5、执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //6、获取搜索的结果
            SearchHits hits = searchResponse.getHits();
            // 匹配的总记录数
            long totalHits = hits.getTotalHits().value;
            pageVO.setTotal(totalHits);
            // 获取匹配数据
            SearchHit[] searchHits = hits.getHits();

            //7、循环封装成DTO对象
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Double amount = (Double) sourceAsMap.get("amount");
                String projectstatus = (String) sourceAsMap.get("projectstatus");
                Integer period = Integer.parseInt(sourceAsMap.get("period").toString());
                String name = (String) sourceAsMap.get("name");
                String description = (String) sourceAsMap.get("description");
                BigDecimal annualRate = new BigDecimal((Double) sourceAsMap.get("annualrate"));

                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setAmount(new BigDecimal(amount));
                projectDTO.setProjectStatus(projectstatus);
                projectDTO.setPeriod(period);
                projectDTO.setName(name);
                projectDTO.setDescription(description);
                projectDTO.setAnnualRate(annualRate);
                projectDTO.setId((Long) sourceAsMap.get("id"));
                list.add(projectDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //8、封装为PageVO对象并返回
        pageVO.setContent(list);
        pageVO.setPageNo(pageNo);
        pageVO.setPageSize(pageSize);

        return pageVO;
    }


//    @Override
//    public PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO queryParamsDTO, Integer pageNo, Integer pageSize, String sortBy, String order) {
//
//        List<ProjectDTO> list = new ArrayList<>();
//        List<ProjectIndex> projectIndexList = projectIndexMapper.findByPeriodBetween(queryParamsDTO.getStartPeriod(), queryParamsDTO.getEndPeriod());
//        projectIndexList.forEach(projectIndex -> {
//            ProjectDTO projectDTO = new ProjectDTO();
//            projectDTO.setAmount(projectIndex.getAmount());
//            projectDTO.setProjectStatus(projectIndex.getProjectstatus());
//            projectDTO.setPeriod(projectIndex.getPeriod());
//            projectDTO.setName(projectIndex.getName());
//            projectDTO.setDescription(projectIndex.getDescription());
//            projectDTO.setAnnualRate(projectIndex.getAnnualRate());
//            projectDTO.setId(projectIndex.getId());
//            list.add(projectDTO);
//        });
//
//        PageVO<ProjectDTO> pageVO = new PageVO<>();
//        pageVO.setTotal(projectIndexList.size());
//
//        //8、封装为PageVO对象并返回
//        pageVO.setContent(list);
//        pageVO.setPageNo(pageNo);
//        pageVO.setPageSize(pageSize);
//        return pageVO;
//    }

}
