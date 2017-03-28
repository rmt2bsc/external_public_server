package org.rmt2.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.rmt2.rest.accounting.AccountingEntityResource;
import org.rmt2.rest.addressbook.ContactProfileResource;
import org.rmt2.rest.media.DocumentMediaResource;

public class PublicGatewayServerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(AccountingEntityResource.class);
        classes.add(ContactProfileResource.class);
        classes.add(DocumentMediaResource.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<Object>();

        // TODO: Add singleton classes

        return singletons;
    }

}
