package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType
@XmlType(name = "RootAnyMixed", propOrder = {
        "content"
})
public class RootAnyMixed {

    private List<Object> content;

    @XmlMixed
    @XmlAnyElement
    public List<Object> getContent() {
        return content;
    }

    public void setContent(List<Object> content) {
        this.content = content;
    }
}