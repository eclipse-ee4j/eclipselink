package defaultPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class PurchaseOrderTypeImpl extends SDODataObject implements PurchaseOrderType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public PurchaseOrderTypeImpl() {}

   /**
    * Gets theID.
    * return This is documentation for element ref theID inside the complextype called PurchaseOrder
    */
   public java.lang.String getTheID() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   /**
    * Sets theID.
    * param value This is documentation for element ref theID inside the complextype called PurchaseOrder
    */
   public void setTheID(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public defaultPackage.AddressType getShipTo() {
      return (defaultPackage.AddressType)get(START_PROPERTY_INDEX + 1);
   }

   public void setShipTo(defaultPackage.AddressType value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   /**
    * Gets billTo.
    * return This is documentation for the billTo element and has newlines now
    * inside the and now
    * complextype called PurchaseOrderType and again now
    * This is the second documentation for the billTo element and has newlines now
    * inside the and now
    * complextype called PurchaseOrderType and again now
    */
   public defaultPackage.AddressType getBillTo() {
      return (defaultPackage.AddressType)get(START_PROPERTY_INDEX + 2);
   }

   /**
    * Sets billTo.
    * param value This is documentation for the billTo element and has newlines now
    * inside the and now
    * complextype called PurchaseOrderType and again now
    * This is the second documentation for the billTo element and has newlines now
    * inside the and now
    * complextype called PurchaseOrderType and again now
    */
   public void setBillTo(defaultPackage.AddressType value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public defaultPackage.Items getItems() {
      return (defaultPackage.Items)get(START_PROPERTY_INDEX + 4);
   }

   public void setItems(defaultPackage.Items value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getOrderDate() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setOrderDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

