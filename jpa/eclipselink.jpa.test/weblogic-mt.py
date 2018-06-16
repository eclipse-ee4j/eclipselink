#
# Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# SPDX-License-Identifier: EPL-2.0
#

import sys
import re
import os
from time import sleep
from sets import Set

# WebLogic Configuration Utility
# ------------------------------
# Allows to execute various WebLogic configuration tasks from command line.

# Print help message to stdout.
def help_msg():
    print ''
    print 'Usage:'
    print ''
    print '    weblogic-mt.py [<global_arguments>] <command> [<command_arguments>]'
    print '                   { <command> [<command_arguments>] }'
    print ''
    print 'Offline commands:'
    print ''
    print '    create-domain --wls-home=<wls_home> --domain=<domain_path> [--name=<server_name>]'
    print '                  [--template=<template_path>]  [--profile-type=<profile_type>]'
    print '                  [--host=<host>] [--port=<port>] [--ssl-port=<ssl_port>]'
    print '                  [--admin-user=<admin_user>] [--admin-password=<admin_password>]'
    print '        wls_home       :: WebLogic server installation directory'
    print '        domain_path    :: Full path of domain to be created'
    print '        server_name    :: WebLogic server name (default AdminServer)'
    print '        template_path  :: Domain template path relative to <wls_home>'
    print '                          (default oracle_common/common/templates/wls/wls.jar)'
    print '        profile_type   :: Domain profile type (default Compact)'
    print '        host           :: WebLogic server host (default 127.0.0.1)'
    print '        port           :: WebLogic server non encrypted port (default 7001)'
    print '        SSL port       :: WebLogic server SSL port (default 7002)'
    print '        admin_user     :: Admin user name to be set (default weblogic)'
    print '        admin_password :: Admin password to be set (default welcome1)'
    print ''
    print '    add-template --domain=<domain_path> --template=<template_path>'
    print '        domain_path    :: Full path of domain to be updated'
    print '        template_path  :: Template full path'
    print ''
    print '    start-domain --domain=<domain_path> [--host=<host>] [--port=<port>]'
    print '                 [--admin-user=<admin_user>] [--admin-password=<admin_password>]'
    print '                 [--jvm-options=<jvm_options>]'
    print '        domain_path    :: Full path of domain to be started'
    print '        host           :: WebLogic server host (default 127.0.0.1)'
    print '        port           :: WebLogic server port (default 7001)'
    print '        admin_user     :: Admin user name (default weblogic)'
    print '        admin_password :: Admin password (default welcome1)'
    print '        jvm_options    :: Command line options to be passed to Java VM executable'
    print '                          (default -XX:PermSize=128m'
    print '                          -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log)'
    print ''
    print '    start-partition --partition=<partition_name> [--host=<host>] [--port=<port>]'
    print '                 [--admin-user=<admin_user>] [--admin-password=<admin_password>]'
    print '                 [--jvm-options=<jvm_options>]'
    print '        partition_name :: Name of the partition to be started'
    print '        host           :: WebLogic server host (default 127.0.0.1)'
    print '        port           :: WebLogic server port (default 7001)'
    print '        admin_user     :: Admin user name (default weblogic)'
    print '        admin_password :: Admin password (default welcome1)'
    print '        jvm_options    :: Command line options to be passed to Java VM executable'
    print '                          (default -XX:PermSize=128m'
    print '                          -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log)'
