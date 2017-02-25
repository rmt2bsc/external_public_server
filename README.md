# Web Services Public Server
The application server providing public access to RMT2 system.  This server currently supports SOAP and REST bases web services. Currently, the web services are deployed to a Tomcat servlet container which is bound to Servlet 3.0 specification.  The SOAP engine is based off a RMT2 custom implementation.  The REST services uses Jersey 2.25 implementation of JAX-RS.

**Application Configuration**

1. Web Application Configurator
   * On UNIX and Windows platforms, create the directories:
      *  */tmp/AppServer/config* - contains the configuration files for application server. 
      * */tmp/AppServer/log* - contains the logger output of the application.
   * Add log4j.properties and RMT2AppServerConfig.xml files to */tmp/AppServer/config*. 

2. Since this server supports both SOAP and REST web services, the following entries will need to be added to the web descriptor file in order for these web service engines to function properly:
  * Add the *com.api.config.WebSystemConfigController* servlet implementation for the SystemConfigurator.
  * Add the servlet, *org.glassfish.jersey.servlet.ServletContainer*, as the REST ServletContainer.   Add the initParam, *javax.ws.rs.Application*, and point it to the custom Application object.
  * Add the servlet, *com.api.messaging.webservice.soap.engine.RMT2SoapEngine*, as the SOAP servlet.

