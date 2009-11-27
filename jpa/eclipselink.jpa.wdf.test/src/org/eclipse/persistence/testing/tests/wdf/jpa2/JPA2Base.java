/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa2;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;

public abstract class JPA2Base extends AbstractBaseTest {

    public JPA2Base() {
        super("jpa2testmodel");
    }

    final protected String[] getClearableTableNames() {
        return new String[0];
    }

}
