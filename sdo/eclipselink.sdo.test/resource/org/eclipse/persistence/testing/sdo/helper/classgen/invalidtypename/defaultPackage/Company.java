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

public interface Company {

   public defaultPackage.InvalidClassname getInvalidClassname();

   public void setInvalidClassname(defaultPackage.InvalidClassname value);

   public defaultPackage.InvalidClassname1 getInvalidClassname1();

   public void setInvalidClassname1(defaultPackage.InvalidClassname1 value);

   public java.util.List getPorder();

   public void setPorder(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getName();

   public void setName(java.lang.String value);


}

