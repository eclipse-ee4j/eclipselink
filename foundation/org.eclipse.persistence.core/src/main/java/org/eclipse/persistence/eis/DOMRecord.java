/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.eis;

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
public interface DOMRecord extends jakarta.resource.cci.Record {
    Node getDOM();

    void setDOM(Element dom);
}
