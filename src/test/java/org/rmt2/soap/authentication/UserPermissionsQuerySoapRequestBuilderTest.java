package org.rmt2.soap.authentication;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AppRoleType;
import org.rmt2.jaxb.AuthCriteriaGroupType;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.UserAppRoleType;
import org.rmt2.jaxb.UserAppRolesCriteriaType;
import org.rmt2.jaxb.UserGroupType;
import org.rmt2.jaxb.UserType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.authentication.AppRoleTypeBuilder;
import org.rmt2.util.authentication.UserAppRoleTypeBuilder;
import org.rmt2.util.authentication.UserGroupTypeBuilder;
import org.rmt2.util.authentication.UserTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class UserPermissionsQuerySoapRequestBuilderTest {

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
                .withTransaction(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthCriteriaGroupType apgt = fact.createAuthCriteriaGroupType();
        UserAppRolesCriteriaType criteria = fact.createUserAppRolesCriteriaType();
        
        criteria.setLoginId(10);
        criteria.setUserName("username");
        criteria.setFirstName("firstName");
        criteria.setLastName("lastName");
        criteria.setAppId(100);
        criteria.setAppCode("AppCode");
        criteria.setRoleId(200);        
        criteria.setRoleName("RoleName");
        criteria.setAppRoleId(300);
        criteria.setAppRoleCode("AppRoleCode");
        criteria.setAppRoleName("AppRoleName");
        criteria.setAppActive(1);
        apgt.setUserAppRolesCriteria(criteria);

        req.setCriteria(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildRespones() {
        ObjectFactory fact = new ObjectFactory();
        AuthenticationResponse resp = fact.createAuthenticationResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AppRoleType art = AppRoleTypeBuilder.Builder.create()
                .withAppRoleId(100)
                .withCode("AppRoleCode")
                .withName("AppRoleName")
                .withDescription("AppRoleDescription")
                .build();

        UserAppRoleType uart = UserAppRoleTypeBuilder.Builder.create()
                .withUserApplicationRoleId(700)
                .withApplicationRoleInfo(art)
                .build();
        UserAppRoleType uart2 = UserAppRoleTypeBuilder.Builder.create()
                .withUserApplicationRoleId(701)
                .withApplicationRoleInfo(art)
                .build();
        UserAppRoleType uart3 = UserAppRoleTypeBuilder.Builder.create()
                .withUserApplicationRoleId(702)
                .withApplicationRoleInfo(art)
                .build();
        UserAppRoleType uart4 = UserAppRoleTypeBuilder.Builder.create()
                .withUserApplicationRoleId(703)
                .withApplicationRoleInfo(art)
                .build();
        
        UserGroupType ugt = UserGroupTypeBuilder.Builder.create()
                .withGroupId(1)
                .withDescription("Group Description 1")
                .build();

        UserType ut = UserTypeBuilder.Builder.create()
                .withLoginId(777)
                .withGroupInfo(ugt)
                .withUsername("username")
                .withFirstname("roy")
                .withLastname("terrell")
                .withActiveFlag(true)
                .addGrantedAppRole(uart)
                .addGrantedAppRole(uart2)
                .addGrantedAppRole(uart3)
                .addGrantedAppRole(uart4)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();
       
        apgt.getUserInfo().add(ut);
        resp.setProfile(apgt);
        resp.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

 }
