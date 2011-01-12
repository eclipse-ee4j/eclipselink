/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

public interface PublisherListener {

    public void beginPackage(String packagename);
    public void endPackage();

    public void beginPlsqlRecord(String recordName, String targetTypeName, int numFields);
    public void beginPlsqlRecordField(String fieldName, int idx);
    public void endPlsqlRecordField(String fieldName, int idx);
    public void endPlsqlRecord(String recordName, String typeDDL, String typeDropDDL);

    public void beginPlsqlTable(String tableName, String targetTypeName);
    public void endPlsqlTable(String tableName, String typeDDL, String typeDropDDL);

    public void beginMethod(String methodName, int len);
    public void handleMethodReturn(String returnTypeName);
    public void beginMethodArg(String argName, String direction, int idx);
    public void endMethodArg(String argName);
    public void endMethod(String methodName);

    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName);

    public void beginObjectType(String objectTypeName);
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes);
    public void endObjectType(String objectTypeName);
    
    public void handleSqlArrayType(String arrayTypeName, String targetTypeName);
    public void handleSqlTableType(String tableTypeName, String targetTypeName);
    public void handleAttributeField(String attributeFieldName, int idx);

}