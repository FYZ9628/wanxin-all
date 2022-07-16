package com.wanxin.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanxin.transaction.entity.Tender;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于操作投标信息的mapper接口
 *
 * @Author Administrator
 * @Date 2022/6/25 13:01
 */
@Repository
public interface TenderMapper extends BaseMapper<Tender> {
    /**
     * 根据标的id,获取标的已投金额,如果未投返回0.0
     *
     * @param id
     * @return 用 list 接收数据是因为用了分库分表的技术，诺每个表都能查到数据，每个表返回一个sum值，list 接收后再计算最后的总和
     */
    @Select("SELECT IFNULL(SUM(AMOUNT), 0.0) FROM tender WHERE PROJECT_ID = #{id} AND STATUS = 1")
    List<BigDecimal> selectAmountInvestedByProjectId(Long id);
}
