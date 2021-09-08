package org.rmt2.soap.authentication.admin.user;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.UserGroupType;
import org.rmt2.jaxb.UserType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.authentication.UserGroupTypeBuilder;
import org.rmt2.util.authentication.UserTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class UserDeleteSoapRequestBuilderTest {

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

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_USER_DELETE)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();
        UserGroupType ugt = UserGroupTypeBuilder.Builder.create()
                .withGroupId(1)
                .withDescription("Group Description")
                .build();

        UserType ut = UserTypeBuilder.Builder.create()
                .withLoginId(777)
                .withGroupInfo(ugt)
                .withUsername("username")
                .withFirstname("roy")
                .withLastname("terrell")
                .withBirthDate(RMT2Date.stringToDate("1989-01-01"))
                .withSsn("999-99-9999")
                .withStartDate(RMT2Date.stringToDate("2015-01-01"))
                .withTermDate(RMT2Date.stringToDate("2020-01-01"))
                .withDescription("description")
                .withPassword("kfdsd8e3k3893kds9sds")
                .withTotalLogins(100)
                .withEmail("royterrell@hotmail.com")
                .withActiveFlag(true)
                .build();

        apgt.getUserInfo().add(ut);
        req.setProfile(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_DELETE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_USER_DELETE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_USER_DELETE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

 }
