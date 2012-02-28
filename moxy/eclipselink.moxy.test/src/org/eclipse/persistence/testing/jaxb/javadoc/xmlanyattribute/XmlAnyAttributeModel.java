/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyattribute;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement
public class XmlAnyAttributeModel {

	@XmlAnyAttribute
	public Map<QName,Object> any;
	public String title;

	    public Map<QName,Object> getAny(){
	        if( any == null ){
	            any = new HashMap<QName,Object>();
	        }
	        return any;
	    }
	    
	    @XmlElement
	    public String getTitle(){
	        return title;
	    }
	    public void setTitle( String value ){
	        title = value;
	    }

     	public boolean equals(Object object) {
     		XmlAnyAttributeModel x = ((XmlAnyAttributeModel)object);
    		//return x.getTitle().equals(this.getTitle());
     		return x.getAny().equals(this.getAny()) && x.getTitle().equals(this.getTitle());
    	}
}
