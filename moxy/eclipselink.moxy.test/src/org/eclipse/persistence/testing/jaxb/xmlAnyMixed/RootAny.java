package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootAny", propOrder = {
        "content"
})
public class RootAny {

    @XmlAnyElement
    protected List<Element> content;

    public List<Element> getContent() {
        if (content == null) {
            content = new ArrayList<Element>();
        }
        return this.content;
    }
}