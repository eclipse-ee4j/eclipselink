/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.util.Iterator;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class PlsqlElemHelper extends SequencedInfo {
    public PlsqlElemHelper(UserArguments r) throws java.sql.SQLException {
        packageName = r.PACKAGE_NAME;
        typeOwner = r.TYPE_OWNER;
        typeName = r.TYPE_NAME;
        typeSubname = r.TYPE_SUBNAME;
        objectName = r.OBJECT_NAME;
        dataType = r.DATA_TYPE;
        sequence = r.sequence;
        position = r.POSITION;
        dataLevel = r.DATA_LEVEL;
        overload = r.OVERLOAD;
        dataLength = r.DATA_LENGTH;
        dataPrecision = r.DATA_PRECISION;
        dataScale = r.DATA_SCALE;
    }

    public static PlsqlElemHelper[] getPlsqlElemHelper(Iterator iter) throws java.sql.SQLException {
        Vector a = new Vector();
        while (iter.hasNext()) {
            a.addElement(new PlsqlElemHelper((UserArguments)iter.next()));
        }
        if (a.size() == 0) {
            return null;
        }
        PlsqlElemHelper[] r = new PlsqlElemHelper[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (PlsqlElemHelper)a.elementAt(i);
        }
        return (PlsqlElemHelper[])reorder(r);
    }

    public String toString() {
        return sequence + ", " + dataLevel + ", " + position + ", " + packageName + ", "
            + typeOwner + ", " + typeName + ", " + typeSubname + ", " + objectName + ", "
            + dataType + ", " + overload;
        // + ", " + DATA_LENGTH
        // + ", " + DATA_PRECISION
        // + ", " + DATA_SCALE
    }

    public String packageName;
    public String typeOwner;
    public String typeName;
    public String typeSubname;
    public String objectName;
    public String dataType;
    public String overload;
    public int dataLength;
    public int dataPrecision;
    public int dataScale;
    public int dataLevel;
    public int position;
}
