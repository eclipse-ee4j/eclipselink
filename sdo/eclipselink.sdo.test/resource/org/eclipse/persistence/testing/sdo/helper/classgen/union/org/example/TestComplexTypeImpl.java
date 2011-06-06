package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class TestComplexTypeImpl extends SDODataObject implements TestComplexType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public TestComplexTypeImpl() {}

   public java.math.BigInteger getTestElement() {
      return getBigInteger(START_PROPERTY_INDEX + 0);
   }

   public void setTestElement(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

