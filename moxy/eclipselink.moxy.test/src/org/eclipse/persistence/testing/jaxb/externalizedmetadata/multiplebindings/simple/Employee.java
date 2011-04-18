package org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.Address;

@XmlRootElement(name="employee")
@XmlType(name="employee-type", propOrder={"id", "name", "age", "address", "bytes"})
public class Employee extends Person {

    public String name;
    
    public int age;
    
    public String address;
    
    public byte[] bytes;
    
    public String jobTitle;
    
    public List<Department> departments;
    
    public Phone phone;
 
    public boolean equals(Object obj){
    	if(!(obj instanceof Employee)){
    		return false;
    	}
    	if(!compareString(name, ((Employee)obj).name)){
    		return false;
    	}
    	if(!compareString(address, ((Employee)obj).address)){
    		return false;
    	}
    	
    	if(!compareString(id, ((Employee)obj).id)){
    		return false;
    	}  
    	
    	if(!compareString(jobTitle, ((Employee)obj).jobTitle)){
    		return false;
    	} 
    	
    	if(age!=((Employee)obj).age){
    		return false;
    	}
   
        if (bytes == null) {
            if (((Employee)obj).bytes != null) {
                return false;
            }
        } else if (((Employee)obj).bytes == null) {
            return false;
        }
        if (!Arrays.equals(bytes, ((Employee)obj).bytes)) {
            return false;
        }
        
        if (phone == null){
        	if(((Employee)obj).phone != null) {
               return false;
        	}
        }else{
        	if(!phone.equals(((Employee)obj).phone)){
        		return false;
        	}

        }
        
    	
    	return true;
    }
    
    private boolean compareString(String control, String test){
    	if(control == null){
    		if(test != null){
    			return false;
    		}
    	}else{
    		if(test == null){
    			return false;
    		}else{
    			if(!control.equals(test)){
    				return false;
    			}
    		}
    	}
    	return true;
    }
}
