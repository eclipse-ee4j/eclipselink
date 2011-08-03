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
 *     Denise Smith - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name = "test")
public class TestClass
{
    private ArrayList<String> itemList = new ArrayList<String>();

	@XmlPath("item/@type")
    public ArrayList<String> getItemList(){
		return itemList;
	}
    
	public void setItemList(ArrayList<String> itemList) {
	   this.itemList = itemList;
    }
	
    public boolean equals(Object obj){
    	if(obj instanceof TestClass){
    		TestClass testObj = ((TestClass)obj);
    		if(testObj == this){
    			return true;
    		}    		
    		if(getItemList().size() != testObj.getItemList().size()){
    			return false;    				
    		}
    		if(!getItemList().containsAll(testObj.getItemList())){
    			return false;
    		}
    		
    		return true;
    	}
    	return false;
    }
}
