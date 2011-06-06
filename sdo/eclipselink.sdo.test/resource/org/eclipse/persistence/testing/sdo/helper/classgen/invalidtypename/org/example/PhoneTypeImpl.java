package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class PhoneTypeImpl extends SDODataObject implements PhoneType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public PhoneTypeImpl() {}

   public java.lang.String getNumber() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setNumber(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

