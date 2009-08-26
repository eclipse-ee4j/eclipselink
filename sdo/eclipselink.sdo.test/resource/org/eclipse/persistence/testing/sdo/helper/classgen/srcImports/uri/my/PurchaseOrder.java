package uri.my;

public interface PurchaseOrder {

   public uri2.my.USAddress getShipTo();

   public void setShipTo(uri2.my.USAddress value);

   public uri2.my.USAddress getBillTo();

   public void setBillTo(uri2.my.USAddress value);

   public int getQuantity();

   public void setQuantity(int value);

   public java.lang.String getPartNum();

   public void setPartNum(java.lang.String value);


}

