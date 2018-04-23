package org.rmt2.rest;

import java.math.BigInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.rmt2.jaxbtest.AddressBookRequest;
import org.rmt2.jaxbtest.BusinessContactCriteria;
import org.rmt2.jaxbtest.ContactCriteriaGroup;
import org.rmt2.jaxbtest.HeaderType;
import org.rmt2.jaxbtest.ObjectFactory;

@Path("/jsonServices")
public class JerseyRestService {

    public JerseyRestService() {

    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public String consumeJSON(BusinessContactCriteria criteria) {
        String results = "Business name: " + criteria.getBusinessName();
        System.out.println(results);
        return results;
        // return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public BusinessContactCriteria fetchJSON() {
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
        return p;
    }

    @GET
    @Path("/get/{application}/{module}/{transaction}")
    @Produces(MediaType.APPLICATION_JSON)
    public AddressBookRequest fetchJSON(@PathParam("application") String app,
            @PathParam("module") String mod,
            @PathParam("transaction") String trans) {
        ObjectFactory f = new ObjectFactory();
        AddressBookRequest r = f.createAddressBookRequest();

        HeaderType h = f.createHeaderType();
        h.setRouting("rmt2.queue.contacts");
        h.setApplication(app);
        h.setModule(mod);
        h.setTransaction(trans);
        h.setDeliveryMode("SYNC");
        h.setDeliveryDate("2016-02-25");
        h.setMessageMode("REQUEST");
        h.setSessionId("kfdksdiewkdkd9el2393w");
        h.setUserId("rterrell_test");

        BusinessContactCriteria criteria = f.createBusinessContactCriteria();
        criteria.setContactId(BigInteger.valueOf(1234));
        criteria.setBusinessName("ABC Company");
        criteria.setMainPhone("99912394832");
        criteria.setContactFname("Roy");
        criteria.setContactLname("Terrell");
        criteria.setContactEmail("royroy@gte.net");
        criteria.setEntityType(BigInteger.valueOf(46));
        criteria.setServiceType(BigInteger.valueOf(6));

        ContactCriteriaGroup ccg = f.createContactCriteriaGroup();
        ccg.setBusinessCriteria(criteria);
        r.setCriteria(ccg);
        r.setHeader(h);
        return r;
    }
}
