//this is the preImplPackage event
package org.example;

//this is the preImplImports event
import org.eclipse.persistence.sdo.SDODataObject;

//this is the preImplClass event
public class PurchaseOrderTypeImpl extends SDODataObject implements PurchaseOrderType {

//this is the preImplAttributes event
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

   public org.example.AddressType getShipTo() {
      return (org.example.AddressType)get(START_PROPERTY_INDEX + 1);
   }

   public void setShipTo(org.example.AddressType value) {
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
   public org.example.AddressType getBillTo() {
      return (org.example.AddressType)get(START_PROPERTY_INDEX + 2);
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
   public void setBillTo(org.example.AddressType value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public org.example.Items getItems() {
      return (org.example.Items)get(START_PROPERTY_INDEX + 4);
   }

   public void setItems(org.example.Items value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getOrderDate() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setOrderDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

