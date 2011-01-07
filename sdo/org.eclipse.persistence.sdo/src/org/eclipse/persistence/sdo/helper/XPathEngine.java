/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * <p><b>Purpose</b>: Perform operations based on SDO XPath against DataObjects.
 * </p>
 */

public class XPathEngine {
    private static final int SET = 1;
    private static final int ISSET = 2;
    private static final int UNSET = 3;
    protected static XPathEngine defaultXPathEngine;

    private XPathEngine() {
    }

    public static XPathEngine getInstance() {
        if (defaultXPathEngine == null) {
            defaultXPathEngine = new XPathEngine();
        }
        return defaultXPathEngine;
    }

    /**Handle queries about setting status of a property through path base access.
     *
     * @param path      the String representation of path based access
     * @param caller    the DataObject that pass path information in
     * @return          true if queried property is set, otherwise false
     */
    public boolean isSet(String path, DataObject caller) {
        try {
            return processPath(path, null, caller, false, ISSET);
            // return lastDataObject.isSet(lastProperty);// spec. did not mention exception
        } catch (Exception e) {
            return false;// spec. did not mention exception
        }
    }

    /**Unset the value of a property through the path base access.
     *
     * @param path      the String representation of path based access
     * @param caller    the DataObject that pass path information in
     */
    public void unset(String path, DataObject caller) {        
        processPath(path, null, caller, false, UNSET);        
    }

    /**Set a property's value through the path base access.
     *
     * @param path      the String representation of path based access
     * @param value     the value to be set as the target property's value
     * @param caller    the DataObject that pass path information in
     * @param convertValue     boolean used for set if we should convert the value 
     */
    public void set(String path, Object value, DataObject caller, boolean convertValue) {
        processPath(path, value, caller, convertValue, SET);
    }

    /**When accessing values corresponding to properties of DataObject by path base accessors,
     * the accessed getters will pass informations to this method to process information and
     * acquire wanted values.
     *
     * @param path          the String representation of path based access
     * @param caller        the DataObject that pass path information in
     * @return              the value gotten by accessing through path
     */
    public Object get(String path, DataObject caller) {// path like "a/b/c"
        if ((path == null) || path.equals(SDOConstants.EMPTY_STRING)) {
            return null;
        }
        if (path.equals("..")) {
            return caller.getContainer();
        }
        if (path.equals(SDOConstants.SDO_XPATH_TO_ROOT)) {
            return ((SDODataObject)caller).getRootObject();
        }
        path = getLocalName(path);

        int index = path.indexOf('/');
        if (index > -1) {
            if (index == (path.length() - 1)) {
                return getValueForFragment(path.substring(0, index), caller);
            } else {
                Object value = getValueForFragment(path.substring(0, index), caller);
                DataObject currentDataObject = (DataObject) value;
                return get(path.substring(index + 1, path.length()), currentDataObject);
            }
        }
        return getValueForFragment(path, caller);
    }
    
        /**extract wanted fragment from the string representation of path and pass processed result to
     * method setIsSetUnSet for further operation.
     *
     * @param path      the String representation of path based access
     * @param value     the value to be set as the target property's value
     * @param caller    the DataObject that pass path information in
     * @param convertValue     boolean used for set if we should convert the value
     * @param _case     an int value indicating what kind of operation to use: set, isset or unset.
     * @return          true if operation is isset and property's value is set, otherwise false.
     */
    private boolean processPath(String path, Object value, DataObject caller, boolean convertValue, int _case) {
        path = getLocalName(path);
        int lastSlashIndex = path.lastIndexOf('/');
        String frag;
        if (-1 < lastSlashIndex) {// case 1: a/b/c
            frag = path.substring(lastSlashIndex + 1);
            return setIsSetUnSet(frag, path, caller, value, lastSlashIndex, convertValue, _case);
        } else {// case 2 : "a"
            frag = path;
            return setIsSetUnSet(frag, path, caller, value, lastSlashIndex, convertValue, _case);
        }
    }  

