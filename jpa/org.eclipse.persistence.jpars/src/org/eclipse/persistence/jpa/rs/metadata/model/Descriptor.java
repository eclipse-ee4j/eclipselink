package org.eclipse.persistence.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"name", "type", "attributes", "linkTemplates", "queries"})
public class Descriptor {
    
    protected String name = null;
    protected String type = null;
    protected List<LinkTemplate> linkTemplates = new ArrayList<LinkTemplate>();
    protected List<Attribute> attributes = new ArrayList<Attribute>();
    protected List<Query> queries = new ArrayList<Query>();
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<LinkTemplate> getLinkTemplates() {
        return linkTemplates;
    }
    public void setLinkTemplates(List<LinkTemplate> linkTemplates) {
        this.linkTemplates = linkTemplates;
    }
    public List<Attribute> getAttributes() {
        return attributes;
    }
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
    public List<Query> getQueries() {
        return queries;
    }
    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }
}
