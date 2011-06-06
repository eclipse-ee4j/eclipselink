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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.platform.database.oracle.xdb;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.persistence.internal.databaseaccess.BindCallCustomParameter;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import oracle.xdb.XMLType;
import oracle.xdb.dom.XDBDocument;
import org.w3c.dom.Document;

public class XMLTypeBindCallCustomParameter extends BindCallCustomParameter {
    private XMLTransformer xmlTransformer;

    public XMLTypeBindCallCustomParameter(Object obj) {
        super(obj);
        xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        xmlTransformer.setFormattedOutput(false);
    }

    public void set(DatabasePlatform platform, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        if (this.obj instanceof String) {
            //Bug#5200836, unwrap the connection prior to using.
            this.obj = XMLType.createXML(session.getServerPlatform().unwrapConnection(statement.getConnection()), (String)this.obj);
        } else if (this.obj instanceof Document) {
            if (this.obj instanceof XDBDocument) {
                //Bug#5200836, unwrap the connection prior to using.
                this.obj = XMLType.createXML(session.getServerPlatform().unwrapConnection(statement.getConnection()), (XDBDocument)this.obj);
            } else {
                Document doc = (Document)obj;
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                xmlTransformer.transform(doc, result);
                //Bug#5200836, unwrap the connection prior to using.
                this.obj = XMLType.createXML(session.getServerPlatform().unwrapConnection(statement.getConnection()), writer.getBuffer().toString());
            }
        }
        super.set(platform, statement, index, session);
    }
}
