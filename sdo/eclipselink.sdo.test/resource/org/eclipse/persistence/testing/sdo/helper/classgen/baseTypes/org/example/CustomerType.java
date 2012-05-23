package org.example;

public interface CustomerType extends java.io.Serializable {

   public java.lang.String getFirstName();

   public void setFirstName(java.lang.String value);

   public java.lang.String getLastName();

   public void setLastName(java.lang.String value);

   public org.example.AddressType getAddress();

   public void setAddress(org.example.AddressType value);

   public int getCustomerID();

   public void setCustomerID(int value);

   public java.lang.String getSin();

   public void setSin(java.lang.String value);


}

