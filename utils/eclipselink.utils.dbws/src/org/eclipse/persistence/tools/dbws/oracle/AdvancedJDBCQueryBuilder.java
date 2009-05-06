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
 *     Mike Norman - 090423
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.dbws.DBWSBuilder.DbStoredProcedureNameAndModel;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.internal.helper.ClassConstants.OBJECT;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;

public class AdvancedJDBCQueryBuilder extends PublisherDefaultListener {

    protected Stack<ListenerHelper> stac = new Stack<ListenerHelper>();
    protected Map<String, DatabaseQuery> queryMap = new HashMap<String, DatabaseQuery>();
    protected List<DbStoredProcedure> storedProcedures;
    protected Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName;
    
    protected String packageName = null;

    public AdvancedJDBCQueryBuilder(List<DbStoredProcedure> storedProcedures,
        Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName) {
        this(storedProcedures, dbStoredProcedure2QueryName, null);
    }
    public AdvancedJDBCQueryBuilder(List<DbStoredProcedure> storedProcedures,
        Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName,
        String packageName) {
        super();
        this.storedProcedures = storedProcedures;
        this.dbStoredProcedure2QueryName = dbStoredProcedure2QueryName;
        this.packageName = packageName;
    }

    public List<DatabaseQuery> getQueries() {
        if (queryMap.isEmpty()) {
            return null;
        }
        ArrayList<DatabaseQuery> al = new ArrayList<DatabaseQuery>();
        al.addAll(queryMap.values());
        return al;
    }

    @Override
    public void beginPackage(String packageName) {
        if (this.packageName == null) {
            // trim-off dotted-prefix
            int dotIdx = packageName.indexOf('.');
            if (dotIdx > -1) {
                this.packageName = packageName.substring(dotIdx+1);
            }
        }
    }

    @Override
    public void beginMethod(String methodName, int numArgs) {
        DatabaseQuery dq = queryMap.get(methodName);
        if (dq == null) {
            stac.push(new MethodHelper(methodName,numArgs));
        }
    }

