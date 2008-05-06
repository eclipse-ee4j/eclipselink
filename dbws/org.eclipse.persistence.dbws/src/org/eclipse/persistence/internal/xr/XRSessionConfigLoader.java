package org.eclipse.persistence.internal.xr;

import java.util.Map;

import org.w3c.dom.Document;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;

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
