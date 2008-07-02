package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class TestTypeImpl extends SDODataObject implements TestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public TestTypeImpl() {}

   public java.lang.String getMyTest0() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest0(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

