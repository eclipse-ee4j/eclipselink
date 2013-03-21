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
package org.eclipse.persistence.oxm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XMLConversionPair;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathPredicate;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;

/**
 * TopLink XML mappings make use of XMLFields based on XPath statements to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain node type, path, and positional information.  The XPath is specified on the
 * field using the <code>setXPath</code> method or by using the appropriate constructor.
 *
 * <p>The following XPath statements may be used to specify the location of XML data relating to an object's
 * name attribute:
 *
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">XPath</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">@name</td>
 * <td headers="c2">The "@" character indicates that the node is an attribute.</td>
 * </tr>
 * <tr>
 * <td headers="c1">text()</td>
 * <td headers="c2">"text()" indicates that the node is a text node.  In this case the name value in the
 * text node belongs to the context node.</td>
 * </tr>
 * <tr>
 * <td headers="c1">full-name/text()</td>
 * <td headers="c2">The name information is stored in the text node of the full-name element.</td>
 * </tr>
 * <tr>
 * <td headers="c1" nowrap="true">personal-info/name/text()</td>
 * <td headers="c2">The XPath statement may be used to specify any valid path.</td>
 * </tr>
 * <tr>
 * <td headers="c1">name[2]/text()</td>
 * <td headers="c2">The XPath statement may contain positional information.  In this case the name
 * information is stored in the text node of the second occurrence of the name element.</td>
 * </tr>
 * </table>
 * <p><b>Mapping to a Specific Schema Type</b>: In most cases TopLink can determine the target format in the
 * XML document.  However, there are cases where you must specify which one of a number of possible targets
 * TopLink should use. For example, a java.util.Calendar could be marshalled to a schema date, time, or dateTime,
 * or a byte[] could be marshalled to a schema hexBinary or base64Binary node.
 *
 * <!--
 *    <?xml version="1.0" encoding="UTF-8"?>
 *    <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *        <xsd:element name="customer" type="customer-type"/>
 *        <xsd:complexType name="customer-type">
 *            <xsd:sequence>
 *                <xsd:element name="picture" type="xsd:hexBinary"/>
 *                <xsd:element name="resume" type="xsd:base64Binary"/>
 *            </xsd:sequence>
 *        </xsd:complexType>
 *    </xsd:schema>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="customer" type="customer-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="customer-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="picture" type="xsd:hexBinary"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="resume" type="xsd:base64Binary"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLField pictureField = new XMLField("picture/text()")<br>
 * pictureField.setSchemaType(XMLConstants.HEX_BINARY_QNAME);<br>
 * </code>
 *
 * <p><b>Setting custom conversion pairs</b>: By default in TopLink XML built-in schema types are associated with
 * java classes and vice versa.  These default pairs can be modified by the user using the addJavaConversion and
 * addXMLConversion api.  For example by default a java.util.Calendar is mapped to the dateTime schema type
 * so the XML will be formated based on that type.  Below are the default schema type to java type conversion pairs
 * and the default java type to schema type conversion pairs.
 * <p><b>XML schema type to Java type default conversion pairs</b>
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Schema Type</th>
 * <th id="c2" align="left">Java Type</th>
 * </tr>
 * <tr>
 * <td headers="c1">base64Binary</td>
 * <td headers="c2">byte[]</td>
 * </tr>
 * <tr>
 * <td headers="c1">boolean</td>
 * <td headers="c2">boolean</td>
 * </tr>
 * <tr>
 * <td headers="c1">byte</td>
 * <td headers="c2">byte</td>
 * </tr>
 * <tr>
 * <td headers="c1">date</td>
 * <td headers="c2">java.util.Calendar</td>
 * </tr>
 * <tr>
 * <td headers="c1">dateTime</td>
 * <td headers="c2">java.util.Calendar</td>
 * </tr>
 * <tr>
 * <td headers="c1">decimal</td>
 * <td headers="c2">java.math.BigDecimal</td>
 * </tr>
 * <tr>
 * <td headers="c1">double</td>
 * <td headers="c2">double</td>
 * </tr>
 * <tr>
 * <td headers="c1">float</td>
 * <td headers="c2">float</td>
 * </tr>
 * <tr>
 * <td headers="c1">hexBinary</td>
 * <td headers="c2">byte[]</td>
 * </tr>
 * <tr>
 * <td headers="c1">int</td>
 * <td headers="c2">int</td>
 * </tr>
 * <tr>
 * <td headers="c1">integer</td>
 * <td headers="c2">java.math.BigInteger</td>
 * </tr>
 * <tr>
 * <td headers="c1">long</td>
 * <td headers="c2">long</td>
 * </tr>
 * <tr>
 * <td headers="c1">QName</td>
 * <td headers="c2">javax.xml.namespace.QName</td>
 * </tr>
 * <tr>
 * <td headers="c1">time</td>
 * <td headers="c2">java.util.Calendar</td>
 * </tr>
 * <tr>
 * <td headers="c1">unsignedByte</td>
 * <td headers="c2">short</td>
 * </tr>
 * <tr>
 * <td headers="c1">unsignedInt</td>
 * <td headers="c2">long</td>
 * </tr>
 * <tr>
 * <td headers="c1">unsignedShort</td>
 * <td headers="c2">int</td>
 * </tr>
 * <tr>
 * <td headers="c1">anySimpleType</td>
 * <td headers="c2">java.lang.String</td>
 * </tr>
 * </table>
 *
 * <p><b>Java type to XML schema type default conversion pairs</b>
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Java Type</th>
 * <th id="c2" align="left">Schema Type</th>
 * </tr>
 * <tr>
 * <td headers="c1">byte[]</td>
 * <td headers="c2">hexBinary</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Byte[]</td>
 * <td headers="c2">hexBinary</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.math.BigDecimal</td>
 * <td headers="c2">decimal</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.math.BigInteger</td>
 * <td headers="c2">integer</td>
 * </tr>
 * <tr>
 * <td headers="c1">boolean</td>
 * <td headers="c2">boolean</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Boolean</td>
 * <td headers="c2">boolean</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Byte</td>
 * <td headers="c2">Byte</td>
 * </tr>
 * <tr>
 * <td headers="c1">byte</td>
 * <td headers="c2">byte</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.util.Calendar</td>
 * <td headers="c2">dateTime</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.util.GregorianCalendar</td>
 * <td headers="c2">dateTime</td>
 * </tr>
 * <tr>
 * <td headers="c1">double</td>
 * <td headers="c2">double</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Double</td>
 * <td headers="c2">double</td>
 * </tr>
 * <tr>
 * <td headers="c1">float</td>
 * <td headers="c2">float</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Float</td>
 * <td headers="c2">float</td>
 * </tr>
 * <tr>
 * <td headers="c1">int</td>
 * <td headers="c2">int</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Integer</td>
 * <td headers="c2">int</td>
 * </tr>
 * <tr>
 * <td headers="c1">long</td>
 * <td headers="c2">long</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Long</td>
 * <td headers="c2">long</td>
 * </tr>
 * <tr>
 * <td headers="c1">short</td>
 * <td headers="c2">short</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.Short</td>
 * <td headers="c2">short</td>
 * </tr>
 * <tr>
 * <td headers="c1">javax.xml.namespace.QName</td>
 * <td headers="c2">QName</td>
 * </tr>
 * <tr>
 * <td headers="c1">java.lang.String</td>
 * <td headers="c2">string</td>
 * </tr>
 * </table>
 * @see org.eclipse.persistence.oxm.XMLUnionField
 */
