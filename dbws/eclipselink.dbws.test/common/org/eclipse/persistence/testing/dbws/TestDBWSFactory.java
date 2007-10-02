/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.testing.dbws;

// Javase imports
import java.io.InputStream;

// Java extension imports

// TopLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.SEARCH_PATHS;

public class TestDBWSFactory extends XRServiceFactory {

    protected static String DEFAULT_DATABASE_USERNAME = "scott";
    protected static String DEFAULT_DATABASE_PASSWORD = "tiger";
    protected static String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    protected static String DEFAULT_DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    protected static String DEFAULT_DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.oracle.OraclePlatform";

    protected String username;
    protected String password;
    protected String url;
    protected String driver;
    protected String platform;

    public TestDBWSFactory() {
        super();
        parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = ClassLoader.getSystemClassLoader();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public XRServiceAdapter buildService() {
        InputStream dbwsServiceStream = null;
        for (String searchPath : SEARCH_PATHS) {
            String path = searchPath + DBWS_SERVICE_XML;
            dbwsServiceStream = parentClassLoader.getResourceAsStream(path);
            if (dbwsServiceStream != null) {
                break;
            }
        }
        if (dbwsServiceStream == null) {
            throw new DBWSException("Cannot find " + DBWS_SERVICE_XML);
        }
        DBWSModelProject xrServiceModelProject = new DBWSModelProject();
        XMLContext xmlContext = new XMLContext(xrServiceModelProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        XRServiceModel dbwsModel = (XRServiceModel)unmarshaller.unmarshal(dbwsServiceStream);
        xrSchemaStream = parentClassLoader.getResourceAsStream(DBWS_SCHEMA_XML);
        if (xrSchemaStream == null) {
            xrSchemaStream = parentClassLoader.getResourceAsStream("/" + DBWS_SCHEMA_XML);
            if (xrSchemaStream == null) {
                throw new DBWSException("Cannot find " + DBWS_SCHEMA_XML);
            }
        }
        return buildService(dbwsModel);
    }

    @Override
    public void customizeSession(Session orSession, Session oxSession) {
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username == null ? DEFAULT_DATABASE_USERNAME :
             username);
        login.setEncryptedPassword(password == null ? DEFAULT_DATABASE_PASSWORD :
            password);
        login.setConnectionString(url == null ? DEFAULT_DATABASE_URL :
            url);
        login.setDriverClassName(driver == null ? DEFAULT_DATABASE_DRIVER :
            driver);
        login.setPlatformClassName(platform == null ? DEFAULT_DATABASE_PLATFORM :
            platform);
        ((DatabaseSession)orSession).setLogin(login);
    }
}
