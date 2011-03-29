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
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;

import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

/**
 *  @version $Header: DocPreservationTestProject.java 02-nov-2006.10:57:11 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class BasicBinderTestsProject extends Project
{
    NamespaceResolver resolver;
	public BasicBinderTestsProject() 
	{
        resolver = new NamespaceResolver();
        resolver.put("ns", "http://www.myns.com");
        
		buildEmployeeDescriptor();
		buildAddressDescriptor();
		buildCanadianAddressDescriptor();
		buildUSAddressDescriptor();
		buildPhoneNumberDescriptor();
	}
	
	public void buildEmployeeDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setDefaultRootElement("ns:employee");
		descriptor.setShouldPreserveDocument(true);
		descriptor.setJavaClass(Employee.class);
        descriptor.setNamespaceResolver(resolver);
        
        descriptor.addDirectMapping("id","@id");
		descriptor.addDirectMapping("firstName","getFirstName","setFirstName","first-name/text()");

		XMLDirectMapping lname = new XMLDirectMapping();
		lname.setAttributeName("lastName");
		lname.setGetMethodName("getLastName");
		lname.setSetMethodName("setLastName");
		lname.setXPath("last-name/text()");
		descriptor.addMapping(lname);
		
		XMLCompositeObjectMapping addr = new XMLCompositeObjectMapping();
		addr.setAttributeName("address");
		addr.setGetMethodName("getAddress");
		addr.setSetMethodName("setAddress");
		addr.setXPath("ns:address");
		addr.setReferenceClass(Address.class);
		descriptor.addMapping(addr);
		
		XMLCompositeDirectCollectionMapping resp = new XMLCompositeDirectCollectionMapping();
		resp.setAttributeName("responsibilities");
		resp.setGetMethodName("getResponsibilities");
		resp.setSetMethodName("setResponsibilities");
		resp.setXPath("responsibility");
		resp.getContainerPolicy().setContainerClass(Vector.class);
		descriptor.addMapping(resp);
		
		XMLCompositeCollectionMapping phone = new XMLCompositeCollectionMapping();
		phone.setAttributeName("phoneNumbers");
		phone.setGetMethodName("getPhoneNumbers");
		phone.setSetMethodName("setPhoneNumbers");
		phone.setXPath("phones/ns:phone");
		phone.setReferenceClass(PhoneNumber.class);
		phone.getContainerPolicy().setContainerClass(Vector.class);
		descriptor.addMapping(phone);
		
		this.addDescriptor(descriptor);		
	}
	public void buildAddressDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setShouldPreserveDocument(true);
		descriptor.setJavaClass(Address.class);
        descriptor.setNamespaceResolver(resolver);
		
		descriptor.getInheritancePolicy().setClassIndicatorField(new org.eclipse.persistence.oxm.XMLField("@type"));
		descriptor.getInheritancePolicy().addClassIndicator(Address.class, "address");
		descriptor.getInheritancePolicy().addClassIndicator(CanadianAddress.class, "canadian-address");
		descriptor.getInheritancePolicy().addClassIndicator(USAddress.class, "us-address");
		
		XMLDirectMapping street = new XMLDirectMapping();
		street.setAttributeName("street");
		street.setGetMethodName("getStreet");
		street.setSetMethodName("setStreet");
		street.setXPath("street/text()");
		descriptor.addMapping(street);
		
		XMLDirectMapping city = new XMLDirectMapping();
		city.setAttributeName("city");
		city.setGetMethodName("getCity");
		city.setSetMethodName("setCity");
		city.setXPath("city/text()");
		descriptor.addMapping(city);
		
		this.addDescriptor(descriptor);
	}
	public void buildCanadianAddressDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setShouldPreserveDocument(true);
		descriptor.setJavaClass(CanadianAddress.class);
        descriptor.setNamespaceResolver(resolver);
		
		descriptor.getInheritancePolicy().setParentClass(Address.class);
		
		XMLDirectMapping province = new XMLDirectMapping();
		province.setAttributeName("province");
		province.setGetMethodName("getProvince");
		province.setSetMethodName("setProvince");
		province.setXPath("province/text()");
		descriptor.addMapping(province);
		
		XMLDirectMapping postalCode = new XMLDirectMapping();
		postalCode.setAttributeName("postalCode");
		postalCode.setGetMethodName("getPostalCode");
		postalCode.setSetMethodName("setPostalCode");
		postalCode.setXPath("postal-code/text()");
		descriptor.addMapping(postalCode);
		
		this.addDescriptor(descriptor);
	}
	public void buildUSAddressDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setShouldPreserveDocument(true);
		descriptor.setJavaClass(USAddress.class);
        descriptor.setNamespaceResolver(resolver);
		
		descriptor.getInheritancePolicy().setParentClass(Address.class);
		
		XMLDirectMapping state = new XMLDirectMapping();
		state.setAttributeName("state");
		state.setGetMethodName("getState");
		state.setSetMethodName("setState");
		state.setXPath("state/text()");
		descriptor.addMapping(state);
		
		XMLDirectMapping zip = new XMLDirectMapping();
		zip.setAttributeName("zipCode");
		zip.setGetMethodName("getZipCode");
		zip.setSetMethodName("setZipCode");
		zip.setXPath("zip-code/text()");
		descriptor.addMapping(zip);
		
		this.addDescriptor(descriptor);
	}
	public void buildPhoneNumberDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setShouldPreserveDocument(true);
		descriptor.setJavaClass(PhoneNumber.class);
        descriptor.setNamespaceResolver(resolver);
		
		XMLDirectMapping areaCode = new XMLDirectMapping();
		areaCode.setAttributeName("areaCode");
		areaCode.setGetMethodName("getAreaCode");
		areaCode.setSetMethodName("setAreaCode");
		areaCode.setXPath("area-code/text()");
		descriptor.addMapping(areaCode);

		XMLDirectMapping exchange = new XMLDirectMapping();
		exchange.setAttributeName("exchange");
		exchange.setGetMethodName("getExchange");
		exchange.setSetMethodName("setExchange");
		exchange.setXPath("exchange/text()");
		descriptor.addMapping(exchange);

		XMLDirectMapping number = new XMLDirectMapping();
		number.setAttributeName("number");
		number.setGetMethodName("getNumber");
		number.setSetMethodName("setNumber");
		number.setXPath("number/text()");
		descriptor.addMapping(number);
		
		this.addDescriptor(descriptor);
	}
}
