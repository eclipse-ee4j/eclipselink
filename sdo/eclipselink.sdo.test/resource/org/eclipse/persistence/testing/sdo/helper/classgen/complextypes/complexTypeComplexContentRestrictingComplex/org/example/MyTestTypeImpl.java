package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends org.example.TestTypeImpl implements MyTestType {

   public static final int START_PROPERTY_INDEX = org.example.TestTypeImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public MyTestTypeImpl() {}

   public java.lang.String getMyTest1() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest1(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getMyTest2() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setMyTest2(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

