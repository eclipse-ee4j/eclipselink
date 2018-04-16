


@XmlSchema(
  namespace = "NS", elementFormDefault = XmlNsForm.QUALIFIED,
  xmlns = {
    @XmlNs(namespaceURI = "NS", prefix = "PRE"),
    @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi") })
    package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;
    
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;