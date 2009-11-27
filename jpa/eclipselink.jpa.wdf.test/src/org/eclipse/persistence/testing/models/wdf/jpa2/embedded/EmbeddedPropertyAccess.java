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

package org.eclipse.persistence.testing.models.wdf.jpa2.embedded;

import javax.persistence.Access;
import javax.persistence.Embeddable;
import static javax.persistence.AccessType.PROPERTY;

@Embeddable
@Access(PROPERTY)
public class EmbeddedPropertyAccess {

    public EmbeddedPropertyAccess() {
    }

    private int value;

    public int getData() {
        return value;
    }

    public void setData(int data) {
        value = data;
    }

}
