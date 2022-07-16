//package com.wanxin.search.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * <p>
// * 标的信息表（发标）
// * </p>
// *
// * @author yuelimin
// * @version 1.0.0
// * @since 1.8
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Document(indexName = "wanxinp2p_project", type = "projectIndex")
//public class ProjectIndex implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 主键
//     */
//    @Id
//    private Long id;
//
//    /**
//     * 发标人用户标识
//     */
//    @Field(type = FieldType.Long)
//    private Long consumerid;
//
//    /**
//     * 发标人用户编码
//     */
//    @Field(type = FieldType.Keyword)
//    private String userno;
//
//    /**
//     * 标的编码
//     */
//    @Field(type = FieldType.Keyword)
//    private String projectno;
//
//    /**
//     * 标的名称
//     */
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String name;
//
//    /**
//     * 标的描述
//     */
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String description;
//
//    /**
//     * 标的类型
//     */
//    @Field(type = FieldType.Keyword)
//    private String type;
//
//    /**
//     * 标的期限(单位:天)
//     */
//    @Field(type = FieldType.Integer)
//    private Integer period;
//
//    /**
//     * 年化利率(投资人视图)
//     */
//    @Field(type = FieldType.Double)
//    private BigDecimal annualRate;
//
//    /**
//     * 年化利率(借款人视图)
//     */
//    @Field(type = FieldType.Double)
//    private BigDecimal borrowerannualrate;
//
//    /**
//     * 年化利率(平台佣金, 利差)
//     */
//    @Field(type = FieldType.Keyword)
//    private BigDecimal commissionannualrate;
//
//    /**
//     * 还款方式
//     */
//    @Field(type = FieldType.Keyword)
//    private String repaymentway;
//
//    /**
//     * 募集金额
//     */
//    @Field(type = FieldType.Double)
//    private BigDecimal amount;
//
//    /**
//     * 标的状态
//     * <p>
//     * COLLECTING: 募集中 1
//     * FULLY: 满标 2
//     * REPAYING: 还款中 3
//     * MISCARRY: 流标 4
//     */
//    @Field(type = FieldType.Keyword)
//    private String projectstatus;
//
//    /**
//     * 创建时间
//     */
//    @Field(type = FieldType.Date)
//    // private LocalDateTime createDate;
//    private Date createdate;
//
//    /**
//     * 可用状态
//     */
//    @Field(type = FieldType.Keyword)
//    private Integer status;
//
//    /**
//     * 是否是债权出让标
//     */
//    @Field(type = FieldType.Keyword)
//    private Integer isassignment;
//
//    /**
//     * 请求流水号
//     */
//    @Field(type = FieldType.Keyword)
//    private String requestno;
//
//    /**
//     * 修改时间
//     */
//    @Field(type = FieldType.Date)
//    private Date modifydate;
//}
