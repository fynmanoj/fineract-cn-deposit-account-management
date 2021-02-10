/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.service.rest;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.deposit.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.Collection;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.CollectionResponse;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateCollectionCommand;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/collection")
public class CollectionRestController {
    private final Logger logger;
    private final CommandGateway commandGateway;

    public CollectionRestController(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                                    CommandGateway commandGateway) {
        this.logger = logger;
        this.commandGateway = commandGateway;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.INSTANCE_MANAGEMENT)
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CollectionResponse> create(@RequestBody @Valid final Collection collection)  throws Throwable{

        CommandCallback<CollectionResponse> result = commandGateway.process(new CreateCollectionCommand(collection),
                CollectionResponse.class);

        return ResponseEntity.ok(result.get());
    }
}
