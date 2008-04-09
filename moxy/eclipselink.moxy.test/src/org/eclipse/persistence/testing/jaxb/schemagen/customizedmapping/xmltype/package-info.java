@XmlSchema( 
     namespace = "http://www.example.com/BaseType",
     xmlns = { @XmlNs( prefix = "bt", 
                       namespaceURI="http://www.example.com/BaseType") }
          ) 
@XmlAccessorType(PUBLIC_MEMBER)
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype;

import javax.xml.bind.annotation.XmlSchema;          
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlAccessorType;
import static javax.xml.bind.annotation.XmlAccessType.PUBLIC_MEMBER; 
