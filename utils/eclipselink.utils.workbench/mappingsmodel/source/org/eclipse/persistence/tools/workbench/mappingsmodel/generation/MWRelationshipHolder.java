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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.generation;
import java.text.Collator;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;



/**
 * This class is used when generating descriptors
 */
public class MWRelationshipHolder
    implements Comparable
{
    private volatile MWReference reference;
    private volatile boolean foreignKeyInTargetTable;

    private volatile String relationshipType; // represented by one of the below static strings
        private static String ONE_TO_MANY = "oneToMany";
        private static String ONE_TO_ONE = "oneToOne";

    public MWRelationshipHolder(MWReference relationshipReference, boolean foreignKeyIsInTargetTable) {
        reference = relationshipReference;
        foreignKeyInTargetTable = foreignKeyIsInTargetTable;
    }
    public boolean canMapOneToMany() {
        return foreignKeyInTargetTable;
    }
    public boolean canMapOneToOne() {
        return true;
    }
    public int compareTo(Object otherObject) {
        MWRelationshipHolder otherHolder = (MWRelationshipHolder) otherObject;
        if (getReference().getName().equals(otherHolder.getReference().getName()))
            return Collator.getInstance().compare(displayString(), otherHolder.displayString());
        else
            return Collator.getInstance().compare(getReference().getName(), otherHolder.getReference().getName());
    }
    public String displayString() {
        StringBuffer buffer = new StringBuffer();
        // print relationship source table with fields
        MWTable sourceTable = getRelationshipSourceTable();
        buffer.append(sourceTable.getShortName());
        if (reference.columnPairsSize() > 0) {
            buffer.append(" ("
            );
            for (Iterator it = reference.columnPairs(); it.hasNext();) {
                MWColumnPair association = (MWColumnPair) it.next();
                if (foreignKeyInTargetTable) {
                    if (association.getTargetColumn() != null)
                        buffer.append(association.getTargetColumn().getName());
                } else {
                    if (association.getSourceColumn() != null)
                        buffer.append(association.getSourceColumn().getName());
                }
                if (it.hasNext())
                    buffer.append(", "
                    );
            }
            buffer.append(")"
            );
        }
        buffer.append(" -> "
        );
        // print relationship target table with fields
        MWTable targetTable = getRelationshipTargetTable();
        buffer.append(targetTable.getShortName());
        if (reference.columnPairsSize() > 0) {
            buffer.append(" ("
            );
            for (Iterator it = reference.columnPairs(); it.hasNext();) {
                MWColumnPair association = (MWColumnPair) it.next();
                if (foreignKeyInTargetTable) {
                    if (association.getSourceColumn() != null)
                        buffer.append(association.getSourceColumn().getName());
                } else {
                    if (association.getTargetColumn() != null)
                        buffer.append(association.getTargetColumn().getName());
                }
                if (it.hasNext())
                    buffer.append(", "
                    );
            }
            buffer.append(")"
            );
        }
        return buffer.toString();
    }
    protected MWReference getReference() {
        return reference;
    }
    public MWTable getRelationshipSourceTable() {
        if (foreignKeyInTargetTable)
            return reference.getTargetTable();
        else
            return reference.getSourceTable();
    }
    public MWTable getRelationshipTargetTable() {
        if (foreignKeyInTargetTable)
            return reference.getSourceTable();
        else
            return reference.getTargetTable();
    }
    public boolean isForeignKeyInTargetTable() {
        return foreignKeyInTargetTable;
    }
    public boolean isOneToMany() {
        return relationshipType == ONE_TO_MANY;
    }
    public boolean isOneToOne() {
        return relationshipType == ONE_TO_ONE;
    }
    public void setNoRelationship() {
        relationshipType = null;
    }
    public void setOneToMany() {
        relationshipType = ONE_TO_MANY;
    }
    public void setOneToOne() {
        relationshipType = ONE_TO_ONE;
    }
}
