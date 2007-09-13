/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.sql.Connection;
import java.util.ArrayList;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.SQLQueryHandler;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;
import static org.eclipse.persistence.tools.dbws.Util.convertJDBCParameterBindingMarkers;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME;

// Ant imports
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class SQLOperation extends Op implements TaskContainer {

    protected String name;
    protected String sqlText;
    protected ArrayList<Binding> bindings = new ArrayList<Binding>();

    public SQLOperation() {
        super();
    }

    public void addTask(Task task) {
    } // ignore

    @Override
    public void buildOperation(XRServiceModel xrServiceModel, Schema schema,
        DatabasePlatform databasePlatform, Connection connection) {
        super.buildOperation(xrServiceModel, schema, databasePlatform, connection);

        QueryOperation qo = new QueryOperation();
        qo.setName(name);
        SQLQueryHandler sqlqh = new SQLQueryHandler();
        Result result;
        if (isCollection) {
            result = new CollectionResult();
        }
        else {
            result = new Result();
        }
        SimpleXMLFormat sxf = null;
        if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
            sxf = new SimpleXMLFormat();
            sxf.setSimpleXMLFormatTag(simpleXMLFormatTag);
            result.setType(SXF_QNAME);
        }
        if (xmlTag != null && xmlTag.length() > 0) {
            if (sxf == null) {
                sxf = new SimpleXMLFormat();
                result.setType(SXF_QNAME);
            }
            sxf.setXMLTag(xmlTag);
        }
        if (sxf != null) {
            result.setSimpleXMLFormat(sxf);
        }
        if (binaryAttachment) {
            Attachment attachment = new Attachment();
            attachment.setMimeType(DEFAULT_ATTACHMENT_MIMETYPE);
            result.setAttachment(attachment);
        }
        if (returnType != null && returnType.length() > 0) {
            result.setType(qNameFromString(returnType, schema));
        }
        else {
            result.setType(SXF_QNAME);
        }
        qo.setResult(result);
        if (bindings.isEmpty()) {
            sqlqh.setSqlString(sqlText);
        }
        else {
            sqlqh.setSqlString(convertJDBCParameterBindingMarkers(sqlText, bindings));
            for (Binding param : bindings) {
                Parameter p = new Parameter();
                p.setName(param.name);
                p.setType(qNameFromString(param.type, schema));
                qo.getParameters().add(p);
            }
        }
        qo.setQueryHandler(sqlqh);
        xrServiceModel.getOperations().put(qo.getName(), qo);

        // check to see if the schema requires sxfType to be added
        if (schema.getTopLevelElements().get("simple-xml-format") == null) {
            addSimpleXMLFormat(schema);
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void addText(String text) {
        sqlText = text.trim();
    }

    public String getSQLText() {
        return sqlText;
    }

    public void addBinding(Binding binding) {
        bindings.add(binding);
    }

    public ArrayList<Binding> getBindings() {
        return bindings;
    }

    public void setBindings(ArrayList<Binding> bindings) {
        this.bindings = bindings;
    }
}
