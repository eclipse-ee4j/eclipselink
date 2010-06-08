//this is the preImplPackage event
package org.example;

//this is the preImplImports event
import org.eclipse.persistence.sdo.SDODataObject;

//this is the preImplClass event
public class ItemsImpl extends SDODataObject implements Items {

//this is the preImplAttributes event
   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public ItemsImpl() {}

   public java.util.List getItem() {
      return getList(START_PROPERTY_INDEX + 0);
   }

   public void setItem(java.util.List value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

