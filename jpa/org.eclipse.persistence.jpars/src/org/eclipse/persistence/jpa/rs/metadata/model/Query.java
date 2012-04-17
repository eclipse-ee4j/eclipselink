package org.eclipse.persistence.jpa.rs.metadata.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Query {
    protected String queryName;
    protected String referenceType;
    protected String jpql;
    protected LinkTemplate linkTemplate;
    
    public Query(){}
    
    public Query(String queryName, String referenceType, String jpql, LinkTemplate linkTemplate){
        this.queryName = queryName;
        this.referenceType = referenceType;
        this.jpql = jpql;
        this.linkTemplate = linkTemplate;
    }
    
    public String getQueryName() {
        return queryName;
    }
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }
    public String getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }
    public String getJpql() {
        return jpql;
    }
    public void setJpql(String jpql) {
        this.jpql = jpql;
    }
    public LinkTemplate getLinkTemplate() {
        return linkTemplate;
    }
    public void setLinkTemplate(LinkTemplate linkTemplate) {
        this.linkTemplate = linkTemplate;
    }
}
