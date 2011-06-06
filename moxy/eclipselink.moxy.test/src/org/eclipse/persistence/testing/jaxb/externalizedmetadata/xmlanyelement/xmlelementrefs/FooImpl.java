package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="foo")
public class FooImpl implements Foo {
    private List<Object> others;

    @XmlAnyElement(lax=true)
    @XmlElementRefs({
        @XmlElementRef(name="a", type=JAXBElement.class),
        @XmlElementRef(name="b", type=JAXBElement.class)
    })
    @XmlMixed
    public List<Object> getOthers() {
        return others;
    }

    public void setOthers(List<Object> others) {
        this.others = others;
    }
}