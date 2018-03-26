# How to setup

All the configuration should be done in the file standalone.xml. This file can be found in WildFly installation: `wildfly-11.0.0.Final/standalone/configuration/standalone.xml`  

## Datasource

1. Find the block `<subsystem xmlns="urn:jboss:domain:datasources:5.0">` in the standalone.xml

2. Add following snippet there:
       
        <datasource jndi-name="java:jboss/datasources/ArchetypeDS" pool-name="ArchetypeDS" enabled="true" use-java-context="true">
            <connection-url>jdbc:h2:~/Temp/ArchetypeDB;AUTO_SERVER=TRUE</connection-url>
            <driver>h2</driver>
            <security>
                <user-name>sa</user-name>
                <password>sa</password>
            </security>
        </datasource>

## Security

1. Find the block `<subsystem xmlns="urn:jboss:domain:security:2.0">` in the standalone.xml file

2. Add following snippet there:

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

3. Just in case there is one user `admin` created with password `admin` by default. See `load.sql` script for details

## Logging

1. Find the block `<subsystem xmlns="urn:jboss:domain:logging:3.0">` in the standalone.xml file

2. Add following snippet to log all messages from the application:

        <logger category="javacourses">
            <level name="ALL"/>
        </logger>
        
   Category - is the package. Level name can be `ALL`, `DEBUG`, `INFO`, `WARN` or `ERROR`. 
   You can configure different levels for different packages.
   
## Sending emails

Application is sending emails to confirm user's email address during the registration and for resetting passwords.

In this example of google mail (gmail) configuration is provided. For other smtp servers configuration might be slightly different. 

To make it working you need to do following:

1. Find the block `<socket-binding-group name="standard-sockets" ...` 

2. Add following snippet:

        <outbound-socket-binding name="gmail-smtp">
            <remote-destination host="smtp.gmail.com" port="587"/>
        </outbound-socket-binding>
        
3. Find the block `<subsystem xmlns="urn:jboss:domain:mail:3.0">`

4. Add following snippet:

        <mail-session name="gmail" debug="false" 
                      jndi-name="java:jboss/mail/gmail" 
                      from="NAME@gmail.com">
            <smtp-server outbound-socket-binding-ref="gmail-smtp" tls="true" 
                         username="NAME@gmail.com" 
                         password="PASSWORD"/>
        </mail-session>

5. Edit above snippet changing `NAME@gmail.com` with your gmail address (in 2 places). 

6. Edit above snippet changing `PASSWORD` with your password.

Happy hacking!