public class XMLField extends DatabaseField implements Field<NamespaceResolver> {
    private NamespaceResolver namespaceResolver;
    private QName schemaType;
    private XPathFragment xPathFragment;
    private XPathFragment lastXPathFragment;
    private boolean isCDATA = false;
    private boolean isRequired = false;
    private boolean isInitialized = false;

    /** Makes this maintain the collection of items in a single attribute or element instead of having one element per item in the collection.
    * Default is false */
    private boolean usesSingleNode;

    // Hashtables for the non-default conversions, these Hashtables are 
    // used if the user has made any modifications to the pairs
    protected HashMap userXMLTypes;
    protected HashMap userJavaTypes;
    protected boolean isTypedTextField;
    protected QName leafElementType;

    /**
     * Default constructor, create a new XMLField
     */
    public XMLField() {
        super();
        isTypedTextField = false;
    }

    /**
     * Default constructor, create a new XMLField based on the specified xPath
     * @param xPath The xPath statement for this field
     */
    public XMLField(String xPath) {
        super(xPath, new DatabaseTable());
        isTypedTextField = false;
    }

    public void initialize() {
        if(null != xPathFragment) {
           initializeXPathFragment(xPathFragment);
        }
        isInitialized = true;
    }

    private void initializeXPathFragment(XPathFragment xPathFragment) {
        XPathPredicate predicate = xPathFragment.getPredicate(); 
        if(null != predicate) {
            initializeXPathFragment(predicate.getXPathFragment());
        }

    	String localName = xPathFragment.getLocalName();
    	if(localName !=null && !localName.equals(XMLConstants.EMPTY_STRING)){
	        if(null == xPathFragment.getNamespaceURI()) {
	            if(xPathFragment.hasNamespace()) {
	                if(null == namespaceResolver) {
	                    throw XMLMarshalException.namespaceNotFound(xPathFragment.getShortName());
	                } else {
	                    String uri = namespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
	                    if(null == uri && null != xPathFragment.getPrefix()) {
	                        throw XMLMarshalException.namespaceNotFound(xPathFragment.getShortName());
	                    }
	                    xPathFragment.setNamespaceURI(uri);
	                }
	            }
	            else if(!xPathFragment.isAttribute() && null != namespaceResolver) {
	                xPathFragment.setNamespaceURI(namespaceResolver.getDefaultNamespaceURI());
	            }
	        }
    	}
        XPathFragment nextXPathFragment = xPathFragment.getNextFragment();
        if(null != nextXPathFragment) {
            initializeXPathFragment(nextXPathFragment);
        }
    }

