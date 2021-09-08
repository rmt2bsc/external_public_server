package org.rmt2.soap.authentication.admin.resources;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthCriteriaGroupType;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ResourceCriteriaType;
import org.rmt2.jaxb.ResourcesInfoType;
import org.rmt2.jaxb.ResourcesubtypeType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.authentication.ResourceSubtypeTypeBuilder;
import org.rmt2.util.authentication.ResourcesInfoTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ResourceSubTypeQuerySoapRequestBuilderTest {

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
                .withTransaction(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthCriteriaGroupType apgt = fact.createAuthCriteriaGroupType();
        ResourceCriteriaType criteria = fact.createResourceCriteriaType();
        criteria.setRsrcSubtypeId(1000);
        criteria.setRsrcTypeId(100);
        criteria.setRsrcSubtypeName("Resource Sub Type Name");
        criteria.setRsrcSubtypeDescription("Resource Type Description");

        apgt.setResourceCriteria(criteria);

        req.setCriteria(apgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
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
                .withTransaction(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET)
                .withRouting(ApiTransactionCodes.ROUTE_AUTHENTICATION)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .build();

        AuthProfileGroupType apgt = fact.createAuthProfileGroupType();
        ResourcesubtypeType rt1 = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(1000)
                .withTypeId(100)
                .withName("rsrouce sub type Name1")
                .withDescription("rsrouce sub type Description1")
                .build();

        ResourcesubtypeType rt2 = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(3000)
                .withTypeId(300)
                .withName("rsrouce sub type Name3")
                .withDescription("rsrouce sub type Description3")
                .build();

        ResourcesubtypeType rt3 = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(4000)
                .withTypeId(400)
                .withName("rsrouce sub type Name4")
                .withDescription("rsrouce sub type Description4")
                .build();

        ResourcesInfoType rit = ResourcesInfoTypeBuilder.Builder.create()
                .addResourceSubType(rt1)
                .addResourceSubType(rt2)
                .addResourceSubType(rt3)
                .build();

        apgt.setResourcesInfo(rit);
        resp.setProfile(apgt);
        resp.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

 }
