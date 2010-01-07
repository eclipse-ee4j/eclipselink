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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr.sxf;

// Javase imports
import java.util.ArrayList;
import org.w3c.dom.Node;

// Java extension imports

// EclipseLink imports

public class SimpleXMLFormatModel {

    public ArrayList<Node> simpleXML;

    public SimpleXMLFormatModel() {
        super();
        simpleXML = new ArrayList<Node>();
    }

}
