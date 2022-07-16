//package com.wanxin.search.mapper;
//
//import com.wanxin.search.entity.ProjectIndex;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * @Author Administrator
// * @Date 2022/7/2 21:06
// */
//@Repository
//public interface ProjectIndexMapper extends ElasticsearchRepository<ProjectIndex, Long> {
//
////    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
//    List<ProjectIndex> findByPeriodBetween(Integer low, Integer high);
//}
