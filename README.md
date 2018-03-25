# How to setup

## Datasource

1. Open standalone.xml from WildFly installation (`wildfly-11.0.0.Final/standalone/configuration/standalone.xml`)

2. Find the block `<subsystem xmlns="urn:jboss:domain:datasources:5.0">`

3. Add following snippet there:
       
        <datasource jndi-name="java:jboss/datasources/ArchetypeDS" pool-name="ArchetypeDS" enabled="true" use-java-context="true">
            <connection-url>jdbc:h2:~/Temp/ArchetypeDB;AUTO_SERVER=TRUE</connection-url>
            <driver>h2</driver>
            <security>
                <user-name>sa</user-name>
                <password>sa</password>
            </security>
        </datasource>

## Security

1. Open standalone.xml from WildFly installation (`wildfly-11.0.0.Final/standalone/configuration/standalone.xml`)

2. Find the block 

3. Add following snippet there:

        <security-domain name="ArchetypeSecurityDomain" cache-type="default">
            <authentication>
                <login-module code="Database" flag="required">
                    <module-option name="dsJndiName" value="java:jboss/datasources/ArchetypeDS"/>
                    <module-option name="principalsQuery" value="SELECT u.password FROM users u WHERE u.email = ?"/>
                    <module-option name="rolesQuery" value="SELECT r.name, 'Roles' FROM users u, roles r, users_roles ur WHERE u.id = ur.user_id AND r.id = ur.role_id AND u.email = ?"/>
                    <module-option name="hashAlgorithm" value="SHA-512"/>
                    <module-option name="hashEncoding" value="base64"/>
                    <module-option name="hashCharset" value="UTF-8"/>
                </login-module>
            </authentication>
        </security-domain>

