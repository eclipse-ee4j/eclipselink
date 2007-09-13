package defaultPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MyTestTypeImpl() {}

   public defaultPackage.ElementTest getMyTest1() {
      return (defaultPackage.ElementTest)get(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest1(defaultPackage.ElementTest value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

