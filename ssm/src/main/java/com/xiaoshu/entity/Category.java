package com.xiaoshu.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "contentcategory")
public class Category implements Serializable {
    @Id
    @Column(name = "contentCategoryId")
    private Integer contentcategoryid;

    private String categoryname;

    private static final long serialVersionUID = 1L;

    /**
     * @return contentCategoryId
     */
    public Integer getContentcategoryid() {
        return contentcategoryid;
    }

    /**
     * @param contentcategoryid
     */
    public void setContentcategoryid(Integer contentcategoryid) {
        this.contentcategoryid = contentcategoryid;
    }

    /**
     * @return categoryname
     */
    public String getCategoryname() {
        return categoryname;
    }

    /**
     * @param categoryname
     */
    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname == null ? null : categoryname.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", contentcategoryid=").append(contentcategoryid);
        sb.append(", categoryname=").append(categoryname);
        sb.append("]");
        return sb.toString();
    }
}