    /**
         * Returns the xpath statement associated with this XMLField
         * @return The xpath statement associated with this XMLField
         */
    public String getXPath() {
        return getName();
    }

    /**
        * Set the xpath statment for this XMLField.
        * @param xPath The xpath statement to be associated with this XMLField
        */
    public void setXPath(String xPath) {
        setName(xPath);
    }

    /**
     * Get the NamespaceResolver associated with this XMLField
     * @return The NamespaceResolver associated with this XMLField
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    /**
     * Set the NamespaceResolver associated with this XMLField
     * @param newNamespaceResolver The namespaceResolver to be associated with this XMLField
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
    public void setNamespaceResolver(NamespaceResolver newNamespaceResolver) {
        namespaceResolver = newNamespaceResolver;
    }

    /**
    * PUBLIC:
    * Sets whether the mapping uses a single node.
    * @param usesSingleNode True if the items in the collection are in a single node or false if each of the items in the collection is in its own node
    */
    public void setUsesSingleNode(boolean usesSingleNode) {
        this.usesSingleNode = usesSingleNode;
    }

    /**
    * PUBLIC:
    * Checks whether the mapping uses a single node.
    *
    * @return True if the items in the collection are in a single node or false if each of the items in the collection is in its own node.
    */
    public boolean usesSingleNode() {
        return usesSingleNode;
    }

    /**
     * Sets the schematype associated with this XMLField
     * This is an optional setting; when set the schema type will be used to format the XML appropriately
     * @param value QName to be added to the list of schema types
     */
    public void setSchemaType(QName value) {
        this.schemaType = value;
    }

    /**
    * Return the schema type associated with this field
    * @return the schema type
    */
    public QName getSchemaType() {
        return schemaType;
    }

    /**
      * Returns if the field is a typed text field
      * True when we should base conversions on the "type" attribute on elements
      * @return True when we should base conversions on the "type" attribute on elements, otherwise false
      */
    public boolean isTypedTextField() {
        return isTypedTextField;
    }

    /**
     * Set if the field is a typed text field
     * True when we should base conversions on the "type" attribute on elements
     * @param value The boolean value specifiy if  this is a typed text field
     */
    public void setIsTypedTextField(boolean value) {
        isTypedTextField = value;
    }

    /**
     * INTERNAL:
     * Indicates if the xpath for this field is "."
     *
     * @return true if the xpath is ".", false otherwise
     */
    public boolean isSelfField() {
        if (null == xPathFragment) {
            return false;
        }
        return xPathFragment.isSelfFragment();
    }

