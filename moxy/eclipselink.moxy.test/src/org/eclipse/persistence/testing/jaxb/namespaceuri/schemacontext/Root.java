package org.eclipse.persistence.testing.jaxb.namespaceuri.schemacontext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "root", propOrder = {
    "id"
})
public class Root {

    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer value) {
        this.id = value;
    }
}