#### Broken, do not publish
#    print ''
#    print '    start-script --domain=<domain_path> [--script=<start_script>]'
#    print '        domain_path    :: Full path of domain to be started'
#    print '        start_script   :: Script to use to start WebLogic. Script path is relative'
#    print '                          to domain path. (default startWebLogic.sh)'
    print ''
    print '    stop-domain  [--host=<host>] [--port=<port>]'
    print '                 [--admin-user=<admin_user>] [--admin-password=<admin_password>]'
    print '        host           :: WebLogic server host (default 127.0.0.1)'
    print '        port           :: WebLogic server port (default 7001)'
    print '        admin_user     :: Admin user name (default weblogic)'
    print '        admin_password :: Admin password (default welcome1)'
    print ''
    print '    create-partition-admin  --name=<partition_name> [--realm=<realm_name>]'
    print '                            --group=<group_name>'
    print '                            --user=<user_name> --password=<user_password>'
    print '        partition_name :: Name of partition'
    print '        realm_name     :: Name of realm (default partition name)'
    print '        group_name     :: Partition admin group name'
    print '        user_name      :: Partition admin user name'
    print '        user_password  :: Partition admin user password'
    print ''
    print 'Online commands:'
    print ''
    print '    Global options: --host=<host> --port=<port>'
    print '                    --admin-user=<admin_user> --admin-password=<admin_password>'
    print ''
    print '        host           :: WebLogic server host (default 127.0.0.1)'
    print '        port           :: WebLogic server port (default 7001)'
    print '        admin_user     :: Admin user name (default weblogic)'
    print '        admin_password :: Admin password (default welcome1)'
    print ''
    print '        Those global; options are used to connect to running WebLogic server.'
    print '        They may also be used for commands if not overwritten.'
    print ''
    print '    create-virtual-host --name=<virtual_host_name> [--targets=<virtual_host_targets>]'
    print '        virtual_host_name    :: Name of virtual host to be created'
    print '        virtual_host_targets :: Comma (with no spaces) separated list of virtual host targets'
    print '                                Each target is <name>\':\'<type> pair, e.g.'
    print '                                AdminServer:Server'
    print ''
    print '    create-virtual-target --name=<partition_name> --prefix=<target_prefix> [--host=<host>] [--port=<port>]'
    print '        partition_name :: Name of partition to be created'
    print '        target_prefix  :: Name of virtual target prefix'
    print '        host           :: WebLogic server host (default to use global option)'
    print '        port           :: WebLogic server port (command argument only,'
    print '                          default not to set any value)'
    print ''
    print '    create-resource-group-template --name=<template_name>'
    print '        template_name  :: Name of resource group template to be created'
    print ''
    print '    create-resource-group --name=<group_name> [--template=<template_name>]'
    print '                          [--targets=<virtual_host_targets>]'
    print '        group_name           :: Name of resource group to be created'
    print '        template_name        :: Name of resource group template to be set to this group'
    print '                                (no resource group template is set when this argument is not present)'
    print '        virtual_host_targets :: Comma (with no spaces) separated list of virtual host targets'
    print '                                Each target is <name>\':\'<type> pair, e.g. AdminServer:Server'
    print ''
    print '    create-realm --name=<realm_name>'
    print '        realm_name     :: Name of realm to be created'
    print ''
    print '    create-realm-providers --name=<realm_name>'
    print '        realm_name     :: Name of realm where providers will be created'
    print ''
    print '    create-partition --name=<partition_name>'
    print '                     --virtual-target=<virtual_target_name> | --virtual-host=<virtual_host_name>'
    print '                     --resource-group=<resource_group_name>'
    print '                     [--resource-group-template=<resource_group_template>]'
    print '                     [--realm=<realm_name>]'
    print '        partition_name          :: Name of partition'
    print '        virtual_target_name     :: Name of virtual target'
    print '        virtual_host_name       :: Name of virtual host'
    print '        resource_group_name     :: Name of resource group'
    print '        resource_group_template :: Name of resource group template'
    print '                                   (default to not set any template)'
    print '        realm_name              :: Name of realm (default to not set any realm)'
    print ''
    print '    set-identity-domain --name=<partition_name> [--realm=<realm_name>]'
    print '        partition_name :: Name of partition'
    print '        realm_name     :: Name of realm (default partition name)'
    print ''
    print '    set-identity-domain-on-admin-realm --idd-name=<identity_domain_name>'
    print '        dentity_domain_name :: Name of identity domain to set on admin realm'
    print ''
    print '    set-jpa-provider --provider=<jpa_provider>'
    print '        jpa_provider   :: JPA provider fully gualified class'
    print '                          (e.g. org.eclipse.persistence.jpa.PersistenceProvider)'
    print ''
    print '    create-jdbc-resource --name=<resource_name> [--jndi-name=<jndi_name>]'
    print '                         --url=<resource_url> --driver=<driver_name>'
    print '                         --user=<user_name> --password=<user_password>'
    print '                         [--transactions-protocol=<transactions_protocol>]'
    print '                         [--test-query=<test_query>]'
    print '                         [--resource-group-template=<resource_group_template>]'
    print '        resource_name           :: JDBC resource name'
    print '        jndi_name               :: JDBC resource JNDI name (default jdbc/<resource_name>)'
    print '        resource_url            :: Database access URL'
    print '        driver_name             :: Fully qualified JDBC driver class'
    print '        user_name               :: Database access user name'
    print '        user_password           :: Database access user password'
    print '        transactions_protocol   :: Transactions protocol (default not to set any value)'
    print '        test_query              :: Simple query to verify database access'
    print '                                   (default \'SQL SELECT 1 FROM DUAL\')'
    print '        resource_group_template :: Target Name of resource group template'
    print '                                   JDBC resource will be created in resource group template'
    print '                                   when this argument is set.'
    print ''
    print '    create-proxy-resource --name=<resource_name> [--jndi-name=<jndi_name>]'
    print '                          [--targets=<targets>]'
    print '        resource_name    :: Proxy resource name'
    print '        jndi_name        :: JDBC resource JNDI name (default jdbc/<resource_name>)'
    print '        targets          :: Comma (with no spaces) separated list of virtual host targets'
    print '                            Each target is <name>\':\'<type> pair, e.g. AdminServer:Server'
    print ''
    print '    create-resource-override --name=<override_name> --partition=<partition_name>'
    print '                             --resource=<resource_name>'
    print '                             [--url=<resource_url>] [--driver=<driver_name>]'
    print '                             [--user=<user_name>] [--password=<user_password>]'
    print '                             [--transactions-protocol=<transactions_protocol>]'
    print '        override_name         :: Resource override name'
    print '        partition_name        :: Name of partition'
    print '        resource_name         :: Overriden JDBC resource name'
    print '        resource_url          :: Database access URL'
    print '        driver_name           :: Fully qualified JDBC driver class'
    print '        user_name             :: Database access user name'
    print '        user_password         :: Database access user password'
    print '        transactions_protocol :: Transactions protocol (default not to set any value)'
    print ''
    print '    deploy --name=<application_name> --path=<application_path>'
    print '           [--targets=<target_servers>] [--template=<template_name>]'
    print '        application_name :: Name of the application or standalone J2EE module'
    print '        application_path :: Name of the application directory, archive file,'
    print '                            or root of the exploded archive directory'
    print '        target_servers   :: Comma-separated list of the target servers'
    print '                            (defaults to actually connected server)'
    print '        template_name    :: Name of target resource group template (dfaults to none)'
    print ''
    print '    undeploy --name=<application_name> [--targets=<target_servers>]'
    print '             [--template=<template_name>]'
    print '        application_name :: Name of the application or standalone J2EE module'
    print '        target_servers   :: Comma-separated list of the target servers'
    print '                            (defaults to actually connected server)'
    print '        template_name    :: Name of target resource group template (dfaults to none)'
    print ''
    print '    activate'
    print '        Activate changes after batch of online commands'
    print ''

# Process command line arguments and extract commands and their arguments from them.
# Command and arguments are stored in a dictionary:
# command[None] contains command
# command[<arg_name>] contains argument value for argument --<arg_name>
#     args Command line arguments.
#     returns An array of dictionaries with commands.
def parse_args(args):
    length = len(sys.argv)
    if  length < 2:
        help_msg()
        exit(1)
    i = 1;
    commands = []
    command = {}
    pattern = re.compile("([^=]+)(?:=(.*))?");
    while (i < length):
        str = sys.argv[i]
        strLen = len(str)
        pos = 0;
        isCommand = True
        # Skip one or two dashes at the beginning if present and distinguish command and argument
        if strLen > 0 and str[pos] == '-':
            pos += 1
            isCommand = False
        if strLen > 1 and str[pos] == '-':
            pos += 1
        # Process next command
        if isCommand:
            # Store previous command
            commands.append(command)
            command = {}
            command[None] = str
        # Process next command argument
        else:
            match = pattern.match(str, pos)
            count = match.lastindex;
            if (count > 0):
                if (count > 1):
                    command[match.group(1)] = match.group(2)
                else:
                    command[match.group(1)] = None
        i += 1
    if not len(command) == 0:
        commands.append(command)
    return commands

# Print command and its arguments to stdout
#     command Command and its arguments dictionary.
def print_command(command):
    print 'Executing command ' + command[None]
    for key in command.keys():
        if not key == None:
            value = command[key]
            if value == None:
                print '  --' + key
            else:
                print '  --' + key + '=' + command[key]

