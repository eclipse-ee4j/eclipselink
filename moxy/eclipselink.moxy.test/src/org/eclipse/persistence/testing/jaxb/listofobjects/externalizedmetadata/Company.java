package org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata;

import java.util.List;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith, Nov. 19, 2009
 ******************************************************************************/
public class Company {
	
    private List departments;
    private Map departmentIdToName; 
    private Map intObjectMap;
    private Map objectStringMap;
    private Map objectIntMap;
    
	public Company(){
		departmentIdToName = new java.util.HashMap();
		intObjectMap = new java.util.TreeMap();		
		objectStringMap = new java.util.HashMap();
		objectIntMap = new java.util.HashMap<Object, Object>();
	}

	public List getDepartments() {
		return departments;
	}

	public void setDepartments(List departments) {
		this.departments = departments;
	}
	
	public boolean equals(Object compareObject){
		if(!(compareObject instanceof Company)){
			return false;
		}
		Company compareCompany = (Company)compareObject;
		if(departments == null){
			if(compareCompany.getDepartments() != null){
			    return false;
			}
		}else {
			if(compareCompany.getDepartments() == null){
				return false;
			}
			if(departments.size() != compareCompany.getDepartments().size()){
				return false;
			}
			if(!departments.containsAll(compareCompany.getDepartments())){
				return false;
			}
		}
		
		if(departmentIdToName == null){
			if(compareCompany.getDepartmentIdToName() != null){
			    return false;
			}
		}else {
			if(compareCompany.getDepartmentIdToName() == null){
				return false;
			}
			if(departmentIdToName.size() != compareCompany.getDepartmentIdToName().size()){
				return false;
			}		
			if(!departmentIdToName.entrySet().containsAll(compareCompany.getDepartmentIdToName().entrySet())){
				return false;
			}
		}
		
		if(intObjectMap == null){
			if(compareCompany.getIntObjectMap() != null){
			    return false;
			}
		}else {
			if(compareCompany.getIntObjectMap() == null){
				return false;
			}
			if(intObjectMap.size() != compareCompany.getIntObjectMap().size()){
				return false;
			}		
			if(!intObjectMap.entrySet().containsAll(compareCompany.getIntObjectMap().entrySet())){
				return false;
			}
		}
		
		if(objectStringMap == null){
			if(compareCompany.getObjectStringMap() != null){
			    return false;
			}
		}else {
			if(compareCompany.getObjectStringMap() == null){
				return false;
			}
			if(objectStringMap.size() != compareCompany.getObjectStringMap().size()){
				return false;
			}		
			if(!objectStringMap.entrySet().containsAll(compareCompany.getObjectStringMap().entrySet())){
				return false;
			}
		}
		
		if(objectIntMap == null){
			if(compareCompany.getObjectIntMap() != null){
			    return false;
			}
		}else {
			if(compareCompany.getObjectIntMap() == null){
				return false;
			}
			if(objectIntMap.size() != compareCompany.getObjectIntMap().size()){
				return false;
			}		
			if(!objectIntMap.entrySet().containsAll(compareCompany.getObjectIntMap().entrySet())){
				return false;
			}
		}
				
		return true;
		
	}

	public Map getDepartmentIdToName() {
		return departmentIdToName;
	}

	public void setDepartmentIdToName(Map departmentIdToName) {
		this.departmentIdToName = departmentIdToName;
	}

	public Map getIntObjectMap() {
		return intObjectMap;
	}

	public void setIntObjectMap(Map intObjectMap) {
		this.intObjectMap = intObjectMap;
	}

	public Map getObjectStringMap() {
		return objectStringMap;
	}

	public void setObjectStringMap(Map objectStringMap) {
		this.objectStringMap = objectStringMap;
	}

	public Map getObjectIntMap() {
		return objectIntMap;
	}

	public void setObjectIntMap(Map objectIntMap) {
		this.objectIntMap = objectIntMap;
	}
}
