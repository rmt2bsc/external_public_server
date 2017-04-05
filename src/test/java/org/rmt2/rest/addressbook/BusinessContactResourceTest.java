package org.rmt2.rest.addressbook;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AddressBookRequest;
import org.rmt2.jaxb.AddressBookResponse;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ContactDetailGroup;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.rest.BaseRestServiceTest;
import org.rmt2.rest.RMT2BaseRestResouce;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;

import com.api.messaging.webservice.router.MessageRoutingInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RMT2BaseRestResouce.class })
public class BusinessContactResourceTest extends BaseRestServiceTest {
    private static final int BUSINESS_ID_1 = 77777;
    private static final int BUSINESS_ID_2 = 88888;
    private static final int BUSINESS_ID_3 = 99999;
    private AddressBookResponse mockAllResponse;
    private AddressBookResponse mockSingleResponse;
    private AddressBookResponse mockCriteriaResponse;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.mockAllResponse = this.createBusinessContactFetchAllResponse();
        this.mockSingleResponse = this.createBusinessContactFetchSingleResponse();
        this.mockCriteriaResponse = this.createBusinessContactFetchCriteriaResponse();
    }

    @After
    public void tearDown() throws Exception {
    }

    private AddressBookResponse createBusinessContactFetchAllResponse() {
        ObjectFactory f = new ObjectFactory();
        AddressBookResponse mockResponse = f.createAddressBookResponse();
        HeaderType header = HeaderTypeBuilder.Builder.create().withApplication("contacts").withModule("profile")
                .withTransaction(ApiTransactionCodes.CONTACTS_BUSINESS_GET_ALL).withUserId("rterrell").build();
        mockResponse.setHeader(header);

        ContactDetailGroup contactGrp = f.createContactDetailGroup();

        // Contact #1
        ZipcodeType zipcode = ZipcodeTypeBuilder.Builder.create().withZipId(71106).withZipcode(71106)
                .withCity("Shreveport").withState("LA").withAreaCode("318").withCountyName("Caddo").build();
        AddressType address = AddressTypeBuilder.Builder.create().withAddrId(1234).withAddressLine1("84394 Allan Ave")
                .withBusinessId(BUSINESS_ID_1).withZipcode(zipcode).withPhoneMain("3186871234").build();
        BusinessTypeBuilder.Builder builder = BusinessTypeBuilder.Builder.create();
        BusinessType bus = builder.withBusinessId(BUSINESS_ID_1).withLongname("ABC Rentals").withContactFirstname("roy")
                .withContactLastname("terrell").withTaxId("777777777").withContactEmail("royterrell@gte.net")
                .withContactPhone("3189999999").withAddress(address).build();
        contactGrp.getBusinessContacts().add(bus);

        // Contact #2
        ZipcodeType zipcode2 = ZipcodeTypeBuilder.Builder.create().withZipId(71106).withZipcode(75232)
                .withCity("Dallas")
                .withState("TX").withAreaCode("214").withCountyName("Dallas").build();
        AddressType address2 = AddressTypeBuilder.Builder.create().withAddrId(2234).withAddressLine1("2343 Reynoldston")
                .withBusinessId(BUSINESS_ID_2).withZipcode(zipcode2).withPhoneMain("2146789483").build();
        BusinessTypeBuilder.Builder builder2 = BusinessTypeBuilder.Builder.create();
        BusinessType bus2 = builder2.withBusinessId(BUSINESS_ID_2).withLongname("XYZ Club")
                .withContactFirstname("terrance")
                .withContactLastname("williams").withTaxId("999999999").withContactEmail("xyz@gmail.com")
                .withContactPhone("9723455847").withAddress(address2).build();
        contactGrp.getBusinessContacts().add(bus2);

        // Contact #3
        ZipcodeType zipcode3 = ZipcodeTypeBuilder.Builder.create().withZipId(75028).withZipcode(75028)
                .withCity("Flower Mound").withState("TX").withAreaCode("972").withCountyName("Denton").build();
        AddressType address3 = AddressTypeBuilder.Builder.create().withAddrId(3334)
                .withAddressLine1("1079 W. Round Grove Rd.").withAddressLine2("Suite 300").withAddressLine3("P.O. 232")
                .withBusinessId(BUSINESS_ID_3).withZipcode(zipcode3).withPhoneMain("9725673213").build();
        BusinessTypeBuilder.Builder builder3 = BusinessTypeBuilder.Builder.create();
        BusinessType bus3 = builder3.withBusinessId(BUSINESS_ID_3).withLongname("Dairy Queen")
                .withContactFirstname("john").withContactLastname("holmes").withTaxId("123456783")
                .withContactEmail("johnholmes@gmail.com").withContactPhone("2145556666").withAddress(address3).build();
        contactGrp.getBusinessContacts().add(bus3);

        mockResponse.setProfile(contactGrp);
        return mockResponse;
    }

    private AddressBookResponse createBusinessContactFetchSingleResponse() {
        ObjectFactory f = new ObjectFactory();
        AddressBookResponse mockResponse = f.createAddressBookResponse();
        HeaderType header = HeaderTypeBuilder.Builder.create().withApplication("contacts").withModule("profile")
                .withTransaction(ApiTransactionCodes.CONTACTS_BUSINESS_GET).withUserId("jfoster").build();
        mockResponse.setHeader(header);

        ContactDetailGroup contactGrp = f.createContactDetailGroup();

        // Contact #1
        ZipcodeType zipcode = ZipcodeTypeBuilder.Builder.create().withZipId(71106).withZipcode(71106)
                .withCity("Shreveport").withState("LA").withAreaCode("318").withCountyName("Caddo").build();
        AddressType address = AddressTypeBuilder.Builder.create().withAddrId(1234).withAddressLine1("84394 Allan Ave")
                .withBusinessId(BUSINESS_ID_1).withZipcode(zipcode).withPhoneMain("3186871234").build();
        BusinessTypeBuilder.Builder builder = BusinessTypeBuilder.Builder.create();
        BusinessType bus = builder.withBusinessId(BUSINESS_ID_1).withLongname("ABC Rentals").withContactFirstname("roy")
                .withContactLastname("terrell").withTaxId("777777777").withContactEmail("royterrell@gte.net")
                .withContactPhone("3189999999").withAddress(address).build();
        contactGrp.getBusinessContacts().add(bus);

        mockResponse.setProfile(contactGrp);
        return mockResponse;
    }

    private AddressBookResponse createBusinessContactFetchCriteriaResponse() {
        ObjectFactory f = new ObjectFactory();
        AddressBookResponse mockResponse = f.createAddressBookResponse();
        HeaderType header = HeaderTypeBuilder.Builder.create().withApplication("contacts").withModule("profile")
                .withTransaction(ApiTransactionCodes.CONTACTS_BUSINESS_GET_CRITERIA).withUserId("jfoster").build();
        mockResponse.setHeader(header);

        ContactDetailGroup contactGrp = f.createContactDetailGroup();

        // Contact #1
        ZipcodeType zipcode = ZipcodeTypeBuilder.Builder.create().withZipId(71106).withZipcode(71106)
                .withCity("Shreveport").withState("LA").withAreaCode("318").withCountyName("Caddo").build();
        AddressType address = AddressTypeBuilder.Builder.create().withAddrId(1234).withAddressLine1("84394 Allan Ave")
                .withBusinessId(BUSINESS_ID_1).withZipcode(zipcode).withPhoneMain("3186871234").build();
        BusinessTypeBuilder.Builder builder = BusinessTypeBuilder.Builder.create();
        BusinessType bus = builder.withBusinessId(BUSINESS_ID_1).withLongname("ABC Rentals").withContactFirstname("roy")
                .withContactLastname("terrell").withTaxId("777777777").withContactEmail("royterrell@gte.net")
                .withContactPhone("3189999999").withAddress(address).build();
        contactGrp.getBusinessContacts().add(bus);

        // Contact #2
        ZipcodeType zipcode2 = ZipcodeTypeBuilder.Builder.create().withZipId(71106).withZipcode(71106)
                .withCity("Shreveport").withState("LA").withAreaCode("318").withCountyName("Caddo").build();
        AddressType address2 = AddressTypeBuilder.Builder.create().withAddrId(4563).withAddressLine1("3875 Masters Dr")
                .withBusinessId(BUSINESS_ID_2).withZipcode(zipcode2).withPhoneMain("31899955555").build();
        BusinessTypeBuilder.Builder builder2 = BusinessTypeBuilder.Builder.create();
        BusinessType bus2 = builder2.withBusinessId(BUSINESS_ID_2).withLongname("Big O's Seafood")
                .withContactFirstname("frank").withContactLastname("footer").withTaxId("555364732")
                .withContactEmail("frankfooter@gmail.com").withContactPhone("3183455847").withAddress(address2).build();
        contactGrp.getBusinessContacts().add(bus2);

        mockResponse.setProfile(contactGrp);
        return mockResponse;
    }


    @Test
    public void testGetAllBusinessContactsSuccess() {
        // MessageRoutingInfo routeInfo = this.msgRouterHelper
        // .getRoutingInfo(ApiTransactionCodes.CONTACTS_BUSINESS_GET_ALL);

        when(mockMsgRouterHelper.getRoutingInfo(ApiTransactionCodes.CONTACTS_BUSINESS_GET_ALL))
                .thenReturn(this.mockMessageRoutingInfo);
        when(mockMsgRouterHelper.routeJsonMessage(any(MessageRoutingInfo.class),
                any(AddressBookRequest.class))).thenReturn(this.mockAllResponse);
        // when(mockMsgRouterHelper.routeJsonMessage(eq(ApiTransactionCodes.CONTACTS_BUSINESS_GET_ALL),
        // any(AddressBookRequest.class))).thenReturn(this.mockAllResponse);

        ContactProfileResource srvc = new ContactProfileResource();
        Response resp = srvc.fetchBusinessContact();
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testGetSingleBusinessContactsSuccess() {
        // when(mockMsgRouterHelper.routeJsonMessage(eq(ApiTransactionCodes.CONTACTS_BUSINESS_GET),
        // any(AddressBookRequest.class))).thenReturn(this.mockSingleResponse);

        when(mockMsgRouterHelper.getRoutingInfo(ApiTransactionCodes.CONTACTS_BUSINESS_GET))
                .thenReturn(this.mockMessageRoutingInfo);
        when(mockMsgRouterHelper.routeJsonMessage(any(MessageRoutingInfo.class), any(AddressBookRequest.class)))
                .thenReturn(this.mockSingleResponse);
        ContactProfileResource srvc = new ContactProfileResource();
        Response resp = srvc.fetchBusinessContact(BUSINESS_ID_1);
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testGetBusinessContactsUsingCriteriaSuccess() {
        // when(mockMsgRouterHelper.routeJsonMessage(eq(ApiTransactionCodes.CONTACTS_BUSINESS_GET_CRITERIA),
        // any(AddressBookRequest.class))).thenReturn(this.mockCriteriaResponse);

        when(mockMsgRouterHelper.getRoutingInfo(ApiTransactionCodes.CONTACTS_BUSINESS_GET_CRITERIA))
                .thenReturn(this.mockMessageRoutingInfo);
        when(mockMsgRouterHelper.routeJsonMessage(any(MessageRoutingInfo.class), any(AddressBookRequest.class)))
                .thenReturn(this.mockCriteriaResponse);
        ContactProfileResource srvc = new ContactProfileResource();
        Response resp = srvc.fetchBusinessContact(null, null, null, null, null, null, null, null, null, null, null,
                "71106");
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }
}