# Verify that command dictionary contains provided mandatory arguments names as keys.
# Will print missing arguments to stdout and terminate execution if any mandatory argument is missing.
#     command Command and its arguments dictionary.
#     optionKeys An array of mandatory argument names to verify
def check_options(command, optionKeys):
    passed = True
    for key in optionKeys:
        if not command.has_key(key):
            if command.has_key(None):
                print 'Missing ' + command[None] + ' command option --' + key
            else:
                print 'Missing global option --' + key
            passed = False
    if not passed:
        help_msg()
        exit(1)

# Verify that global or command dictionary contains provided mandatory arguments names as keys.
# Will print missing arguments to stdout and terminate execution if any mandatory argument is missing.
#     command Command and its arguments dictionary.
#     optionKeys An array of mandatory argument names to verify
def check_all_options(globals, command, optionKeys):
    passed = True
    for key in optionKeys:
        if not command.has_key(key) and not globals.has_key(key):
            if command.has_key(None):
                print 'Missing ' + command[None] + ' command option --' + key
            else:
                print 'Missing global option --' + key
            passed = False
    if not passed:
        help_msg()
        exit(1)

# Get argument value from global dictionary only.
# Return None when global dictionary does not contain argument value.
#     globals Global command arguments.
#     name    Argument name to search for.
#     returns Argument value found or None when no value was found in dictionary.
def get_global_option(globals, name):
    if globals.has_key(name):
        return globals[name]
    return None

# Get argument value from command dictionary only.
# Return None when command dictionary does not contain argument value.
#     command Command and its arguments dictionary.
#     name    Argument name to search for.
#     returns Argument value found or None when no value was found in dictionaries.
def get_command_option(command, name):
    if command.has_key(name):
        return command[name]
    return None

# Initialize argument value from global dictionary only.
# Use default value when dictionary does not contain argument value.
#     globals Global command arguments.
#     name    Argument name to search for.
#     default Default value when argument is not found in dictionary.
#     returns Initialized argument value.
def init_global_option(globals, name, default):
    if globals.has_key(name):
        return globals[name]
    return default

# Get argument value from command dictionary first or global dictionary second.
# Return None when neither of the two contains argument value.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
#     name    Argument name to search for.
#     returns Argument value found or None when no value was found in dictionaries.
def get_option(globals, command, name):
    if command.has_key(name):
        return command[name]
    if globals.has_key(name):
        return globals[name]
    return None

# Initialize argument value from command dictionary first or global dictionary second.
# Use default value when dictionaries do not contain argument value.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
#     name    Argument name to search for.
#     default Default value when argument is not found in dictionary.
#     returns Initialized argument value.
def init_option(globals, command, name, default):
    if command.has_key(name):
        return command[name]
    if globals.has_key(name):
        return globals[name]
    return default

# Build path from path elements using OS dependent path elements separator.
def build_path(*elements):
    path = None
    pathElements = [];
    for element in elements:
        if len(pathElements) > 0:
            pathElements.append(os.sep)
        pathElements.append(element)
    return ''.join(pathElements)

# Initialize template argument value. Template can be absolute path or path telative to wls-home if present.
#     globals      Global command arguments.
#     command      Command and its arguments dictionary.
#     wlHome       WebLogic server installation directory argument value.
#     templateName Template path argument name.
#     default      Default Template path argument value.
#     returns      Template path if template file exists or None otherwise.
def getTemplate(globals, command, wlHome, templateName, default):
    templateArg = init_option(globals, command, templateName, default)
    # Template argument itself points to existing readable file.
    if os.path.isfile(templateArg):
        return templateArg
    # Concatenate WLS home and template path.
    if wlHome != None:
        templateFile = wlHome + os.sep + templateArg
        if os.path.isfile(templateFile):
            return templateFile
    return None

# Initialize admin user argument value. Get it from admin-user command or global argument
# or use 'weblogic' as default value if argument was not found.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
#     returns Admin user argument value.
def get_admin_user(globals, command):
    return init_option(globals, command, 'admin-user', 'weblogic')

# Initialize admin password argument value. Get it from admin-password command or global argument
# or use 'welcome1' as default value if argument was not found.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
#     returns Admin password argument value.
def  get_admin_password(globals, command):
    return init_option(globals, command, 'admin-password', 'welcome1')

def get_host(globals, command):
    return init_option(globals, command, 'host', '127.0.0.1')

def get_port(globals, command):
    return init_option(globals, command, 'port', '7001')

def get_ssl_port(globals, command):
    return init_option(globals, command, 'ssl-port', '7002')

# Default domain template
DEFAULT_DOMAIN_TEMPLATE = build_path('common', 'templates', 'wls', 'wls.jar')

# Convert value to string to be passed as value in stdout message
#     value   Value to be converted
#     returns Converted value
def value_to_message(value):
    if value == None:
        return 'Not set'
    else:
        return value

# Get partition name as virtual target name (append '-vt' cuffix)
#     name    Partition name
#     returns Virtual target name
#def virtual_target_id(name):
#    return name + '-vt'

# Conmstructs identity domain id as <partition_name> + '-idd'
#     name    Partition name
#     returns Identity domain id
def identity_domain_id(name):
    return name + '-idd'

# Conmstructs resource group id as <partition_name> + '-rg'
#     name    Partition name
#     returns Resource group id
def resource_group_id(name):
    return name + '-rg'

# Conmstructs object id as <object_name> + '_' <realm_name>
#     name      Object name
#     realmName Realm name
#     returns   Object id
def provider_id(name, realmName):
    return name + '_' + realmName

# Execute create-domain command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_domain(globals, command):
    check_all_options(globals, command, ['wls-home', 'domain'])
    wlHome = get_option(globals, command, 'wls-home')
    domain = get_option(globals, command, 'domain')
    name = init_option(globals, command, 'name', 'AdminServer')
    template = getTemplate(globals, command, wlHome, 'template', DEFAULT_DOMAIN_TEMPLATE)
    profileType = init_option(globals, command, 'profile-type', 'Compact')
    host = get_host(globals, command)
    port = get_port(globals, command)
    sslPort = get_ssl_port(globals, command)
    adminUser = get_admin_user(globals, command)
    adminPassword = get_admin_password(globals, command)
    # Print command and options
    print 'Executing command create-domain'
    print '    WLS home:        ' + wlHome
    print '    Domain path:     ' + domain
    print '    Domain Template: ' + template
    print '    Server name:     ' + name
    print '    Profile type:    ' + profileType
    print '    Host:            ' + host
    print '    Port:            ' + port
    print '    SSL port:        ' + sslPort
    print '    Admin user:      ' + adminUser
    print '    Admin password:  ' + adminPassword
    # Process
    readTemplate(template, profileType)
    cd('/')
    cd('Servers/AdminServer')
    set('Name', name)
    set('ListenAddress', host)
    set('ListenPort', int(port))
    create('AdminServer','SSL')
    cd('SSL/AdminServer')
    set('Enabled', 'True')
    #set('ListenAddress', host)
    set('ListenPort', int(sslPort))
    cd('/')
    cd('Security/base_domain/User/weblogic')
    set('Name', adminUser)
    cmo.setPassword(adminPassword)
    writeDomain(domain)
    closeTemplate()
    dumpStack()

