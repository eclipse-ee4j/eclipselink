/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  XPathNodes are used together to build a tree.  The tree
 * is built from all of the XPath statments specified in the mapping metadata 
 * (mappings and policies).  This tree is then navigated by the 
 * TreeObjectBuilder to perform marshal and umarshal operations.</p>
 * <p>The XPaths "a/b" and "a/c" would result in a tree with the root "a" and 
 * two child nodes "b" and "c".</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>All tree relationships must be bi-directional.</li>
 * <li>Reference a NodeValue, XPathNodes without a Node value represent grouping
 * elements.</li>
 * <li>Reference an XPathFragment, XPathFragments contain name and namespace
 * information.</li>
 * <li>Must differentiate between child nodes that correspond to elements and 
 * those that do not.</li>
 * <li>Must represent special mapping situations like any and self mappings.</li>
 * </ul> 
 */

public class XPathNode {
    private NodeValue nodeValue;
    private XPathFragment xPathFragment;
    private XPathNode parent;
    private List attributeChildren;
    private List nonAttributeChildren;
    private List selfChildren;
    private Map attributeChildrenMap;
    private Map nonAttributeChildrenMap;
    private XMLAnyAttributeMappingNodeValue anyAttributeNodeValue;
    private XPathNode anyAttributeNode;

    public XPathFragment getXPathFragment() {
        return xPathFragment;
    }

    public void setXPathFragment(XPathFragment xPathFragment) {
        this.xPathFragment = xPathFragment;
    }

    public NodeValue getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(NodeValue nodeValue) {
        this.nodeValue = nodeValue;
        if (null != nodeValue) {
            nodeValue.setXPathNode(this);
        }
    }

    public XPathNode getParent() {
        return parent;
    }

    public void setParent(XPathNode parent) {
        this.parent = parent;
    }

    public List getAttributeChildren() {
        return this.attributeChildren;
    }

    public List getNonAttributeChildren() {
        return this.nonAttributeChildren;
    }

    public List getSelfChildren() {
        return this.selfChildren;
    }

    public Map getNonAttributeChildrenMap() {
        return this.nonAttributeChildrenMap;
    }

    public Map getAttributeChildrenMap() {
        return this.attributeChildrenMap;
    }

    public void setAnyAttributeNodeValue(XMLAnyAttributeMappingNodeValue nodeValue) {
        this.anyAttributeNodeValue = nodeValue;
    }

    public XMLAnyAttributeMappingNodeValue getAnyAttributeNodeValue() {
        return this.anyAttributeNodeValue;
    }

    public XPathNode getAnyAttributeNode() {
        return this.anyAttributeNode;
    }

