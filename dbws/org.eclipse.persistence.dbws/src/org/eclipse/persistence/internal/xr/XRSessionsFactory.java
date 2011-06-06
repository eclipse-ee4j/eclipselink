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
 *   mnorman - May 15/2008 - 1.x - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

// javase imports
import java.net.URL;
import java.security.AccessController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// EclipseLink imports
import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.internal.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.internal.sessions.factories.SessionsFactory;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;

public class XRSessionsFactory extends SessionsFactory {

    static Pattern matchDBWSOrProject =
        Pattern.compile(/*DBWS_OR_XML*/"eclipselink-db.s-or.xml", Pattern.CASE_INSENSITIVE);
    static Pattern matchDBWSOxProject =
        Pattern.compile(/*DBWS_OX_XML*/"eclipselink-db.s-ox.xml", Pattern.CASE_INSENSITIVE);

    @SuppressWarnings("unchecked")
    @Override
    protected Project loadProjectConfig(ProjectConfig projectConfig) {
        Project project = null;
        String projectString = projectConfig.getProjectString();

        if (projectConfig.isProjectClassConfig()) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    project = (Project)AccessController.doPrivileged(
                        new PrivilegedNewInstanceFromClass(m_classLoader.loadClass(projectString)));
                }else{
                    project = (Project)PrivilegedAccessHelper.newInstanceFromClass(
                        m_classLoader.loadClass(projectString));
                }
            } catch (Throwable exception) {
                throw SessionLoaderException.failedToLoadProjectClass(projectString, exception);
            }
        } else {
            try {
                URL url = null;
                Matcher matcher = matchDBWSOrProject.matcher(projectString);
                if (matcher.find()) {
                    // need to try a variety of URLs to find DBWS OR/OX Project
                    String orXml = matcher.group();
                    for (String prefix : META_INF_PATHS) {
                        String searchPath = prefix + orXml;
                        url = m_classLoader.getResource(searchPath);
                        if (url != null) {
                            break;
                        }
                    }
                }
                else {
                    matcher = matchDBWSOxProject.matcher(projectString);
                    if (matcher.find()) {
                        String oxXml = matcher.group();
                        for (String prefix : META_INF_PATHS) {
                            String searchPath = prefix + oxXml;
                            url = m_classLoader.getResource(searchPath);
                            if (url != null) {
                                break;
                            }
                        }
                    }
                }
                if (url != null) {
                    Project p = new EclipseLinkObjectPersistenceRuntimeXMLProject();
                    XMLLogin xmlLogin = new XMLLogin();
                    xmlLogin.setDatasourcePlatform(new DOMPlatform());
                    p.setDatasourceLogin(xmlLogin);
                    if (m_classLoader != null) {
                        p.getDatasourceLogin().getDatasourcePlatform().getConversionManager().
                            setLoader(m_classLoader);
                    }
                    XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
                    XMLParser parser = xmlPlatform.newXMLParser();
                    parser.setNamespaceAware(true);
                    parser.setWhitespacePreserving(false);
                    XMLContext context = new XMLContext(p);
                    context.getSession(Project.class).getEventManager().addListener(
                        new MissingDescriptorListener());
                    XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                    project = (Project)unmarshaller.unmarshal(url);
                }
            }
            catch (ValidationException validationException) {
                if (validationException.getErrorCode() == ValidationException.PROJECT_XML_NOT_FOUND) {
                    throw SessionLoaderException.failedToLoadProjectXml(projectString,
                        validationException);
                }
                else {
                    throw SessionLoaderException. failedToParseXML(projectString,
                            validationException);
                }
            }
        }
        return project;
    }
}
