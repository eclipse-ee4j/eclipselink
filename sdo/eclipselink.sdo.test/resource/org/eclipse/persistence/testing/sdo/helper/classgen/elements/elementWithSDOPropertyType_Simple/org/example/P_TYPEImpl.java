package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class P_TYPEImpl extends SDODataObject implements P_TYPE {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public P_TYPEImpl() {}

   public java.lang.String getPname() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setPname(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getPid() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setPid(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

