/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.internal.sessions.factories.model.login.AppendNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DescriptorLevelDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.IgnoreNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.NoDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.NodeOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.RelativePositionOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLBinderPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.GlassfishPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.Oc4jPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.SunAS9PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_10_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_9_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_6_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_7_0_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.Oc4jJGroupsTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.TransportManagerConfig;

/**
 * INTERNAL:
 * OX mapping project for the 11gR1 sessions XML schema.
 * This subclasses the 10.1.3 project and adds any changes.
 */
public class XMLSessionConfigProject_11_1_1 extends XMLSessionConfigProject {
    // Default null values
    public static final boolean BIND_ALL_PARAMETERS_DEFAULT = true;
    public static final boolean USE_SINGLE_THREADED_NOTIFICATION_DEFAULT = false;

    public XMLSessionConfigProject_11_1_1() {
        super();
        addDescriptor(buildOc4jJGroupsTransportManagerConfigDescriptor());
        addDescriptor(buildServerPlatformConfigDescriptorFor(Oc4jPlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(GlassfishPlatformConfig.class));      
    	addDescriptor(buildServerPlatformConfigDescriptorFor(SunAS9PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebLogic_9_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebLogic_10_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_6_1_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_7_0_PlatformConfig.class));
        
        // 242452 -- add metadata support for XMLLogin's DocumentPreservationPolicy
        addDescriptor(buildDocumentPreservationPolicyConfigDescriptor());
        addDescriptor(buildDescriptorLevelDocumentPreservationPolicyConfigDescriptor());
        addDescriptor(buildNoDocumentPreservationPolicyConfigDescriptor());
        addDescriptor(buildXMLBinderPolicyConfigDescriptor());
        addDescriptor(buildNodeOrderingPolicyConfigDescriptor());
        addDescriptor(buildAppendNewElementsOrderingPolicyConfigDescriptor());
        addDescriptor(buildIgnoreNewElementsOrderingPolicyConfigDescriptor());
        addDescriptor(buildRelativePositionOrderingPolicyConfigDescriptor());
        
        // Set the namespaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        namespaceResolver.put("xsd", W3C_XML_SCHEMA_NS_URI);

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }        
    }

