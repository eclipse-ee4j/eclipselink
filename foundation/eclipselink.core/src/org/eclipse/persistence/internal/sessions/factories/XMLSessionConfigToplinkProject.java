package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.oxm.XMLDescriptor;
/**
 * INTERNAL:
 * OX mapping project provides back compatibility for toplink 
 * 10g and 11g sessions XML meta-data reading.
 */

public class XMLSessionConfigToplinkProject extends  XMLSessionConfigProject{
    public ClassDescriptor buildSessionConfigsDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildSessionConfigsDescriptor();
        descriptor.setDefaultRootElement("toplink-sessions");
        return descriptor;
    }
    
    public ClassDescriptor buildLogConfigDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildLogConfigDescriptor();
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSessionLogConfig.class, "toplink-log");
        return descriptor;
    }

}
