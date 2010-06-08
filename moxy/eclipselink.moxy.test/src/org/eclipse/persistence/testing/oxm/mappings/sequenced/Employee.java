/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.oxm.sequenced.*;
import org.w3c.dom.Node;

public class Employee implements SequencedObject {
	
	private String id;
	private String firstName;
    private String lastName;
	private Address address;
	private List<Setting> settings = new ArrayList<Setting>();
	private Dependent dependent;
	private Object any;
	private Object choice;
	private Node node;
	
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
        Setting nodeSetting = new Setting();
        nodeSetting.setName("fragment");
        nodeSetting.setValue(node, false);

        settings.add(nodeSetting);
		this.node = node;
	}

	public Object getChoice() {
		return choice;
	}

	public void setChoice(Object choice) {
		this.choice = choice;
	}

	public Object getAny() {
		return any;
	}

	public void setAny(Object any) {
		Setting setting = new Setting();
		setting.setValue(any, false);
		settings.add(setting);
		this.any = any;
	}

	public Dependent getDependent() {
		return dependent;
	}

	public void setDependent(Dependent dependent) {
		Setting dependentSetting = new Setting();
		dependentSetting.setName("dependent");
		dependentSetting.setValue(dependent, false);

		settings.add(dependentSetting);
		this.dependent = dependent;
	}
		
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public List<Setting> getSettings() {
		return settings;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean equals(Object object) {
	    try {
            Employee testEmployee = (Employee) object;
            /*
            if(!Comparer.equals(id, testEmployee.getId())) {
                return false;
            }
            if(!Comparer.equals(name, testEmployee.getName())) {
                return false;
            }
            */
            if(!Comparer.equals(settings, testEmployee.getSettings())) {
                return false;
            }
            return true;
	    } catch(ClassCastException e) {
	        return false;
	    }
	}
	
	public String toString() {
	    String string = "Employee(id=";
	    if(null == id) {
	        string += "null";
	    } else {
	        string += "'" + id + "'";
	    }
	    string += ", firstName=";
        if(null == firstName) {
            string += "null";
        } else {
            string += "'" + firstName + "'";
        }
        string += ", lastName=";
        if(null == lastName) {
            string += "null";
        } else {
            string += "'" + lastName + "'";
        }
	    return string;
	}
	
}
