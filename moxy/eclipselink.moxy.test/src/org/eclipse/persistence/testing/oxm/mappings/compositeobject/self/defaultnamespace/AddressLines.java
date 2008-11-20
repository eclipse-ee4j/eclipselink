package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

public class AddressLines {
    public String addressLine1;
    public String addressLine2;
    public String addressLine3;
    public String addressLine4;

    public boolean equals(Object obj) {
        AddressLines objAdd;
        try {
            objAdd = (AddressLines) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        
        if ((!addressLine1.equals(objAdd.addressLine1)) ||
                (!addressLine2.equals(objAdd.addressLine2)) ||
                (!addressLine3.equals(objAdd.addressLine3)) ||
                (!addressLine4.equals(objAdd.addressLine4))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return addressLine1 + ", " + addressLine2 + ", " +addressLine3 + ", " +addressLine4;
    }
}
