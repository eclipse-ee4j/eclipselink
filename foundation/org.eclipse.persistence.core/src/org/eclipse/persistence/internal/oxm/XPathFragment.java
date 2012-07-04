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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  Represents a token from an XPath statement.</p>
 * <p>For example the following XPath statment a/b[2]/text() corresponds to three
 * XPathFragments:  "a", "b[2]", and "text()".</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>Maintain name, namespace, and prefix information.</li>
 * <li>Maintain information about the corresponding node type.</li>
 * <li>Maintain index information if any.  The XPathFragment corresponding to
 * b[2] would have an index value of 2.</li>
 * </ul>
 */
public class XPathFragment {
    public static final XPathFragment TEXT_FRAGMENT = new XPathFragment(XMLConstants.TEXT);
    public static final String SELF_XPATH = ".";
    public static final XPathFragment SELF_FRAGMENT = new XPathFragment(SELF_XPATH);
    public static final XPathFragment ANY_FRAGMENT = null;

    private XPathFragment nextFragment;
    private XMLField xmlField;
    private String xpath;
    private boolean hasAttribute = false;
    private boolean hasText = false;
    private boolean hasNamespace = false;
    private boolean containsIndex = false;
    private int indexValue = -1;//if containsIndex, then this is the value of the index.
    private boolean shouldExecuteSelectNodes = false;
    private String shortName;
    private byte[] shortNameBytes;
    private String prefix;
    private String localName;
    private String namespaceURI;
    private QName qname;
    protected boolean nameIsText = false;
    protected boolean isSelfFragment = false;
    private QName leafElementType;
    private boolean generatedPrefix = false;   
    private XPathPredicate predicate; 

    public XPathPredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(XPathPredicate condition) {
        this.predicate = condition;
    }

    public XPathFragment(String xpathString) {
        setXPath(xpathString);
    }

    public XPathFragment() {
        super();
    }

    public XPathFragment getNextFragment() {
        return nextFragment;
    }

    public void setNextFragment(XPathFragment nextFragment) {
        this.nextFragment = nextFragment;
    }

    public void setXPath(String xpathString) {

        xpath = xpathString;
        shortName = xpathString;

        // handle case:  company[name/text()="Oracle"]
        if(xpathString.length() > 0){
            if ((xpath.indexOf('[') != -1) && (xpath.indexOf(']') == -1)) {
                setShouldExecuteSelectNodes(true);
                return;
            }

            // handle case:  ancestor::*/jaxb:class/@name
            if (xpath.indexOf("::") != -1) {
                setShouldExecuteSelectNodes(true);
                return;
            }

            if (xpathString.charAt(0) == '@') {
                hasAttribute = true;
                shortName = xpathString.substring(1).intern();
                indexValue = hasIndex(xpathString);
                setupNamespaceInformation(shortName);
                return;
            }

            if (xpathString.charAt(0) == '/') {
                setShouldExecuteSelectNodes(true);
                shortName = xpathString.substring(xpathString.lastIndexOf('/') + 1).intern();
                indexValue = hasIndex(xpathString);
                setupNamespaceInformation(shortName);
                return;
            }
        }

        if (xpathString.equals(XMLConstants.TEXT)) {
            nameIsText = true;
            shortName = xpathString.intern();
            return;
        } else {
            nameIsText = false;
        }

        // handle "self" xpath
        if (xpathString.equals(SELF_XPATH)) {
            isSelfFragment = true;
            shortName = xpathString.intern();
            return;
        }

        indexValue = hasIndex(xpathString);
        setupNamespaceInformation(shortName);
    }

    private void setupNamespaceInformation(String xpathString) {
        int nsindex = xpathString.indexOf(XMLConstants.COLON);
        if (nsindex != -1) {
            hasNamespace = true;
            localName = xpathString.substring(nsindex + 1).intern();
            prefix = xpathString.substring(0, nsindex).intern();
        } else {
        	localName = xpathString.intern();
        }
    }

    public boolean isAttribute() {
        return hasAttribute;
    }

    public void setAttribute(boolean isAttribute) {
        hasAttribute = isAttribute;
    }

    public String getShortName() {
    	if(shortName == null){
    		if(prefix !=null && prefix.length() >0){
      			shortName = prefix + XMLConstants.COLON + localName;
      		}else{
      		    shortName = localName;
      		}
    	}
        return shortName;
    }

