/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */

package com.fishqq.biz.models;

import java.util.Date;

/**
 * 业务板块DO 在原来的基础上增加空间和数据域的个数
 *
 * @author tianda.lt
 * @date 2017/8/8 16:07
 */
public class BizUnit {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 可以识别的名称
     */
    private String name;

    /**
     * 中文名
     */
    private String cn;

    /**
     * 描述信息
     */
    private String des;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 最后一次修改人
     */
    private String lastModifier;

    /**
     * 责任人
     */
    private String owner;

    /**
     * 状态
     */
    private String status;

    /**
     * 业务板块的公共空间的默认catalog，针对odps则为project
     */
    private String pubNsDefaultProject;

    /**
     * 包含空间的个数
     */
    private Integer nameSpaceNum;

    /**
     * 包含数据域的个数
     */
    private Integer domainNum;

    /**
     * 默认项目Id
     */
    private Long defaultProjectId;

    /**
     * 租户Id
     */
    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPubNsDefaultProject() {
        return pubNsDefaultProject;
    }

    public void setPubNsDefaultProject(String pubNsDefaultProject) {
        this.pubNsDefaultProject = pubNsDefaultProject;
    }

    public Integer getNameSpaceNum() {
        return nameSpaceNum;
    }

    public void setNameSpaceNum(Integer nameSpaceNum) {
        this.nameSpaceNum = nameSpaceNum;
    }

    public Integer getDomainNum() {
        return domainNum;
    }

    public void setDomainNum(Integer domainNum) {
        this.domainNum = domainNum;
    }

    public Long getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
