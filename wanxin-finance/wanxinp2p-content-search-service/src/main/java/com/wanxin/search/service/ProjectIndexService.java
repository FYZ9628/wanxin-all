package com.wanxin.search.service;

import com.wanxin.api.search.model.ProjectQueryParamsDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.PageVO;

/**
 * 标的索引业务层接口
 *
 * @Author Administrator
 * @Date 2022/6/25 0:27
 */
public interface ProjectIndexService {
    /**
     * 标的检索
     *
     * @param queryParamsDTO 查询集
     * @param pageNo         页码
     * @param pageSize       数据条数
     * @param sortBy         排序
     * @param order          顺序(升序/降序)
     * @return
     */
    PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO queryParamsDTO,
                                         Integer pageNo, Integer pageSize,
                                         String sortBy, String order);
}
