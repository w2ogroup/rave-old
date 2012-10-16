/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.rave.portal.repository.impl;

import org.apache.rave.portal.model.PageTemplate;
import org.apache.rave.portal.model.PageType;
import org.apache.rave.portal.model.conversion.MongoDbConverter;
import org.apache.rave.portal.model.impl.PageTemplateImpl;
import org.apache.rave.portal.repository.PageTemplateRepository;
import org.apache.rave.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 */
public class MongoDbPageTemplateRepository implements PageTemplateRepository {
    public static final String COLLECTION = "pageTemplate";

    @Autowired
    private MongoDbConverter converter;

    @Autowired
    private MongoOperations template;

    @Override
    public List<PageTemplate> getAll() {
        List<PageTemplateImpl> templates = template.findAll(PageTemplateImpl.class, COLLECTION);
        for(PageTemplateImpl temp : templates) {
            converter.hydrate(temp, PageTemplate.class);
        }
        return CollectionUtils.<PageTemplate>toBaseTypedList(templates);
    }

    @Override
    public PageTemplate getDefaultPage(PageType pageType) {
        PageTemplate temp = template.findOne(new Query(where("pageType").is(pageType).andOperator(where("defaultTemplate").is(true))), PageTemplateImpl.class, COLLECTION);
        converter.hydrate(temp, PageTemplate.class);
        return temp;
    }
}