package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Reference {
	
	public String name;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reference)) {
			return false;
		}
		Reference r = (Reference)obj;
		if (name != null)
			return name.equals(r.name);
		else
			return r.name == null;
	}
	
	

}