    /**
    * INTERNAL:
    * Returns false since this is a union field
     * The subclass XMLUnionField returns true for this
    */
    public boolean isUnionField() {
        return false;
    }

    /**
     * Override setName in superclass
     */
    @Override
    public void setName(String xPath, String startDelimiter, String endDelimiter) {
        super.setName(xPath, null, null);

        if (hasPath(xPath)) {
            buildFragments(xPath);
        } else {
            XPathFragment xPathFragment = new XPathFragment(xPath.intern());
            xPathFragment.setXMLField(this);
            setXPathFragment(xPathFragment);
            setLastXPathFragment(xPathFragment);
        }
    }
    
    /**
     * This has the same effect as calling the setXPath method
     *
     * @param xPath The xPath associated with this XMLField
     */
    public void setName(String xPath) {
        setName(xPath, null, null);
    }

    private boolean hasPath(String xpathString) {
        return ((xpathString != null) && (xpathString.indexOf('/') != -1));
    }

    private void buildFragments(String xpathString) {
        StringTokenizer st = new StringTokenizer(xpathString, "/", true);        
        String next;
        int i = 0;
        XPathFragment currentXPathFragment = null;
        XPathFragment nextXPathFragment = null;

        while (st.hasMoreTokens()) {
            next = st.nextToken();
            if (null != next) {
                if ("/".equals(next)) {
                    if (0 == i) {
                        next = "/" + st.nextToken();
                    } else { 
                        continue;
                    }
                }                   
                if (next.contains("[") && !next.contains("]")) {
                    StringBuilder sb = new StringBuilder(next);
                    String more;
                    while (st.hasMoreTokens()) {
                        more = st.nextToken();
                        sb.append(more);
                        if (more.contains("]"))
                            break;
                    }   
                    next = sb.toString().intern();
                } else {
                    next = next.intern();
                }
                nextXPathFragment = new XPathFragment(next);
                if (0 == i) {
                    setXPathFragment(nextXPathFragment);
                } else {
                    currentXPathFragment.setNextFragment(nextXPathFragment);
                    if (nextXPathFragment.isAttribute() || nextXPathFragment.nameIsText()) {
                        currentXPathFragment.setHasText(true);
                    }
                }
                nextXPathFragment.setXMLField(this);
                currentXPathFragment = nextXPathFragment;
                i++;
            }
            setLastXPathFragment(currentXPathFragment);
        } 
    }

    /**
     * INTERNAL:
     * Maintain a direct pointer to the first XPathFragment.  For example given
     * the following XPath first/middle/@last, first is the first XPathFragment.
     */
    public XPathFragment getXPathFragment() {
        return xPathFragment;
    }

    /**
     * INTERNAL:
     * Return the first XPathFragment.
     */
    public void setXPathFragment(XPathFragment xPathFragment) {
        this.xPathFragment = xPathFragment;
    }

    /**
     * INTERNAL:
     * Return the last XPathFragment.
     */
    public XPathFragment getLastXPathFragment() {
        return lastXPathFragment;
    }

    /**
     * INTERNAL:
     * Maintain a direct pointer to the last XPathFragment.  For example given
     * the following XPath first/middle/@last, @last is the last XPathFragment.
     */
    public void setLastXPathFragment(XPathFragment lastXPathFragment) {
        this.lastXPathFragment = lastXPathFragment;
    }

    /**
    * Return the class for a given qualified XML Schema type
    * @param qname The qualified name of the XML Schema type to use as a key in the lookup
    * @return The class corresponding to the specified schema type, if no corresponding match found returns null
    */
    public Class getJavaClass(QName qname) {
        if (userXMLTypes != null) {
        	Class theClass = (Class)userXMLTypes.get(qname);
        	if(theClass != null){
        		return theClass;
        	}            
        }
        return (Class)XMLConversionManager.getDefaultXMLTypes().get(qname);
    }

    /**
     * Return the qualified XML Schema type for a given class
     * @param javaClass The class to use as a key in the lookup
     * @return QName The qualified XML Schema type, if no corresponding match found returns null
     */
    public QName getXMLType(Class javaClass) {
        if (userJavaTypes != null) {
            QName theQName = (QName)userJavaTypes.get(javaClass);
            if (theQName !=null) {
                return theQName;
            }
        }

        return (QName)XMLConversionManager.getDefaultJavaTypes().get(javaClass);
    }

