package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MyTestTypeImpl() {}

   public int getMyTest1() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest1(int value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

