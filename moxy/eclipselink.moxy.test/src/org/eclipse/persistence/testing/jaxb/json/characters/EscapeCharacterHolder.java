/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EscapeCharacterHolder {
	public String stringValue;
	public List<Character> characters;
	
	public EscapeCharacterHolder(){
		characters = new ArrayList<Character>();		
	}
	
	public boolean equals(Object obj){
		if(obj instanceof EscapeCharacterHolder){
			if(!stringValue.equals(((EscapeCharacterHolder)obj).stringValue)){
			   return false;
			}
			return compareLists(characters, ((EscapeCharacterHolder)obj).characters);
		}
		
		return false;
	}
	
	private boolean compareLists(List list1, List list2){
		if(list1.size() != list2.size()){
			return false;
		}
		for(int i=0;i<list1.size(); i++){
			if(!list1.get(i).equals(list2.get(i))){
				return false;
			}
		}
		return true;
	}
}
