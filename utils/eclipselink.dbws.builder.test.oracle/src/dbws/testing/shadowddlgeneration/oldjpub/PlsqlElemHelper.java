/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class PlsqlElemHelper extends SequencedInfo {

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

    public PlsqlElemHelper(UserArguments r) throws SQLException {
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

    public static PlsqlElemHelper[] getPlsqlElemHelper(Iterator<ViewRow> iter) throws SQLException {
        ArrayList<PlsqlElemHelper> a = new ArrayList<PlsqlElemHelper>();
        while (iter.hasNext()) {
            a.add(new PlsqlElemHelper((UserArguments)iter.next()));
        }
        if (a.size() == 0) {
            return null;
        }
        PlsqlElemHelper[] r = a.toArray(new PlsqlElemHelper[a.size()]);
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
}
