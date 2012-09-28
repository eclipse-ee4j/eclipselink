package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "method", "href", "rel" })
public class Link {

    public Link() {
    }

    public Link(String rel, String method, String href) {
        this.rel = rel;
        this.method = method;
        this.href = href;
    }

    private String rel;
    private String method;
    private String href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}