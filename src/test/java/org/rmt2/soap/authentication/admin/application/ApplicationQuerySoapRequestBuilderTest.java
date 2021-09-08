package org.rmt2.soap.authentication.admin.application;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ApplicationType;
import org.rmt2.jaxb.AuthCriteriaGroupType;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.UserAppRolesCriteriaType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.RecordTrackingTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ApplicationQuerySoapRequestBuilderTest {

    private JaxbUtil jaxb;
    
    @Before
    public void setUp() throws Exception {
        try {
            jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        }
        catch (Exception e) {
            jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
    }
    
    @Test
    public void testBuildRequest() {
        ObjectFactory fact = new ObjectFactory();
        AuthenticationRequest req = fact.createAuthenticationRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_APPLICATION_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthCriteriaGroupType criteriaGroup = fact.createAuthCriteriaGroupType();
        UserAppRolesCriteriaType criteria = fact.createUserAppRolesCriteriaType();
        criteria.setAppId(200);
        criteria.setAppDescription("Authentication application");
        criteria.setAppCode("auth_app");
        criteria.setAppActive(1);
        
        criteriaGroup.setUserAppRolesCriteria(criteria);
        req.setCriteria(criteriaGroup);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        AuthenticationResponse req = fact.createAuthenticationResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_APPLICATION_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();

        // Setup first application record
        ApplicationType app = fact.createApplicationType();
        app.setActive(1);
        app.setAppCode("auth_app1");
        app.setAppId(1000);
        app.setDescription("Authenticationa Application 1");

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(new Date())
                .withDateUpdate(new Date())
                .withUserId("test_user")
                .build();
        app.setTracking(tracking);
        apgt.getApplicationInfo().add(app);

        // Setup second application record
        app = fact.createApplicationType();
        app.setActive(1);
        app.setAppCode("auth_app2");
        app.setAppId(1002);
        app.setDescription("Authenticationa Application 2");

        tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(new Date())
                .withDateUpdate(new Date())
                .withUserId("test_user")
                .build();
        app.setTracking(tracking);
        apgt.getApplicationInfo().add(app);

        req.setProfile(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_APPLICATION_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
 }
