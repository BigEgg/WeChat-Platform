package com.thoughtworks.wechat_io.resources.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class WebApplicationNotAcceptableException extends WebApplicationException {
    public WebApplicationNotAcceptableException() {
        super(Response.Status.NOT_ACCEPTABLE);
    }
}
