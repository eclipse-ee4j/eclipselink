package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public MyTestTypeImpl() {}

   public int getValue() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setValue(int value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getMyAttribute() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setMyAttribute(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

