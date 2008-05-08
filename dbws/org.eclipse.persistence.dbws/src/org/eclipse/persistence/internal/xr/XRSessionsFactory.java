package org.eclipse.persistence.internal.xr;

import java.net.URL;
import java.security.AccessController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
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
import org.eclipse.persistence.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;

public class XRSessionsFactory extends SessionsFactory {

	static Pattern matchDBWSOrProject =
		Pattern.compile(DBWS_OR_XML, Pattern.CASE_INSENSITIVE);
	static Pattern matchDBWSOxProject =
		Pattern.compile(DBWS_OX_XML, Pattern.CASE_INSENSITIVE);

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
            		for (String prefix : META_INF_PATHS) {
                        String searchPath = prefix + DBWS_OR_XML;
                        url = m_classLoader.getResource(searchPath);
                        if (url != null) {
                        	break;
                        }
            		}
            	}
            	else {
	            	matcher = matchDBWSOxProject.matcher(projectString);
	            	if (matcher.find()) {
	            		for (String prefix : META_INF_PATHS) {
	                        String searchPath = prefix + DBWS_OX_XML;
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
	                try {
						project = (Project)unmarshaller.unmarshal(url);
					}
	                catch (RuntimeException e) {
						e.printStackTrace();
					}
            	}
            }
            catch (ValidationException validationException) {
                if (validationException.getErrorCode() == ValidationException.PROJECT_XML_NOT_FOUND) {
                    try {
                        project = XMLProjectReader.read(projectString);
                    } catch (Exception e) {
                        throw SessionLoaderException.failedToLoadProjectXml(projectString,
                        	validationException);
                    }
                } else {
                    throw SessionLoaderException.failedToLoadProjectXml(projectString,
                    	validationException);
                }
            }
        }

        return project;
	}

}
