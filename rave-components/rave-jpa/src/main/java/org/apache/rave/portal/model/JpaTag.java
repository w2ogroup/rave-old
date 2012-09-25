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
package org.apache.rave.portal.model;

import org.apache.rave.persistence.BasicEntity;
import org.apache.rave.portal.model.conversion.ConvertingListProxyFactory;
import org.apache.rave.portal.model.conversion.JpaWidgetTagConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group in the social database. The assumption in this object is that groups are
 * associated with individuals and are used by those individuals to manage people.
 */
@Entity
@Table(name = "tag")
@Access(AccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = JpaTag.GET_ALL, query = "select t from JpaTag t order by t.keyword asc"),
        @NamedQuery(name = JpaTag.COUNT_ALL, query = "select count(t) from JpaTag t "),
        @NamedQuery(name = JpaTag.FIND_BY_KEYWORD, query = "select t from JpaTag t where UPPER(t.keyword) = UPPER(:keyword)")
})

public class JpaTag implements BasicEntity, Tag {

    public static final String FIND_BY_KEYWORD = "findByKeyword";
    public static final String GET_ALL = "getAll";
    public static final String COUNT_ALL = "countAll";
    public static final String KEYWORD_PARAM = "keyword";

    @Autowired
    private JpaWidgetTagConverter jpaWidgetTagConverter;

    /**
     * The internal object ID used for references to this object. Should be generated by the
     * underlying storage mechanism
     */
    @Id
    @Column(name = "entity_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tagIdGenerator")
    @TableGenerator(name = "tagIdGenerator", table = "RAVE_PORTAL_SEQUENCES", pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT", pkColumnValue = "tag", allocationSize = 1, initialValue = 1)
    private Long entityId;

    @Basic
    @Column(name = "keyword", unique = true)
    private String keyword;

    public JpaTag() {

    }

    public JpaTag(String keyword) {
        this.keyword = keyword;
    }

    public JpaTag(Long entityId, String keyword) {
        this.entityId = entityId;
        this.keyword = keyword;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getId() {
        return entityId == null ? null : entityId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JpaTag tag = (JpaTag) o;

        if (entityId != null ? !entityId.equals(tag.entityId) : tag.entityId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return entityId != null ? entityId.hashCode() : 0;
    }
}
