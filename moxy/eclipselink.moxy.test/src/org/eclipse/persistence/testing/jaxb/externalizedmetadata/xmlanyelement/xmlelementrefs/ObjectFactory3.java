package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory3 {
    @XmlElementDecl(name = "a")
    public JAXBElement<Integer> createFooA(Integer i) {
        return new JAXBElement(new QName("a"), Integer.class, i);
    }

    @XmlElementDecl(name = "b", scope = FooImpl.class)
    public JAXBElement<Integer> createFooB(Integer i) {
        return new JAXBElement(new QName("b"), Integer.class, i);
    }
}
