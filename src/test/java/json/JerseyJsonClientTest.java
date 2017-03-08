package json;

import java.math.BigInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.json.JSONConfiguration;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.Invocation;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.client.Client;
//import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.ObjectFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JerseyJsonClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void testPost() {
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

        try {
            // NOTE: Does not work well...
            // ClientConfig cc = new DefaultClientConfig();
            // cc.getProperties()
            // .put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            // // Add Jackson JSON serializer
            // cc.getClasses().add(JacksonJsonProvider.class);

            Client c = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
            // c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS,
            // true);
            // c.setConnectTimeout(3000);
            WebTarget srvc = c.target("http://localhost:8080/PublicServer/rest/jsonServices/send");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(p);
            Response response = srvc.request("application/json").post(Entity.entity(p, MediaType.APPLICATION_JSON));

            // ClientResponse response = webResource.accept("application/json")
            // .type("application/json").post(ClientResponse.class, p);

            // if (response.getStatus() != 200) {
            // throw new RuntimeException("Failed : HTTP error code : "
            // + response.getStatus() + ", Reason: "
            // + response.getResponseStatus().getReasonPhrase());
            // }
        } catch (Exception e) {

            e.printStackTrace();

        }
        // Client client = ClientBuilder.newClient(new ClientConfig()
        // .register(LoggingFilter.class));
        // WebTarget webTarget = client.target(
        // "http://localhost:8080/PublicServer/rest/jsonServices")
        // .path("send");
        //
        // Invocation.Builder invocationBuilder = webTarget
        // .request(MediaType.APPLICATION_JSON);
        // Response response = invocationBuilder.post(Entity.entity(p,
        // MediaType.APPLICATION_JSON));
        // System.out.println(response.getStatus());
        // System.out.println(response.getEntity().toString());

        // Client client = ClientBuilder.newClient();
        // String callResult = client
        // .target("http://localhost:8080/PublicServer/rest/jsonServices")
        // .path("send")
        // .request(MediaType.APPLICATION_XML)
        // .post(Entity.entity(p, MediaType.APPLICATION_XML),
        // String.class);
        //
        // System.out.println(callResult);

        // try {
        //
        // ClientConfig clientConfig = new DefaultClientConfig();
        //
        // clientConfig.getFeatures().put(
        // JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        //
        // Client client = Client.create(clientConfig);
        //
        // WebResource webResource = client
        // .resource("http://localhost:8080/PublicServer/rest/jsonServices/send");
        //
        // ClientResponse response = webResource
        // .accept("application/json").type("application/json")
        // .post(ClientResponse.class, p);
        //
        // if (response.getStatus() != 200) {
        // throw new RuntimeException("Failed : HTTP error code : "
        // + response.getStatus());
        // }
        //
        // String output = response.getEntity(String.class);
        //
        // System.out.println("Server response .... \n");
        // System.out.println(output);
        //
        // } catch (Exception e) {
        //
        // e.printStackTrace();
        //
        // }

    }
}