    /**According to the requirement, correspondingly perform isset, unset or set function.
     *
     * @param frag              one string fragment in the path
     * @param path              the String representation of path based access
     * @param caller            the DataObject that pass path information in
     * @param value             the value to be set as the target property's value
     * @param lastSlashIndex    the last index of '/' in the path string
     * @param convertValue     boolean used for set if we should convert the value
     * @param _case             an int value indicating what kind of operation to use: set, isset or unset.
     * @return                  true if operation is isset and property's value is set, otherwise false.
     */
    private boolean setIsSetUnSet(String frag, String path, DataObject caller, Object value, int lastSlashIndex, boolean convertValue, int _case) {
        int indexOfDot = frag.lastIndexOf('.');
        int indexOfOpenBracket = frag.lastIndexOf('[');
        int indexOfCloseBracket = frag.lastIndexOf(']');
        int numInLastProperty = getNumberInFrag(frag, indexOfDot, indexOfOpenBracket, indexOfCloseBracket);
        String lastPropertyName = getPropertyNameInFrag(frag, numInLastProperty, indexOfDot, indexOfOpenBracket);// get last property name on path for case 1
        DataObject lastDataObject;
        if (-1 < lastSlashIndex) {
            Object lastObject = get(path.substring(0, lastSlashIndex), caller);// get last dataobject on path
            // If trying to access a list element from a null list, this object will be
            // an instance of ListWrapper, not DataObject, but the error message is the same
            // as if it was a null DataObject
            if (lastObject == null || lastObject instanceof ListWrapper) {
                throw SDOException.cannotPerformOperationOnProperty(lastPropertyName, path);
            }
            lastDataObject = (SDODataObject) lastObject;
        } else {
            lastDataObject = caller;
        }
        Property lastProperty = lastDataObject.getInstanceProperty(lastPropertyName);// get property of this dataobject
        
        switch (_case) {
        case SET:                        
            if (lastProperty == null) {
                lastProperty = ((SDODataObject)lastDataObject).defineOpenContentProperty(lastPropertyName, value);
            }if(lastProperty != null){
              set(lastProperty, lastDataObject, numInLastProperty, value,convertValue);
            }
            return false;
        case ISSET:
            if(lastProperty == null){
             return false; 
            }
            return isSet(lastProperty, lastDataObject);
        case UNSET:
            if(lastProperty == null){
             return false; 
            }
            unSet(lastProperty, lastDataObject, numInLastProperty);
            return false;
        default:
            return false;
        }
    }

    /** Method that returns whether a property is set.
     *
     * @param lastProperty      the property to queries.
     * @param lastDataObject    the DataObject, owner of the queried property
     * @return      return true, if property's value is set, otherwise false.
     */
    private boolean isSet(Property lastProperty, DataObject lastDataObject) {
        return lastDataObject.isSet(lastProperty);
    }

    /** Method that unset a certain property's value.
     *
     * @param lastProperty      the property to queries.
     * @param lastDataObject    the DataObject, owner of the queried property
     */
    private void unSet(Property lastProperty, DataObject lastDataObject, int numInLastProperty) {
        if (numInLastProperty == -1) {
            lastDataObject.unset(lastProperty);
        } else {
            List objects = lastDataObject.getList(lastProperty);
            if (numInLastProperty <= objects.size()) {
                objects.remove(numInLastProperty);
            }
        }
    }

    /** Set a property's value.
     *
     * @param lastProperty      the property to queries.
     * @param lastDataObject    the DataObject, owner of the queried property
     * @param numInLastProperty the index number in the value list of the above property
     * @param value             the value to be set as the target property's value
     * @param convertValue     boolean used for set if we should convert the value 
     */
    private void set(Property lastProperty, DataObject lastDataObject, int numInLastProperty, Object value, boolean convertValue) {
        if (numInLastProperty == -1) {
            if (lastDataObject != null) {
                if(convertValue){
                  DataHelper dataHelper = ((SDODataObject)lastDataObject).getType().getHelperContext().getDataHelper(); 
                  value = dataHelper.convert(lastProperty, value);
                }
                lastDataObject.set(lastProperty, value);
            } else {
                throw new IllegalArgumentException("lastDataObject is null");
            }
        } else {// case like set("a/b.1", List) will cause a list be added into a existed list
            List objects = lastDataObject.getList(lastProperty);

            if (convertValue) {
                DataHelper dataHelper = ((SDODataObject)lastDataObject).getType().getHelperContext().getDataHelper(); 
                value =  dataHelper.convert(lastProperty.getType(), value);
            }
            
            Sequence seq = lastDataObject.getSequence();
            if (seq != null) {
                seq.setValue(numInLastProperty, value);
            } else {
                objects.set(numInLastProperty, value);
            }
        }
    }

