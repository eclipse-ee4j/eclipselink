package org.eclipse.persistence.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"queryName", "returnTypes", "linkTemplate", "jpql"})
public class Query {
    protected String queryName;
    protected String jpql;
    protected LinkTemplate linkTemplate;
    protected List<String> returnTypes = new ArrayList<String>();
    
    public Query(){}
    
    public Query(String queryName, String jpql, LinkTemplate linkTemplate){
        this.queryName = queryName;
        this.jpql = jpql;
        this.linkTemplate = linkTemplate;
    }
    
    public String getQueryName() {
        return queryName;
    }
    public void setQueryName(String queryName) {
        this.queryName = queryName;
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

    public List<String> getReturnTypes() {
        return returnTypes;
    }

    public void setReturnTypes(List<String> returnTypes) {
        this.returnTypes = returnTypes;
    }
}
