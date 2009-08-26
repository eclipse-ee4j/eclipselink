package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class TestTypeImpl extends SDODataObject implements TestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public TestTypeImpl() {}

   public java.lang.String getValue() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setValue(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getTester() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setTester(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

