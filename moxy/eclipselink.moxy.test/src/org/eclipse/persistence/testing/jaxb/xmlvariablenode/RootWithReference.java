package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

@XmlRootElement(name = "root-with-reference")
public class RootWithReference {

	public String name;
	
	@XmlVariableNode("thingName")	
	public List<ThingWithCollection> things;
	
	public Reference ref;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RootWithReference)) {
			return false;
		}
		RootWithReference rwr = (RootWithReference)obj;
		return things.equals(rwr.things) && 	
				name.equals(rwr.name) &&
				ref.equals(rwr.ref);
	}
	
	
}
