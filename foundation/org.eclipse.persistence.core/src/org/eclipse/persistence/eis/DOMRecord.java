/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.eis;

import javax.resource.cci.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p><code>DOMRecord</code> is an extension of the JCA Record interface that 
 * provides support for XML data.  This is required as JCA currently has no 
 * formal support for XML records.  A JCA adapter will normally have its own 
 * XML/DOM record interface;  the TopLink record <code>EISDOMRecord</code> 
 * implements this interface and can be constructed with a DOM instance 
 * retrieved from the adapter XML/DOM record and converted in the platform.
 *
 * @see EISDOMRecord
 * 
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface DOMRecord extends Record {
    public Node getDOM();

    public void setDOM(Element dom);
}
