package org.example;

public interface PurchaseOrder extends java.io.Serializable {

   public java.lang.String getPoID();

   public void setPoID(java.lang.String value);

   public org.example.USAddress getShipTo();

   public void setShipTo(org.example.USAddress value);

   public org.example.USAddress getBillTo();

   public void setBillTo(org.example.USAddress value);

   public java.util.List getComment();

   public void setComment(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

