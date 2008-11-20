package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

public class Address {
    public String attentionOfName;
    public String careOfName;
    public AddressLines addressLines;
    public String city;
    public String state;
    public String postalCode;
    public String countryCode;
    
    public boolean equals(Object obj) {
        Address objAdd;
        try {
            objAdd = (Address) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        
        if ((!attentionOfName.equals(objAdd.attentionOfName)) ||
                (!careOfName.equals(objAdd.careOfName)) ||
                (!city.equals(objAdd.city)) ||
                (!state.equals(objAdd.state)) ||
                (!postalCode.equals(objAdd.postalCode)) ||
                (!countryCode.equals(objAdd.countryCode)) ||
                (!addressLines.equals(objAdd.addressLines))) {
            return false;
        }
        return true;
    }
    
    public String toString() {
        return "Address: " + attentionOfName + ", " + careOfName + ", addressLines[" + addressLines + "], " + city + ", " + state + ", " + postalCode + ", " +countryCode;
    }
}
