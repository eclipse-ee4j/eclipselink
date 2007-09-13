package defaultPackage;

public interface PurchaseOrder {

   public defaultPackage.USAddress getShipTo();

   public void setShipTo(defaultPackage.USAddress value);

   public defaultPackage.USAddress getBillTo();

   public void setBillTo(defaultPackage.USAddress value);

   public int getQuantity();

   public void setQuantity(int value);

   public java.lang.String getPartNum();

   public void setPartNum(java.lang.String value);


}

