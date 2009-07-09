package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso.other;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="myotherclass", namespace="http://www.example.com/xsd")
public class MyOtherClass {
    public int myId;
}