# Execute add-template command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def add_template(globals, command):
    check_all_options(globals, command, ['domain', 'template'])
    domain = get_option(globals, command, 'domain')
    wlHome = get_option(globals, command, 'wls-home')
    template = getTemplate(globals, command, wlHome, 'template', None)
    print 'Executing command add-template'
    print '    Domain path: ' + domain
    print '    Template:    ' + template
    readDomain(domain)
    addTemplate(template)
    updateDomain()
    closeDomain()
    dumpStack()

# Execute start-domain command.
# Works only with simple domain. Using it with JRF template will cause this task to fail.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def start_domain(globals, command):
    check_all_options(globals, command, ['domain'])
    domain = get_option(globals, command, 'domain')
    host = get_host(globals, command)
    port = get_port(globals, command)
    adminUser = get_admin_user(globals, command)
    adminPassword = get_admin_password(globals, command)
    jvmOptions = init_option(globals, command, 'jvm-options', \
            '-XX:PermSize=128m -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log')
    serverURL = 't3://' + host + ':' + port
    print 'Executing command start-domain'
    print '    Domain path:    ' + domain
    print '    Host:           ' + host
    print '    Port:           ' + port
    print '    Admin user:     ' + adminUser
    print '    Admin password: ' + adminPassword
    print '    JVM options:    ' + jvmOptions
    startServer(url=serverURL, username=adminUser, password=adminPassword, \
            domainDir=domain, jvmArgs=jvmOptions)

# Execute start-partition command.
# Starts the partition passed as an argument.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def start_partition(globals, command):
    check_all_options(globals, command, ['partition'])
    partition = get_option(globals, command, 'partition')
    jvmOptions = init_option(globals, command, 'jvm-options', \
            '-XX:PermSize=128m -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log')
    print 'Executing command start-partition'
    print '    Partition:      ' + partition
    print '    JVM options:    ' + jvmOptions
    p = cmo.lookupPartition(partition)
    startPartitionWait(p)

##### Broken, do not use.
# Execute start-script command.
# Uses default WebLogic startup script to start server. UNIX only feature.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def start_script(globals, command):
    check_all_options(globals, command, ['domain'])
    domain = get_option(globals, command, 'domain')
    script = init_option(globals, command, 'start_script', 'startWebLogic.sh')
    scriptPath = domain + os.sep + script
    print 'Executing command start-script'
    print '    Domain path:    ' + domain
    print '    Startup script: ' + scriptPath
    #pid = os.fork()
    #if pid == 0:
    os.system(scriptPath + ' &')
    #else:
    sleep(60)

# Execute stop-domain command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def stop_domain(globals, command):
    global wlsSessionActive
    if wlsSessionActive:
        print 'Shall not be executed after any online command'
        help_msg()
        exit(1)
    host = get_host(globals, command)
    port = get_port(globals, command)
    user = get_admin_user(globals, command)
    password = get_admin_password(globals, command)
    print 'Executing command stop-domain'
    print '    Host:           ' + host
    print '    Port:           ' + port
    print '    Admin user:     ' + user
    print '    Admin password: ' + password
    connect(user, password,'t3://' + host + ':' + port)
    shutdown(entityType='Server', ignoreSessions='true')

# Execute create-partition-admin command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_partition_admin(globals, command):
    check_all_options(globals, command, ['name', 'user', 'password', 'group'])
    name =  get_option(globals, command, 'name')
    realmName =  get_option(globals, command, 'realm')
    group = get_option(globals, command, 'group')
    user = get_option(globals, command, 'user')
    password = get_option(globals, command, 'password')
    if realmName == None:
        realmName = name
    print 'Executing command create-partition-admin'
    print '    Partition name:      ' + name
    print '    Realm name:          ' + realmName
    print '    Admin user group:    ' + group
    print '    Admin user name:     ' + user
    print '    Admin user password: ' + password
    serverConfig()
    realm = cmo.getSecurityConfiguration().lookupRealm(realmName)
    id = provider_id("DefaultAuthenticator", name)
    atn = realm.lookupAuthenticationProvider(id)
    if atn.groupExists(group):
        print 'Partition admins group ' + group + ' already exists in realm ' + realmName
    else:
        print 'Creating admins group ' + group + ' in realm ' + realmName
        atn.createGroup(group, realmName + ' Realm Group')
    if atn.userExists(user):
        print 'Partition admin user ' + user + ' already exists in realm ' + realmName
    else:
        print 'Creating partition admin user ' + user + ' in realm ' + realmName
        atn.createUser(user, password, realmName + ' Partition Administrator')
    if atn.isMember(group, user, true) == 0:
        print 'Adding admin user ' + user + ' into admins group ' + group + ' in realm ' + realmName
        atn.addMemberToGroup(group, user)
    else:
        print 'Admin user ' + user + ' is already in admins group ' + group + ' in realm ' + realmName

# Set targets list for current path
#     name        Target object name (e.g. 'virtual host' or 'resource group') used in logging
#     targetsList Comma (with no spaces) separated list of virtual host targets
#                 Each target is <name>\':\'<type> pair, e.g. AdminServer:Server
def set_targets(name, targetsList):
    if targetsList != None:
        targets = targetsList.split(',')
        objectNames = []
        for target in targets:
            items = target.split(':')
            if len(items) == 2 and len(items[0]) > 0 and len(items[1]) > 0:
                objectName = 'com.bea:Name=' + items[0] + ',Type=' + items[1]
                print 'Adding ' + objectName + ' as target for ' + name
                objectNames.append(ObjectName(objectName))
            else:
                print 'Invalid virtual host target ' + target
        if len(objectNames) > 0:
            print 'Setting ' + str(objectNames) + ' as targets for ' + name
            set('Targets', jarray.array(objectNames, ObjectName))

