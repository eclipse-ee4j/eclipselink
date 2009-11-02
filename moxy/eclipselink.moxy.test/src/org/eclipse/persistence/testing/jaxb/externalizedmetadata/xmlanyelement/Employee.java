package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

@javax.xml.bind.annotation.XmlRootElement
public class Employee {
    public int a;
    public String b;
    
    //@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomAdapter.class)
    //@javax.xml.bind.annotation.XmlAnyElement(lax=false, value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomHandler.class)
    public java.util.List<Object> stuff;
}