    public byte[] getShortNameBytes() {
        if(null == shortNameBytes) {
            try {
                shortNameBytes = getShortName().getBytes(XMLConstants.DEFAULT_XML_ENCODING);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return shortNameBytes;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        resetShortName();
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
    	this.localName = localName;
    	resetShortName();
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
    	if (isSelfFragment || namespaceURI !=null && namespaceURI.length() == 0) {
            this.namespaceURI = null;
        } else {
        	this.namespaceURI = namespaceURI;
        }
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName q) {
        qname = q;
    }

    private int hasIndex(String xpathString) {
        int index = -1;
        int startindex = xpathString.lastIndexOf('[');
        if ((startindex != -1) && (xpathString.lastIndexOf(']') != -1)) {
            StringTokenizer st = new StringTokenizer(xpathString, "[]");
            String element = st.nextToken();

            while(st.hasMoreTokens()) {
                String indexString = st.nextToken();
                try {
                    index = Integer.valueOf(indexString).intValue();
                    setContainsIndex(true);
                } catch (NumberFormatException e) {
                    StringTokenizer st2 = new StringTokenizer(indexString, "=");
                    if(2 == st2.countTokens()) {
                        XPathFragment xPathFragment = new XPathFragment(st2.nextToken());
                        String value = st2.nextToken();
                        value = value.substring(1, value.length() - 1);
                        predicate = new XPathPredicate(xPathFragment, value);
                    } else {
                        setContainsIndex(true);
                    }
                    setShouldExecuteSelectNodes(true);
                }
            }
            shortName = element;

        } else {
            index = -1;
        }
        return index;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(int indexValue) {
        this.indexValue = indexValue;
    }

    public String getXPath() {
        return xpath;
    }

    public boolean hasNamespace() {
        return hasNamespace;
    }

    /**
     * INTERNAL:
     * Indicates if the xpath is "."
     *
     * @return true if the xpath is ".", false otherwise
     */
    public boolean isSelfFragment() {
        return isSelfFragment;
    }

    public boolean nameIsText() {
        return nameIsText;
    }

    public void setHasText(boolean hasText) {
        this.hasText = hasText;
    }

    public boolean getHasText() {
        return hasText;
    }

    public void setContainsIndex(boolean containsIndex) {
        this.containsIndex = containsIndex;
    }

    public boolean containsIndex() {
        return containsIndex;
    }

    public void setShouldExecuteSelectNodes(boolean newShouldExecuteSelectNodes) {
        this.shouldExecuteSelectNodes = newShouldExecuteSelectNodes;
    }

    public boolean shouldExecuteSelectNodes() {
        return shouldExecuteSelectNodes;
    }

    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            } else if (this == object) {
                return true;
            }
            XPathFragment xPathFragment = (XPathFragment)object;
            if(null == predicate && null != xPathFragment.predicate) {
                return false;
            }
            if(null != predicate && !predicate.equals(xPathFragment.predicate)) {
                return false;
            }
            return ((nameIsText && xPathFragment.nameIsText) || (localName == xPathFragment.localName) || ((localName != null) && localName.equals(xPathFragment.localName))) && ((namespaceURI == xPathFragment.namespaceURI) || ((namespaceURI != null) && namespaceURI.equals(xPathFragment.namespaceURI))) && (this.indexValue == xPathFragment.indexValue) && (nameIsText == xPathFragment.nameIsText);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean qNameEquals(Object object) {
        try {
            if (this == object) {
                return true;
            }
            XPathFragment xPathFragment = (XPathFragment)object;
            return ((localName == xPathFragment.localName) || ((localName != null) && localName.equals(xPathFragment.localName))) && ((namespaceURI == xPathFragment.namespaceURI) || ((namespaceURI != null) && namespaceURI.equals(xPathFragment.namespaceURI))) && (nameIsText == xPathFragment.nameIsText);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        if(null == localName) {
            return 1;
        } else {
            return localName.hashCode();
        }
    }

    public QName getLeafElementType() {
        return leafElementType;
    }

    public boolean hasLeafElementType() {
        return leafElementType != null;
    }

    public void setLeafElementType(QName type) {
        leafElementType = type;
    }

    public void setGeneratedPrefix(boolean isGenerated) {
        generatedPrefix = isGenerated;
    }

    public boolean isGeneratedPrefix() {
        return generatedPrefix;
    }
    
    public XMLField getXMLField() {
        return this.xmlField;
    }
    
    public void setXMLField(XMLField field) {
        this.xmlField = field;
    }
    
    private void resetShortName(){
    	shortName = null;
    	shortNameBytes = null;
    }   
}
