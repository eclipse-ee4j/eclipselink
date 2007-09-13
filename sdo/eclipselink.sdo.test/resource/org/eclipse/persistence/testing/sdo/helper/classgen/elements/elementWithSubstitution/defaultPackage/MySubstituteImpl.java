package defaultPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class MySubstituteImpl extends SDODataObject implements MySubstitute {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MySubstituteImpl() {}

   public defaultPackage.MyTestType getOtherTest() {
      return (defaultPackage.MyTestType)get(START_PROPERTY_INDEX + 0);
   }

   public void setOtherTest(defaultPackage.MyTestType value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

