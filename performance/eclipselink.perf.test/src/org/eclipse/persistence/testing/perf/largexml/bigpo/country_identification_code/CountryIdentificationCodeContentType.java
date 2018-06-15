/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.country_identification_code;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CountryIdentificationCodeContentType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CountryIdentificationCodeContentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="AD"/>
 *     &lt;enumeration value="AE"/>
 *     &lt;enumeration value="AF"/>
 *     &lt;enumeration value="AG"/>
 *     &lt;enumeration value="AI"/>
 *     &lt;enumeration value="AL"/>
 *     &lt;enumeration value="AM"/>
 *     &lt;enumeration value="AN"/>
 *     &lt;enumeration value="AO"/>
 *     &lt;enumeration value="AQ"/>
 *     &lt;enumeration value="AR"/>
 *     &lt;enumeration value="AS"/>
 *     &lt;enumeration value="AT"/>
 *     &lt;enumeration value="AU"/>
 *     &lt;enumeration value="AW"/>
 *     &lt;enumeration value="AZ"/>
 *     &lt;enumeration value="BA"/>
 *     &lt;enumeration value="BB"/>
 *     &lt;enumeration value="BD"/>
 *     &lt;enumeration value="BE"/>
 *     &lt;enumeration value="BF"/>
 *     &lt;enumeration value="BG"/>
 *     &lt;enumeration value="BH"/>
 *     &lt;enumeration value="BI"/>
 *     &lt;enumeration value="BJ"/>
 *     &lt;enumeration value="BM"/>
 *     &lt;enumeration value="BN"/>
 *     &lt;enumeration value="BO"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="BS"/>
 *     &lt;enumeration value="BT"/>
 *     &lt;enumeration value="BV"/>
 *     &lt;enumeration value="BW"/>
 *     &lt;enumeration value="BY"/>
 *     &lt;enumeration value="BZ"/>
 *     &lt;enumeration value="CA"/>
 *     &lt;enumeration value="CC"/>
 *     &lt;enumeration value="CD"/>
 *     &lt;enumeration value="CF"/>
 *     &lt;enumeration value="CG"/>
 *     &lt;enumeration value="CH"/>
 *     &lt;enumeration value="CI"/>
 *     &lt;enumeration value="CK"/>
 *     &lt;enumeration value="CL"/>
 *     &lt;enumeration value="CM"/>
 *     &lt;enumeration value="CN"/>
 *     &lt;enumeration value="CO"/>
 *     &lt;enumeration value="CR"/>
 *     &lt;enumeration value="CU"/>
 *     &lt;enumeration value="CV"/>
 *     &lt;enumeration value="CX"/>
 *     &lt;enumeration value="CY"/>
 *     &lt;enumeration value="CZ"/>
 *     &lt;enumeration value="DE"/>
 *     &lt;enumeration value="DJ"/>
 *     &lt;enumeration value="DK"/>
 *     &lt;enumeration value="DM"/>
 *     &lt;enumeration value="DO"/>
 *     &lt;enumeration value="DZ"/>
 *     &lt;enumeration value="EC"/>
 *     &lt;enumeration value="EE"/>
 *     &lt;enumeration value="EG"/>
 *     &lt;enumeration value="EH"/>
 *     &lt;enumeration value="ER"/>
 *     &lt;enumeration value="ES"/>
 *     &lt;enumeration value="ET"/>
 *     &lt;enumeration value="FI"/>
 *     &lt;enumeration value="FJ"/>
 *     &lt;enumeration value="FK"/>
 *     &lt;enumeration value="FM"/>
 *     &lt;enumeration value="FO"/>
 *     &lt;enumeration value="FR"/>
 *     &lt;enumeration value="GA"/>
 *     &lt;enumeration value="GB"/>
 *     &lt;enumeration value="GD"/>
 *     &lt;enumeration value="GE"/>
 *     &lt;enumeration value="GF"/>
 *     &lt;enumeration value="GH"/>
 *     &lt;enumeration value="GI"/>
 *     &lt;enumeration value="GL"/>
 *     &lt;enumeration value="GM"/>
 *     &lt;enumeration value="GN"/>
 *     &lt;enumeration value="GP"/>
 *     &lt;enumeration value="GQ"/>
 *     &lt;enumeration value="GR"/>
 *     &lt;enumeration value="GS"/>
 *     &lt;enumeration value="GT"/>
 *     &lt;enumeration value="GU"/>
 *     &lt;enumeration value="GW"/>
 *     &lt;enumeration value="GY"/>
 *     &lt;enumeration value="HK"/>
 *     &lt;enumeration value="HM"/>
 *     &lt;enumeration value="HN"/>
 *     &lt;enumeration value="HR"/>
 *     &lt;enumeration value="HT"/>
 *     &lt;enumeration value="HU"/>
 *     &lt;enumeration value="ID"/>
 *     &lt;enumeration value="IE"/>
 *     &lt;enumeration value="IL"/>
 *     &lt;enumeration value="IN"/>
 *     &lt;enumeration value="IO"/>
 *     &lt;enumeration value="IQ"/>
 *     &lt;enumeration value="IR"/>
 *     &lt;enumeration value="IS"/>
 *     &lt;enumeration value="IT"/>
 *     &lt;enumeration value="JM"/>
 *     &lt;enumeration value="JO"/>
 *     &lt;enumeration value="JP"/>
 *     &lt;enumeration value="KE"/>
 *     &lt;enumeration value="KG"/>
 *     &lt;enumeration value="KH"/>
 *     &lt;enumeration value="KI"/>
 *     &lt;enumeration value="KM"/>
 *     &lt;enumeration value="KN"/>
 *     &lt;enumeration value="KP"/>
 *     &lt;enumeration value="KR"/>
 *     &lt;enumeration value="KW"/>
 *     &lt;enumeration value="KY"/>
 *     &lt;enumeration value="KZ"/>
 *     &lt;enumeration value="LA"/>
 *     &lt;enumeration value="LB"/>
 *     &lt;enumeration value="LC"/>
 *     &lt;enumeration value="LI"/>
 *     &lt;enumeration value="LK"/>
 *     &lt;enumeration value="LR"/>
 *     &lt;enumeration value="LS"/>
 *     &lt;enumeration value="LT"/>
 *     &lt;enumeration value="LU"/>
 *     &lt;enumeration value="LV"/>
 *     &lt;enumeration value="LY"/>
 *     &lt;enumeration value="MA"/>
 *     &lt;enumeration value="MC"/>
 *     &lt;enumeration value="MD"/>
 *     &lt;enumeration value="MG"/>
 *     &lt;enumeration value="MH"/>
 *     &lt;enumeration value="MK"/>
 *     &lt;enumeration value="ML"/>
 *     &lt;enumeration value="MM"/>
 *     &lt;enumeration value="MN"/>
 *     &lt;enumeration value="MO"/>
 *     &lt;enumeration value="MP"/>
 *     &lt;enumeration value="MQ"/>
 *     &lt;enumeration value="MR"/>
 *     &lt;enumeration value="MS"/>
 *     &lt;enumeration value="MT"/>
 *     &lt;enumeration value="MU"/>
 *     &lt;enumeration value="MV"/>
 *     &lt;enumeration value="MW"/>
 *     &lt;enumeration value="MX"/>
 *     &lt;enumeration value="MY"/>
 *     &lt;enumeration value="MZ"/>
 *     &lt;enumeration value="NA"/>
 *     &lt;enumeration value="NC"/>
 *     &lt;enumeration value="NE"/>
 *     &lt;enumeration value="NF"/>
 *     &lt;enumeration value="NG"/>
 *     &lt;enumeration value="NI"/>
 *     &lt;enumeration value="NL"/>
 *     &lt;enumeration value="NO"/>
 *     &lt;enumeration value="NP"/>
 *     &lt;enumeration value="NR"/>
 *     &lt;enumeration value="NU"/>
 *     &lt;enumeration value="NZ"/>
 *     &lt;enumeration value="OM"/>
 *     &lt;enumeration value="PA"/>
 *     &lt;enumeration value="PE"/>
 *     &lt;enumeration value="PF"/>
 *     &lt;enumeration value="PG"/>
 *     &lt;enumeration value="PH"/>
 *     &lt;enumeration value="PK"/>
 *     &lt;enumeration value="PL"/>
 *     &lt;enumeration value="PM"/>
 *     &lt;enumeration value="PN"/>
 *     &lt;enumeration value="PR"/>
 *     &lt;enumeration value="PS"/>
 *     &lt;enumeration value="PT"/>
 *     &lt;enumeration value="PW"/>
 *     &lt;enumeration value="PY"/>
 *     &lt;enumeration value="QA"/>
 *     &lt;enumeration value="RE"/>
 *     &lt;enumeration value="RO"/>
 *     &lt;enumeration value="RU"/>
 *     &lt;enumeration value="RW"/>
 *     &lt;enumeration value="SA"/>
 *     &lt;enumeration value="SB"/>
 *     &lt;enumeration value="SC"/>
 *     &lt;enumeration value="SD"/>
 *     &lt;enumeration value="SE"/>
 *     &lt;enumeration value="SG"/>
 *     &lt;enumeration value="SH"/>
 *     &lt;enumeration value="SI"/>
 *     &lt;enumeration value="SJ"/>
 *     &lt;enumeration value="SK"/>
 *     &lt;enumeration value="SL"/>
 *     &lt;enumeration value="SM"/>
 *     &lt;enumeration value="SN"/>
 *     &lt;enumeration value="SO"/>
 *     &lt;enumeration value="SR"/>
 *     &lt;enumeration value="ST"/>
 *     &lt;enumeration value="SV"/>
 *     &lt;enumeration value="SY"/>
 *     &lt;enumeration value="SZ"/>
 *     &lt;enumeration value="TC"/>
 *     &lt;enumeration value="TD"/>
 *     &lt;enumeration value="TF"/>
 *     &lt;enumeration value="TG"/>
 *     &lt;enumeration value="TH"/>
 *     &lt;enumeration value="TJ"/>
 *     &lt;enumeration value="TK"/>
 *     &lt;enumeration value="TL"/>
 *     &lt;enumeration value="TM"/>
 *     &lt;enumeration value="TN"/>
 *     &lt;enumeration value="TO"/>
 *     &lt;enumeration value="TR"/>
 *     &lt;enumeration value="TT"/>
 *     &lt;enumeration value="TV"/>
 *     &lt;enumeration value="TW"/>
 *     &lt;enumeration value="TZ"/>
 *     &lt;enumeration value="UA"/>
 *     &lt;enumeration value="UG"/>
 *     &lt;enumeration value="UM"/>
 *     &lt;enumeration value="US"/>
 *     &lt;enumeration value="UY"/>
 *     &lt;enumeration value="UZ"/>
 *     &lt;enumeration value="VA"/>
 *     &lt;enumeration value="VC"/>
 *     &lt;enumeration value="VE"/>
 *     &lt;enumeration value="VG"/>
 *     &lt;enumeration value="VI"/>
 *     &lt;enumeration value="VN"/>
 *     &lt;enumeration value="VU"/>
 *     &lt;enumeration value="WF"/>
 *     &lt;enumeration value="WS"/>
 *     &lt;enumeration value="YE"/>
 *     &lt;enumeration value="YT"/>
 *     &lt;enumeration value="YU"/>
 *     &lt;enumeration value="ZA"/>
 *     &lt;enumeration value="ZM"/>
 *     &lt;enumeration value="ZW"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "CountryIdentificationCodeContentType")
