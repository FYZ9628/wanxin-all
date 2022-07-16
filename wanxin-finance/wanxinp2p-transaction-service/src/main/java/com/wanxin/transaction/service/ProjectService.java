package com.wanxin.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxin.api.transaction.model.ProjectDTO;
import com.wanxin.api.transaction.model.ProjectInvestDTO;
import com.wanxin.api.transaction.model.ProjectQueryDTO;
import com.wanxin.api.transaction.model.TenderDTO;
import com.wanxin.api.transaction.model.TenderOverviewDTO;
import com.wanxin.common.domain.PageVO;
import com.wanxin.transaction.entity.Project;

import java.util.List;

/**
 * @Author Administrator
 * @Date 2022/6/21 23:27
 */
public interface ProjectService extends IService<Project> {

    /**
     * 查询借款人发标(也就是借钱)资格
     *
     * @return 是否有资格发标-0 无资格 1 有资格
     */
    Integer queryQualifications();

    /**
     * 修改标的状态为还款中
     *
     * @param project
     * @return
     */
    Boolean updateProjectStatusAndStartRepayment(Project project);

    /**
     * 审核标的满标放款
     *
     * @param id 标的id
     * @param approveStatus 审核结果：通过/拒绝
     * @param commission 佣金（手续费）
     * @return String
     */
    String loansApprovalStatus(Long id, String approveStatus, String commission);

    /**
     * 用户投标
     *
     * @param projectInvestDTO
     * @return
     */
    TenderDTO createTender(ProjectInvestDTO projectInvestDTO);

    /**
     * 根据标的id查询投标记录
     *
     * @param id
     * @return
     */
    List<TenderOverviewDTO> queryTendersByProjectId(Long id);

    /**
     * 通过ids获取多个标的
     *
     * @param ids
     * @return
     */
    List<ProjectDTO> queryProjectsIds(String ids);

    /**
     * 根据分页条件检索标的信息(ES文档查询)
     *
     * @param projectQueryDTO 查询请求体
     * @param pageNo          页码
     * @param pageSize        数据条数
     * @param sortBy          排序
     * @param order           顺序
     * @return
     */
    PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order,
                                     Integer pageNo, Integer pageSize, String sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id            标的id
     * @param approveStatus 状态
     * @return String
     */
    String projectsApprovalStatus(Long id, String approveStatus);

    /**
     * 根据分页条件检索标的信息
     *
     * @param projectQueryDTO 查询实体
     * @param pageNo          页码
     * @param pageSize        条数
     * @return
     */
    PageVO<ProjectDTO> queryProjectsByQueryDTO(ProjectQueryDTO projectQueryDTO, Integer pageNo, Integer pageSize);

    /**
     * 借款人发标(创建标的)
     *
     * @param projectDTO 标的信息
     * @return
     */
    ProjectDTO createProject(ProjectDTO projectDTO);
}
