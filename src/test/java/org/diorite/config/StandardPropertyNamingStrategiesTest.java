package org.diorite.config;

import junit.framework.TestCase;
import org.diorite.config.impl.naming.PropertyNameStrategy;
import org.diorite.config.impl.naming.StandardPropertyNamingStrategies;
import org.junit.Assert;

public class StandardPropertyNamingStrategiesTest extends TestCase
{
    public void testIdentityStrategy()
    {
        PropertyNameStrategy identity = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.IDENTITY);

        assertName(identity, "fieldName", "fieldName");
        assertName(identity, "_fieldName", "_fieldName");
        assertName(identity, "field_Name", "field_Name");
        assertName(identity, "fieldname", "fieldname");
        assertName(identity, "field__Name", "field__Name");
        assertName(identity, "longerFieldName", "longerFieldName");
        assertName(identity, "longerFieldNameURL", "longerFieldNameURL");
        assertName(identity, "_longerFieldName", "_longerFieldName");
        assertName(identity, "longerFieldName_URL", "longerFieldName_URL");
        assertName(identity, "longerURLFieldWithAName", "longerURLFieldWithAName");
    }

    public void testLowerCaseStrategy()
    {
        PropertyNameStrategy lowerCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.LOWER_CASE);
        assertName(lowerCase, "fieldName", "fieldname");
        assertName(lowerCase, "_fieldName", "_fieldname");
        assertName(lowerCase, "field_Name", "field_name");
        assertName(lowerCase, "fieldname", "fieldname");
        assertName(lowerCase, "field__Name", "field__name");
        assertName(lowerCase, "longerFieldName", "longerfieldname");
        assertName(lowerCase, "longerFieldNameURL", "longerfieldnameurl");
        assertName(lowerCase, "_longerFieldName", "_longerfieldname");
        assertName(lowerCase, "longerFieldName_URL", "longerfieldname_url");
        assertName(lowerCase, "longerURLFieldWithAName", "longerurlfieldwithaname");
    }

    public void testUpperCaseStrategy()
    {
        PropertyNameStrategy upperCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.UPPER_CASE);
        assertName(upperCase, "fieldName", "FIELDNAME");
        assertName(upperCase, "_fieldName", "_FIELDNAME");
        assertName(upperCase, "field_Name", "FIELD_NAME");
        assertName(upperCase, "fieldname", "FIELDNAME");
        assertName(upperCase, "field__Name", "FIELD__NAME");
        assertName(upperCase, "longerFieldName", "LONGERFIELDNAME");
        assertName(upperCase, "longerFieldNameURL", "LONGERFIELDNAMEURL");
        assertName(upperCase, "_longerFieldName", "_LONGERFIELDNAME");
        assertName(upperCase, "longerFieldName_URL", "LONGERFIELDNAME_URL");
        assertName(upperCase, "longerURLFieldWithAName", "LONGERURLFIELDWITHANAME");
    }

    public void testCamelCaseStrategy()
    {
        PropertyNameStrategy upperCamelCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.CAMEL_CASE);
        assertName(upperCamelCase, "fieldName", "fieldName");
        assertName(upperCamelCase, "_fieldName", "_fieldName");
        assertName(upperCamelCase, "field_Name", "fieldName");
        assertName(upperCamelCase, "fieldname", "fieldname");
        assertName(upperCamelCase, "field__Name", "fieldName");
        assertName(upperCamelCase, "longerFieldName", "longerFieldName");
        assertName(upperCamelCase, "longerFieldNameURL", "longerFieldNameURL");
        assertName(upperCamelCase, "_longerFieldName", "_longerFieldName");
        assertName(upperCamelCase, "longerFieldName_URL", "longerFieldNameURL");
        assertName(upperCamelCase, "longerURLFieldWithAName", "longerURLFieldWithAName");
        assertName(upperCamelCase, "URLIsFirstWord", "urlIsFirstWord");
    }

    public void testUpperCamelCaseStrategy()
    {
        PropertyNameStrategy camelCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.UPPER_CAMEL_CASE);
        assertName(camelCase, "fieldName", "FieldName");
        assertName(camelCase, "_fieldName", "_FieldName");
        assertName(camelCase, "field_Name", "FieldName");
        assertName(camelCase, "fieldname", "Fieldname");
        assertName(camelCase, "field__Name", "FieldName");
        assertName(camelCase, "longerFieldName", "LongerFieldName");
        assertName(camelCase, "longerFieldNameURL", "LongerFieldNameURL");
        assertName(camelCase, "_longerFieldName", "_LongerFieldName");
        assertName(camelCase, "longerFieldName_URL", "LongerFieldNameURL");
        assertName(camelCase, "longerURLFieldWithAName", "LongerURLFieldWithAName");
        assertName(camelCase, "URLIsFirstWord", "URLIsFirstWord");
    }

    public void testSnakeCaseStrategy()
    {
        PropertyNameStrategy snakeCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.SNAKE_CASE);
        assertName(snakeCase, "fieldName", "field_name");
        assertName(snakeCase, "_fieldName", "_field_name");
        assertName(snakeCase, "field_Name", "field_name");
        assertName(snakeCase, "fieldname", "fieldname");
        assertName(snakeCase, "field__Name", "field_name");
        assertName(snakeCase, "longerFieldName", "longer_field_name");
        assertName(snakeCase, "longerFieldNameURL", "longer_field_name_url");
        assertName(snakeCase, "_longerFieldName", "_longer_field_name");
        assertName(snakeCase, "longerFieldName_URL", "longer_field_name_url");
        assertName(snakeCase, "longerURLFieldWithAName", "longer_url_field_with_a_name");
        assertName(snakeCase, "URLIsFirstWord", "url_is_first_word");
    }

    public void testUpperSnakeCaseStrategy()
    {
        PropertyNameStrategy upperSnakeCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.UPPER_SNAKE_CASE);
        assertName(upperSnakeCase, "fieldName", "Field_Name");
        assertName(upperSnakeCase, "_fieldName", "_Field_Name");
        assertName(upperSnakeCase, "field_Name", "Field_Name");
        assertName(upperSnakeCase, "fieldname", "Fieldname");
        assertName(upperSnakeCase, "field__Name", "Field_Name");
        assertName(upperSnakeCase, "longerFieldName", "Longer_Field_Name");
        assertName(upperSnakeCase, "longerFieldNameURL", "Longer_Field_Name_URL");
        assertName(upperSnakeCase, "_longerFieldName", "_Longer_Field_Name");
        assertName(upperSnakeCase, "longerFieldName_URL", "Longer_Field_Name_URL");
        assertName(upperSnakeCase, "longerURLFieldWithAName", "Longer_URL_Field_With_A_Name");
        assertName(upperSnakeCase, "URLIsFirstWord", "URL_Is_First_Word");
    }

    public void testHyphenCaseStrategy()
    {
        PropertyNameStrategy hyphenCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.HYPHEN_CASE);
        assertName(hyphenCase, "fieldName", "field-name");
        assertName(hyphenCase, "_fieldName", "_field-name");
        assertName(hyphenCase, "field_Name", "field-name");
        assertName(hyphenCase, "fieldname", "fieldname");
        assertName(hyphenCase, "field__Name", "field-name");
        assertName(hyphenCase, "longerFieldName", "longer-field-name");
        assertName(hyphenCase, "longerFieldNameURL", "longer-field-name-url");
        assertName(hyphenCase, "_longerFieldName", "_longer-field-name");
        assertName(hyphenCase, "longerFieldName_URL", "longer-field-name-url");
        assertName(hyphenCase, "longerURLFieldWithAName", "longer-url-field-with-a-name");
        assertName(hyphenCase, "URLIsFirstWord", "url-is-first-word");
    }

    public void testUpperHyphenCaseStrategy()
    {
        PropertyNameStrategy upperHyphenCase = StandardPropertyNamingStrategies.byName(StandardPropertyNamingStrategies.UPPER_HYPHEN_CASE);
        assertName(upperHyphenCase, "fieldName", "Field-Name");
        assertName(upperHyphenCase, "_fieldName", "_Field-Name");
        assertName(upperHyphenCase, "field_Name", "Field-Name");
        assertName(upperHyphenCase, "fieldname", "Fieldname");
        assertName(upperHyphenCase, "field__Name", "Field-Name");
        assertName(upperHyphenCase, "longerFieldName", "Longer-Field-Name");
        assertName(upperHyphenCase, "longerFieldNameURL", "Longer-Field-Name-URL");
        assertName(upperHyphenCase, "_longerFieldName", "_Longer-Field-Name");
        assertName(upperHyphenCase, "longerFieldName_URL", "Longer-Field-Name-URL");
        assertName(upperHyphenCase, "longerURLFieldWithAName", "Longer-URL-Field-With-A-Name");
        assertName(upperHyphenCase, "URLIsFirstWord", "URL-Is-First-Word");
    }

    private void assertName(PropertyNameStrategy strategy, String str, String expected)
    {
        Assert.assertEquals("[given: `" + str + "`]", expected, strategy.applyStrategy(str));
    }
}
