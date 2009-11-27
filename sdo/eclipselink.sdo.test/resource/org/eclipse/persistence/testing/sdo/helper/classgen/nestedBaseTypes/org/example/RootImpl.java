package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class RootImpl extends SDODataObject implements Root {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public RootImpl() {}

   public java.util.List getRootA() {
      return getList(START_PROPERTY_INDEX + 0);
   }

   public void setRootA(java.util.List value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getRootB() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setRootB(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

