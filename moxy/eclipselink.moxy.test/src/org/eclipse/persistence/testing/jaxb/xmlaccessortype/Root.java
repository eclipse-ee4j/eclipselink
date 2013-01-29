package org.eclipse.persistence.testing.jaxb.xmlaccessortype;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    public String name;
    
    private ChildInterface i;
}