    public ClassDescriptor buildSessionConfigsDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildSessionConfigsDescriptor();
        descriptor.setDefaultRootElement("sessions");
        return descriptor;
    }
    
    public ClassDescriptor buildDatabaseLoginConfigDescriptor() {
        ClassDescriptor descriptor = super.buildDatabaseLoginConfigDescriptor();

        XMLDirectMapping bindAllParametersMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("m_bindAllParameters");
        bindAllParametersMapping.setNullValue(Boolean.valueOf(BIND_ALL_PARAMETERS_DEFAULT));

        XMLDirectMapping validateConnectionHealthOnErrorMapping = new XMLDirectMapping();
        validateConnectionHealthOnErrorMapping.setAttributeName("connectionHealthValidatedOnError");
        validateConnectionHealthOnErrorMapping.setGetMethodName("isConnectionHealthValidatedOnError");
        validateConnectionHealthOnErrorMapping.setSetMethodName("setConnectionHealthValidatedOnError");
        validateConnectionHealthOnErrorMapping.setXPath("connection-health-validated-on-error/text()");
        validateConnectionHealthOnErrorMapping.setNullPolicy(new NullPolicy(null, false, false, false));
        validateConnectionHealthOnErrorMapping.setNullValue(true);
        descriptor.addMapping(validateConnectionHealthOnErrorMapping);

        XMLDirectMapping delayBetweenReconnectAttempts = new XMLDirectMapping();
        delayBetweenReconnectAttempts.setAttributeName("delayBetweenConnectionAttempts");
        delayBetweenReconnectAttempts.setGetMethodName("getDelayBetweenConnectionAttempts");
        delayBetweenReconnectAttempts.setSetMethodName("setDelayBetweenConnectionAttempts");
        delayBetweenReconnectAttempts.setXPath("delay-between-reconnect-attempts/text()");
        delayBetweenReconnectAttempts.setNullPolicy(new NullPolicy(null, false, false, false));
        descriptor.addMapping(delayBetweenReconnectAttempts);

        XMLDirectMapping queryRetryAttemptCount = new XMLDirectMapping();
        queryRetryAttemptCount.setAttributeName("queryRetryAttemptCount");
        queryRetryAttemptCount.setGetMethodName("getQueryRetryAttemptCount");
        queryRetryAttemptCount.setSetMethodName("setQueryRetryAttemptCount");
        queryRetryAttemptCount.setXPath("query-retry-attempt-count/text()");
        queryRetryAttemptCount.setNullPolicy(new NullPolicy(null, false, false, false));
        descriptor.addMapping(queryRetryAttemptCount);

        XMLDirectMapping pingSQLMapping = new XMLDirectMapping();
        pingSQLMapping.setAttributeName("pingSQL");
        pingSQLMapping.setGetMethodName("getPingSQL");
        pingSQLMapping.setSetMethodName("setPingSQL");
        pingSQLMapping.setXPath("ping-sql/text()");
        pingSQLMapping.setNullPolicy(new NullPolicy(null, false, false, false));
        descriptor.addMapping(pingSQLMapping);

        return descriptor;
    }

    public ClassDescriptor buildOc4jJGroupsTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Oc4jJGroupsTransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(TransportManagerConfig.class);

        XMLDirectMapping useSingleThreadedNotificationMapping = new XMLDirectMapping();
        useSingleThreadedNotificationMapping.setAttributeName("m_useSingleThreadedNotification");
        useSingleThreadedNotificationMapping.setGetMethodName("useSingleThreadedNotification");
        useSingleThreadedNotificationMapping.setSetMethodName("setUseSingleThreadedNotification");
        useSingleThreadedNotificationMapping.setXPath("use-single-threaded-notification/text()");
        useSingleThreadedNotificationMapping.setNullValue(Boolean.valueOf(USE_SINGLE_THREADED_NOTIFICATION_DEFAULT));
        descriptor.addMapping(useSingleThreadedNotificationMapping);

        XMLDirectMapping topicNameMapping = new XMLDirectMapping();
        topicNameMapping.setAttributeName("m_topicName");
        topicNameMapping.setGetMethodName("getTopicName");
        topicNameMapping.setSetMethodName("setTopicName");
        topicNameMapping.setXPath("topic-name/text()");
        descriptor.addMapping(topicNameMapping);

        return descriptor;
    }

    public ClassDescriptor buildTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildTransportManagerConfigDescriptor();
        descriptor.getInheritancePolicy().addClassIndicator(Oc4jJGroupsTransportManagerConfig.class, "oc4j-jgroups-transport");

        return descriptor;
    }
    
    public ClassDescriptor buildServerPlatformConfigDescriptor() {
        XMLDescriptor descriptor =(XMLDescriptor)super.buildServerPlatformConfigDescriptor();
        descriptor.getInheritancePolicy().addClassIndicator(Oc4jPlatformConfig.class, "oc4j-platform");
        descriptor.getInheritancePolicy().addClassIndicator(SunAS9PlatformConfig.class, "sunas-9-platform");
        descriptor.getInheritancePolicy().addClassIndicator(GlassfishPlatformConfig.class, "glassfish-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebLogic_9_PlatformConfig.class, "weblogic-9-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebLogic_10_PlatformConfig.class, "weblogic-10-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_6_1_PlatformConfig.class, "websphere-61-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_7_0_PlatformConfig.class, "websphere-7-platform");	
        return descriptor;
    }

    public ClassDescriptor buildXMLLoginConfigDescriptor() {
        ClassDescriptor descriptor = super.buildXMLLoginConfigDescriptor();

        XMLDirectMapping equalNamespaceResolversMapping = new XMLDirectMapping();
        equalNamespaceResolversMapping.setAttributeName("m_equalNamespaceResolvers");
        equalNamespaceResolversMapping.setGetMethodName("getEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setSetMethodName("setEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setXPath("equal-namespace-resolvers/text()");
        equalNamespaceResolversMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(equalNamespaceResolversMapping);

        XMLCompositeObjectMapping documentPreservationPolicyMapping = new XMLCompositeObjectMapping();
        documentPreservationPolicyMapping.setReferenceClass(DocumentPreservationPolicyConfig.class);
        documentPreservationPolicyMapping.setAttributeName("m_documentPreservationPolicy");
        documentPreservationPolicyMapping.setGetMethodName("getDocumentPreservationPolicy");
        documentPreservationPolicyMapping.setSetMethodName("setDocumentPreservationPolicy");
        documentPreservationPolicyMapping.setXPath("document-preservation-policy");
        descriptor.addMapping(documentPreservationPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDocumentPreservationPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DocumentPreservationPolicyConfig.class);
        descriptor.setDefaultRootElement("document-preservation-policy");

        XMLCompositeObjectMapping nodeOrderingPolicyMapping = new XMLCompositeObjectMapping();
        nodeOrderingPolicyMapping.setReferenceClass(NodeOrderingPolicyConfig.class);
        nodeOrderingPolicyMapping.setAttributeName("m_nodeOrderingPolicy");
        nodeOrderingPolicyMapping.setGetMethodName("getNodeOrderingPolicy");
        nodeOrderingPolicyMapping.setSetMethodName("setNodeOrderingPolicy");
        nodeOrderingPolicyMapping.setXPath("node-ordering-policy");
        descriptor.addMapping(nodeOrderingPolicyMapping);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DescriptorLevelDocumentPreservationPolicyConfig.class, "descriptor-level-document-preservation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(NoDocumentPreservationPolicyConfig.class, "no-document-preservation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(XMLBinderPolicyConfig.class, "xml-binder-policy");

        return descriptor;
    }

    protected ClassDescriptor buildDescriptorLevelDocumentPreservationPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DescriptorLevelDocumentPreservationPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicyConfig.class);

        return descriptor;
    }

    protected ClassDescriptor buildNoDocumentPreservationPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NoDocumentPreservationPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicyConfig.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLBinderPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLBinderPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicyConfig.class);

        return descriptor;
    }
    
    protected ClassDescriptor buildNodeOrderingPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NodeOrderingPolicyConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(AppendNewElementsOrderingPolicyConfig.class, "append-new-elements-ordering-policy");
        descriptor.getInheritancePolicy().addClassIndicator(IgnoreNewElementsOrderingPolicyConfig.class, "ignore-new-elements-ordering-policy");
        descriptor.getInheritancePolicy().addClassIndicator(RelativePositionOrderingPolicyConfig.class, "relative-position-ordering-policy");

        return descriptor;
    }

    protected ClassDescriptor buildAppendNewElementsOrderingPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AppendNewElementsOrderingPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicyConfig.class);

        return descriptor;
    }

    protected ClassDescriptor buildIgnoreNewElementsOrderingPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IgnoreNewElementsOrderingPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicyConfig.class);

        return descriptor;
    }

    protected ClassDescriptor buildRelativePositionOrderingPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RelativePositionOrderingPolicyConfig.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicyConfig.class);

        return descriptor;
    }

}
