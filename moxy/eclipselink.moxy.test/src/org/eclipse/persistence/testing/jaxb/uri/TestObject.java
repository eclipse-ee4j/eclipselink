/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - February 2012 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

	public URI theURI;
	public List<URI> theURIs;
	
	public boolean equals(Object compareObject){
		if(compareObject instanceof TestObject){
		    if(theURI == null){
		    	if(((TestObject)compareObject).theURI != null){
		    		return false;
		    	}
		    }else if(!theURI.equals(((TestObject)compareObject).theURI)){
		    	return false;
		    }
		    if(theURIs == null){
		    	if(((TestObject)compareObject).theURIs != null){
		    		return false;
		    	}
		    }else{
		    	if(!theURIs.containsAll(((TestObject)compareObject).theURIs) || !((TestObject)compareObject).theURIs.containsAll(theURIs)){
		    		return false;
		    	}
		    }
			
			return true;
		}		
		return false;
	}
}