    private String getLocalName(String qualifiedName) {
        int index = qualifiedName.indexOf(':');
        if (index > -1) {
            String local = qualifiedName.substring(index + 1, qualifiedName.length());
            return local;
        } else {
            return qualifiedName;
        }
    }

    /** Process the passed in fragment, extract the position information if available,
     *  acquire the property name hidden in this fragment and check if this fragment is
     *  actually query base. Then perform corresponding actions to access values.
     *
     * @param frag      one string fragment in the path
     * @param caller    a DataObject that originally took the path information
     * @return          values to be accessed
     */
    private Object getValueForFragment(String frag, DataObject caller) {
        int indexOfDot = frag.lastIndexOf('.');
        int indexOfOpenBracket = frag.lastIndexOf('[');
        int indexOfCloseBracket = frag.lastIndexOf(']');
        int position = getNumberInFrag(frag, indexOfDot, indexOfOpenBracket, indexOfCloseBracket);
        String propertyName = getPropertyNameInFrag(frag, position, indexOfDot, indexOfOpenBracket);
        int equalSignIndex = isQueryPath(frag, indexOfOpenBracket, indexOfCloseBracket);
        if (equalSignIndex == -1) {// not query path, note:
            return getObjectByFragment(propertyName, position, caller);
        }
        return getDataObjectFromQuery(frag, indexOfOpenBracket, indexOfCloseBracket, equalSignIndex, caller, propertyName);
    }

    /** Extract the property name hidden in a fragment of path
     *
     * @param frag          one string fragment in the path
     * @param position      the index of values to be accessed
     * @param indexOfDot    the index of . in the fragment
     * @param indexOfOpenBracket    the indexof [ in the fragment
     * @return              the property name hidden in this fragment
     */
    private String getPropertyNameInFrag(String frag, int position, int indexOfDot, int indexOfOpenBracket) {
        int startIndex = 0;
        if (frag.indexOf('@') != -1) {
            startIndex += 1;
        }
        if (indexOfOpenBracket != -1) {
            return frag.substring(startIndex, indexOfOpenBracket);
        }
        if ((indexOfDot != -1) && (position != -1)) {
            return frag.substring(startIndex, indexOfDot);
        }
        return frag.substring(startIndex);
    }

    /** Judge if positional path belongs to bracket case or dot case, then perform
     * different actions.
     *
     * @param frag                  one string fragment in the path
     * @param indexOfDot            the index of . in the fragment
     * @param indexOfOpenBracket    the index of [ in the fragment
     * @param indexOfCloseBracket   the index of ] in the fragment
     * @return      the position hidden in fragment
     */
    private int getNumberInFrag(String frag, int indexOfDot, int indexOfOpenBracket, int indexOfCloseBracket) {
        if ((indexOfOpenBracket != -1) && (indexOfCloseBracket != -1) && (indexOfOpenBracket < indexOfCloseBracket)) {
            return acquireNumberInBrackets(frag, indexOfOpenBracket, indexOfCloseBracket);
        }
        if (indexOfDot != -1) {
            return acquireNumberAtDot(frag, indexOfDot);
        }
        return -1;
    }

    /**check if information in brackets is qury or not.
     *
     * @param frag                 a fragment in path
     * @param openBracketIndex     index of open bracket in fragment
     * @param closeBracketIndex    index of close bracket in fragment
     * @return
     */
    private int isQueryPath(String frag, int openBracketIndex, int closeBracketIndex) {
        if ((openBracketIndex != -1) && (closeBracketIndex != -1) && (openBracketIndex < closeBracketIndex)) {
            return frag.substring(openBracketIndex, closeBracketIndex).indexOf('=');
        }
        return -1;
    }

