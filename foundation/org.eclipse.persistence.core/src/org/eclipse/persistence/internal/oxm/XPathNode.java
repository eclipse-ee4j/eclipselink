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
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  XPathNodes are used together to build a tree.  The tree
 * is built from all of the XPath statements specified in the mapping metadata 
 * (mappings and policies).  This tree is then navigated by an 
 * EventObjectBuilder to perform marshal and unmarshal operations.</p>
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
    private NodeValue unmarshalNodeValue;
    private NodeValue marshalNodeValue;
    private boolean isMarshalOnlyNodeValue;
    private XPathFragment xPathFragment;
    private XPathNode parent;
    private List<XPathNode> attributeChildren;
    private List<XPathNode> nonAttributeChildren;
    private List<XPathNode> selfChildren;
    private Map<XPathFragment, XPathNode> attributeChildrenMap;
    private Map<XPathFragment, XPathNode> nonAttributeChildrenMap;
    private XMLAnyAttributeMappingNodeValue anyAttributeNodeValue;
    private XPathNode anyAttributeNode;
    private XPathNode textNode;
    private XPathNode anyNode;
    private boolean hasTypeChild;
    private boolean hasPredicateSiblings;
    private boolean hasPredicateChildren;


	public XPathFragment getXPathFragment() {
        return xPathFragment;
    }

    public void setXPathFragment(XPathFragment xPathFragment) {
        this.xPathFragment = xPathFragment;
    }

    public NodeValue getNodeValue() {
        return unmarshalNodeValue;
    }

    public void setNodeValue(NodeValue nodeValue) {
        this.marshalNodeValue = nodeValue;
        this.unmarshalNodeValue = nodeValue;
        if (null != nodeValue) {
            nodeValue.setXPathNode(this);
            isMarshalOnlyNodeValue =  nodeValue.isMarshalOnlyNodeValue();
        }
    }
    
    public NodeValue getUnmarshalNodeValue() {
        return unmarshalNodeValue;
    }
    
    public void setUnmarshalNodeValue(NodeValue nodeValue) {
        if (null != nodeValue) {
            nodeValue.setXPathNode(this);
        }
        this.unmarshalNodeValue = nodeValue;
    }

    public NodeValue getMarshalNodeValue() {
        return marshalNodeValue;
    }
    
    public void setMarshalNodeValue(NodeValue nodeValue) {
        if (null != nodeValue) {
            nodeValue.setXPathNode(this);
        }
        this.marshalNodeValue = nodeValue;
        isMarshalOnlyNodeValue =  marshalNodeValue.isMarshalOnlyNodeValue();
    }
    
    public XPathNode getParent() {
        return parent;
    }

    public void setParent(XPathNode parent) {
        this.parent = parent;
    }

    public List<XPathNode> getAttributeChildren() {
        return this.attributeChildren;
    }

    public List<XPathNode> getNonAttributeChildren() {
        return this.nonAttributeChildren;
    }

    public List<XPathNode> getSelfChildren() {
        return this.selfChildren;
    }

    public Map<XPathFragment, XPathNode> getNonAttributeChildrenMap() {
        return this.nonAttributeChildrenMap;
    }

    public Map<XPathFragment, XPathNode> getAttributeChildrenMap() {
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

    public XPathNode getAnyNode() {
        return this.anyNode;
    }

    public void setAnyNode(XPathNode xPathNode) {
        this.anyNode = xPathNode;
    }

    public XPathNode getTextNode() {
        return this.textNode;
    }

    public void setTextNode(XPathNode xPathNode) {
        this.textNode = xPathNode;
    }

    public boolean hasTypeChild() {
        return hasTypeChild;
    }

    public boolean equals(Object object) {
        try {
            XPathFragment perfNodeXPathFragment = ((XPathNode)object).getXPathFragment();
            if(xPathFragment == perfNodeXPathFragment) {
                return true;
            } else if(null == xPathFragment) {
                return false;
            } else if(null == perfNodeXPathFragment) {
                return false;
            }
            return xPathFragment.equals(perfNodeXPathFragment);

            // turn fix off for now until we re-enable XMLAnyObjectAndAnyCollectionTestCases
            //          } catch (NullPointerException npe) {
            // b5259059 all cases X0X1 (1mapping xpath=null, 2nd mapping xpath=filled
            // catch when object.getXPathFragment() == null
            // (this will also catch case where perfNode XPath is null)
            //          	return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public XPathNode addChild(XPathFragment anXPathFragment, NodeValue aNodeValue, NamespaceResolver namespaceResolver) {
        if (null != anXPathFragment && anXPathFragment.nameIsText()) {
            if (aNodeValue.isOwningNode(anXPathFragment)) {
                XPathNode textXPathNode = this.getTextNode();
                if(textXPathNode == null) {
                    textXPathNode = new XPathNode();
                }
                textXPathNode.setParent(this);
                textXPathNode.setXPathFragment(anXPathFragment);
                if (aNodeValue.isMarshalNodeValue()) {
                    textXPathNode.setMarshalNodeValue(aNodeValue);
                }
                if (aNodeValue.isUnmarshalNodeValue()) {
                    textXPathNode.setUnmarshalNodeValue(aNodeValue);
                }
                this.setTextNode(textXPathNode);
                if(null != nonAttributeChildren && !nonAttributeChildren.contains(textXPathNode)) {
                    nonAttributeChildren.add(textXPathNode);
                }
                if(aNodeValue instanceof XMLCompositeObjectMappingNodeValue) {
                    if (null == selfChildren) {
                        selfChildren = new ArrayList<XPathNode>();
                    }
                    selfChildren.add(textXPathNode);
                }
                return textXPathNode;
            }
        }

        if (anXPathFragment != null && namespaceResolver != null && anXPathFragment.getNamespaceURI() == null && !anXPathFragment.nameIsText()) {
            if(!anXPathFragment.isAttribute()) {
                anXPathFragment.setNamespaceURI(namespaceResolver.resolveNamespacePrefix(anXPathFragment.getPrefix()));
            } else if(anXPathFragment.hasNamespace()) {
                anXPathFragment.setNamespaceURI(namespaceResolver.resolveNamespacePrefix(anXPathFragment.getPrefix()));
            }
        }

        XPathNode xPathNode = new XPathNode();
        xPathNode.setXPathFragment(anXPathFragment);

        List children;
        Map childrenMap;

        if ((anXPathFragment != null) && anXPathFragment.isAttribute()) {
            if (null == attributeChildren) {
                attributeChildren = new ArrayList();
            }
            if (null == attributeChildrenMap) {
                attributeChildrenMap = new HashMap();
            }
            children = attributeChildren;
            childrenMap = attributeChildrenMap;
        } else {
            if (null == nonAttributeChildren) {
                nonAttributeChildren = new ArrayList();
                if(null != textNode) {
                    nonAttributeChildren.add(textNode);
                }
            }
            if (null == nonAttributeChildrenMap) {
                nonAttributeChildrenMap = new HashMap();
            }
            if(anXPathFragment !=null && Constants.SCHEMA_TYPE_ATTRIBUTE.equals(anXPathFragment.getLocalName())){
            	hasTypeChild = true;
            }
            children = nonAttributeChildren;
            childrenMap = nonAttributeChildrenMap;
        }

        if (null == anXPathFragment) {
            if(aNodeValue.isMarshalNodeValue()) {
                xPathNode.setMarshalNodeValue(aNodeValue);
            }
            if(aNodeValue.isUnmarshalNodeValue() && xPathNode.getUnmarshalNodeValue() == null) {
                xPathNode.setUnmarshalNodeValue(aNodeValue);
            }
            xPathNode.setParent(this);
            if (aNodeValue instanceof XMLAnyAttributeMappingNodeValue) {
                setAnyAttributeNodeValue((XMLAnyAttributeMappingNodeValue)aNodeValue);
                anyAttributeNode = xPathNode;
            } else {
                if(!children.contains(xPathNode)) {
                    children.add(xPathNode);
                }
                setAnyNode(xPathNode);
            }
            return xPathNode;
        }
        this.hasPredicateChildren = hasPredicateChildren || anXPathFragment.getPredicate() != null;
        if(this.getNonAttributeChildren() != null && this.hasPredicateChildren) {
            for(XPathNode nextChild: this.getNonAttributeChildren()) {
                XPathFragment nextFrag = nextChild.getXPathFragment();
                if(nextFrag != null && nextFrag.equals(anXPathFragment, true)) {
                    if(nextFrag.getPredicate() == null && anXPathFragment.getPredicate() != null) {
                        nextChild.setHasPredicateSiblings(true);
                    } else if(anXPathFragment.getPredicate() == null && nextFrag.getPredicate() != null) {
                        xPathNode.setHasPredicateSiblings(true);
                    }
                }
            }
        }

        boolean isSelfFragment = XPathFragment.SELF_FRAGMENT.equals(anXPathFragment);
        if(isSelfFragment){
            children.add(xPathNode);
            if (null == selfChildren) {
                selfChildren = new ArrayList<XPathNode>();
            }
            selfChildren.add(xPathNode);
        }else{
            int index = children.indexOf(xPathNode);
            if (index >= 0) {
                xPathNode = (XPathNode)children.get(index);
            } else {
                xPathNode.setParent(this);
                if(!children.contains(xPathNode)) {
                    children.add(xPathNode);
                }
                childrenMap.put(anXPathFragment, xPathNode);
            }
        }
        
        if (aNodeValue.isOwningNode(anXPathFragment)) {
            if(aNodeValue.isMarshalNodeValue()) {
                xPathNode.setMarshalNodeValue(aNodeValue);
            } 
            if(aNodeValue.isUnmarshalNodeValue() && xPathNode.getUnmarshalNodeValue() == null) {
                xPathNode.setUnmarshalNodeValue(aNodeValue);
            }
        } else {
            XPathFragment nextFragment = anXPathFragment.getNextFragment();
            xPathNode.addChild(nextFragment, aNodeValue, namespaceResolver);
        }
        return xPathNode;
    }

    private void setHasPredicateSiblings(boolean b) {
        this.hasPredicateSiblings = b;
    }
    
    public boolean hasPredicateSiblings() {
        return this.hasPredicateSiblings;
    }

    public boolean marshal(MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller, MarshalContext marshalContext, XPathFragment rootFragment) {
        if ((null == marshalNodeValue) || isMarshalOnlyNodeValue) {
            if(marshalRecord.isWrapperAsCollectionName() && null != nonAttributeChildren && nonAttributeChildren.size() == 1) {
                XPathNode childXPathNode = nonAttributeChildren.get(0);
                NodeValue childXPathNodeUnmarshalNodeValue = childXPathNode.getUnmarshalNodeValue();
                if(childXPathNode != null && childXPathNodeUnmarshalNodeValue.isContainerValue()) {
                    ContainerValue containerValue = (ContainerValue) childXPathNodeUnmarshalNodeValue;
                    if(containerValue.isWrapperAllowedAsCollectionName()) {
                        XPathNode wrapperXPathNode = new XPathNode();
                        wrapperXPathNode.setXPathFragment(this.getXPathFragment());
                        wrapperXPathNode.setMarshalNodeValue(childXPathNode.getMarshalNodeValue());
                        return wrapperXPathNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller, marshalContext, rootFragment);
                    }
                }
            }
            marshalRecord.addGroupingElement(this);

            boolean hasValue = false;
            if (null != attributeChildren) {
                for (int x = 0, size = attributeChildren.size(); x < size; x++) {
                    XPathNode xPathNode = attributeChildren.get(x);
                    hasValue = xPathNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller, ObjectMarshalContext.getInstance(), this.xPathFragment) || hasValue;
                }
            }
            if (anyAttributeNode != null) {
                hasValue = anyAttributeNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller, ObjectMarshalContext.getInstance(), null) || hasValue;
            }
            if (null == nonAttributeChildren) {
                if (textNode != null) {
                    hasValue = textNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller, ObjectMarshalContext.getInstance(), null) || hasValue;
                }
            } else {
                for (int x = 0, size = marshalContext.getNonAttributeChildrenSize(this); x < size; x++) {
                    XPathNode xPathNode = (XPathNode)marshalContext.getNonAttributeChild(x, this);
                    MarshalContext childMarshalContext = marshalContext.getMarshalContext(x);
                    hasValue = xPathNode.marshal(marshalRecord, object, session, namespaceResolver, marshaller, childMarshalContext, this.xPathFragment) || hasValue;
                }
            }

            if (hasValue) {
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            } else {
                marshalRecord.removeGroupingElement(this);
            }

            return hasValue;
        } else {
            if(marshalNodeValue.isMappingNodeValue()) {
                Mapping mapping = ((MappingNodeValue)marshalNodeValue).getMapping();
                CoreAttributeGroup currentGroup = marshalRecord.getCurrentAttributeGroup();
                if(!(currentGroup.containsAttributeInternal(mapping.getAttributeName()))) {
                    return false;
                }
            }
            return marshalContext.marshal(marshalNodeValue, xPathFragment, marshalRecord, object, session, namespaceResolver, rootFragment);
        }
    }

    public boolean startElement(MarshalRecord marshalRecord, XPathFragment anXPathFragment, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, ObjectBuilder compositeObjectBuilder, Object compositeObject) {    
        if (null == anXPathFragment) {
            return false;
        }

        marshalRecord.openStartElement(anXPathFragment, namespaceResolver);
        boolean hasValue = false;

        marshalRecord.predicateAttribute(anXPathFragment, namespaceResolver);

        if (null != attributeChildren) {
            for (int x = 0, size = attributeChildren.size(); x < size; x++) {
                XPathNode attributeNode = attributeChildren.get(x);
                hasValue = attributeNode.marshal(marshalRecord, object, session, namespaceResolver, null, ObjectMarshalContext.getInstance(), null) || hasValue;
            }
        }
        if (anyAttributeNode != null) {
            //marshal the anyAttribute node here before closeStartElement()
            hasValue = anyAttributeNode.marshal(marshalRecord, object, session, namespaceResolver, null, ObjectMarshalContext.getInstance(), null) || hasValue;
        }

        if (null != compositeObjectBuilder) {
            hasValue = compositeObjectBuilder.marshalAttributes(marshalRecord, compositeObject, session) || hasValue;
        }
        marshalRecord.closeStartElement();
        return hasValue;
    }

    /**
     * Marshal any 'self' mapped attributes.  
     * 
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param marshaller
     * @return
     */
    public boolean marshalSelfAttributes(MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
        if (marshalNodeValue == null) {
            return false;
        }
        return marshalNodeValue.marshalSelfAttributes(xPathFragment, marshalRecord, object, session, namespaceResolver, marshaller);
    }

    public boolean isWhitespaceAware() {
        return unmarshalNodeValue.isWhitespaceAware();
    }

}