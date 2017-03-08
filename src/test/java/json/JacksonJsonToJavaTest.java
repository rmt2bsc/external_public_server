package json;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.After;
import org.junit.Assert;
import org.rmt2.jaxb.AddressBookRequest;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.ContactCriteriaGroup;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ZipcodeType;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.xml.jaxb.JaxbUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonJsonToJavaTest {

    // @Before
    // public void setUp() throws Exception {
    // SystemConfigurator config = new SystemConfigurator();
    // try {
    // config.start("/tmp/AppServer/config/RMT2AppServerConfig.xml");
    // } catch (Exception e) {
    // e.printStackTrace();
    // System.exit(1);
    // }
    // }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void testConvertingJaxbObject() {
        ObjectFactory f = new ObjectFactory();
        BusinessContactCriteria p = f.createBusinessContactCriteria();
        p.setContactId(BigInteger.valueOf(1234));
        p.setBusinessName("ABC Company");
        p.setMainPhone("99912394832");
        p.setContactFname("Roy");
        p.setContactLname("Terrell");
        p.setContactEmail("royroy@gte.net");
        p.setEntityType(BigInteger.valueOf(46));
        p.setServiceType(BigInteger.valueOf(6));

        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        // Convert object to string
        try {
            json = mapper.writeValueAsString(p);
            // json = mapper.writerWithDefaultPrettyPrinter()
            // .writeValueAsString(p);
            System.out.print(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert String to object
        Object obj = null;
        try {
            obj = mapper.readValue(json, BusinessContactCriteria.class);
            Assert.assertNotNull(obj);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert JAXB object to XML
        JaxbUtil util = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        AddressBookRequest r = f.createAddressBookRequest();

        HeaderType h = f.createHeaderType();
        h.setRouting("rmt2.queue.contacts");
        h.setApplication("contacts");
        h.setModule("business");
        h.setTransaction("FetchBusiness");
        h.setDeliveryMode("SYNC");
        h.setDeliveryDate("2016-02-25");
        h.setMessageMode("REQUEST");
        h.setSessionId("kfdksdiewkdkd9el2393w");
        h.setUserId("rterrell_test");

        ContactCriteriaGroup ccg = f.createContactCriteriaGroup();
        BusinessContactCriteria bcc = (BusinessContactCriteria) obj;
        ccg.setBusinessCriteria(bcc);
        r.setCriteria(ccg);
        r.setHeader(h);

        String xml = util.marshalMessage(r);
        Assert.assertNotNull(xml);
        System.out.print(xml);
    }

    // @Test
    public void testConvertComplexJaxbRequestObject() {
        BusinessType jaxbObj = this.createJaxbAddressBookBusinessContact();
        String json = null;
        ObjectMapper mapper = new ObjectMapper();
        // mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());

        try {
            json = mapper.writeValueAsString(jaxbObj);
        } catch (JsonGenerationException e1) {
            e1.printStackTrace();
            Assert.fail(e1.getMessage());
        } catch (JsonMappingException e1) {
            e1.printStackTrace();
            Assert.fail(e1.getMessage());
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail(e1.getMessage());
        }

        // Convert String to object
        Object obj = null;
        try {
            obj = mapper.readValue(json, BusinessType.class);
            Assert.assertNotNull(obj);
        } catch (JsonParseException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private BusinessType createJaxbAddressBookBusinessContact() {
        ObjectFactory f = new ObjectFactory();
        BusinessType bus = f.createBusinessType();
        bus.setBusinessId(BigInteger.valueOf(1234567));
        bus.setContactFirstname("Roy");
        bus.setContactLastname("Terrell");
        bus.setContactEmail("rterrell@gte.net");
        bus.setContactPhone("9726981234");
        bus.setContactExt("123");
        bus.setLongName("Test MDB Company");
        bus.setShortName("TMC");
        bus.setWebsite("www.royroy.com");
        bus.setTaxId(null);
        bus.setCategory(null);

        CodeDetailType cdtEntity = f.createCodeDetailType();
        cdtEntity.setCodeId(BigInteger.valueOf(26));
        bus.setEntityType(cdtEntity);

        CodeDetailType cdtServ = f.createCodeDetailType();
        cdtServ.setCodeId(BigInteger.valueOf(41));
        bus.setServiceType(cdtServ);

        AddressType addr = f.createAddressType();
        addr.setAddr1("9328 Hall Ave");
        addr.setPhoneMain("3189993339");

        ZipcodeType zip = f.createZipcodeType();
        zip.setZipcode(BigInteger.valueOf(75232));
        addr.setZip(zip);
        bus.setAddress(addr);

        return bus;
    }
}
