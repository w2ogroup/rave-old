 -- Licensed to the Apache Software Foundation (ASF) under one
 -- or more contributor license agreements.  See the NOTICE file
 -- distributed with this work for additional information
 -- regarding copyright ownership.  The ASF licenses this file
 -- to you under the Apache License, Version 2.0 (the
 -- "License"); you may not use this file except in compliance
 -- with the License.  You may obtain a copy of the License at
  ~
 --   http://www.apache.org/licenses/LICENSE-2.0
  ~
 -- Unless required by applicable law or agreed to in writing,
 -- software distributed under the License is distributed on an
 -- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 -- KIND, either express or implied.  See the License for the
 -- specific language governing permissions and limitations
 -- under the License.
create table page
(
    id                   number(38)    not null,
    name                 varchar2(100) not null,
    owner_id             varchar2(50)  not null,
    page_layout_id       number(38)    not null,
    render_sequence      number(10)    not null,
    constraint page_pk
        primary key (id)
);

alter table page
add constraint page_person_id_fk
foreign key (owner_id)
references person (user_id);

alter table page
add constraint page_layout_id_fk
foreign key (page_layout_id)
references page_layout (id);
