package org.rmt2.rest.addressbook;

import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.rmt2.constants.TransId;
import org.rmt2.jaxb.AddressBookRequest;
import org.rmt2.jaxb.AddressBookResponse;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.ContactCriteriaGroup;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.rest.RMT2BaseRestResouce;

import com.api.messaging.MessageRoutingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/contacts/entity")
public class ContactEntityResource extends RMT2BaseRestResouce {
    private static final Logger LOGGER = Logger
            .getLogger(ContactEntityResource.class);


    public ContactEntityResource() {
        super("contacts", "entity");
    }

    @GET
    @Path("business")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact() {
        LOGGER.info("REST method, fetchBusinessContact(), was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        this.getHeader().setTransaction(TransId.CONTACTS_BUSINESS_GET_ALL);
        req.setHeader(this.getHeader());

        AddressBookResponse r = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object response = this.msgRouterHelper.routeJsonMessage(TransId.CONTACTS_BUSINESS_GET_ALL, req);
            if (response != null && response instanceof MultimediaResponse) {
                r = (AddressBookResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error(
"Unable to route /contacts/entity//business to its destination",
                    e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }

    @GET
    @Path("business/{businessId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact(@PathParam("businessId") long businessId) {
        LOGGER.info("REST method, fetchBusinessContact(businessId), was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        this.getHeader().setTransaction(TransId.CONTACTS_BUSINESS_GET);
        req.setHeader(this.getHeader());

        // Validations
        if (businessId <= 0) {
            this.msg = "Business Id must be greater than zero in order to retrieve business contact";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
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
            Object response = this.msgRouterHelper.routeJsonMessage(TransId.CONTACTS_BUSINESS_GET, req);
            if (response != null && response instanceof MultimediaResponse) {
                r = (AddressBookResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Unable to route /contacts/entity/business/" + businessId + " to its destination", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }

    @GET
    @Path("business/criteria")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessContact(@QueryParam("contactId") final Long contactId,
            @QueryParam("businessIdList") List<BigInteger> businessIdList,
            @QueryParam("busienssName") String businessName,
            @QueryParam("entityType") Integer entityType, @QueryParam("serviceType") Integer serviceType,
            @QueryParam("taxId") String taxId, @QueryParam("contactFirstName") String contactFirstName,
            @QueryParam("contactLastName") String contactLastName, @QueryParam("contactEmail") String contactEmail,
            @QueryParam("city") String city, @QueryParam("state") String state, @QueryParam("zipcode") String zipcode) {
        LOGGER.info("REST method, fetchBusinessContact(usinessContactCriteria), was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest req = f.createAddressBookRequest();
        this.getHeader().setTransaction(TransId.CONTACTS_BUSINESS_GET_CRITERIA);
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
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        ContactCriteriaGroup criteriaGrp = f.createContactCriteriaGroup();
        criteriaGrp.setBusinessCriteria(criteria);
        req.setCriteria(criteriaGrp);

        AddressBookResponse respnose = f.createAddressBookResponse();
        // Route message to business server
        try {
            Object responseMsg = this.msgRouterHelper.routeJsonMessage(TransId.CONTACTS_BUSINESS_GET_CRITERIA, req);
            if (responseMsg != null && responseMsg instanceof MultimediaResponse) {
                respnose = (AddressBookResponse) responseMsg;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Unable to route /contacts/entity/business/criteria to its destination", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(respnose)).build();
    }
}
