package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends org.example.TestTypeImpl implements MyTestType {

   public static final int START_PROPERTY_INDEX = org.example.TestTypeImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MyTestTypeImpl() {}

   public java.lang.String getMyAttribute() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setMyAttribute(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

