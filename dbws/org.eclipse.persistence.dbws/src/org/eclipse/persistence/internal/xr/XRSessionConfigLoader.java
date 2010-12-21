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
 *   mnorman - May 15/2008 - 1.x - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// javase imports
import java.util.Map;
import org.w3c.dom.Document;

// EclipseLink imports
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

public class XRSessionConfigLoader extends XMLSessionConfigLoader {

    public XRSessionConfigLoader(String resourceName) {
        super(resourceName);
    }

    @SuppressWarnings("unchecked")
    public boolean load(SessionManager sessionManager, ClassLoader loader) {
        Document document = loadDocument(loader);

        if(getExceptionStore().isEmpty()){
            if (document.getDocumentElement().getTagName().equals("sessions")) {
                XMLContext context = new XMLContext(new XMLSessionConfigProject_11_1_1());
                XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                SessionConfigs configs = (SessionConfigs)unmarshaller.unmarshal(document);
                XRSessionsFactory factory = new XRSessionsFactory();
                Map<String, Session> sessions = factory.buildSessionConfigs(configs, loader);
                for (Map.Entry<String, Session> entry : sessions.entrySet()) {
                    if (!sessionManager.getSessions().containsKey(entry.getKey())) {
                        sessionManager.addSession(entry.getKey(), entry.getValue());
                    }
                }
                return true;
            }
        }
        return false;
    }
}
