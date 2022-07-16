package com.wanxin.transaction.agent;

import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.api.transaction.model.ProjectQueryDTO;
import com.wanxin.common.domain.CommonErrorCode;
import com.wanxin.common.domain.PageVO;
import com.wanxin.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Administrator
 * @Date 2022/6/25 0:30
 */
@FeignClient(value = "content-search-service",
        fallback = ContentSearchApiAgentFallback.class,
        configuration = {ContentSearchApiAgentConfiguration.class})
public interface ContentSearchApiAgent {

    @PostMapping(value = "/search/l/projects/indexes/q")
    RestResponse<PageVO<ProjectDTO>> queryProjectIndex(
            @RequestBody ProjectQueryDTO projectQueryDTO,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "order", required = false) String order);

}

class ContentSearchApiAgentConfiguration {
    @Bean
    public ContentSearchApiAgentFallback contentSearchApiAgentFallback() {
        return new ContentSearchApiAgentFallback();
    }
}

class ContentSearchApiAgentFallback implements ContentSearchApiAgent {

    @Override
    public RestResponse<PageVO<ProjectDTO>> queryProjectIndex(ProjectQueryDTO projectQueryDTO, Integer pageNo, Integer pageSize, String sortBy, String order) {
        return new RestResponse<PageVO<ProjectDTO>>(CommonErrorCode.E_999995.getCode(), CommonErrorCode.E_999995.getDesc());
    }
}