# Execute create-virtual-host command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_virtual_host(globals, command):
    check_all_options(globals, command, ['name'])
    name = get_option(globals, command, 'name')
    targetsList = get_option(globals, command, 'targets')
    print 'Executing command create-partition-admin'
    print '    Virtual host name: ' + name
    print '    Targets:           ' + value_to_message(targetsList)
    cd('/')
    print 'Creating virtual host ' + name
    cmo.createVirtualHost(name)
    cd('VirtualHosts/' + name)
    set('VirtualHostNames',jarray.array([String(name)], String))
    set_targets(name, targetsList)

# Execute create-virtual-target command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_virtual_target(globals, command):
    check_all_options(globals, command, ['name','prefix'])
    name =  get_option(globals, command, 'name')
    prefix =  get_option(globals, command, 'prefix')
    host = get_host(globals, command)
    port = get_command_option(command, 'port')
    print 'Executing command create-virtual-target'
    print '    Name:   ' + name
    print '    Prefix: ' + prefix
    print '    Host:   ' + host
    print '    Port:   ' + value_to_message(port)
    cd('/')
    vt = cmo.lookupVirtualTarget(name)
    if vt is None:
        print 'Creating virtual target ' + name
        vt = cmo.createVirtualTarget(name)
        vt.setUriPrefix('/' + prefix)
        for s in cmo.getServers():
            vt.addTarget(s)
            print 'Added server ' + s.getName() + ' to virtual target ' + name
        if host != None:
            vt.setHostNames(array([host],java.lang.String))
    else:
         print 'Virtual target ' + name + ' already exists'

# Get resource group template of given name
#     name Resource group template name
#     returns  Resource group template object or None if no template object was found
def get_resource_group_template(name):
    return getMBean('/ResourceGroupTemplates/' + name)

# Execute create-resource-group-template command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_resource_group_template(globals, command):
    check_all_options(globals, command, ['name'])
    name =  get_option(globals, command, 'name')
    print 'Executing command create-resource-group-template'
    print '    Name: ' + name
    cd('/')
    t = cmo.lookupResourceGroupTemplate(name)
    if t is None:
        print 'Creating resource group template ' + name
        cmo.createResourceGroupTemplate(name)
    else:
        print 'Template ' + name + ' already exists'

# Execute create-resource-group command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_resource_group(globals, command):
    check_all_options(globals, command, ['name'])
    name =  get_option(globals, command, 'name')
    templateName = get_option(globals, command, 'template')
    targetsList = get_option(globals, command, 'targets')
    print 'Executing command create-resource-group'
    print '    Name:          ' + name
    print '    Template name: ' + value_to_message(templateName)
    print '    Targets:       ' + value_to_message(targetsList)
    cd('/')
    rg = cmo.lookupResourceGroup(name)
    if rg is None:
        print 'Creating resource group ' + name
        cmo.createResourceGroup(name)
        cd('ResourceGroups/' + name)
        if templateName != None:
            print 'Setting resource group template ' + templateName + ' to resource group ' + name
            cmo.setResourceGroupTemplate(get_resource_group_template(templateName))
        set_targets(name, targetsList)
    else:
        print 'Resource group ' + name + ' already exists'

# Execute create-realm command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_realm(globals, command):
    check_all_options(globals, command, ['name'])
    name = get_option(globals, command, 'name')
    cd('/')
    domainName = cmo.getName()
    print 'Executing command create-realm'
    print '    Domain name:  ' + domainName
    print '    Realm name:   ' + name
    cd('SecurityConfiguration/' + domainName)
    if cmo.lookupRealm(name) is None:
        print 'Creating realm ' + name
        cmo.createRealm(name)
    else:
        print 'Realm ' + name + ' already exists'

# Create Authentication Provider object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_authentication_provider(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupAuthenticationProvider(id)
    if object == None:
        print 'Creating AuthenticationProvider ' + id + ' in realm ' + realmName
        cmo.createAuthenticationProvider(id, type)
    else:
        print 'AuthenticationProvider ' + id + ' already exists in realm ' + realmName

# Create Authorizer object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_authorizer(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupAuthorizer(id)
    if object == None:
        print 'Creating Authorizer ' + id + ' in realm ' + realmName
        cmo.createAuthorizer(id, type)
    else:
        print 'Authorizer ' + id + ' already exists in realm ' + realmName

# Create RoleMapper object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_role_mapper(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupRoleMapper(id)
    if object == None:
        print 'Creating RoleMapper ' + id + ' in realm ' + realmName
        cmo.createRoleMapper(id, type)
    else:
        print 'RoleMapper ' + id + ' already exists in realm ' + realmName

# Create Adjudicator object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_adjudicator(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    #object = realm.lookupAdjudicator(id)
    #if object == None:
    if True:
        print 'Creating Adjudicator ' + id + ' in realm ' + realmName
        cmo.createAdjudicator(id, type)
    else:
        print 'Adjudicator ' + id + ' already exists in realm ' + realmName

# Create CredentialMapper object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_credential_mapper(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupCredentialMapper(id)
    if object == None:
        print 'Creating CredentialMapper ' + id + ' in realm ' + realmName
        cmo.createCredentialMapper(id, type)
    else:
        print 'CredentialMapper ' + id + ' already exists in realm ' + realmName

