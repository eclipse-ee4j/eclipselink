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
//     bdoughan - Mar 18/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

public interface NonDataTypeA {

   public org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeB getB();

   public void setB(org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeB value);

   public org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeC getC();

   public void setC(org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl.NonDataTypeC value);


}

