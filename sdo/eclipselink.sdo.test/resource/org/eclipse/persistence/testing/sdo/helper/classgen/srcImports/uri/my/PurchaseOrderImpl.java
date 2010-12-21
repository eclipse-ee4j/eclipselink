package uri.my;

import org.eclipse.persistence.sdo.SDODataObject;

public class PurchaseOrderImpl extends SDODataObject implements PurchaseOrder {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 3;

   public PurchaseOrderImpl() {}

   public uri2.my.USAddress getShipTo() {
      return (uri2.my.USAddress)get(START_PROPERTY_INDEX + 0);
   }

   public void setShipTo(uri2.my.USAddress value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public uri2.my.USAddress getBillTo() {
      return (uri2.my.USAddress)get(START_PROPERTY_INDEX + 1);
   }

   public void setBillTo(uri2.my.USAddress value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public int getQuantity() {
      return getInt(START_PROPERTY_INDEX + 2);
   }

   public void setQuantity(int value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getPartNum() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setPartNum(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }


}

