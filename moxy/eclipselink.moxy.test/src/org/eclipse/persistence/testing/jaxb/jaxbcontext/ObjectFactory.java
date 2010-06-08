package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(namespace="foons", name="foo")
    public JAXBElement<String> createFoo() {
        return new JAXBElement(new javax.xml.namespace.QName("foons", "foo"), String.class, "");
    }
}
