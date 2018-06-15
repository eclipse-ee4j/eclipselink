/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - September 17, 2009
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.staticclasses;

import org.eclipse.persistence.sdo.SDODataObject;

public class CompanyTypeImpl extends SDODataObject implements CompanyType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public CompanyTypeImpl() {}

   public java.lang.String getCompanyName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setCompanyName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public boolean getPublic() {
      return getBoolean(START_PROPERTY_INDEX + 1);
   }

   public void setPublic(boolean value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