    /**
    * @return a HashMap of Java to XML Schema type conversion pairs
    */
    private HashMap getUserJavaTypes() {
        // If no manual modifications have been made to the conversion pairs yet,
        // userJavaTypes will be null and needs to be built
        if (userJavaTypes == null) {
            userJavaTypes = new HashMap();
        }
        return userJavaTypes;
    }

    /**
     * @return a HashMap of XML Schema types to Java types conversion pairs
     */
    private HashMap getUserXMLTypes() {
        // If no manual modifications have been made to the conversion pairs yet,
        // userXMLTypes will be null and needs to be built
        if (userXMLTypes == null) {
            userXMLTypes = new HashMap();
        }
        return userXMLTypes;
    }

    /**
    * INTERNAL:
    */
    public ArrayList getUserXMLTypesForDeploymentXML() {
        if (userXMLTypes != null) {
            ArrayList types = new ArrayList(userXMLTypes.size());
            Iterator iter = userXMLTypes.entrySet().iterator();
            while (iter.hasNext()) {
            	Map.Entry entry = (Map.Entry)iter.next();                
                XMLConversionPair pair = new XMLConversionPair((QName)entry.getKey(),((Class)entry.getValue()).getName());
                types.add(pair);
            }
            return types;
        }
        return null;
    }

    /**
    * INTERNAL:
    */
    public void setUserXMLTypesForDeploymentXML(ArrayList pairs) throws Exception {
        if (pairs.size() > 0) {
            userXMLTypes = new HashMap();
            Iterator iter = pairs.iterator();
            while (iter.hasNext()) {
                XMLConversionPair pair = (XMLConversionPair)iter.next();
                if ((pair.getXmlType() != null) && (pair.getJavaType() != null)) {
                    userXMLTypes.put(pair.getXmlType(), Class.forName(pair.getJavaType()));
                }
            }
        }
    }

    /**
    * INTERNAL:
    */
    public ArrayList getUserJavaTypesForDeploymentXML() {
        if (userJavaTypes != null) {
            ArrayList types = new ArrayList(userJavaTypes.size());
            Iterator iter = userJavaTypes.entrySet().iterator();
            while (iter.hasNext()) {
            	Map.Entry entry = (Map.Entry)iter.next();                
                XMLConversionPair pair = new XMLConversionPair((QName)entry.getValue(), ((Class)entry.getKey()).getName());
                types.add(pair);
            }
            return types;
        }
        return null;
    }

    /**
    * INTERNAL:
    */
    public void setUserJavaTypesForDeploymentXML(ArrayList pairs) throws Exception {
        if (pairs.size() > 0) {
            userJavaTypes = new HashMap();
            Iterator iter = pairs.iterator();
            while (iter.hasNext()) {
                XMLConversionPair pair = (XMLConversionPair)iter.next();
                if ((pair.getXmlType() != null) && (pair.getJavaType() != null)) {
                    userJavaTypes.put(Class.forName(pair.getJavaType()), pair.getXmlType());
                }
            }
        }
    }

    /**
    * INTERNAL:
    * Called from DOMRecord and XMLReader.  MappingNodeValues call XMLReader which calls this method so that other XMLReader subclasses can override.
    */
    public Object convertValueBasedOnSchemaType(Object value, XMLConversionManager xmlConversionManager, AbstractUnmarshalRecord record) {
        if (schemaType != null) { 
        	if(XMLConstants.QNAME_QNAME.equals(schemaType)){
        		return xmlConversionManager.buildQNameFromString((String)value, record);        		
        	}else{
	            Class fieldType = getType();
	            if (fieldType == null) {
	                fieldType = getJavaClass(schemaType);
	            }            
	            return xmlConversionManager.convertObject(value, fieldType, schemaType);
        	}
        }
        return value;
    }

    
    /**
    * Add an XML to Java Conversion pair entry
    * @param qname The qualified name of the XML schema type
    * @param javaClass The class to add
    */
    public void addXMLConversion(QName qname, Class javaClass) {
        getUserXMLTypes().put(qname, javaClass);
    }

