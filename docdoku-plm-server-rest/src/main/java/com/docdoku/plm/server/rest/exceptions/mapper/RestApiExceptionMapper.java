/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2020 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.docdoku.plm.server.rest.exceptions.mapper;

import com.docdoku.plm.server.rest.exceptions.RestApiException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Taylor LABEJOF
 */
@Provider
public class RestApiExceptionMapper implements ExceptionMapper<RestApiException> {
    private static final Logger LOGGER = Logger.getLogger(RestApiExceptionMapper.class.getName());

    public RestApiExceptionMapper() {
    }

    @Override
    public Response toResponse(RestApiException e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
        LOGGER.log(Level.FINE, null, e);
        return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }
}
