package defaultPackage;

public interface PurchaseOrder {

   public java.lang.String getPoID();

   public void setPoID(java.lang.String value);

   public defaultPackage.USAddress getShipTo();

   public void setShipTo(defaultPackage.USAddress value);

   public defaultPackage.USAddress getBillTo();

   public void setBillTo(defaultPackage.USAddress value);

   public java.util.List getComment();

   public void setComment(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