@XmlEnum
public enum CountryIdentificationCodeContentType {


    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ANDORRA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UNITED ARAB EMIRATES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;AFGHANISTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ANTIGUA AND BARBUDA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ANGUILLA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ALBANIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ARMENIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NETHERLANDS ANTILLES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ANGOLA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ANTARCTICA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AQ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ARGENTINA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;AMERICAN SAMOA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;AUSTRIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;AUSTRALIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ARUBA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;AZERBAIJAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    AZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BOSNIA AND HERZEGOVINA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BARBADOS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BB,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BANGLADESH&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BELGIUM&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BURKINA FASO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BULGARIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BAHRAIN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BURUNDI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BENIN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BJ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BERMUDA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BRUNEI DARUSSALAM&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BOLIVIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BRAZIL&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BAHAMAS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BHUTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BOUVET ISLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BOTSWANA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BELARUS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BELIZE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    BZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CANADA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COCOS (KEELING) ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CONGO, THE DEMOCRATIC REPUBLIC OF THE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CENTRAL AFRICAN REPUBLIC&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CONGO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SWITZERLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COTE D'IVOIRE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COOK ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CHILE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CAMEROON&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CHINA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COLOMBIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COSTA RICA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CUBA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CAPE VERDE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CHRISTMAS ISLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CX,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CYPRUS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CZECH REPUBLIC&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    CZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GERMANY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;DJIBOUTI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DJ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;DENMARK&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;DOMINICA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;DOMINICAN REPUBLIC&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ALGERIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    DZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ECUADOR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    EC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ESTONIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    EE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;EGYPT&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    EG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;WESTERN SAHARA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    EH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ERITREA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ER,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SPAIN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ES,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ETHIOPIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ET,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FINLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FIJI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FJ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FALKLAND ISLANDS (MALVINAS)&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MICRONESIA, FEDERATED STATES OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FAROE ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FRANCE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    FR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GABON&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UNITED KINGDOM&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GB,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GRENADA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GEORGIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FRENCH GUIANA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GHANA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GIBRALTAR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GREENLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GAMBIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUINEA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUADELOUPE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GP,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;EQUATORIAL GUINEA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GQ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GREECE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUATEMALA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUAM&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUINEA-BISSAU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;GUYANA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    GY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HONG KONG&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HEARD ISLAND AND MCDONALD ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HONDURAS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CROATIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HAITI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HUNGARY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    HU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;INDONESIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ID,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;IRELAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ISRAEL&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;INDIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;BRITISH INDIAN OCEAN TERRITORY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;IRAQ&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IQ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;IRAN, ISLAMIC REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ICELAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ITALY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    IT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;JAMAICA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    JM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;JORDAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    JO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;JAPAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    JP,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KENYA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KYRGYZSTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CAMBODIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KIRIBATI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;COMOROS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAINT KITTS AND NEVIS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KP,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KOREA, REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KUWAIT&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CAYMAN ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;KAZAKHSTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    KZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LAO PEOPLE'S DEMOCRATIC REPUBLIC&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LEBANON&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LB,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAINT LUCIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LIECHTENSTEIN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SRI LANKA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LIBERIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LESOTHO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LITHUANIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LUXEMBOURG&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LATVIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;LIBYAN ARAB JAMAHIRIYA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    LY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MOROCCO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MONACO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MOLDOVA, REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MADAGASCAR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MARSHALL ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MALI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ML,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MYANMAR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MONGOLIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MACAO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NORTHERN MARIANA ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MP,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MARTINIQUE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MQ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MAURITANIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MONTSERRAT&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MALTA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MAURITIUS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MALDIVES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MALAWI&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MEXICO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MX,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MALAYSIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MOZAMBIQUE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    MZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NAMIBIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NEW CALEDONIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NIGER&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NORFOLK ISLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NIGERIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NICARAGUA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NETHERLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NORWAY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NEPAL&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NP,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NAURU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NIUE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;NEW ZEALAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    NZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;OMAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    OM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PANAMA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PERU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FRENCH POLYNESIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PAPUA NEW GUINEA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PHILIPPINES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PAKISTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;POLAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAINT PIERRE AND MIQUELON&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PITCAIRN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PUERTO RICO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PALESTINIAN TERRITORY, OCCUPIED&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PORTUGAL&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PALAU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;PARAGUAY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    PY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;QATAR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    QA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;REUNION&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    RE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ROMANIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    RO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;RUSSIAN FEDERATION&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    RU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;RWANDA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    RW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAUDI ARABIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SOLOMON ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SB,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SEYCHELLES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SUDAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SWEDEN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SINGAPORE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAINT HELENA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SLOVENIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SVALBARD AND JAN MAYEN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SJ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SLOVAKIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SIERRA LEONE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAN MARINO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SENEGAL&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SOMALIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SURINAME&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAO TOME AND PRINCIPE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ST,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;EL SALVADOR&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SYRIAN ARAB REPUBLIC&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SWAZILAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    SZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TURKS AND CAICOS ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;CHAD&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TD,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;FRENCH SOUTHERN TERRITORIES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TOGO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;THAILAND&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TH,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TAJIKISTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TJ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TOKELAU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TK,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TIMOR-LESTE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TL,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TURKMENISTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TUNISIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TONGA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TO,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TURKEY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TR,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TRINIDAD AND TOBAGO&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TUVALU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TV,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TAIWAN, PROVINCE OF CHINA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TW,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;TANZANIA, UNITED REPUBLIC OF&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    TZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UKRAINE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    UA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UGANDA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    UG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UNITED STATES MINOR OUTLYING ISLANDS&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    UM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UNITED STATES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    US,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;URUGUAY&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    UY,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UZBEKISTAN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    UZ,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;HOLY SEE (VATICAN CITY STATE)&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAINT VINCENT AND THE GRENADINES&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VC,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;VENEZUELA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;VIRGIN ISLANDS, BRITISH&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VG,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;VIRGIN ISLANDS, U.S.&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VI,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;VIET NAM&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VN,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;VANUATU&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    VU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;WALLIS AND FUTUNA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    WF,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SAMOA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    WS,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;YEMEN&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    YE,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;MAYOTTE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    YT,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;YUGOSLAVIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    YU,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;SOUTH AFRICA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ZA,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ZAMBIA&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ZM,

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;ZIMBABWE&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    ZW;

    public String value() {
        return name();
    }

    public static CountryIdentificationCodeContentType fromValue(String v) {
        return valueOf(v);
    }

}
