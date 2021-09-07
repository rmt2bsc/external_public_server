package org.rmt2.soap.accounting.inventory;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.InventoryDetailGroup;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryItemtypeType;
import org.rmt2.jaxb.InventoryRequest;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ItemMasterUpdateRequestBuilderTest {

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
    public void testRequest() {
        ObjectFactory fact = new ObjectFactory();
        InventoryRequest req = fact.createInventoryRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("accounting")
                .withModule("inventory")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        

        InventoryItemType item = InventoryItemTypeBuilder.Builder.create()
                .withItemId(100)
                .withActive(true)
                .withItemName("Dell Computer")
                .withItemSerialNo("11111111")
                .withMarkup(3)
                .withUnitCost(150.99)
                .withQtyOnHand(10)
                .withVendorItemNo("1234-4839").build();
        
        InventoryItemtypeType iit = fact.createInventoryItemtypeType();
        iit.setItemTypeId(BigInteger.valueOf(222));
        item.setItemType(iit);
        
        CreditorType cred = fact.createCreditorType();
        cred.setCreditorId(BigInteger.valueOf(1234567));
        item.setCreditor(cred);
        
        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated("2018-01-01 10:10:44").build();
        
        item.setTracking(tracking);
        InventoryDetailGroup detailGroup = fact.createInventoryDetailGroup();
        detailGroup.getInvItem().add(item);
        req.setProfile(detailGroup);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);

        soapXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(soapXml);
        System.out.println(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE));
    }
    
}
