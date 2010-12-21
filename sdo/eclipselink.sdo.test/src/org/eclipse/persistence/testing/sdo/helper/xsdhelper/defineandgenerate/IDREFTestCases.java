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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.Type;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class IDREFTestCases extends XSDHelperDefineAndGenerateTestCases {
    public IDREFTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithIDREF.xsd";
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithIDREFGenerated.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.IDREFTestCases" };
        TestRunner.main(arguments);
    }

    /*
    public void testDefineAndGenerate() {
      super.testDefineAndGenerate();
      try{
      XMLHelper xmlHelper = aHelperContext.getXMLHelper()
      FileInputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREF.xml");
      Project p = ((SDOXMLHelper)xmlHelper).getTopLinkProject();
      XMLProjectWriter projectWriter = new XMLProjectWriter();
      PrintWriter pw = new PrintWriter(System.out);
      projectWriter.write(p, pw);
      XMLDocument doc = xmlHelper.load(is);
      FileWriter writer = new FileWriter("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREFGenerated.xml");
      xmlHelper.save(doc, writer, null);
      System.out.println(writer.toString());
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    */
    public List getControlTypes() {
        List types = new ArrayList();
        String uri = NON_DEFAULT_URI;
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type yearMonthDayType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");
        String javaPackage = NON_DEFAULT_JAVA_PACKAGE_NAME;

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setDataType(false);
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrder");
        SDOType custType = new SDOType(uri, "Customer");
        custType.setDataType(false);
        custType.setInstanceClassName(javaPackage + "." + "Customer");

        SDOType companyType = new SDOType(uri, "Company");
        companyType.setDataType(false);
        companyType.setInstanceClassName(javaPackage + "." + "Company");

        SDOType itemType = new SDOType(uri, "Item");
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage + "." + "Item");

        /**Item type**/
        SDOProperty itemIDProp = new SDOProperty(aHelperContext);

        //itemIDProp.setAttribute(true);
        //itemIDProp.setElement(false);
        //itemIDProp.setContainment(true);
        itemIDProp.setName("itemID");
        itemIDProp.setXsdLocalName("itemID");
        itemIDProp.setXsd(true);
        itemIDProp.setContainingType(itemType);
        itemIDProp.setType(stringType);
        itemType.addDeclaredProperty(itemIDProp);

        SDOProperty itemNameProp = new SDOProperty(aHelperContext);

        //itemNameProp.setAttribute(true);
        //itemNameProp.setElement(false);
        //itemNameProp.setContainment(true);
        itemNameProp.setName("name");
        itemNameProp.setXsdLocalName("name");
        itemNameProp.setXsd(true);
        itemNameProp.setContainingType(itemType);
        itemNameProp.setType(stringType);
        itemType.addDeclaredProperty(itemNameProp);

        /**company type**/
        SDOProperty companyNameProp = new SDOProperty(aHelperContext);

        //companyNameProp.setAttribute(true);
        //companyNameProp.setContainment(true);
        companyNameProp.setName("name");
        companyNameProp.setXsdLocalName("name");
        companyNameProp.setXsd(true);
        companyNameProp.setContainingType(companyType);
        companyNameProp.setType(stringType);
        companyType.getDeclaredProperties().add(companyNameProp);

        SDOProperty customersProp = new SDOProperty(aHelperContext);

        //customersProp.setAttribute(false);
        //customersProp.setElement(true);
        customersProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        customersProp.setMany(true);
        customersProp.setContainment(true);
        //addrNameProp.setContainment(true);
        customersProp.setName("cust");
        customersProp.setXsdLocalName("cust");
        customersProp.setXsd(true);
        customersProp.setContainingType(companyType);
        customersProp.setType(custType);
        companyType.getDeclaredProperties().add(customersProp);

        SDOProperty porderssProp = new SDOProperty(aHelperContext);

        //porderssProp.setAttribute(false);
        //porderssProp.setElement(true);
        porderssProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        porderssProp.setContainment(true);
        //addrNameProp.setContainment(true);
        porderssProp.setMany(true);
        porderssProp.setName("porder");
        porderssProp.setXsdLocalName("porder");
        porderssProp.setXsd(true);
        porderssProp.setContainingType(companyType);
        porderssProp.setType(POtype);
        companyType.getDeclaredProperties().add(porderssProp);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);

        //itemsProp.setAttribute(false);
        //itemsProp.setElement(true);
        itemsProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemsProp.setContainment(true);
        itemsProp.setMany(true);
        itemsProp.setName("item");
        itemsProp.setXsdLocalName("item");
        itemsProp.setXsd(true);
        itemsProp.setContainingType(companyType);
        itemsProp.setType(itemType);
        companyType.getDeclaredProperties().add(itemsProp);

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName(javaPackage + "." + "USAddress");
        SDOProperty addrNameProp = new SDOProperty(aHelperContext);

        //addrNameProp.setAttribute(true);
        //addrNameProp.setContainment(true);
        addrNameProp.setName("name");
        addrNameProp.setXsdLocalName("name");
        addrNameProp.setXsd(true);
        addrNameProp.setContainingType(USaddrType);
        addrNameProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsdLocalName("street");
        streetProp.setXsd(true);
        streetProp.setContainingType(USaddrType);
        streetProp.setType(stringType);
        //streetProp.setAttribute(true);
        //streetProp.setContainment(true);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        //cityProp.setAttribute(true);
        //cityProp.setContainment(true);
        cityProp.setXsdLocalName("city");
        cityProp.setXsd(true);
        cityProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setXsdLocalName("state");
        stateProp.setXsd(true);
        stateProp.setContainingType(USaddrType);
        stateProp.setType(stringType);
        //stateProp.setAttribute(true);
        //stateProp.setContainment(true);
        USaddrType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsdLocalName("zip");
        zipProp.setXsd(true);
        zipProp.setContainingType(USaddrType);
        zipProp.setType(decimalType);
        //zipProp.setAttribute(true);
        //zipProp.setContainment(true);
        USaddrType.getDeclaredProperties().add(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setXsdLocalName("country");
        countryProp.setXsd(true);
        countryProp.setContainingType(USaddrType);
        countryProp.setType(stringType);
        //countryProp.setAttribute(true);
        //countryProp.setContainment(true);
        countryProp.setDefault("US");
        USaddrType.getDeclaredProperties().add(countryProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setType(USaddrType);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setXsd(true);
        shipToProp.setContainingType(POtype);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setType(USaddrType);
        billToProp.setXsdLocalName("billTo");
        billToProp.setXsd(true);
        billToProp.setContainingType(POtype);

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setContainment(false);
        //itemProp.setElement(true);
        itemProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemProp.setMany(true);
        itemProp.setType(itemType);
        itemProp.setXsdLocalName("item");
        itemProp.setXsd(true);
        itemProp.setContainingType(POtype);

        SDOProperty poIDProp = new SDOProperty(aHelperContext);

        //poIDProp.setElement(true);
        poIDProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        poIDProp.setContainment(true);
        poIDProp.setName("poID");
        poIDProp.setXsdLocalName("poID");
        poIDProp.setXsd(true);
        poIDProp.setContainingType(POtype);
        poIDProp.setType(stringType);

        SDOProperty customerProp = new SDOProperty(aHelperContext);
        customerProp.setName("customer");
        customerProp.setType(custType);
        //customerProp.setAttribute(true);
        //customerProp.setElement(false);
        //customerProp.setContainment(false);
        //customerProp.setContainment(true);
        customerProp.setXsdLocalName("customer");
        customerProp.setXsd(true);
        customerProp.setContainingType(POtype);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setType(stringType);
        //commentProp.setAttribute(true);
        //commentProp.setContainment(false);
        //commentProp.setContainment(true);
        commentProp.setXsdLocalName("comment");
        commentProp.setXsd(true);
        commentProp.setContainingType(POtype);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        //orderDateProp.setAttribute(true);
        orderDateProp.setType(yearMonthDayType);
        //orderDateProp.setContainment(false);
        //orderDateProp.setContainment(true);
        orderDateProp.setXsdLocalName("orderDate");
        orderDateProp.setXsd(true);
        orderDateProp.setContainingType(POtype);

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(itemProp);
        POtype.getDeclaredProperties().add(poIDProp);
        POtype.getDeclaredProperties().add(customerProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(orderDateProp);

        /****CUSTOMER TYPE*****/
        SDOProperty custNameProp = new SDOProperty(aHelperContext);

        //custNameProp.setAttribute(false);
        //custNameProp.setElement(true);
        custNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        custNameProp.setName("custName");
        custNameProp.setXsdLocalName("custName");
        custNameProp.setXsd(true);
        custNameProp.setContainingType(custType);
        custNameProp.setContainment(true);
        custNameProp.setType(stringType);
        custType.addDeclaredProperty(custNameProp);

        SDOProperty custIDProp = new SDOProperty(aHelperContext);

        //custIDProp.setAttribute(true);
        //custIDProp.setElement(false);
        //custIDProp.setContainment(true);
        custIDProp.setName("custID");
        custIDProp.setXsdLocalName("custID");
        custIDProp.setXsd(true);
        custIDProp.setContainingType(custType);
        custIDProp.setType(stringType);
        custType.addDeclaredProperty(custIDProp);

        SDOProperty poProp = new SDOProperty(aHelperContext);

        //poProp.setAttribute(false);
        //poProp.setElement(true);
        poProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        poProp.setName("purchaseOrder");
        poProp.setXsdLocalName("purchaseOrder");
        poProp.setXsd(true);
        poProp.setContainingType(custType);
        poProp.setType(POtype);
        // TODO: 20060906
        poProp.setOpposite(customerProp);
        customerProp.setOpposite(poProp);
        custType.addDeclaredProperty(poProp);
        types.add(itemType);
        types.add(companyType);
        types.add(custType);
        types.add(USaddrType);

        types.add(POtype);

        return types;
    }
    
    public void testWithError() throws Exception
    {
      String fileNameWithError = "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithIDREFNoTargetID.xsd";
      InputStream is = getSchemaInputStream(fileNameWithError);
      try{
        List types = xsdHelper.define(is, getSchemaLocation());
      }catch(SDOException sdoException)
      {
        assertTrue(sdoException.getErrorCode() == SDOException.NO_ID_SPECIFIED);        
        return;
      }
      fail("An SDOException should have occurred.");
    }
}
