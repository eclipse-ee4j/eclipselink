package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.none;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="employee-type")
public class Employee {
    @XmlElement
    public String firstName;
    public String lastName;
    private int id;

    public Employee(){
    }
    
    public Employee(int id){
    	this.id = id;
    }
    
    @XmlElement
    public int getId() {
        return id;
    }
    
    public boolean getIsSet() {
        return true;
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof Employee){
    		Employee empObj = (Employee)obj;
    		if(firstName == null){
    			if(empObj.firstName !=null){
    				return false;
    			}    				
    		}else if(!firstName.equals(empObj.firstName)){
    			return false;
    		}
    		if(lastName == null){
    			if(empObj.lastName !=null){
    				return false;
    			}    				
    		}else if(!lastName.equals(empObj.lastName)){
    			return false;
    		}
    		if(id != empObj.id){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }  
}