    public boolean equals(Object object) {
        try {
            XPathNode perfNode = (XPathNode)object;

            if ((getXPathFragment() == null) && (perfNode.getXPathFragment() != null)) {
                return false;
            }
            if ((getXPathFragment() != null) && (perfNode.getXPathFragment() == null)) {
                return false;
            }
            if (getXPathFragment() == perfNode.getXPathFragment()) {
                return true;
            }
            return this.getXPathFragment().equals(perfNode.getXPathFragment());

            // turn fix off for now until we reenable XMLAnyObjectAndAnyCollectionTestCases
            //          } catch (NullPointerException npe) {
            // b5259059 all cases X0X1 (1mapping xpath=null, 2nd mapping xpath=filled
            // catch when object.getXPathFragment() == null
            // (this will also catch case where perfNode XPath is null)
            //          	return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public void addChild(XPathFragment anXPathFragment, NodeValue aNodeValue, NamespaceResolver namespaceResolver) {
        if ((anXPathFragment != null) && anXPathFragment.hasNamespace() && anXPathFragment.getNamespaceURI() == null && (namespaceResolver != null)) {
            anXPathFragment.setNamespaceURI(namespaceResolver.resolveNamespacePrefix(anXPathFragment.getPrefix()));
        }

        XPathNode xPathNode = new XPathNode();
        xPathNode.setXPathFragment(anXPathFragment);

        List children;
        Map childrenMap;

        if ((anXPathFragment != null) && anXPathFragment.isAttribute()) {
            if (null == attributeChildren) {
                attributeChildren = new ArrayList();
                attributeChildrenMap = new HashMap();
            }
            children = attributeChildren;
            childrenMap = attributeChildrenMap;
        } else {
            if (null == nonAttributeChildren) {
                nonAttributeChildren = new ArrayList();
                nonAttributeChildrenMap = new HashMap();
            }
            children = nonAttributeChildren;
            childrenMap = nonAttributeChildrenMap;
        }

        if (null == anXPathFragment) {
            xPathNode.setNodeValue(aNodeValue);
            xPathNode.setParent(this);
            if (aNodeValue instanceof XMLAnyAttributeMappingNodeValue) {
                setAnyAttributeNodeValue((XMLAnyAttributeMappingNodeValue)aNodeValue);
                anyAttributeNode = xPathNode;
            } else {
                children.add(xPathNode);
                childrenMap.put(anXPathFragment, xPathNode);
            }
            return;
        }

        int index = children.indexOf(xPathNode);
        if (index >= 0) {
            xPathNode = (XPathNode)children.get(index);
        } else {
            xPathNode.setParent(this);
            children.add(xPathNode);
            if (XPathFragment.SELF_FRAGMENT.equals(anXPathFragment)) {
                if (null == selfChildren) {
                    selfChildren = new ArrayList();
                }
                selfChildren.add(xPathNode);
            } else {
                childrenMap.put(anXPathFragment, xPathNode);
            }
        }

        if (aNodeValue.isOwningNode(anXPathFragment)) {
            xPathNode.setNodeValue(aNodeValue);
        } else {
            XPathFragment nextFragment = anXPathFragment.getNextFragment();
            xPathNode.addChild(nextFragment, aNodeValue, namespaceResolver);
        }
    }

    public boolean marshal(MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(marshalRecord, object, session, namespaceResolver, null);
    }

    public boolean marshal(MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, org.eclipse.persistence.oxm.XMLMarshaller marshaller) {
        if ((null == nodeValue) || nodeValue.isMarshalOnlyNodeValue()) {
            marshalRecord.addGroupingElement(this);

            XPathNode xPathNode;
            boolean hasValue = false;
            if (null != attributeChildren) {
                int size = attributeChildren.size();
                for (int x = 0; x < size; x++) {
                    xPathNode = (XPathNode)attributeChildren.get(x);
                    hasValue = xPathNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller) || hasValue;
                }
            }
            if (anyAttributeNode != null) {
                hasValue = anyAttributeNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller) || hasValue;
            }
            if (null != nonAttributeChildren) {
                int size = nonAttributeChildren.size();
                for (int x = 0; x < size; x++) {
                    xPathNode = (XPathNode)nonAttributeChildren.get(x);
                    hasValue = xPathNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller) || hasValue;
                }
            }

            if (hasValue) {
                marshalRecord.endElement(this.getXPathFragment(), namespaceResolver);
            } else {
                marshalRecord.removeGroupingElement(this);
            }

            return hasValue;
        } else {
            return nodeValue.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, marshaller);
        }
    }

    public boolean startElement(MarshalRecord marshalRecord, XPathFragment anXPathFragment, Object object, AbstractSession session, NamespaceResolver namespaceResolver, TreeObjectBuilder compositeObjectBuilder, Object compositeObject) {
        if (null == anXPathFragment) {
            return false;
        }
        marshalRecord.openStartElement(anXPathFragment, namespaceResolver);
        XPathNode attributeNode;
        boolean hasValue = false;
        if (null != attributeChildren) {
            int size = attributeChildren.size();
            for (int x = 0; x < size; x++) {
                attributeNode = (XPathNode)attributeChildren.get(x);
                hasValue = attributeNode.marshal(marshalRecord, object, session, namespaceResolver) || hasValue;
            }
        }
        if (anyAttributeNode != null) {
            //marshal the anyAttribute node here before closeStartElement()
            hasValue = anyAttributeNode.marshal(marshalRecord, object, session, namespaceResolver) || hasValue;
        }

        if (null != compositeObjectBuilder) {
            hasValue = compositeObjectBuilder.marshalAttributes(marshalRecord, compositeObject, session) || hasValue;
        }
        marshalRecord.closeStartElement();
        return hasValue;
    }
}