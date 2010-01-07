/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Mar 18/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

public interface NonDataTypeA {

   public org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeB getB();

   public void setB(org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeB value);

   public org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeC getC();

   public void setC(org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeC value);


}

