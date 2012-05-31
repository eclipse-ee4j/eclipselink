package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "someNamespace")
@XmlRootElement
public class BaseTypeWithRootElement {
	public boolean equals(Object obj){
		if(!(obj instanceof BaseType)){
			return false;
		}
		return true;
	}
}
