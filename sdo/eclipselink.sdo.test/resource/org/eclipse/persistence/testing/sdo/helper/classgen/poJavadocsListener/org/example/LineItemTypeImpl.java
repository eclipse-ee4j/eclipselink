//this is the preImplPackage event
package org.example;

//this is the preImplImports event
import org.eclipse.persistence.sdo.SDODataObject;

//this is the preImplClass event
public class LineItemTypeImpl extends SDODataObject implements LineItemType {

//this is the preImplAttributes event
   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 6;

   public LineItemTypeImpl() {}

   public java.lang.String getProductName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setProductName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   /**
    * Gets productSize.
    * return This is documentation for the productSize element inside the complextype called LineItemType
    */
   public java.lang.String getProductSize() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   /**
    * Sets productSize.
    * param value This is documentation for the productSize element inside the complextype called LineItemType
    */
   public void setProductSize(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public int getQuantity() {
      return getInt(START_PROPERTY_INDEX + 2);
   }

   public void setQuantity(int value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   /**
    * Gets USPrice.
    * return This is documentation for the USPrice element inside the complextype called LineItemType
    */
   public float getUSPrice() {
      return getFloat(START_PROPERTY_INDEX + 3);
   }

   /**
    * Sets USPrice.
    * param value This is documentation for the USPrice element inside the complextype called LineItemType
    */
   public void setUSPrice(float value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getShipDate() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setShipDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }

   /**
    * Gets partNum.
    * return This is documentation for the partNum attribute inside the complextype called LineItemType
    * This is the second documentation for the partNum attribute inside the complextype called LineItemType
    */
   public java.lang.String getPartNum() {
      return getString(START_PROPERTY_INDEX + 6);
   }

   /**
    * Sets partNum.
    * param value This is documentation for the partNum attribute inside the complextype called LineItemType
    * This is the second documentation for the partNum attribute inside the complextype called LineItemType
    */
   public void setPartNum(java.lang.String value) {
      set(START_PROPERTY_INDEX + 6 , value);
   }


}

