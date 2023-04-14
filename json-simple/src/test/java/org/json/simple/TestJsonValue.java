/*
 * $Id: Test.java,v 1.1 2006/04/15 14:40:06 platform Exp $
 * Created on 2006-4-15
 */
package org.json.simple;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;


public class TestJsonValue {
    @Test
    public void testParseWhenGivenValidJSONStringThenReturnsObjectAndDoesNotThrowAnyException() {
        class TestCase {
            final String rawJson;
            final Object expected;

            public TestCase(String rawJson, Object expected) {
                this.rawJson = rawJson;
                this.expected = expected;
            }
        }

        List<TestCase> testCases = Arrays.asList(
                // simple arrays
                new TestCase("[1, 2,30]", JSONArray.of(1L, 2L, 30L)),
                new TestCase("[\"a\",\"bbb\"]", JSONArray.of("a", "bbb")),
                new TestCase("[1.01,22.004]", JSONArray.of(1.01, 22.004)),

                // simple object
                new TestCase(
                        "{ \"a\": false,   \"bb\" : 20, \"ddd\": null }",
                        JSONObject.of("a", false, "bb", 20L, "ddd", null)),

                // nested object
                new TestCase(
                        "{ \"aa\": [1.0, 2.1, 3.14],   \"bb\" : { \"ccc\": \"text\", \"ddd\": [\"t1\", \"t2\"]} }",
                        JSONObject.of(
                                "aa", JSONArray.of(1.0, 2.1, 3.14),
                                "bb",
                                JSONObject.of(
                                        "ccc", "text",
                                        "ddd", JSONArray.of("t1", "t2")
                                )
                        )
                ),

                // empty and null values
                new TestCase("[]", new JSONArray()),
                new TestCase("{}", new JSONObject()),
                new TestCase("null", null),

                // single values
                new TestCase("100", 100L),
                new TestCase("\"abc\"", "abc"),
                new TestCase("10.10", 10.10),
                new TestCase("false", false),
                new TestCase("true", true)
        );

        for (final TestCase testCase : testCases) {
            Object got = JSONValue.parse(testCase.rawJson);
            assertThat(got).isEqualTo(testCase.expected);
            assertThatCode(() -> JSONValue.parseWithException(testCase.rawJson)).doesNotThrowAnyException();
            assertThatCode(() -> JSONValue.parseWithException(new StringReader(testCase.rawJson))).doesNotThrowAnyException();
        }
    }

    @Test
    public void testParseWhenGivenInvalidJSONStringThenReturnsNullAndThrowsException() {
        List<String> invalidJSONStrings = Arrays.asList(
                // empty
                "",

                // unclosed brackets
                "[1, [2]", "[",

                // unclosed braces
                "{ 1, {1:2}", "{",

                // unexpected characters
                "[1,,", "[}", "{1, 2, 3]",
                "1, test", "[1, hello]", "{1,2}"
        );

        for (final String invalidString : invalidJSONStrings) {
            assertThat(JSONValue.parse(invalidString)).isNull();
            assertThatThrownBy(() -> JSONValue.parseWithException(invalidString))
                    .isInstanceOf(ParseException.class)
                    .extracting(Throwable::getMessage)
                    .asString()
                    .isNotBlank();
            assertThatThrownBy(() -> JSONValue.parseWithException(new StringReader(invalidString)))
                    .isInstanceOf(ParseException.class)
                    .extracting(Throwable::getMessage)
                    .asString()
                    .isNotBlank();
        }
    }

    @Test
    public void testWriteJSONStringWhenGivenNonNullObjectThenWritesItCorrectly() throws IOException {
        class TestCase {
            final Object given;
            final String expected;

            public TestCase(String expected, Object given) {
                this.expected = expected;
                this.given = given;
            }
        }

        List<TestCase> testCases = Arrays.asList(
                // simple arrays
                new TestCase("[1,2,30]", JSONArray.of(1L, 2L, 30L)),
                new TestCase("[\"a\",\"bbb\"]", JSONArray.of("a", "bbb")),
                new TestCase("[1.01,22.004]", JSONArray.of(1.01, 22.004)),

                // simple object
                new TestCase(
                        "{\"bb\":20,\"a\":false,\"ddd\":null}",
                        JSONObject.of("bb", 20L, "a", false, "ddd", null)
                ),

                // nested object
                new TestCase(
                        "{\"aa\":[1.0,2.1,3.14],\"bb\":{\"ddd\":[\"t1\",\"t2\"],\"ccc\":\"text\"}}",
                        JSONObject.of(
                                "aa", JSONArray.of(1.0, 2.1, 3.14),
                                "bb",
                                JSONObject.of(
                                        "ccc", "text",
                                        "ddd", JSONArray.of("t1", "t2")
                                )
                        )
                ),

                // empty and null values
                new TestCase("[]", new JSONArray()),
                new TestCase("{}", new JSONObject()),
                new TestCase("null", null),

                // single values
                new TestCase("100", 100L),
                new TestCase("\"abc\"", "abc"),
                new TestCase("10.1", 10.1),
                new TestCase("false", false),
                new TestCase("true", true)
        );

        for (final TestCase testCase : testCases) {
            StringWriter writer = new StringWriter();
            JSONValue.writeJSONString(testCase.given, writer);
            String got = writer.toString();
            assertThat(got).isEqualTo(testCase.expected);
        }
    }
}
