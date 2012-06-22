/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - 2.3.0 - Initial Contribution
 ******************************************************************************/
package dbws.testing;

//javase imports
import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceProvider;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.XRPackager;
import org.junit.AfterClass;
import org.w3c.dom.Document;

public class DBWSTestProviderHelper extends ProviderHelper {
	// database properties
	public static final String DATABASE_USERNAME_KEY = "db.user";
	public static final String DATABASE_PASSWORD_KEY = "db.pwd";
	public static final String DATABASE_URL_KEY = "db.url";
	public static final String DATABASE_DRIVER_KEY = "db.driver";
	public static final String DATABASE_PLATFORM_KEY = "db.platform";
	public static final String DEFAULT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DEFAULT_DATABASE_PLATFORM = "org.eclipse.persistence.platform.database.MySQLPlatform";
	public static final String DEFAULT_DATABASE_USERNAME = "user";
	public static final String DEFAULT_DATABASE_PASSWORD = "password";
	public static final String DEFAULT_DATABASE_URL = "jdbc:mysql://localhost:3306/db";
	
	// JUnit test fixtures
	static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
	static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
	static ByteArrayOutputStream DBWS_WSDL_STREAM = new ByteArrayOutputStream();
	static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
	static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();
	
	protected static Endpoint endpoint = null;
	protected static QName portQName = null;
	protected static Service testService = null;
	protected static DBWSBuilder builder = new DBWSBuilder();
	protected static XMLParser xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();

	public static void serviceSetup(String builderString, String endpointAddress, DBWSTestProviderHelper endPoint) throws WSDLException {
		XMLContext context = new XMLContext(new DBWSBuilderModelProject());
		XMLUnmarshaller unmarshaller = context.createUnmarshaller();
		DBWSBuilderModel builderModel = (DBWSBuilderModel) unmarshaller.unmarshal(new StringReader(builderString));
		builder.quiet = true;
		builder.properties = builderModel.properties;
		builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
		builder.getTargetNamespace();
		builder.operations = builderModel.operations;
		builder.setPackager(new XRPackager(null, "EmptyPackager", noArchive) {
			@Override
			public void start() {
			}
		});
		builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM,
				DBWS_OR_STREAM, DBWS_OX_STREAM, __nullStream, __nullStream,
				DBWS_WSDL_STREAM, __nullStream, __nullStream, __nullStream,
				__nullStream, null);
		endpoint = Endpoint.create(endPoint);
		endpoint.publish(endpointAddress);
		WebServiceProvider testProvider = endPoint.getClass().getAnnotation(WebServiceProvider.class);
		String serviceNamespace = testProvider.targetNamespace();
		String serviceName = testProvider.serviceName();
		String portName = testProvider.portName();
		QName serviceQName = new QName(serviceNamespace, serviceName);
		portQName = new QName(serviceNamespace, portName);
		testService = Service.create(serviceQName);
		testService.addPort(portQName, SOAP11HTTP_BINDING, endpointAddress);
	}

	@Override
	protected InputStream initXRServiceStream(ClassLoader parentClassLoader,
			ServletContext sc) {
		return new ByteArrayInputStream(DBWS_SERVICE_STREAM.toByteArray());
	}

	@Override
	protected InputStream initXRSchemaStream(ClassLoader parentClassLoader,
			ServletContext sc) {
		return new ByteArrayInputStream(DBWS_SCHEMA_STREAM.toByteArray());
	}

	@Override
	protected InputStream initWSDLInputStream(ClassLoader parentClassLoader,
			ServletContext sc) {
		return new ByteArrayInputStream(DBWS_WSDL_STREAM.toByteArray());
	}

	@PostConstruct
	protected void init() {
		super.init(new XRDynamicClassLoader(Thread.currentThread().getContextClassLoader()), null, false);
	}

	@Override
	public void logoutSessions() {
		if (xrService.getORSession() != null) {
			((DatabaseSession) xrService.getORSession()).logout();
		}
		if (xrService.getOXSession() != null) {
			((DatabaseSession) xrService.getOXSession()).logout();
		}
	}

	@Override
	public void buildSessions() {
		Project oxProject = XMLProjectReader.read(new StringReader(DBWS_OX_STREAM.toString()), parentClassLoader);
		((XMLLogin) oxProject.getDatasourceLogin()).setEqualNamespaceResolvers(false);
		Project orProject = XMLProjectReader.read(new StringReader(DBWS_OR_STREAM.toString()), parentClassLoader);
		DatasourceLogin login = orProject.getLogin();
		login.setUserName(builder.getUsername());
		login.setPassword(builder.getPassword());
		((DatabaseLogin) login).setConnectionString(builder.getUrl());
		((DatabaseLogin) login).setDriverClassName(System.getProperty(DBWSTestProviderHelper.DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER));
		Platform platform = builder.getDatabasePlatform();
		ConversionManager cm = platform.getConversionManager();
		cm.setLoader(parentClassLoader);
		login.setDatasourcePlatform(platform);
		((DatabaseLogin) login).bindAllParameters();
		orProject.setDatasourceLogin(login);
		ProjectHelper.fixOROXAccessors(orProject, oxProject);
		DatabaseSession databaseSession = orProject.createDatabaseSession();
		databaseSession.dontLogMessages();
		xrService.setORSession(databaseSession);
		xrService.setXMLContext(new XMLContext(oxProject));
		xrService.setOXSession(xrService.getXMLContext().getSession(0));
	}

	@AfterClass
	public static void teardown() {
		if (endpoint != null) {
			endpoint.stop();
		}
	}

	@PreDestroy
	public void destroy() {
		super.destroy();
	}

	public static Transformer getTransformer() {
		Transformer transformer = null;
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			/* extremely rare, safe to ignore */
		}
		return transformer;
	}

	public static String documentToString(Document doc) {
		DOMSource domSource = new DOMSource(doc);
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.transform(domSource, result);
			return stringWriter.toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return "<empty/>";
		}
	}
}
