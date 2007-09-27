/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.helper;

// Javse imports

// Java extension imports

// EclipseLink imports
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;

/**
 * <b>PUBLIC</b>: Marker interface for Database type metadata,
 * plus helper methods to generate Strings for the DECLARE stanza
 * of an Anonymous PL/SQL block
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public interface DatabaseType {

    public boolean isComplexDatabaseType();
    
    public int getTypeCode();

    public String getTypeName();

    public String buildTargetDeclaration(DatabaseField databaseField, Integer direction, int index);

    public String buildOutAssignment(DatabaseField databaseField, Integer direction, int index);

    public void setConversionType(DatabaseField databaseField);

    public String buildBeginBlock(DatabaseField databaseField, Integer direction, int i);
    
    public enum DatabaseTypeHelper {
        databaseTypeHelper;

        public String buildTargetDeclaration(String name, String typeName, Integer direction,
            int index) {
            StringBuilder sb = buildTargetStringBuilder(name, typeName);
            setTargetDirection(sb, direction, index);
            return sb.toString();
        }

        public String buildTargetDeclaration(String name, String typeName, Integer direction,
            int index, int length) {
            StringBuilder sb = buildTargetStringBuilder(name, typeName);
            sb.append("(");
            sb.append(length);
            sb.append(")");
            setTargetDirection(sb, direction, index);
            return sb.toString();
        }

        public String buildTargetDeclaration(String name, String typeName, Integer direction,
            int index, int precision, int scale) {
            StringBuilder sb = buildTargetStringBuilder(name, typeName);
            sb.append("(");
            sb.append(precision);
            sb.append(",");
            sb.append(scale);
            sb.append(")");
            setTargetDirection(sb, direction, index);
            return sb.toString();
        }

        public String buildOutAssignment(String name, Integer direction, int index) {
            StringBuilder sb = new StringBuilder(" :");
            sb.append(index);
            sb.append(" := ");
            sb.append(name);
            sb.append("_TARGET;");
            return sb.toString();
        }

        protected StringBuilder buildTargetStringBuilder(String name, String typeName) {
            StringBuilder sb = new StringBuilder(name);
            sb.append("_TARGET ");
            sb.append(typeName);
            return sb;
        }

        protected void setTargetDirection(StringBuilder sb, Integer direction, int index) {
            if (direction == IN || direction == INOUT) {
                sb.append(" := :");
                sb.append(index);
            }
            sb.append("; ");
        }
    }

}
