package org.rmt2.rest.media;

import java.math.BigInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.rest.RMT2BaseRestResouce;

import com.api.messaging.webservice.router.MessageRoutingException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/media/document")
public class DocumentMediaResource extends RMT2BaseRestResouce {
    private static final Logger LOGGER = Logger.getLogger(DocumentMediaResource.class);



    public DocumentMediaResource() {
        super("media", "document");
    }

    /**
     * Fetch the metadata and binary content by content id.
     * 
     * @param contentId
     *            the unique identifier of the content record. Must be greater
     *            than zero.
     * @return JSON containing metadata and base64 encoded binary content
     */
    @GET
    @Path("attachment/{contentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchImageContent(@PathParam("contentId") final long contentId) {
        LOGGER.info("REST method, fetchImageContent, was called");
        if (contentId <= 0) {
            this.msg = "Content Id must be greater than zero in order to retrieve image attachment";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Create multimedia request object with "contentId" param
        ObjectFactory f = new ObjectFactory();
        MultimediaRequest req = f.createMultimediaRequest();
        req.setHeader(this.getHeader());
        req.setContentId(BigInteger.valueOf(contentId));

        // Route message to business server
        MultimediaResponse r = null;

        try {
            Object response = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.MEDIA_GET_CONTENT, req);
            if (response != null && response instanceof MultimediaResponse) {
                r = (MultimediaResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error routing single attachment content fetch request", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }

    /**
     * Add the metadata and binary content.
     * 
     * @param content
     *            the metadata and binary content to be added. The binary
     *            content should be recieved as base64 encoded.
     * @return content metadata only in which a new content id should be
     *         provided.
     */
    @PUT
    @Path("attachment/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveImageContent(final MimeContentType content) {
        LOGGER.info("REST method, saveImageContent, was called");
        if (content == null) {
            this.msg = "Content object cannot be null";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }
        if (content.getContentId() == null) {
            this.msg = "Content id is required";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }
        if (content.getContentId().longValue() != 0) {
            this.msg = "Content id must be zero when adding content";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }
        try {
            Verifier.verifyNotEmpty(content.getBinaryData());
        }
        catch (VerifyException e) {
            this.msg = "Binary content is required";
            LOGGER.error(this.msg);
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Create multimedia request object with "content" param
        ObjectFactory f = new ObjectFactory();
        MultimediaRequest req = f.createMultimediaRequest();
        req.setHeader(this.getHeader());
        req.getContent().add(content);

        // Route message to business server
        MultimediaResponse r = null;
        try {
            Object response = this.msgRouterHelper.routeJsonMessage(ApiTransactionCodes.MEDIA_SAVE_CONTENT, req);
            if (response != null && response instanceof MultimediaResponse) {
                r = (MultimediaResponse) response;
            }
        } catch (MessageRoutingException e) {
            this.msg = e.getMessage();
            LOGGER.error("Server error for single attachment content save request", e);
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE).entity(this.msg).build());
        }

        // Exclude binary data for save. Only include metadata.
        r.getContent().setBinaryData(null);

        // Marshal to JSON
        final Gson gson = new GsonBuilder().create();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(r)).build();
    }
}
