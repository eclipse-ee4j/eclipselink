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
 *     Mike Norman - May 01 2008, created DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.util.ArrayList;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.SQLQueryHandler;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.tools.dbws.BindingModel.convertJDBCParameterBindingMarkers;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;

public class SQLOperationModel extends OperationModel {

    protected String sql;
    protected String buildSql;
    protected ArrayList<BindingModel> bindings = new ArrayList<BindingModel>();

    public SQLOperationModel() {
    }
    
    @Deprecated
    public String getSQLText() {
        return getSql();
    }
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    @Deprecated
    public void setSQLText(String sql) {
        setSql(sql);
    }

    public String getBuildSql() {
        return buildSql;
    }
    public void setBuildSql(String buildSql) {
        if (buildSql != null && buildSql.length() > 0) {
            this.buildSql = buildSql;
            setIsSimpleXMLFormat(false);
        }
        else {
            // clears build SQL string; back to simple XML
            this.buildSql = null;
            setIsSimpleXMLFormat(true);
        }
    }
    
    public void addBinding(BindingModel binding) {
        bindings.add(binding);
    }

    public ArrayList<BindingModel> getBindings() {
        return bindings;
    }
    public void setBindings(ArrayList<BindingModel> bindings) {
        this.bindings = bindings;
    }

    @Override
    public boolean isSQLOperation() {
        return true;
    }

    public boolean hasBuildSql() {
        if (buildSql != null && buildSql.length() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void buildOperation(DBWSBuilder builder) {

        super.buildOperation(builder);

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
        if (isSimpleXMLFormat() || getReturnType() == null) {
            sxf = new SimpleXMLFormat();
            if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
                sxf.setSimpleXMLFormatTag(simpleXMLFormatTag);
            }
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
            // check to see if the O-X project needs descriptor for SimpleXMLFormat
            if (builder.oxProject.getDescriptorForAlias(DEFAULT_SIMPLE_XML_FORMAT_TAG) == null) {
                SimpleXMLFormatProject sxfProject = new SimpleXMLFormatProject();
                builder.oxProject.addDescriptor(sxfProject.buildXRRowSetModelDescriptor());
            }
        }
        if (binaryAttachment) {
            Attachment attachment = new Attachment();
            attachment.setMimeType(DEFAULT_ATTACHMENT_MIMETYPE);
            result.setAttachment(attachment);
        }
        if (returnType != null && returnType.length() > 0) {
            result.setType(qNameFromString("{" +builder.getTargetNamespace() + "}" +
                returnType, builder.schema));
        }
        else {
            result.setType(SXF_QNAME);
            result.setSimpleXMLFormat( sxf == null ? new SimpleXMLFormat() : sxf);
        }
        qo.setResult(result);
        sqlqh.setSqlString(convertJDBCParameterBindingMarkers(sql, bindings));
        if (!bindings.isEmpty()) {
            for (BindingModel param : bindings) {
                Parameter p = new Parameter();
                p.setName(param.name);
                p.setType(qNameFromString(param.type, builder.schema));
                qo.getParameters().add(p);
            }
        }
        qo.setQueryHandler(sqlqh);
        builder.xrServiceModel.getOperations().put(qo.getName(), qo);

        // check to see if the schema requires sxfType to be added
        if (requiresSimpleXMLFormat(builder.xrServiceModel) && builder.schema.getTopLevelElements().
            get("simple-xml-format") == null) {
            addSimpleXMLFormat(builder.schema);
        }
    }

}
