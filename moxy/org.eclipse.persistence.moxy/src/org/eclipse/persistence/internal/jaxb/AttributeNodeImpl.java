package org.eclipse.persistence.internal.jaxb;

import org.eclipse.persistence.jaxb.AttributeNode;

public class AttributeNodeImpl<X> implements AttributeNode {
    protected String currentAttribute;

    public AttributeNodeImpl() {
    }
    
    public AttributeNodeImpl(String attributeName) {
        this.currentAttribute = attributeName;
    }
    
    public String getAttributeName() {
        return currentAttribute;
    }
}
