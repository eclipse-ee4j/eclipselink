package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class Sub1Impl extends org.example.RootImpl implements Sub1 {

   public static final int START_PROPERTY_INDEX = org.example.RootImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public Sub1Impl() {}

   public java.lang.String getSub1Elem() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setSub1Elem(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getSub1Attr() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setSub1Attr(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

