package com.wanxin.api.search;

import com.wanxin.api.search.model.ProjectQueryParamsDTO;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.common.domain.PageVO;
import com.wanxin.common.domain.RestResponse;

/**
 * @Author Administrator
 * @Date 2022/6/25 0:24
 */
public interface ContentSearchAPI {
    /**
     * 检索标的
     *
     * @param projectQueryParamsDTO
     * @return
     */
    RestResponse<PageVO<ProjectDTO>> queryProjectIndex(
            ProjectQueryParamsDTO projectQueryParamsDTO,
            Integer pageNo, Integer pageSize, String sortBy, String order);
}
