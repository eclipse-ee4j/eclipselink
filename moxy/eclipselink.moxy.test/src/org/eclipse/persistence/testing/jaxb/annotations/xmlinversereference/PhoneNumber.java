package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class PhoneNumber implements Linkable {

    public String number;
    
    Customer customer;

    @XmlInverseReference(mappedBy="phoneNumber")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    private String link;
    
    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return link;
    }

    @Override
    public void setLink(String link) {
        // TODO Auto-generated method stub
        this.link = link;
    }


}