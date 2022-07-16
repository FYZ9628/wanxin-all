package com.wanxin.depository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 存管交易记录表
 * </p>
 *
 * @author yuelimin
 * @since 1.8
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DepositoryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private Long id;

    /**
     * 请求流水号
     */
    @TableField("REQUEST_NO")
    private String requestNo;

    /**
     * 请求类型:
     * 1.用户信息(新增, 编辑)
     * 2.绑卡信息
     */
    @TableField("REQUEST_TYPE")
    private String requestType;

    /**
     * 业务实体类型
     */
    @TableField("OBJECT_TYPE")
    private String objectType;

    /**
     * 关联业务实体标识
     */
    @TableField("OBJECT_ID")
    private Long objectId;

    /**
     * 请求时间
     */
    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    /**
     * 是否是同步调用
     */
    @TableField("IS_SYN")
    private Integer isSyn;

    /**
     * 数据同步状态
     * 0-未同步
     * 1-已同步
     */
    @TableField("REQUEST_STATUS")
    private Integer requestStatus;

    /**
     * 消息确认时间
     */
    @TableField("CONFIRM_DATE")
    private LocalDateTime confirmDate;

    /**
     * 返回数据
     */
    @TableField("RESPONSE_DATA")
    private String responseData;

    public DepositoryRecord() {
    }

    /**
     *
     * @param requestNo 请求流水
     * @param requestType 请求类型（增删改，用 DepositoryRequestTypeCode 枚举类的值）
     * @param objectType 实体类型（就是表名对应的实体类）
     * @param objectId 实体类的id（就是表的主键）
     */
    public DepositoryRecord(String requestNo, String requestType, String objectType, Long objectId) {
        this.requestNo = requestNo;
        this.requestType = requestType;
        this.objectType = objectType;
        this.objectId = objectId;
    }
}