    // extract value from query and acquire dataobject that meets this requirement

    /**Access the DataObject value by using the fragment containing query informations.
     *
     * @param frag                  one string fragment in the path
     * @param openBracketIndex      the index of open bracket in a fragment
     * @param closeBracketIndex     the index of close bracket in a fragment
     * @param equalsignIndex        the index of equalsign in string fragment quoted by brackets
     * @param caller                the DataObject that passes the path information in
     * @param callerProperty        the name of the property
     * @return                      the DataObject as value of the property having name as the above callerProperty
     */
    private DataObject getDataObjectFromQuery(String frag, int openBracketIndex, int closeBracketIndex, int equalsignIndex, DataObject caller, String callerProperty) {
        try {
            // trim off any whitespace for property names
            String propertyNameOfQryDataObject = frag.substring(openBracketIndex + 1, equalsignIndex + openBracketIndex).trim();            
            List objects = caller.getList(caller.getInstanceProperty(callerProperty));
            String query = frag.substring(equalsignIndex + openBracketIndex + 1, closeBracketIndex);
            String value = null;
            int firstQuoteIndex = query.indexOf('\'');
            int lastQuoteIndex = query.lastIndexOf('\'');
            if ((firstQuoteIndex == -1) && (lastQuoteIndex == -1)) {// !! note: case: [number=1'23'] is assume not to happen !!
                firstQuoteIndex = query.indexOf("\"");
                lastQuoteIndex = query.lastIndexOf("\"");
            }
            if ((firstQuoteIndex != -1) && (lastQuoteIndex != -1) && (firstQuoteIndex < lastQuoteIndex)) {// quoted string existed
                value = query.substring(firstQuoteIndex + 1, lastQuoteIndex);
            } else {
                // if the value is not enclosed on quotes, trim off any whitespace
                value = query.trim();
            }
            Iterator iterObjects = objects.iterator();
            Object queryValue = value;
            Object actualValue = null;
            while (iterObjects.hasNext()) {
                DataObject cur = (DataObject)iterObjects.next();
                Property p = cur.getInstanceProperty(propertyNameOfQryDataObject);
                if(p != null){
                  try {
                      queryValue = XMLConversionManager.getDefaultXMLManager().convertObject(queryValue, p.getType().getInstanceClass());
                  } catch (ConversionException e) {
                      //do nothing, skip
                  }
  
                  if (!p.isMany()) {
                      actualValue = cur.get(p);
                      if (actualValue.equals(queryValue)) {
                          return cur;
                      }
                  } else {// case p is many type
                      List values = cur.getList(p);
                      Iterator iterValues = values.iterator();
  
                      while (iterValues.hasNext()) {
                          actualValue = iterValues.next();
                          if (actualValue.equals(queryValue)) {
                              return cur;
                          }
                      }
                  }
                }
            }

            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // process a portion of path, find out if it is positional or not.
    // if it is, then check it is using '.' or '[ ]' and perform corresponding
    // process.

    /** Check if position exists, then either acquire value through position or directly.
     *
     * @param propertyName      the name of property of the caller
     * @param position          the index of values to be accessed
     * @param caller            the DataObject containing property with the passed in property name
     * @return                  the values to be accessed
     */
    private Object getObjectByFragment(String propertyName, int position, DataObject caller) {
        Property prop = caller.getInstanceProperty(propertyName);
        if (prop == null){
          return null;
        }
        
        if (prop.isMany() && position > -1) {
            return caller.getList(prop).get(position);
        } else {
            return caller.get(prop);        	
        }
    }

    /** Extract position (index) from a bracket fragment of path
     *
     * @param frag               a fragment of path
     * @param openBracketIndex   the index of [ in path
     * @param closeBracketIndex  the index of ] in path
     * @return                   the index hidden in fragment
     */
    private int acquireNumberInBrackets(String frag, int openBracketIndex, int closeBracketIndex) {
        String number = frag.substring(openBracketIndex + 1, closeBracketIndex);

        if (number.matches("[1-9][0-9]*")) {
            return Integer.parseInt(number) - 1;
            //value = number;
        }
        return -1;// throw Illegal path?
    }

    /**Extract the position (index) from a dot fragment of path
     *
     * @param frag      a fragment of path
     * @param dotIndex  the index of . in path
     * @return          the index hidden in the fragment
     */
    private int acquireNumberAtDot(String frag, int dotIndex) {
        int value = -1;
        String position = frag.substring(dotIndex + 1);
        if (position.matches("[0-9]+")) {
            value = Integer.parseInt(position);
            //value = position;
        }
        return value;
    }

    // dataobject a's property a has value dataobject b, dataobject b's property b has value dataobject c,
    //dataobject c's property c has value boolean,

    /** access the wanted values through path and convert it into required java class.
     * If conversion is not supported, exception is thrown.
     *
     * @param path      string representation of accessing path
     * @param cls       the java class that accessed value is to be converted to
     * @param caller    the DataObject that pass the path in
     * @return          values to be accessed
     * @throws ClassCastException
     */
    public Object convertObjectToValueByPath(String path, Class cls, DataObject caller) throws ClassCastException {
        if (null == path || XMLConstants.EMPTY_STRING.equals(path)) {
            throw new ClassCastException("Attempting null value conversion.");
        }
        try {
            int lastSlashIndex = path.lastIndexOf('/');
            SDODataObject lastDataObject;
            String lastPropertyName;
            Property lastProperty;
            int numInLastProperty = -1;

            // to do: if "/" or ".." lastDataObject = container or root
            if (-1 < lastSlashIndex) {// case 1 "a/b/c"
                String frag = path.substring(lastSlashIndex + 1);
                int indexOfDot = frag.lastIndexOf('.');
                int indexOfOpenBracket = frag.lastIndexOf('[');
                int indexOfCloseBracket = frag.lastIndexOf(']');
                numInLastProperty = getNumberInFrag(frag, indexOfDot, indexOfOpenBracket, indexOfCloseBracket);
                lastPropertyName = getPropertyNameInFrag(frag, numInLastProperty, indexOfDot, indexOfOpenBracket);//getPropertyNameFromFragment(path.substring(lastSlashIndex + 1));// get last property name on path for case 1
                lastDataObject = (SDODataObject)caller.getDataObject(path.substring(0, lastSlashIndex));// get last dataobject on path
                if(lastDataObject == null){
                  return null;
                }
                lastProperty = lastDataObject.getInstanceProperty(lastPropertyName);// get property of this dataobject
            } else {// case 2 "a"
                //to do: call eextractPositionAndPropertyName() here
                String frag = path;
                int indexOfDot = frag.lastIndexOf('.');
                int indexOfOpenBracket = frag.lastIndexOf('[');
                int indexOfCloseBracket = frag.lastIndexOf(']');
                numInLastProperty = getNumberInFrag(frag, indexOfDot, indexOfOpenBracket, indexOfCloseBracket);
                lastPropertyName = getPropertyNameInFrag(frag, numInLastProperty, indexOfDot, indexOfOpenBracket);//getPropertyNameFromFragment(path);// get last property name on path for case 1
                lastDataObject = (SDODataObject)caller;
                if(lastDataObject == null){
                  return null;
                }
                lastProperty = caller.getInstanceProperty(lastPropertyName);// get property of this dataobject
            }

            if ((lastProperty != null) && (cls == Date.class) && lastProperty.getType().equals(SDOConstants.SDO_STRING)) {// in case getDate, for string property, use DataHelper
                DataHelper dHelper = HelperProvider.getDefaultContext().getDataHelper();
                String dateString;
                if (numInLastProperty == -1) {
                    dateString = (String)lastDataObject.get(lastProperty);
                } else {
                    dateString = (String)(lastDataObject.getList(lastProperty)).get(numInLastProperty);
                }
                return dHelper.toDate(dateString);
            }
            return lastDataObject.convertObjectToValue(lastProperty, numInLastProperty, cls);
        } catch (IllegalArgumentException e) {// according to exception table 1st row, when get(Property) and get(Index) throw IllegalArgument, get(String) return null
            throw new ClassCastException("Conversion is not supported.");
            //return null;
        }
    }
}
