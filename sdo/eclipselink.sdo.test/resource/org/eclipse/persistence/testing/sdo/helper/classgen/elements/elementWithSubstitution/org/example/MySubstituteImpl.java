package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MySubstituteImpl extends SDODataObject implements MySubstitute {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MySubstituteImpl() {}

   public org.example.MyTestType getOtherTest() {
      return (org.example.MyTestType)get(START_PROPERTY_INDEX + 0);
   }

   public void setOtherTest(org.example.MyTestType value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