    @Override
    public void handleMethodReturn(String returnTypeName) {
        String returnType = returnTypeName;
        // trim-off dotted-prefix
        int dotIdx = returnTypeName.indexOf('.');
        if (dotIdx > -1) {
            returnType = returnTypeName.substring(dotIdx+1);
        }
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper =(MethodHelper)helper;
                methodHelper.setFunc(true);
                MethodArgHelper methodArgHelper = new MethodArgHelper("", returnType);
                methodHelper.args().add(methodArgHelper);
            }
        }
    }
    
    @Override
    public void beginMethodArg(String argName, String direction, int idx) {
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper =(MethodHelper)helper;
                MethodArgHelper methodArgHelper = new MethodArgHelper(argName, null);
                methodArgHelper.setDirection(direction);
                methodHelper.args().add(methodArgHelper);
            }
        }
    }
    
    @Override
    public void endMethod(String methodName) {
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper = (MethodHelper)helper;
                DbStoredProcedureNameAndModel nameAndModel = null;
                DbStoredProcedure targetProcedure = null;
                for (DbStoredProcedure storedProcedure : storedProcedures) {
                    nameAndModel = dbStoredProcedure2QueryName.get(storedProcedure);
                    if (sqlMatch(nameAndModel.procOpModel.getProcedurePattern(),
                            methodHelper.methodName())) {
                        targetProcedure = storedProcedure;
                        break;
                    }
                }
                if (nameAndModel != null) {
                    DatabaseQuery dq = null;
                    StoredProcedureCall spCall = null;
                    List<MethodArgHelper> args = methodHelper.args();
                    int startIdx = 0;
                    int len = args.size();
                    if (methodHelper.isFunc()) {
                        startIdx = 1;
                        MethodArgHelper returnArg = args.get(0);
                        if (returnArg.isComplex()) {
                            String javaClassName = 
                                (packageName + "." + returnArg.sqlTypeName()).toLowerCase();
                            spCall = new StoredFunctionCall(Types.STRUCT, returnArg.sqlTypeName(),
                                javaClassName);
                        }
                        else {
                            spCall = new StoredFunctionCall();
                            ((StoredFunctionCall)spCall).setResult(null, OBJECT);
                        }
                    }
                    else {
                        spCall = new StoredProcedureCall();
                    }
                    String returnType = nameAndModel.procOpModel.getReturnType();
                    boolean hasResponse = returnType != null;
                    if (hasResponse) {
                        if (nameAndModel.procOpModel.isCollection()) {
                            dq = new DataReadQuery();
                        }
                        else {
                            dq = new ValueReadQuery();
                        }
                    }
                    else {
                        dq = new DataModifyQuery();
                    }
                    dq.bindAllParameters();
                    dq.setName(nameAndModel.name);
                    for (int i = startIdx; i < len; i++) {
                        MethodArgHelper arg = args.get(i);
                        String argName = arg.argName();
                        String javaClassName = null;
                        if (arg.isComplex()) {
                            javaClassName = (packageName + "." + arg.sqlTypeName()).toLowerCase();
                        }
                        if (arg.direction().equalsIgnoreCase("IN")) {
                            dq.addArgument(argName);
                            if (arg.isComplex()) {
                                spCall.addNamedArgument(argName, argName,
                                    Types.STRUCT, arg.sqlTypeName(), javaClassName);
                            }
                            else {
                                spCall.addNamedArgument(argName, argName);
                            }
                        }
                        else if (arg.direction().equalsIgnoreCase("OUT")) {
                            if (arg.isComplex()) {
                                spCall.addNamedOutputArgument(argName, argName,
                                    Types.STRUCT, arg.sqlTypeName());
                            }
                            else {
                                spCall.addNamedOutputArgument(argName, argName);
                            }
                        }
                        else if (arg.direction().equalsIgnoreCase("IN OUT")) {
                            dq.addArgument(argName);
                            spCall.addNamedInOutputArgument(argName);
                        }
                    }
                    String catalogPrefix = null;
                    String cat = targetProcedure.getCatalog();
                    if (cat == null | cat.length() == 0) {
                        catalogPrefix = "";
                    }
                    else {
                        catalogPrefix = cat + ".";
                    }
                    spCall.setProcedureName(catalogPrefix + targetProcedure.getName());
                    dq.setCall(spCall);
                    queryMap.put(methodName, dq);
                }
                stac.pop();
            }
        }
    }

    @Override
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes) {
        String objectType = objectTypeName;
        // trim-off dotted-prefix
        int dotIdx = objectTypeName.indexOf('.');
        if (dotIdx > -1) {
            objectType = objectTypeName.substring(dotIdx+1);
        }
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper = (MethodHelper)helper;
                int size = methodHelper.args().size();
                MethodArgHelper methodArgHelper = methodHelper.args().get(size-1);
                if (methodArgHelper.sqlTypeName() == null) {
                    methodArgHelper.setSqlTypeName(objectType);
                }
                methodArgHelper.setIsComplex(true);
            }
            else if (helper.isAttribute()) {
                stac.pop();
            }
        }
    }

    @Override
    public void handleSqlArrayType(String arrayTypename, String targetTypeName) {
        String arrayType = arrayTypename;
        // trim-off dotted-prefix
        int dotIdx = arrayTypename.indexOf('.');
        if (dotIdx > -1) {
            arrayType = arrayTypename.substring(dotIdx+1);
        }
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper = (MethodHelper)helper;
                int size = methodHelper.args().size();
                MethodArgHelper methodArgHelper = methodHelper.args().get(size-1);
                methodArgHelper.setSqlTypeName(arrayType);
                methodArgHelper.setIsComplex(true);
            }
            else if (helper.isAttribute()) {
                stac.pop();
            }
        }
    }

    @Override
    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
        String tableType = tableTypeName;
        // trim-off dotted-prefix
        int dotIdx = tableTypeName.indexOf('.');
        if (dotIdx > -1) {
            tableType = tableTypeName.substring(dotIdx+1);
        }
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper = (MethodHelper)helper;
                int size = methodHelper.args().size();
                MethodArgHelper methodArgHelper = methodHelper.args().get(size-1);
                methodArgHelper.setSqlTypeName(tableType);
                methodArgHelper.setIsComplex(true);
            }
            else if (helper.isAttribute()) {
                stac.pop();
            }
        }
    }

    @Override
    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName) {
        if (!stac.isEmpty()) {
            ListenerHelper helper = stac.peek();
            if (helper.isMethod()) {
                MethodHelper methodHelper = (MethodHelper)helper;
                int size = methodHelper.args().size();
                MethodArgHelper methodArgHelper = methodHelper.args().get(size-1);
                methodArgHelper.setSqlTypeName(sqlTypeName);
            }
            else if (helper.isAttribute()) {
                stac.pop();
            }
        }
    }
    @Override
    public void handleAttributeField(String attributeFieldName) {
        stac.push(new AttributeFieldHelper(attributeFieldName, null));
    }

}