# Create CertPathProvider object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name  Object name
#     type  Object type
#     realm Realm object
def create_cert_path_provider(name, type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupCertPathProvider(id)
    if object == None:
        print 'Creating CertPathProvider ' + id + ' in realm ' + realmName
        cert = cmo.createCertPathProvider(id, type)
        cmo.setCertPathBuilder(cert)
    else:
        print 'CertPathProvider ' + id + ' already exists in realm ' + realmName

# Create PasswordValidator object.
# cd('/SecurityConfiguration/' + <domain_name> + '/Realms/' + <realm_name>) shall be already done before calling
# this function.
#     name              Object name
#     minPasswordLength Minimum password length
#     minSpecialChars   Minimum number of numeric or special characters
#     type              Object type
#     realm             Realm object
def create_password_validator(name, minPasswordLength, minSpecialChars,type, realm):
    realmName = realm.getName()
    id = provider_id(name, realmName)
    object = realm.lookupPasswordValidator(id)
    if object == None:
        print 'Creating PasswordValidator ' + id + ' in realm ' + realmName
        pv = cmo.createPasswordValidator(id, type)
        pv.setMinPasswordLength(minPasswordLength)
        pv.setMinNumericOrSpecialCharacters(minSpecialChars)
    else:
        print 'PasswordValidator ' + id + ' already exists in realm ' + realmName


# Execute create-realm-providers command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_realm_providers(globals, command):
    check_all_options(globals, command, ['name'])
    name = get_option(globals, command, 'name')
    cd('/')
    domainName = cmo.getName()
    print 'Executing command create-providers'
    print '    Domain name:  ' + domainName
    print '    Realm name:   ' + name
    cd('SecurityConfiguration/' + domainName)
    realm=cmo.lookupRealm(name)
    if realm != None:
        cd('Realms/' + name)
        create_authentication_provider('DefaultAuthenticator',
            'weblogic.security.providers.authentication.DefaultAuthenticator', realm)
        create_authentication_provider('DefaultIdentityAsserter',
            'weblogic.security.providers.authentication.DefaultIdentityAsserter', realm)
        create_authorizer('XACMLAuthorizer',
            'weblogic.security.providers.xacml.authorization.XACMLAuthorizer', realm)
        create_role_mapper('XACMLRoleMapper',
            'weblogic.security.providers.xacml.authorization.XACMLRoleMapper', realm)
        # Workaround because realm.lookupAdjudicator does not exist
        if realm.lookupAuthenticationProvider(provider_id('DefaultAuthenticator', realm.getName())) == None:
            create_adjudicator('DefaultAdjudicator',
                'weblogic.security.providers.authorization.DefaultAdjudicator', realm)
        create_credential_mapper('DefaultCredentialMapper',
            'weblogic.security.providers.credentials.DefaultCredentialMapper', realm)
        create_cert_path_provider('WebLogicCertPathProvider',
            'weblogic.security.providers.pk.WebLogicCertPathProvider', realm)
        create_password_validator('SystemPasswordValidator', 8, 1,
            'com.bea.security.providers.authentication.passwordvalidator.SystemPasswordValidator', realm)
    else:
        print 'Realm ' + name + ' was not found'
    # uncomment below if you want to configure a default auditor
    # print "Creating mbean of type Auditor ... "
    # cmo.createAuditor('DefaultAuditor' + suffix,'weblogic.security.providers.audit.DefaultAuditor')

# Execute create-partition command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_partition(globals, command):
    check_all_options(globals, command, ['name', 'resource-group'])
    name =  get_option(globals, command, 'name')
    realmName =  get_option(globals, command, 'realm')
    vhName = get_option(globals, command, 'virtual-host')
    vtName = get_option(globals, command, 'virtual-target')
    rgName = get_option(globals, command, 'resource-group')
    rgTemplateName = get_option(globals, command, 'resource-group-template')
    cd('/')
    domainName = cmo.getName()
    print 'Executing command create-partition'
    print '    Domain name:             ' + domainName
    print '    Partition name:          ' + name
    print '    Virtual host name:       ' + value_to_message(vhName)
    print '    Virtual target name:     ' + value_to_message(vtName)
    print '    Resource group name:     ' + rgName
    print '    Resource group template: ' + value_to_message(rgTemplateName)
    print '    Realm name:              ' + value_to_message(realmName)
    cd("/")
    partition = cmo.lookupPartition(name)
    if partition == None:
        vt = None
        if vhName != None:
            cd('/VirtualHosts')
            vt = cmo.lookupVirtualHost(vhName)
            print 'Creating partition ' + name + ' with virtual host ' + vhName
        if vt == None and vtName != None:
            cd('/')
            vt = cmo.lookupVirtualTarget(vtName)
            print 'Creating partition ' + name + ' with virtual target ' + vtName
        cd('/')
        if vt != None:
            if realmName != None:
                cd('SecurityConfiguration/' + domainName)
                realm = cmo.lookupRealm(realmName)
                cd("/")
            else:
                realm = None
            partition = cmo.createPartition(name)
            rg = partition.lookupResourceGroup(rgName)
            if rg == None:
                print 'Creating resource group ' + rgName + ' in partition ' + name
                rg = partition.createResourceGroup(rgName)
                if rgTemplateName != None:
                    print 'Setting resource group template ' + rgTemplateName + ' to resource group ' + rgName
                    tmpl = get_resource_group_template(rgTemplateName)
                    if tmpl != None:
                        rg.setResourceGroupTemplate(tmpl)
                    else:
                        print 'Template ' + rgTemplateName + ' was not found'
            else:
                print 'Resource group ' + rgName + ' already exists in partition ' + name
            print 'Setting ' + str(vt) + ' as available and default target for partition ' + name
            cd('Partitions/' + name)
            partition.addAvailableTarget(vt)
            partition.addDefaultTarget(vt)
            if vhName != None:
                print 'Setting partition ' + name + ' host name to ' + vhName + ' virtual host name'
                set('HostNames', jarray.array([String(vhName)], String))
            if realmName != None:
                print 'Setting realm ' +  realmName + ' for partition ' + name
                partition.setRealm(realm)
        else:
            print 'Virtual host or target was not found'
    else:
        print 'Partition ' + name + ' already exists'

# Execute set-identity-domain command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def set_identity_domain(globals, command):
    check_all_options(globals, command, ['name'])
    name =  get_option(globals, command, 'name')
    realmName =  get_option(globals, command, 'realm')
    if realmName == None:
        realmName = name
    cd('/')
    domainName = cmo.getName()
    print 'Executing command set-identity-domain'
    print '    Domain name:    ' + domainName
    print '    Partition name: ' + name
    print '    Realm name:     ' + realmName
    cd('SecurityConfiguration/' + domainName)
    realm = cmo.lookupRealm(realmName)
    if realm != None:
        providerId = provider_id('DefaultAuthenticator', realmName)
        provider = realm.lookupAuthenticationProvider(providerId)
        if provider != None:
            id = identity_domain_id(name)
            provider.setIdentityDomain(id)
            print 'Setting identity domain ' + id + ' in DefaultAuthenticator ' + providerId + ' in realm ' + realmName
        else:
            print 'DefaultAuthenticator ' + providerId + ' was not found in realm ' + realmName
    else:
        print 'Realm ' + realmName + ' was not found'

# Execute set-identity-domain-on-admin-realm command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def set_identity_domain_on_admin_realm(globals, command):
    check_all_options(globals, command, ['idd-name'])
    iddName = get_option(globals, command, 'idd-name')
    print 'Executing command set-identity-domain-on-admin-realm'
    print '    Identity domain name: ' + iddName
    cd('/')
    secCfg = cmo.getSecurityConfiguration()
    id = identity_domain_id(iddName)
    secCfg.setAdministrativeIdentityDomain(id)
    realm = secCfg.getDefaultRealm()
    provider = realm.lookupAuthenticationProvider('DefaultAuthenticator')
    provider.setIdentityDomain(id)

# Execute set-jpa-provider command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def set_jpa_provider(globals, command):
    check_all_options(globals, command, ['provider'])
    jpaProvider = get_option(globals, command, 'provider')
    cd('/')
    domainName = cmo.getName()
    print 'Executing command set-jpa-provider'
    print '    Domain name:  ' + domainName
    print '    JPA provider: ' + jpaProvider
    cd('JPA/' + domainName)
    cmo.setDefaultJPAProvider(jpaProvider)

# Execute create-jdbc-resource command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_jdbc_resource(globals, command):
    check_all_options(globals, command, ['name', 'url', 'driver', 'user', 'password'])
    name = get_option(globals, command, 'name')
    jndiName = init_option(globals, command, 'jndi-name', 'jdbc/' + name)
    url = get_option(globals, command, 'url')
    driver = get_option(globals, command, 'driver')
    user = get_option(globals, command, 'user')
    password = get_option(globals, command, 'password')
    transProtocol = get_option(globals, command, 'transactions-protocol')
    testQuery = init_option(globals, command, 'test-query', 'SQL SELECT 1 FROM DUAL')
    template = get_option(globals, command, 'resource-group-template')
    print 'Executing command create-jdbc-resource'
    print '    Resource name:   ' + name
    print '    JNDI name:       ' + jndiName
    print '    Resource URL:    ' + url
    print '    JDBC driver:     ' + driver
    print '    User name:       ' + user
    print '    User password:   ' + password
    print '    Transactions:    ' + value_to_message(transProtocol)
    print '    Test query:      ' + testQuery
    print '    Target template: ' + value_to_message(template)
    if template == None:
        root = '/'
    else:
        root = '/ResourceGroupTemplates/' + template + '/'
    resourcePath= root + 'JDBCSystemResources/' + name + '/JDBCResource/' + name
    dsParamsPath = resourcePath + '/JDBCDataSourceParams/' + name
    jdbcParamsPath = resourcePath + '/JDBCDriverParams/' + name
    jdbcParamsPropertiesPath =  jdbcParamsPath + '/Properties/' + name
    jdbcParamsPropertyUserPath = jdbcParamsPropertiesPath + '/Properties/user'
    jdbcPoolParamsPath = resourcePath + '/JDBCConnectionPoolParams/' + name
    cd(root)
    cmo.createJDBCSystemResource(name)
    cd(resourcePath)
    cmo.setName(name)
    cd(dsParamsPath)
    set('JNDINames',jarray.array([String(jndiName)], String))
    if transProtocol != None:
        cmo.setGlobalTransactionsProtocol(transProtocol)
    cd(jdbcParamsPath)
    cmo.setUrl(url)
    cmo.setDriverName(driver)
    set('PasswordEncrypted',password)
    cd(jdbcParamsPropertiesPath)
    cmo.createProperty('user')
    cd(jdbcParamsPropertyUserPath)
    cmo.setValue(user)
    cd(jdbcPoolParamsPath)
    cmo.setTestTableName(testQuery)

# Execute create-proxy-resource command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_proxy_resource(globals, command):
    check_all_options(globals, command, ['name', 'targets'])
    name = get_option(globals, command, 'name')
    jndiName = init_option(globals, command, 'jndi-name', 'jdbc/' + name)
    targetsList = get_option(globals, command, 'targets')
    print 'Executing command create-proxy-resource'
    print '    Resource name: ' + name
    print '    JNDI name:     ' + jndiName
    print '    Targets:       ' + value_to_message(targetsList)
    resourcePath='/JDBCSystemResources/' + name + '/JDBCResource/' + name
    dsParamsPath = resourcePath + '/JDBCDataSourceParams/' + name
    jdbcParamsPath = resourcePath + '/JDBCDriverParams/' + name
    jdbcParamsPropertiesPath =  jdbcParamsPath + '/Properties/' + name
    cd('/')
    if cmo.lookupJDBCSystemResource(name) is None:
        print 'Creating proxy resource ' + name
        cmo.createJDBCSystemResource(name)
        cd(resourcePath)
        cmo.setName(name)
        cmo.setDatasourceType('PROXY')
        cd(dsParamsPath)
        print 'Setting JNDI name ' + jndiName + ' to resoiurce ' + name
        set('JNDINames',jarray.array([String(jndiName)], String))
        cd(jdbcParamsPropertiesPath)
        print 'Creating property dbSwitchingCallbackClassName'
        print '    with value com.oracle.jrf.mt.datasource.PartitionedJNDIBasedDataSourceSwitchingCallbackImpl'
        cmo.createProperty('dbSwitchingCallbackClassName')
        cd('Properties/dbSwitchingCallbackClassName')
        cmo.setValue('com.oracle.jrf.mt.datasource.PartitionedJNDIBasedDataSourceSwitchingCallbackImpl')
        cd(jdbcParamsPropertiesPath)
        print 'Creating property switchingProperties'
        cd('/JDBCSystemResources/' + name)
        set_targets(name, targetsList)
    else:
        print 'Proxy resource ' + name + ' already exists'

# Execute create-resource-override command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def create_resource_override(globals, command):
    check_all_options(globals, command, ['name', 'partition', 'resource'])
    name = get_option(globals, command, 'name')
    pName = get_option(globals, command, 'partition')
    resource = get_option(globals, command, 'resource')
    url = get_option(globals, command, 'url')
    driver = get_option(globals, command, 'driver')
    user = get_option(globals, command, 'user')
    password = get_option(globals, command, 'password')
    transProtocol = get_option(globals, command, 'transactions-protocol')
    print 'Executing command create-resource-override'
    print '    Override name:   ' + name
    print '    Partition name:  ' + pName
    print '    Resource name:   ' + resource
    print '    Resource URL:    ' + value_to_message(url)
    print '    JDBC driver:     ' + value_to_message(driver)
    print '    User name:       ' + value_to_message(user)
    print '    User password:   ' + value_to_message(password)
    print '    Transactions:    ' + value_to_message(transProtocol)
    cd("/")
    partition = cmo.lookupPartition(pName)
    if partition != None:
        print 'Creating JDBC resource override object ' + name + ' in partition ' + pName
        partition.createJDBCSystemResourceOverride(name)
        cd('/Partitions/' + pName + '/JDBCSystemResourceOverrides/' + name)
        print 'Setting base JDBC resource to ' + resource
        cmo.setDataSourceName(resource)
        if url != None:
            print 'Setting JDBC resource ' + resource + ' URL override to ' + url
            cmo.setURL(url)
        if driver != None:
            print 'Setting JDBC resource ' + resource + ' driver override to ' + driver
            cmo.setDriverName(driver)
        if user != None:
            print 'Setting JDBC resource ' + resource + ' user name override to ' + user
            cmo.setUser(user)
        if password != None:
            print 'Setting JDBC resource ' + resource + ' user password override to ' + password
            cmo.setPassword(password)
        if transProtocol != None:
            print 'Setting JDBC resource ' + resource + ' transaction protocol override to ' + transProtocol
            cmo.setGlobalTransactionsProtocol(transProtocol)
    else:
        print 'Partition ' + name + ' does not exist'

# Execute deploy command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def deploy(globals, command):
    check_all_options(globals, command, ['name', 'path'])
    name = get_option(globals, command, 'name')
    appPath = get_option(globals, command, 'path')
    targetServers = get_option(globals, command, 'targets')
    rgTemplate = get_option(globals, command, 'template')
    print 'Executing command deploy'
    print '    Application name:        ' + name
    print '    Application path:        ' + appPath
    print '    Target servers:          ' + value_to_message(targetServers)
    print '    Resource group template: ' + value_to_message(rgTemplate)
    cd('/')
    if targetServers != None:
        if rgTemplate != None:
            print 'Application ' + name + ' from ' + appPath + ' is being deployed on ' + targetServers + ' and template ' + rgTemplate
            result = deploy(appName=name, path=appPath, targets=targetServers, resourceGroupTemplate=rgTemplate)
        else:
            print 'Application ' + name + ' from ' + appPath + ' is being deployed on ' + targetServers
            result = deploy(appName=name, path=appPath, targets=targetServers)
    else:
        if rgTemplate != None:
            print 'Application ' + name + ' from ' + appPath + ' is being deployed on template ' + rgTemplate
            result = deploy(appName=name, path=appPath, resourceGroupTemplate=rgTemplate)
        else:
            print 'Application ' + name + ' from ' + appPath + ' is being deployed'
            result = deploy(appName=name, path=appPath)
    print result.getMessage()

# Execute undeploy command.
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def undeploy(globals, command):
    check_all_options(globals, command, ['name'])
    name = get_option(globals, command, 'name')
    targetServers = get_option(globals, command, 'targets')
    rgTemplate = get_option(globals, command, 'template')
    print 'Executing command undeploy'
    print '    Application name:        ' + name
    print '    Target servers:          ' + value_to_message(targetServers)
    print '    Resource group template: ' + value_to_message(rgTemplate)
    cd('/')
    if targetServers != None:
        if rgTemplate != None:
            print 'Application ' + name + ' is being undeployed on ' + targetServers + ' and template ' + rgTemplate
            result = undeploy(appName=name, targets=targetServers, resourceGroupTemplate=rgTemplate)
        else:
            print 'Application ' + name + ' is being undeployed on ' + targetServers
            result = undeploy(appName=name, targets=targetServers)
    else:
        if rgTemplate != None:
            print 'Application ' + name + ' is being undeployed on template ' + rgTemplate
            result = undeploy(appName=name, resourceGroupTemplate=rgTemplate)
        else:
            print 'Application ' + name + ' is being undeployed'
            result = undeploy(appName=name)
    print result.getMessage()

# Activate changes after batch of online commands and open new edit session
#     globals Global command arguments.
#     command Command and its arguments dictionary.
def activate(globals, command):
    activate()
    startEdit()

# Open edit session before batch of online commands
#     globals Global command arguments.
def initSession(globals):
    check_options(globals, ['host', 'port', 'admin-user', 'admin-password'])
    host = init_global_option(globals, 'host', '127.0.0.1')
    port = init_global_option(globals, 'port', '7001')
    user = init_global_option(globals, 'admin-user', 'weblogic')
    password = init_global_option(globals, 'admin-password', 'welcome1')
    connect(user, password,'t3://' + host + ':' + port)
    edit()
    startEdit()

# Close edit session after batch of online commands
#     globals Global command arguments.
def closeSession():
    save()
    activate()

# Command executors
commandExecutors = {
    'create-domain'                      : create_domain,
    'add-template'                       : add_template,
    'start-domain'                       : start_domain,
    'start-partition'                    : start_partition,
    'start-script'                       : start_script,
    'stop-domain'                        : stop_domain,
    'create-partition-admin'             : create_partition_admin,
    'create-virtual-host'                : create_virtual_host,
    'create-virtual-target'              : create_virtual_target,
    'create-resource-group-template'     : create_resource_group_template,
    'create-resource-group'              : create_resource_group,
    'create-realm'                       : create_realm,
    'create-realm-providers'             : create_realm_providers,
    'create-partition'                   : create_partition,
    'set-identity-domain'                : set_identity_domain,
    'set-identity-domain-on-admin-realm' : set_identity_domain_on_admin_realm,
    'set-jpa-provider'                   : set_jpa_provider,
    'create-jdbc-resource'               : create_jdbc_resource,
    'create-proxy-resource'              : create_proxy_resource,
    'create-resource-override'           : create_resource_override,
    'deploy'                             : deploy,
    'undeploy'                           : undeploy,
    'activate'                           : activate
}

# Offline commands that do not need active session
offlineCommands = Set([
    'create-domain', 'add-template', 'start-domain', 'start-script', 'stop-domain', 'create-partition-admin'
])

# Global code to be executed on startup
wlsCommands = parse_args(sys.argv)
wlsSessionActive = False
globalArguments = {}
for wlsCommand in wlsCommands:
    # Global options if present
    if not wlsCommand.has_key(None):
        globalArguments = wlsCommand;
    # Command to process
    else:
        commandStr = wlsCommand[None]
        if not commandExecutors.has_key(commandStr):
            print '\nUnknown command: ' + commandStr
            help_msg()
        else:
            if not commandStr in offlineCommands:
                if not wlsSessionActive:
                    initSession(globalArguments)
                    wlsSessionActive = True
            else:
                if wlsSessionActive:
                    closeSession()
                    wlsSessionActive = False
            #print_command(command)
            # Run command executor
            #try:
            commandExecutors[commandStr](globalArguments, wlsCommand)
            #except Exception, e:
            #    e.printStackTrace()
            #    print e.getLocalizedMessage()
            #    exit(1)
if wlsSessionActive:
    closeSession()
    wlsSessionActive = False