    /**
     * Add a Java to XML Conversion pair entry
     * @param javaClass The class to add
     * @param qname The qualified name of the XML schema type
     */
    public void addJavaConversion(Class javaClass, QName qname) {
        getUserJavaTypes().put(javaClass, qname);
    }

    /**
     * Add an entry for both an XML Conversion and a Java Conversion entry
     * @param qname The qualified name of the XML schema type
     * @param javaClass
     */
    public void addConversion(QName qname, Class javaClass) {
        addJavaConversion(javaClass, qname);
        addXMLConversion(qname, javaClass);
    }

    /**
     * Remove an XML to Java Conversion entry
     * @param qname
     */
    public void removeXMLConversion(QName qname) {
        getUserXMLTypes().remove(qname);
    }

    /**
     * Remove a Java to XML Conversion entry
     *
     * @param javaClass
     */
    public void removeJavaConversion(Class javaClass) {
        getUserJavaTypes().remove(javaClass);
    }

    /**
     * Remove both a Java to XML Conversion and the corresponding XML to Java Conversion entry
     *
     * @param qname
     * @param javaClass
     */
    public void removeConversion(QName qname, Class javaClass) {
        removeJavaConversion(javaClass);
        removeXMLConversion(qname);
    }
	
	/**
     * Assumes type is in the format prefix:localPart, or localPart.
     * 
     * @param type
     */
    public void setLeafElementType(QName type) {
        leafElementType = type;
        if (hasLastXPathFragment()) {
            getLastXPathFragment().setLeafElementType(type);
        }
    }
    
	public QName getLeafElementType() {
        if (lastXPathFragment != null) {
            return lastXPathFragment.getLeafElementType();
        }
        return leafElementType; 
    }

	/**
     * INTERNAL:
     * @return
     */
    public boolean hasLastXPathFragment() {
        return lastXPathFragment != null;
    }

    /**
     * INTERNAL:
     */
    public QName getSchemaTypeForValue(Object value, CoreAbstractSession session) {
        if(leafElementType != null){
            return leafElementType;
        }else if (isTypedTextField) {
        	if (CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(value.getClass())){        	
        		return ((XMLGregorianCalendar) value).getXMLSchemaType();
        	}else if (CoreClassConstants.DURATION.isAssignableFrom(value.getClass())){
        		return getXMLType(CoreClassConstants.DURATION);
        	}
            return getXMLType(value.getClass());
        } 
        return schemaType;        
    }

    
    /**
     * INTERNAL:
     * @param CDATA
     */
    public void setIsCDATA(boolean CDATA) {
        this.isCDATA = CDATA;
    }
    
    /**
     * INTERNAL:
     * @return
     */
    public boolean isCDATA() {
        return isCDATA;
    }    
    /**
     * INTERNAL
     */
    public boolean isSchemaType(QName schemaType){
        if(getSchemaType() == null){
            return false;
        }
        return getSchemaType().equals(schemaType);    	
    }

    /**
     * Indicates if this XMLField represents a "required" XML element or attribute
     * ([minOccurs="1"] for elements, [use="required"] for attributes).  NOTE: This
     * API is used only for Schema Generation.
     * 
     * @see org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Set whether this XMLField represents a "required" XML element or attribute
     * ([minOccurs="1"] for elements, [use="required"] for attributes).  NOTE: This
     * API is used only for Schema Generation.
     * 
     * @see org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    @Override
    public boolean equals(Object object) {
        try {
            if(!isInitialized) {
                return super.equals(object);
            }
            if(this == object) {
                return true;
            }
            XMLField xmlField = (XMLField) object;
            if(!xPathFragment.equals(xmlField.getXPathFragment())) {
                return false;
            }
            XPathFragment xpf = xPathFragment;
            XPathFragment xpf2 = xmlField.getXPathFragment();
            while(xpf.getNextFragment() != null) {
                xpf = xpf.getNextFragment();
                xpf2 = xpf2.getNextFragment();
                if(!xpf.equals(xpf2)) {
                    return false;
                }
            }
            return null == xpf2.getNextFragment();
        } catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if(null == xPathFragment) {
            return 1;
        }
        return xPathFragment.hashCode();
    }

}
