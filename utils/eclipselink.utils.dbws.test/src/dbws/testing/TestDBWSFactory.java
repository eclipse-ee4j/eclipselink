/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/

package dbws.testing;

//javase imports
import java.io.InputStream;

//Java extension imports

//EclipseLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.exceptions.DBWSException;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;

public class TestDBWSFactory extends XRServiceFactory {

    public TestDBWSFactory() {
        super();
        parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = ClassLoader.getSystemClassLoader();
        }
    }

    @Override
    public XRServiceAdapter buildService() {
        InputStream dbwsServiceStream = null;
        for (String searchPath : META_INF_PATHS) {
            String path = searchPath + DBWS_SERVICE_XML;
            dbwsServiceStream = parentClassLoader.getResourceAsStream(path);
            if (dbwsServiceStream != null) {
                break;
            }
        }
        if (dbwsServiceStream == null) {
            throw DBWSException.couldNotLocateFile(DBWS_SERVICE_XML);
        }
        DBWSModelProject xrServiceModelProject = new DBWSModelProject();
        XMLContext xmlContext = new XMLContext(xrServiceModelProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        XRServiceModel dbwsModel = (XRServiceModel)unmarshaller.unmarshal(dbwsServiceStream);
        xrSchemaStream = parentClassLoader.getResourceAsStream(DBWS_SCHEMA_XML);
        if (xrSchemaStream == null) {
            xrSchemaStream = parentClassLoader.getResourceAsStream("/" + DBWS_SCHEMA_XML);
            if (xrSchemaStream == null) {
                throw DBWSException.couldNotLocateFile(DBWS_SCHEMA_XML);
            }
        }
        return buildService(dbwsModel);
    }

}