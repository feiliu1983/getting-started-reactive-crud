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

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiOnItem;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.reactivestreams.Publisher;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class Fruit {
    public Long id;

    @NotBlank(message = "name is null")
    public String name;

    public Fruit() {
        // default constructor.
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Multi<Fruit> findAll(MySQLPool client) {
        Multi<Fruit> result = client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(Fruit::from);

        Multi<Fruit> fdas = f1(new Fruit())
            .toUni().invoke(airportPairEntity -> {
                if (airportPairEntity == null) {
                    throw new RuntimeException("error");
                }
            }).onItem().transform(item -> item.getName())
           .onItem().transformToMulti(item -> f2(item));

        // return result.collectItems().first().call(item -> f1(item));

        return result;

        //chain(item -> f1(item));


//        Uni<List<Fruit>> xxxxxx = result.collectItems().asList().invoke(airportPairEntity -> {
//            if (airportPairEntity.isEmpty()) {
//                System.out.println("fdsf");
//            }
//        });
//
//
//        xxxxxx.chain(airportPairEntity->Uni.createFrom().item(airportPairEntity.get(0).getFlightLegTypeCode()));


        // return result;

        //  String  name = "wi";

        //  StringUtils.isBlank("fd");

//        MultiOnItem<Row> result = client.query("SELECT id, name FROM fruits where name like " +
//            "'%$1%' ORDER BY name ASC").execute()
//            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set)).onItem();


//                MultiOnItem<Row> result = client.preparedQuery(
//                    "SELECT id, name FROM fruits where name like CONCAT('%',$1,'%') ORDER BY name ASC").execute(Tuple.of(name))
//            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set)).onItem();
//
//
//
//        return  result.transform(Fruit::print).onItem().transform(Fruit::from);
    }

    private static Multi<Fruit> f2(String item2) {
        return null;
    }

    private static Multi<Fruit> f1(Fruit item) {
        return null;
    }

    public static Uni<Fruit> findById(MySQLPool client, Long id) {
        Uni<Fruit> result = client.preparedQuery("SELECT id, name FROM fruits WHERE id = $1").execute(Tuple.of(id))
            .onItem().transform(RowSet::iterator)
            .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);


        return result;
    }


    public Uni<Long> save(MySQLPool client) {
//        return client.preparedQuery("INSERT INTO fruits (name) VALUES (?)").execute(Tuple.of(name))
////            .chain(
////                pgRowSet -> pgRowSet.iterator().next().getLong("id"));
//            .chain(rowRowSet ->
//                rowRowSet.rowCount() == 0 ? Uni.createFrom().nullItem() : rowRowSet.property(MySQLClient.LAST_INSERTED_ID));


        return client.preparedQuery("INSERT INTO fruits (name) VALUES (?)")
            .execute(Tuple.of(name)).onItem().transform(r -> r.property(MySQLClient.LAST_INSERTED_ID));

    }

//        public Uni<Long> save(PgPool client) {
//        return client.preparedQuery("INSERT INTO fruits (id, name) VALUES ($1,$2) RETURNING id").
//            execute(Tuple.of(123434L,name))
//            .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
//    }

    public Uni<Boolean> update(MySQLPool client) {
        return client.preparedQuery("UPDATE fruits SET name = $1 WHERE id = $2").execute(Tuple.of(name, id))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM fruits WHERE id = ?").execute(Tuple.of(id))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    private static Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

    private static Row print(Row row) {
        System.out.println(row.getLong("id"));
        System.out.println(row.getString("name"));
        return row;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
