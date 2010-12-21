package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class LineItemTypeImpl extends SDODataObject implements LineItemType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public LineItemTypeImpl() {}

   public java.lang.String getProductName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setProductName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getQuantity() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setQuantity(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public float getUSPrice() {
      return getFloat(START_PROPERTY_INDEX + 2);
   }

   public void setUSPrice(float value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getShipDate() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setShipDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getPartNum() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setPartNum(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

