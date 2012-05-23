package org.example;

public interface PurchaseOrderType extends java.io.Serializable {

   public java.lang.String getTheID();

   public void setTheID(java.lang.String value);

   public org.example.AddressType getShipTo();

   public void setShipTo(org.example.AddressType value);

   public org.example.AddressType getBillTo();

   public void setBillTo(org.example.AddressType value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public org.example.Items getItems();

   public void setItems(org.example.Items value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

