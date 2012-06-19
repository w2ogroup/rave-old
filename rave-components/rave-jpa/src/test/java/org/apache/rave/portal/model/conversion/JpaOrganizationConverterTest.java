package org.apache.rave.portal.model.conversion;

import org.apache.rave.portal.model.JpaOrganization;
import org.apache.rave.portal.model.Organization;
import org.apache.rave.portal.model.impl.AddressImpl;
import org.apache.rave.portal.model.impl.OrganizationImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml", "classpath:test-dataContext.xml"})
public class JpaOrganizationConverterTest {

    @Autowired
    JpaOrganizationConverter converter;

    @Before
    public void setup() {

    }

    @Test
    public void testNoConversion() {
        JpaOrganization template = new JpaOrganization();
        assertThat(converter.convert(template), is(sameInstance(template)));
    }

    @Test
    public void nullConversion() {
        Organization template = null;
        assertThat(converter.convert(template), is(nullValue()));
    }


    @Test
    public void convertValid() {
        Organization template = new OrganizationImpl();
        template.setAddress(new AddressImpl("123 Sesame Street"));
        template.setDescription("TEST_A");
        template.setEndDate(new Date());
        template.setField("TEST_B");
        template.setName("TEST_C");
        template.setStartDate(new Date());
        template.setSubField("TEST_D");
        template.setTitle("TEST_E");
        template.setWebpage("TEST_F");
        template.setQualifier("TEST_G");
        template.setPrimary(true    );

        JpaOrganization jpaTemplate = converter.convert(template);

        assertThat(jpaTemplate, is(not(sameInstance(template))));
        assertThat(jpaTemplate, is(instanceOf(JpaOrganization.class)));
        assertThat(jpaTemplate.getAddress().getStreetAddress(), is(equalTo(template.getAddress().getStreetAddress())));
        assertThat(jpaTemplate.getDescription(), is(equalTo(template.getDescription())));
        assertThat(jpaTemplate.getEndDate(), is(equalTo(template.getEndDate())));
        assertThat(jpaTemplate.getField(), is(equalTo(template.getField())));
        assertThat(jpaTemplate.getName(), is(equalTo(template.getName())));
        assertThat(jpaTemplate.getStartDate(), is(equalTo(template.getStartDate())));
        assertThat(jpaTemplate.getSubField(), is(equalTo(template.getSubField())));
        assertThat(jpaTemplate.getTitle(), is(equalTo(template.getTitle())));
        assertThat(jpaTemplate.getWebpage(), is(equalTo(template.getWebpage())));
        assertThat(jpaTemplate.getQualifier(), is(equalTo(template.getQualifier())));
        assertThat(jpaTemplate.getPrimary(), is(equalTo(template.getPrimary())));
    }

}
