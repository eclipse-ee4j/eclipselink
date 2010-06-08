package com.example.myPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class USAddressImpl extends SDODataObject implements USAddress {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public USAddressImpl() {}

   public java.lang.String getName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getStreet() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setStreet(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.lang.String getCity() {
      return getString(START_PROPERTY_INDEX + 2);
   }

   public void setCity(java.lang.String value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getState() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setState(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.math.BigDecimal getZip() {
      return getBigDecimal(START_PROPERTY_INDEX + 4);
   }

   public void setZip(java.math.BigDecimal value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getCountry() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setCountry(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

