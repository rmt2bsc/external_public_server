package org.rmt2.rest.accounting;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccounttypeType;
import org.rmt2.jaxb.GlDetailGroup;
import org.rmt2.jaxb.ObjectFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/accounting/entity")
public class AccountingEntityResource {
    private static final Logger LOGGER = Logger
            .getLogger(AccountingEntityResource.class);

    public AccountingEntityResource() {

    }

    @GET
    @Path("account-types-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchAccountTypes() {
        LOGGER.info("REST method, account-types-list, was called");
        ObjectFactory f = new ObjectFactory();
        AccountingGeneralLedgerResponse r = f
                .createAccountingGeneralLedgerResponse();
        GlAccounttypeType acctType = f.createGlAccounttypeType();
        GlDetailGroup g = f.createGlDetailGroup();
        g.getAccountType().add(acctType);
        // r.getProfile().add(g);
        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(gson.toJson(r)).build();
    }

}
