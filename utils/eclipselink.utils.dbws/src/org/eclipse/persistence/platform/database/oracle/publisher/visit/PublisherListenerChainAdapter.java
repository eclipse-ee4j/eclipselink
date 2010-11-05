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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

//javase imports
import java.util.ArrayList;
import java.util.List;

public class PublisherListenerChainAdapter implements PublisherListener {

    protected List<PublisherListener> chain = new ArrayList<PublisherListener>();

    public PublisherListenerChainAdapter() {
        // possibly some init code required
    }
    public PublisherListenerChainAdapter(List<PublisherListener> chain) {
        this();
        this.chain.addAll(chain);
    }

    public void addListener(PublisherListener listener) {
        chain.add(listener);
    }

    public void beginPackage(String packageName) {
        for (PublisherListener listener : chain) {
            listener.beginPackage(packageName);
        }
    }
    public void endPackage() {
        for (PublisherListener listener : chain) {
            listener.endPackage();
        }
    }

    public void beginPlsqlTable(String tableName, String targetTypeName) {
        for (PublisherListener listener : chain) {
            listener.beginPlsqlTable(tableName, targetTypeName);
        }
    }
    public void endPlsqlTable(String tableName, String typeDDL, String typeDropDDL) {
        for (PublisherListener listener : chain) {
            listener.endPlsqlTable(tableName, typeDDL, typeDropDDL);
        }
    }

    public void beginPlsqlRecord(String recordName, String targetTypeName, int numFields) {
        for (PublisherListener listener : chain) {
            listener.beginPlsqlRecord(recordName, targetTypeName, numFields);
        }
    }
    public void beginPlsqlRecordField(String fieldName, int idx) {
        for (PublisherListener listener : chain) {
            listener.beginPlsqlRecordField(fieldName, idx);
        }
    }
    public void endPlsqlRecordField(String fieldName, int idx) {
        for (PublisherListener listener : chain) {
            listener.endPlsqlRecordField(fieldName, idx);
        }
    }
    public void endPlsqlRecord(String recordName, String typeDDL, String typeDropDDL) {
        for (PublisherListener listener : chain) {
            listener.endPlsqlRecord(recordName, typeDDL, typeDropDDL);
        }
    }

    public void beginMethod(String methodName, int numArgs) {
        for (PublisherListener listener : chain) {
            listener.beginMethod(methodName, numArgs);
        }
    }
    public void handleMethodReturn(String returnTypeName) {
        for (PublisherListener listener : chain) {
            listener.handleMethodReturn(returnTypeName);
        }
    }
    public void beginMethodArg(String argName, String direction, int idx) {
        for (PublisherListener listener : chain) {
            listener.beginMethodArg(argName, direction, idx);
        }
    }
    public void endMethodArg(String argName) {
        for (PublisherListener listener : chain) {
            listener.endMethodArg(argName);
        }
    }
    public void endMethod(String methodName) {
        for (PublisherListener listener : chain) {
            listener.endMethod(methodName);
        }
    }

    public void beginObjectType(String objectTypeName) {
        for (PublisherListener listener : chain) {
            listener.beginObjectType(objectTypeName);
        }
    }
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes) {
        for (PublisherListener listener : chain) {
            listener.handleObjectType(objectTypeName, targetTypeName, numAttributes);
        }
    }
    public void endObjectType(String objectTypeName) {
        for (PublisherListener listener : chain) {
            listener.endObjectType(objectTypeName);
        }
    }

    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName) {
        for (PublisherListener listener : chain) {
            listener.handleSqlType(sqlTypeName, typecode, targetTypeName);
        }
    }

    public void handleSqlArrayType(String arrayTypeName, String targetTypeName) {
        for (PublisherListener listener : chain) {
            listener.handleSqlArrayType(arrayTypeName, targetTypeName);
        }
    }

    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
        for (PublisherListener listener : chain) {
            listener.handleSqlTableType(tableTypeName, targetTypeName);
        }
    }

    public void handleAttributeField(String attributeFieldName, int idx) {
        for (PublisherListener listener : chain) {
            listener.handleAttributeField(attributeFieldName, idx);
        }
    }
}