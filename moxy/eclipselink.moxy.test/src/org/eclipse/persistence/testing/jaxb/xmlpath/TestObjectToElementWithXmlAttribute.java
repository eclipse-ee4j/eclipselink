package org.eclipse.persistence.testing.jaxb.xmlpath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;
@XmlRootElement(name="root")
public class TestObjectToElementWithXmlAttribute {

	@XmlAttribute
	@XmlPath("something/theFlag/text()")
	public boolean theFlag;
		
	public boolean equals(Object obj){
		if(obj instanceof TestObjectToElementWithXmlAttribute){
			return theFlag == ((TestObjectToElementWithXmlAttribute)obj).theFlag; 
		}
		return false;
	}
}