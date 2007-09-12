/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

