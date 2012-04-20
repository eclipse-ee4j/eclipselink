package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="otherns")
public class Bar {
    public String id;
    
    public boolean equals(Object obj){
    	if(!(obj instanceof Bar)){
    		return false;
    	}
    	return id.equals(((Bar)obj).id);
    }
}
