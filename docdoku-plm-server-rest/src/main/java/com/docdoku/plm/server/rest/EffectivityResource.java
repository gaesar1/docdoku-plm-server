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

package com.docdoku.plm.server.rest;

import io.swagger.annotations.*;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;
import com.docdoku.plm.server.core.exceptions.*;
import com.docdoku.plm.server.core.product.*;
import com.docdoku.plm.server.core.security.UserGroupMapping;
import com.docdoku.plm.server.core.services.IEffectivityManagerLocal;
import com.docdoku.plm.server.core.services.IProductManagerLocal;
import com.docdoku.plm.server.rest.dto.EffectivityDTO;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Api(hidden = true, value = "effectivity", description = "Operations about effectivities",
        authorizations = {@Authorization(value = "authorization")})
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class EffectivityResource {

    @Inject
    private IEffectivityManagerLocal effectivityManager;

    @Inject
    private IProductManagerLocal productManager;

    private Mapper mapper;

    public EffectivityResource() {
    }

    @PostConstruct
    public void init() {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    @GET
    @Path("/{effectivityId}")
    @ApiOperation(value = "Get an effectivity from its ID", response = EffectivityDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of effectivity"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public EffectivityDTO getEffectivity(
            @ApiParam(required = true, value = "Workspace id") @PathParam("workspaceId") String workspaceId,
            @ApiParam(required = true, value = "Effectivity id") @PathParam("effectivityId") int effectivityId)
            throws EntityNotFoundException, UserNotActiveException, WorkspaceNotEnabledException {

        Effectivity effectivity = effectivityManager.getEffectivity(workspaceId, effectivityId);
        EffectivityDTO effectivityDTO = mapper.map(effectivity, EffectivityDTO.class);

        TypeEffectivity typeEffectivity = null;
        if (effectivity.getClass().equals(SerialNumberBasedEffectivity.class)) {
            typeEffectivity = TypeEffectivity.SERIALNUMBERBASEDEFFECTIVITY;
        } else if (effectivity.getClass().equals(DateBasedEffectivity.class)) {
            typeEffectivity = TypeEffectivity.DATEBASEDEFFECTIVITY;
        } else if (effectivity.getClass().equals(LotBasedEffectivity.class)) {
            typeEffectivity = TypeEffectivity.LOTBASEDEFFECTIVITY;
        }
        effectivityDTO.setTypeEffectivity(typeEffectivity);

        ConfigurationItem configurationItem = effectivity.getConfigurationItem();
        effectivityDTO.setConfigurationItemKey(configurationItem != null ? configurationItem.getKey() : null);

        return effectivityDTO;
    }

    @PUT
    @ApiOperation(value = "Update effectivity", response = EffectivityDTO.class)
    @Path("/{effectivityId}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of updated effectivity"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEffectivity(
            @ApiParam(required = true, value = "Workspace id") @PathParam("workspaceId") String workspaceId,
            @ApiParam(required = true, value = "Effectivity id") @PathParam("effectivityId") int effectivityId,
            @ApiParam(required = true, value = "Effectivity values to update") EffectivityDTO effectivityDTO)
            throws EntityNotFoundException, UpdateException, WorkspaceNotEnabledException, AccessRightException, CreationException {

        Effectivity effectivity;
        TypeEffectivity typeEffectivity = effectivityDTO.getTypeEffectivity();

        if (TypeEffectivity.SERIALNUMBERBASEDEFFECTIVITY.equals(typeEffectivity)) {
            effectivity = effectivityManager.updateSerialNumberBasedEffectivity(workspaceId, effectivityId,
                    effectivityDTO.getName(), effectivityDTO.getDescription(),
                    effectivityDTO.getStartNumber(), effectivityDTO.getEndNumber());
        } else if (TypeEffectivity.DATEBASEDEFFECTIVITY.equals(typeEffectivity))  {
            effectivity = effectivityManager.updateDateBasedEffectivity(workspaceId, effectivityId,
                    effectivityDTO.getName(), effectivityDTO.getDescription(),
                    effectivityDTO.getStartDate(), effectivityDTO.getEndDate());
        } else if (TypeEffectivity.LOTBASEDEFFECTIVITY.equals(typeEffectivity))  {
            effectivity = effectivityManager.updateLotBasedEffectivity(workspaceId, effectivityId,
                    effectivityDTO.getName(), effectivityDTO.getDescription(),
                    effectivityDTO.getStartLotId(), effectivityDTO.getEndLotId());
        } else {
            effectivity = effectivityManager.updateEffectivity(workspaceId, effectivityId,
                    effectivityDTO.getName(), effectivityDTO.getDescription());
        }

        EffectivityDTO returnedEffectivityDTO = mapper.map(effectivity, EffectivityDTO.class);
        returnedEffectivityDTO.setTypeEffectivity(typeEffectivity);

        ConfigurationItem configurationItem = effectivity.getConfigurationItem();
        returnedEffectivityDTO.setConfigurationItemKey(configurationItem != null ? configurationItem.getKey() : null);

        return Response.ok(returnedEffectivityDTO).build();
    }

}
