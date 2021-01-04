/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.acme.reactive.crud;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
//import io.vertx.mutiny.sqlclient.

@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {


    @Inject
    Validator validator;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

//    @Inject
//    PgPool client;
    @Inject
    MySQLPool client;

    @PostConstruct
    void config() {
        if (schemaCreate) {
            initdb();
        }
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS fruits").execute()
            .flatMap(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)").execute())
            .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Kiwi')").execute())
            .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Durian')").execute())
            .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Pomelo')").execute())
            .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Lychee')").execute())
            .await().indefinitely();
    }

//    @GET
//    @Valid
//    @Path("/{count}/{name}")
//    public Multi<Fruit> get(@PathParam int count, @PathParam String name) {
//        Fruit faf = new Fruit(1233L, "");
//        Set<ConstraintViolation<Fruit>> fdsa = new HashSet<>();
//        try {
//            fdsa = validator.validate(faf);
//            if (!fdsa.isEmpty()) {
//                System.out.println("");
//            }
//        } catch (Exception e) {
//            if (fdsa.isEmpty()) {
//                System.out.println("");
//            } else {
//                System.out.println("");
//            }
//        }
//
//        return Fruit.findAll(client).onItem().transform(item -> myvalidate(item, validator));
//    }

    @GET
    @Valid
    public Multi<Fruit> get(@QueryParam("limit") @DefaultValue("20") int limit,
                            @QueryParam("0") String name) {
        Fruit faf = new Fruit(1233L, "");
        Set<ConstraintViolation<Fruit>> fdsa = new HashSet<>();
        try {
            fdsa = validator.validate(faf);
            if (!fdsa.isEmpty()) {
                System.out.println("");
            }
        } catch (Exception e) {
            if (fdsa.isEmpty()) {
                System.out.println("");
            } else {
                System.out.println("");
            }
        }


        Multi<Fruit> f = Multi.createFrom().item(new Fruit(111L,"name"));


       return Fruit.findAll(client).onItem().transform(item -> myvalidate(item, validator));
    }




    @GET
    @Valid
    public Multi<Fruit> get() {
        Fruit faf = new Fruit(1233L, "");
        Set<ConstraintViolation<Fruit>> fdsa = new HashSet<>();
        try {
            fdsa = validator.validate(faf);
            if (!fdsa.isEmpty()) {
                System.out.println("");
            }
        } catch (Exception e) {
            if (fdsa.isEmpty()) {
                System.out.println("");
            } else {
                System.out.println("");
            }
        }

        return Fruit.findAll(client).onItem().transform(item -> myvalidate(item, validator));

    }

    private Fruit myvalidate(Fruit item, Validator validator) {
        Set<ConstraintViolation<Fruit>> set = validator.validate(item);
        if (!set.isEmpty()) {
            set.forEach(i -> System.out.println(i.getMessage()) );
        }
        return item;
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(@PathParam Long id) {
        return Fruit.findById(client, id)
            .onItem().transform(fruit -> fruit != null ? Response.ok(fruit) : Response.status(Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(Fruit fruit) {
        return fruit.save(client)
            .onItem().transform(id -> URI.create("/fruits/" + id))
            .onItem().transform(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam Long id, Fruit fruit) {
        return fruit.update(client)
            .onItem().transform(updated -> updated ? Status.OK : Status.NOT_FOUND)
            .onItem().transform(status -> Response.status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam Long id) {
        return Fruit.delete(client, id)
            .onItem().transform(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
            .onItem().transform(status -> Response.status(status).build());
    }
}
