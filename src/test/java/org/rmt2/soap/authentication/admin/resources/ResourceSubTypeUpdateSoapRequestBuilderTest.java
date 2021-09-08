package org.rmt2.soap.authentication.admin.resources;

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
import org.rmt2.jaxb.ResourcesInfoType;
import org.rmt2.jaxb.ResourcesubtypeType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.authentication.ResourceSubtypeTypeBuilder;
import org.rmt2.util.authentication.ResourcesInfoTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ResourceSubTypeUpdateSoapRequestBuilderTest {

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
    public void testBuildRequest_New() {
        ObjectFactory fact = new ObjectFactory();
        AuthenticationRequest req = fact.createAuthenticationRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();
        ResourcesubtypeType rt = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(0)
                .withTypeId(77)
                .withName("resource sub type name")
                .withDescription("rsrouce sub type description")
                .build();

        ResourcesInfoType rit = ResourcesInfoTypeBuilder.Builder.create()
                .addResourceSubType(rt)
                .build();

        apgt.setResourcesInfo(rit);
        req.setProfile(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildRequest_Existing() {
        ObjectFactory fact = new ObjectFactory();
        AuthenticationRequest req = fact.createAuthenticationRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication(ApiHeaderNames.APP_NAME_AUTHENTICATION)
                .withModule("admin")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();
        ResourcesubtypeType rt = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(0)
                .withTypeId(77)
                .withName("resource sub type name")
                .withDescription("rsrouce sub type description")
                .build();

        ResourcesInfoType rit = ResourcesInfoTypeBuilder.Builder.create()
                .addResourceSubType(rt)
                .build();

        apgt.setResourcesInfo(rit);
        req.setProfile(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

 }
