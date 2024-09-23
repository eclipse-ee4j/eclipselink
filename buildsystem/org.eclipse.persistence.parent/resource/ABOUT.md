About EclipseLink 2.7.16

# EclipseLink 2.7.16

Target Date - TBD 2024

## About

The [EclipseLink](http://www.eclipse.org/eclipselink) project's goal is to provide a complete persistence framework that is both comprehensive and universal. It will run in any Java environment and read and write objects to virtually any type of data source, including relational databases, and XML. EclipseLink will focus on providing leading edge support, including advanced feature extensions, for the dominant persistence standards for each target data source; Java Persistence API (JPA) for relational databases, Java API for XML Binding (JAXB) for XML, Service Data Objects (SDO), and Database Web services (DBWS).

For tips on getting started with EclipseLink, please see the following resources:

*   [EclipseLink 2.7.16 Release Notes](http://www.eclipse.org/eclipselink/releases/2.7.php)
*   [Documentation](http://www.eclipse.org/eclipselink/documentation)
*   [Examples and How To](http://wiki.eclipse.org/EclipseLink/Examples)

### License

The Eclipse Foundation makes available all content in this plug-in ("Content"). Unless otherwise indicated below, the Content is provided to you under the terms and conditions of the Eclipse Public License Version 2.0 ("EPL"). A copy of the EPL is available at [http://www.eclipse.org/legal/epl-2.0](http://www.eclipse.org/legal/epl-2.0). For purposes of the EPL, "Program" will mean the Content.

If you did not receive this Content directly from the Eclipse Foundation, the Content is being redistributed by another party ("Redistributor") and different terms and conditions may apply to your use of any object code in the Content. Check the Redistributor's license that was provided with the Content. If no such license exists, contact the Redistributor. Unless otherwise indicated below, the terms and conditions of the EPL still apply to any source code in the Content and such source code may be obtained at [http://www.eclipse.org](http://www.eclipse.org/).

### Third Party Content

The Content includes items that have been sourced from third parties as set out below. If you did not receive this Content directly from the Eclipse Foundation, the following is provided for informational purposes only, and you should look to the Redistributor's license for terms and conditions of use.

*   Foundation Dependencies
    *   [ASM](#asm-v970)
    *   [Java API for JSON Processing - Reference Implementation](#json-processing-v116)
*   EclipseLink JPA
    *   [ANTLR](#antlr-v353)
    *   [Java Persistence API (JPA) 2.2](#java-persistence-jpa-22)
    *   [Java API for JSON Processing - Reference Implementation](#json-processing-v116)
*   EclipseLink MOXy
    *   [Java Architecture for XML Binding (JAXB)](#java-architecture-for-xml-binding-jaxb-v232)
    *   [Java Mail](#java-mail-v164)
    *   [Java API for JSON Processing - Reference Implementation](#json-processing-v116)
*   EclipseLink SDO
    *   [Service Data Objects (SDO)](#service-data-objects-sdo-v211)
*   EclipseLink DBWS
    *   [Java Servlet 2.4 (or higher)](#java-servlet-40)
*   Utilities
    *   [Java Connector](#java-connector-v17)
    *   [WSDL4J 1.6.2](#wsdl4j-v163)

* * *

#### ASM v9.7.0

> The EclipseLink Project includes ASM for the purpose of byte code weaving. The AMS library is re-packaged within the source of the project (org.persistence.eclipse.internal.libraries.asm.\*) to avoid version collisions with other usage of ASM.
>
> The source code is available within the project's git repository. The binaries are distributed within the eclipselink.jar and in the org.eclipse.persistence.asm\_\*.jar bundle.
>
> [https://asm.ow2.io/license.html](https://asm.ow2.io/license.html)
>
> Copyright (c) 2000-2011 INRIA, France Telecom, All rights reserved.
>
> Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
>
> 1.  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
> 2.  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
> 3.  Neither the name of the copyright holders nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
>
> THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#### ANTLR v3.5.3

> The [ANTLR library](https://www.antlr3.org/) ([license](https://www.antlr3.org/license.html)) is included within EclipseLink Project to enable parsing of the Java Persistence Query language (JP QL). The ANTLR library is re-packaged within the project in the org.eclipse.persistence.internal.libraries.antlr.\* packages.
>
> The source is available with the project's git repository. The binaries are distributed within the eclipselink.jar and in the org.eclipse.persistence.antlr\_3.5.3.v\*.jar bundle.

#### Java Architecture for XML Binding (JAXB) v2.3.2

> The [JAXB libraries](https://github.com/eclipse-ee4j/jaxb-api) are distributed under [Eclipse Distribution License (EDL) v1.0.](https://www.eclipse.org/org/documents/edl-v10.php) and [Eclipse Public License (EPL) v2.0](https://www.eclipse.org/legal/epl-2.0/) and [GNU Classpath](https://www.gnu.org/software/classpath/license.html).
>
> These components are used within the EclipseLink Project to enable the MOXy component's implementation of JAXB.
>
> JAXB Libraries:
>
> *   jakarta.xml.bind-api.jar
> *   jaxb-impl.jar
> *   jaxb-xjc.jar

#### Java Persistence (JPA) 2.2

> The JPA 2.2 specification API is included in EclipseLink under [Eclipse Distribution License (EDL) v1.0.](https://www.eclipse.org/org/documents/edl-v10.php) and [Eclipse Public License (EPL) v2.0](https://www.eclipse.org/legal/epl-2.0/)
>
> EclipseLink is the Java Persistence (JPA) 2.2 Reference Implementation ([JSR 338](http://jcp.org/en/jsr/detail?id=338)).

#### JSON Processing v1.1.6

> The [JSON Processing](https://github.com/eclipse-ee4j/jsonp) is distributed under and [Eclipse Public License (EPL) v2.0](https://www.eclipse.org/legal/epl-2.0/) and [GNU Classpath](https://www.gnu.org/software/classpath/license.html).
>
> This is used within EclipseLink Project to enable parsing of JSON files.

#### Java Mail v1.6.4

> The Java Mail library (mail.jar) can be downloaded from [eclipse-ee4j/mail](https://github.com/eclipse-ee4j/mail/releases) under [Eclipse Public License (EPL) v2.0](http://www.eclipse.org/legal/epl-2.0) and [GNU Classpath](https://www.gnu.org/software/classpath/license.html)
>
> This jar is included within the EclipseLink Project distribution to support Web Services attachment handling in the MOXy component. It is only required when using Java SE 5 (Java Mail is included in Java SE 6).

#### Service Data Objects (SDO) v2.1.1

> The [Service Data Objects (SDO) API](http://jcp.org/aboutJava/communityprocess/pr/jsr235/index.html) is distributed under a [CDDLv1.0](http://glassfish.dev.java.net/public/CDDLv1.0.html) and [custom license](http://jcp.org/aboutJava/communityprocess/pr/jsr235/index.html). It provides the standard API implemented by the EclipseLink Project's SDO component.

#### Java Connector v1.7

> The JCA 1.7 API is distributed under a [Eclipse Public License (EPL) v2.0](http://www.eclipse.org/legal/epl-2.0) and [GNU Classpath](https://www.gnu.org/software/classpath/license.html)
>
> This jar is being shipped and required by the Workbench only. When using EclipseLink in a container where JCA integration is required that container will provide the necessary API libraries.

#### Java Servlet 4.0

> The [Java Servlet 4.0 specification](https://jcp.org/aboutJava/communityprocess/final/jsr369/index.html) is distributed under the [Eclipse Public License (EPL) v2.0](http://www.eclipse.org/legal/epl-2.0) and [GNU Classpath](https://www.gnu.org/software/classpath/license.html)
>
> This jar is only required at design time for the DBWS Builder utility.

#### WSDL4j v1.6.3

> WSDL4J 1.6.3 is available for download from the [wsdl4j project](http://sourceforge.net/projects/wsdl4j). It distributed under [CPLv1.0](http://opensource.org/licenses/cpl1.0.php) .
>
> This jar is only required at design time for the DBWS Builder utility.