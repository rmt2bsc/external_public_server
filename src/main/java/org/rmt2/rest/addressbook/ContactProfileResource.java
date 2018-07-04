package org.rmt2.rest.addressbook;

import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AddressBookRequest;
import org.rmt2.jaxb.AddressBookResponse;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ContactCriteriaGroup;
import org.rmt2.jaxb.ContactDetailGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.rest.RMT2BaseRestResouce;
import org.rmt2.util.addressbook.BusinessTypeBuilder;

import com.api.messaging.webservice.router.MessageRoutingException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REST service for common, personal, and business contact profiles
 * 
 * @author Roy Terrell
 *
 */
@Path("/addressbook/profile")
public class ContactProfileResource extends RMT2BaseRestResouce {
    private static final Logger LOGGER = Logger.getLogger(ContactProfileResource.class);

    public ContactProfileResource() {
        super("addressbook", "profile");
    }

    /**
     * Fetch all business contacts.
     * 
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @GET
    @Path("business")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact() {
        LOGGER.info("REST method, fetchBusinessContact(), was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        AddressBookResponse r = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object response = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_GET, req);
            if (response != null && response instanceof AddressBookResponse) {
                r = (AddressBookResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error routing all business contact request to its destination", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }

    /**
     * Fetch a single business contact profile.
     * 
     * @param businessId
     *            the unique identifier of the business contact to fetch.
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @GET
    @Path("business/{businessId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact(@PathParam("businessId") long businessId) {
        LOGGER.info("REST method, fetchBusinessContact(businessId), was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        // Validations
        if (businessId <= 0) {
            this.msg = "Business Id must be greater than zero in order to retrieve business contact";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }

        // Setup business contact criteria
        BusinessContactCriteria criteria = f.createBusinessContactCriteria();
        criteria.setContactId(BigInteger.valueOf(businessId));
        ContactCriteriaGroup criteriaGrp = f.createContactCriteriaGroup();
        criteriaGrp.setBusinessCriteria(criteria);
        req.setCriteria(criteriaGrp);

        AddressBookResponse r = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object response = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_GET, req);
            if (response != null && response instanceof AddressBookResponse) {
                r = (AddressBookResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error routing single business contact request to its destination", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }

    /**
     * Fetch business contact(s) based on multiple selection criteria.
     * 
     * @param contactId
     *            the contact id
     * @param businessIdList
     *            a list of business id's
     * @param businessName
     *            the name of the business
     * @param entityType
     *            the id of the entity type
     * @param serviceType
     *            the id of the service type
     * @param taxId
     *            the tax id
     * @param contactFirstName
     *            the first name of the assoicated contact person
     * @param contactLastName
     *            the last name of the assoicated contact person
     * @param contactEmail
     *            the email address of the assoicated contact person
     * @param city
     *            the business contact's city
     * @param state
     *            city the business contact's state
     * @param zipcode
     *            city the business contact's zip code
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @GET
    @Path("business/criteria")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact(@QueryParam("contactId") final Long contactId,
            @QueryParam("businessIdList") List<BigInteger> businessIdList,
            @QueryParam("busienssName") String businessName, @QueryParam("entityType") Integer entityType,
            @QueryParam("serviceType") Integer serviceType, @QueryParam("taxId") String taxId,
            @QueryParam("contactFirstName") String contactFirstName,
            @QueryParam("contactLastName") String contactLastName, @QueryParam("contactEmail") String contactEmail,
            @QueryParam("city") String city, @QueryParam("state") String state, @QueryParam("zipcode") String zipcode) {
        LOGGER.info("REST method, fetchBusinessContact with selection criteria was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        // Setup business contact criteria. At least one criteria item is
        // required.
        BusinessContactCriteria criteria = f.createBusinessContactCriteria();
        int criteriaCount = 0;
        if (contactId != null) {
            criteria.setContactId(BigInteger.valueOf(contactId));
            criteriaCount++;
        }
        if (businessIdList != null && businessIdList.size() > 0) {
            criteria.getBusinessId().addAll(businessIdList);
            criteriaCount++;
        }
        if (businessName != null) {
            criteria.setBusinessName(businessName);
            criteriaCount++;
        }
        if (entityType != null) {
            criteria.setEntityType(BigInteger.valueOf(entityType));
            criteriaCount++;
        }
        if (serviceType != null) {
            criteria.setServiceType(BigInteger.valueOf(serviceType));
            criteriaCount++;
        }
        if (taxId != null) {
            criteria.setTaxId(taxId);
            criteriaCount++;
        }
        if (contactFirstName != null) {
            criteria.setContactFname(contactFirstName);
            criteriaCount++;
        }
        if (contactLastName != null) {
            criteria.setContactLname(contactLastName);
            criteriaCount++;
        }
        if (contactEmail != null) {
            criteria.setContactEmail(contactEmail);
            criteriaCount++;
        }
        if (city != null) {
            criteria.setCity(city);
            criteriaCount++;
        }
        if (state != null) {
            criteria.setState(state);
            criteriaCount++;
        }
        if (zipcode != null) {
            criteria.setZipcode(zipcode);
            criteriaCount++;
        }

        // Verify that at least one criteria item has been set.
        if (criteriaCount == 0) {
            this.msg = "At least one selection criteria item must be set in order to fetch Business Contact(s) with criteria";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }

        ContactCriteriaGroup criteriaGrp = f.createContactCriteriaGroup();
        criteriaGrp.setBusinessCriteria(criteria);
        req.setCriteria(criteriaGrp);

        AddressBookResponse respnose = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object responseMsg = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_GET, req);
            if (responseMsg != null && responseMsg instanceof AddressBookResponse) {
                respnose = (AddressBookResponse) responseMsg;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error for business contact request with selectin criteria", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(respnose)).build();
    }

    /**
     * Adds a buisness contact
     * 
     * @param profile
     *            an instance o f{@link BusinessType}
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @PUT
    @Path("business/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBusinessContact(final BusinessType profile) {
        LOGGER.info("REST method, addBusinessContact, was called");
        // Verify that business type object is valid.
        try {
            Verifier.verifyNotNull(profile);
        } catch (VerifyException e) {
            this.msg = "Unable to add business contact due to business contact data object is invalid or null";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }

        // Build request
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        ContactDetailGroup contactGrp = f.createContactDetailGroup();
        contactGrp.getBusinessContacts().add(profile);
        req.setProfile(contactGrp);

        AddressBookResponse respnose = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object responseMsg = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_UPDATE, req);
            if (responseMsg != null && responseMsg instanceof AddressBookResponse) {
                respnose = (AddressBookResponse) responseMsg;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error for business contact ADD request", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(respnose)).build();
    }

    /**
     * Updates an existing business contact
     * 
     * @param businessId
     *            the uinique identifier of the buisness contact
     * @param profile
     *            an instance of {@link BusinessType}
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @POST
    @Path("business/{businessId}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBusinessContact(@PathParam("businessId") long businessId, final BusinessType profile) {
        LOGGER.info("REST method, updateBusinessContact, was called");
        // verify business is greater than zero
        try {
            Verifier.verifyPositive(businessId);
        } catch (VerifyException e) {
            this.msg = "Unable to update business contact...business id is invalid [" + businessId + "]";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }
        // Verify that business type object is valid.
        try {
            Verifier.verifyNotNull(profile);
        } catch (VerifyException e) {
            this.msg = "Unable to update business contact...business contact data object is invalid or null";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }

        // Ensure that business id assoicated with "profile" is the same as the
        // path param, business id.
        profile.setBusinessId(BigInteger.valueOf(businessId));

        // Build request
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        ContactDetailGroup contactGrp = f.createContactDetailGroup();
        contactGrp.getBusinessContacts().add(profile);
        req.setProfile(contactGrp);

        AddressBookResponse respnose = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object responseMsg = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_UPDATE, req);
            if (responseMsg != null && responseMsg instanceof AddressBookResponse) {
                respnose = (AddressBookResponse) responseMsg;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error for business contact UPDATE request", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(respnose)).build();
    }

    /**
     * Deletes a business contact
     * 
     * @param businessId
     *            the unique of the business contact
     * @return {@link Response} in which the enclosed entity is of type
     *         {@link AddressBookResponse}
     */
    @DELETE
    @Path("business/{businessId}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBusinessContact(@PathParam("businessId") long businessId) {
        LOGGER.info("REST method, deleteBusinessContact, was called");
        // verify business is greater than zero
        try {
            Verifier.verifyPositive(businessId);
        } catch (VerifyException e) {
            this.msg = "Unable to delete business contact...business id is invalid [" + businessId + "]";
            LOGGER.error(this.msg);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(this.msg).build());
        }

        // Build request
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        req.setHeader(this.getHeader());

        BusinessType profile = BusinessTypeBuilder.Builder.create().withBusinessId(Long.valueOf(businessId).intValue())
                .build();
        ContactDetailGroup contactGrp = f.createContactDetailGroup();
        contactGrp.getBusinessContacts().add(profile);
        req.setProfile(contactGrp);

        AddressBookResponse respnose = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object responseMsg = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.CONTACTS_DELETE, req);
            if (responseMsg != null && responseMsg instanceof AddressBookResponse) {
                respnose = (AddressBookResponse) responseMsg;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error for business contact DELETE request", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(respnose)).build();
    }
}
