package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class PersonTypeImpl extends SDODataObject implements PersonType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public PersonTypeImpl() {}

   public java.lang.String getFirstName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setFirstName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getLastName() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setLastName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

