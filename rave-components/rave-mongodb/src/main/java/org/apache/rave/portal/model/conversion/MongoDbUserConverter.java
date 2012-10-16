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

package org.apache.rave.portal.model.conversion;

import com.google.common.collect.Lists;
import org.apache.rave.portal.model.MongoDbUser;
import org.apache.rave.portal.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.rave.portal.model.util.MongoDbModelUtil.generateId;

@Component
public class MongoDbUserConverter implements HydratingModelConverter<User, MongoDbUser> {
    @Override
    public void hydrate(MongoDbUser dehydrated) {
        //NOOP
    }

    @Override
    public Class<User> getSourceType() {
        return User.class;
    }

    @Override
    public MongoDbUser convert(User source) {
        MongoDbUser user = source instanceof MongoDbUser ? ((MongoDbUser)source) : new MongoDbUser();
        List<String> authorityCodes = Lists.newArrayList();
        for(GrantedAuthority authority : source.getAuthorities()) {
            authorityCodes.add(authority.getAuthority());
        }
        updateProperties(source, user);
        return user;
    }

    private void updateProperties(User source, MongoDbUser converted) {
        converted.setId(source.getId() == null ? generateId() : source.getId());
        converted.setUsername(source.getUsername());
        converted.setEmail(source.getEmail());
        converted.setDisplayName(source.getDisplayName());
        converted.setAdditionalName(source.getUsername());
        converted.setFamilyName(source.getFamilyName());
        converted.setGivenName(source.getGivenName());
        converted.setHonorificPrefix(source.getHonorificPrefix());
        converted.setHonorificSuffix(source.getHonorificSuffix());
        converted.setPreferredName(source.getPreferredName());
        converted.setAboutMe(source.getAboutMe());
        converted.setStatus(source.getStatus());
        converted.setAddresses(source.getAddresses());
        converted.setOrganizations(source.getOrganizations());
        converted.setProperties(source.getProperties());
        converted.setPassword(source.getPassword());
        converted.setConfirmPassword(source.getConfirmPassword());
        converted.setDefaultPageLayout(source.getDefaultPageLayout());
        converted.setDefaultPageLayoutCode(source.getDefaultPageLayoutCode());
        converted.setEnabled(source.isEnabled());
        converted.setExpired(source.isExpired());
        converted.setLocked(source.isLocked());
        converted.setOpenId(source.getOpenId());
        converted.setForgotPasswordHash(source.getForgotPasswordHash());
        converted.setForgotPasswordTime(source.getForgotPasswordTime());
    }
}