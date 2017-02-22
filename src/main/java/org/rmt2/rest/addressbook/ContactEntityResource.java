package org.rmt2.rest.addressbook;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.rmt2.jaxb.AddressBookResponse;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ContactDetailGroup;
import org.rmt2.jaxb.ObjectFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/contacts/entity")
public class ContactEntityResource {
    private static final Logger LOGGER = Logger
            .getLogger(ContactEntityResource.class);

    public ContactEntityResource() {

    }

    @GET
    @Path("business-type-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBusinessTypes() {
        LOGGER.info("REST method, business-type-list, was called");
        ObjectFactory f = new ObjectFactory();
        AddressBookResponse r = f.createAddressBookResponse();
        ContactDetailGroup cdg = f.createContactDetailGroup();
        BusinessType bus = f.createBusinessType();
        cdg.getBusinessContacts().add(bus);
        r.setProfile(cdg);
        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(gson.toJson(r)).build();
    }

}
