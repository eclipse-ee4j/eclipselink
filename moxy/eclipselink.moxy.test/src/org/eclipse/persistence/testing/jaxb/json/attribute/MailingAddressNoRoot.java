package org.eclipse.persistence.testing.jaxb.json.attribute;

public class MailingAddressNoRoot extends AddressNoRoot {
   public String postalCode;
   
   public boolean equals(Object obj) {
	   MailingAddressNoRoot add;
       try {
           add = (MailingAddressNoRoot) obj;
       } catch (ClassCastException cce) {
           return false;
       }
       if(!super.equals(obj)){
    	   return false;
       }
       if(postalCode == null){
       	if(add.postalCode != null){
       		return false;
       	}
       }else if(!postalCode.equals(add.postalCode)){
       	return false;
       }
       return true;
   }
      
       
}
