package org.eclipse.persistence.json.bind.defaultmapping.performance;

import org.eclipse.persistence.json.bind.internal.JsonBindingBuilder;
import org.eclipse.persistence.json.bind.defaultmapping.anonymous.OuterPojo;
import org.eclipse.persistence.json.bind.defaultmapping.collections.Language;
import org.eclipse.persistence.json.bind.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.persistence.json.bind.defaultmapping.jsonp.JsonpLong;
import org.eclipse.persistence.json.bind.defaultmapping.jsonp.JsonpString;
import org.eclipse.persistence.json.bind.defaultmapping.specific.ObjectGraphTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.json.*;
import javax.json.bind.Jsonb;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by David Král on 18.11.2015.
 */
@State(Scope.Benchmark)
public class PerformanceTest {

    @Benchmark
    public void testObjectGraph() {
        ObjectGraphTest test = new ObjectGraphTest();
        test.testObjectFromJson();
        test.testObjectToJson();
    }

    @Benchmark
    public void testJsonMarshal() throws URISyntaxException, MalformedURLException, ParseException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        assertEquals("{\"id\":1,\"name\":\"pojoName\",\"anonymousField\":\"anonymousValue\"}", jsonb.toJson(new InnerPojo() {
            public String anonymousField = "anonymousValue";

            @Override
            public Integer getId() {
                return 1;
            }

            @Override
            public String getName() {
                return "pojoName";
            }
        }));

        assertEquals("{\"id\":1,\"anonymousField\":\"anonymousValue\"}", jsonb.toJson(new OuterPojo() {
            public String anonymousField = "anonymousValue";

            @Override
            public Integer getId() {
                return 1;
            }

            @Override
            public String getName() {
                return "pojoName";
            }
        }));

        assertEquals("\"some_string\"", jsonb.toJson("some_string"));
        assertEquals("\"\uFFFF\"", jsonb.toJson('\uFFFF'));
        assertEquals("1", jsonb.toJson((byte) 1));
        assertEquals("1", jsonb.toJson((short) 1));
        assertEquals("1", jsonb.toJson(1));
        assertEquals("5", jsonb.toJson(5L));
        assertEquals("1.2", jsonb.toJson(1.2f));
        assertEquals("1.2", jsonb.toJson(1.2));
        assertEquals("1", jsonb.toJson(new BigInteger("1")));
        assertEquals("1.2", jsonb.toJson(new BigDecimal("1.2")));
        assertEquals("1.2", jsonb.toJson(1.2));
        assertEquals("true", jsonb.toJson(true));
        assertEquals("false", jsonb.toJson(false));
        assertEquals("null", jsonb.toJson(null));

        assertEquals("\"NEGATIVE_INFINITY\"", jsonb.toJson(Double.NEGATIVE_INFINITY));
        assertEquals("\"POSITIVE_INFINITY\"", jsonb.toJson(Double.POSITIVE_INFINITY));
        assertEquals("\"NaN\"", jsonb.toJson(Double.NaN));
        assertEquals("\" \\ \" / \b \f \n \r \t 9\"", jsonb.toJson(" \\ \" / \b \f \n \r \t \u0039"));

        final Writer writer = new StringWriter();
        jsonb.toJson(5L, writer);
        assertEquals("5", writer.toString());


        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            jsonb.toJson(5L, baos);
            assertEquals("5", baos.toString("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final Collection<Integer> collection = Arrays.asList(1, 2, 3);
        assertEquals("[1,2,3]", jsonb.toJson(collection));

        final Map<String, Integer> map = new LinkedHashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        assertEquals("{\"1\":1,\"2\":2,\"3\":3}", jsonb.toJson(map));

        final Deque<String> deque = new ArrayDeque<>();
        deque.add("first");
        deque.add("second");
        assertEquals("[\"first\",\"second\"]", jsonb.toJson(deque));

        final Byte[] byteArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(byteArray));

        final Integer[] integerArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(integerArray));

        final String[] stringArray = {"first", "second", "third"};
        assertEquals("[\"first\",\"second\",\"third\"]", jsonb.toJson(stringArray));

        final byte[] bytePrimitivesArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(bytePrimitivesArray));

        final int[] intArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(intArray));

        final String[][] stringMultiArray = {{"first", "second"}, {"third", "fourth"}};
        assertEquals("[[\"first\",\"second\"],[\"third\",\"fourth\"]]", jsonb.toJson(stringMultiArray));

        final Map<String, Object>[][] mapMultiArray = new LinkedHashMap[2][2];
        mapMultiArray[0][0] = new LinkedHashMap<>(1);
        mapMultiArray[0][0].put("0", 0);
        mapMultiArray[0][1] = new LinkedHashMap<>(1);
        mapMultiArray[0][1].put("0", 1);
        mapMultiArray[1][0] = new LinkedHashMap<>(1);
        mapMultiArray[1][0].put("1", 0);
        mapMultiArray[1][1] = new LinkedHashMap<>(1);
        mapMultiArray[1][1].put("1", 1);

        assertEquals("[[{\"0\":0},{\"0\":1}],[{\"1\":0},{\"1\":1}]]", jsonb.toJson(mapMultiArray));

        final Language language = Language.Russian;
        assertEquals("\"Russian\"", jsonb.toJson(language));

        final EnumSet<Language> languageEnumSet = EnumSet.of(Language.Czech, Language.Slovak);

        String result = jsonb.toJson(languageEnumSet);
        assertTrue("[\"Czech\",\"Slovak\"]".equals(result) || "[\"Slovak\",\"Czech\"]".equals(result));

        final EnumMap<Language, String> languageEnumMap = new EnumMap<>(Language.class);
        languageEnumMap.put(Language.Russian, "ru");
        languageEnumMap.put(Language.English, "en");

        result = jsonb.toJson(languageEnumMap);
        assertTrue("{\"Russian\":\"ru\",\"English\":\"en\"}".equals(result) ||
                "{\"English\":\"en\",\"Russian\":\"ru\"}".equals(result));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date parsedDate = sdf.parse("04.03.2015");
        assertEquals("\"2015-03-04T00:00:00\"", jsonb.toJson(parsedDate));

        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        parsedDate = sdf.parse("04.03.2015 12:10:20");
        assertEquals("\"2015-03-04T12:10:20\"", jsonb.toJson(parsedDate));

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.clear();
        dateCalendar.set(2015, Calendar.APRIL, 3);

        assertEquals("\"2015-04-03\"", jsonb.toJson(dateCalendar));

        Calendar dateTimeCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        assertEquals("\"2015-04-03T00:00:00\"", jsonb.toJson(dateTimeCalendar));
        final Calendar dateGregorianCalendar = GregorianCalendar.getInstance();
        dateGregorianCalendar.clear();
        dateGregorianCalendar.set(2015, Calendar.APRIL, 3);

        assertEquals("\"2015-04-03\"", jsonb.toJson(dateGregorianCalendar));

        final Calendar dateTimeGregorianCalendar = new Calendar.Builder().setDate(2015, 3, 3).build();
        assertEquals("\"2015-04-03T00:00:00\"", jsonb.toJson(dateTimeGregorianCalendar));

        assertEquals("\"Europe/Prague\"", jsonb.toJson(TimeZone.getTimeZone("Europe/Prague")));
        assertEquals("\"Europe/Prague\"", jsonb.toJson(SimpleTimeZone.getTimeZone("Europe/Prague")));
        assertEquals("\"2015-03-03T23:00:00Z\"", jsonb.toJson(Instant.parse("2015-03-03T23:00:00Z")));
        assertEquals("\"PT5H4M\"", jsonb.toJson(Duration.ofHours(5).plusMinutes(4)));
        assertEquals("\"P10Y\"", jsonb.toJson(Period.between(LocalDate.of(1960, Month.JANUARY, 1), LocalDate.of(1970, Month.JANUARY, 1))));
        assertEquals("\"2013-08-10\"", jsonb.toJson(LocalDate.of(2013, Month.AUGUST, 10)));
        assertEquals("\"22:33:00\"", jsonb.toJson(LocalTime.of(22, 33)));
        assertEquals("\"2015-02-16T13:21:00\"", jsonb.toJson(LocalDateTime.of(2015, 2, 16, 13, 21)));
        assertEquals("\"2015-02-16T13:21:00+01:00[Europe/Prague]\"",
                jsonb.toJson(ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneId.of("Europe/Prague"))));
        assertEquals("\"Europe/Prague\"", jsonb.toJson(ZoneId.of("Europe/Prague")));
        assertEquals("\"+02:00\"", jsonb.toJson(ZoneOffset.of("+02:00")));
        assertEquals("\"2015-02-16T13:21:00+02:00\"",
                jsonb.toJson(OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+02:00"))));
        assertEquals("\"13:21:15.000000016+02:00\"", jsonb.toJson(OffsetTime.of(13, 21, 15, 16, ZoneOffset.of("+02:00"))));

        final GenericTestClass<String, Integer> myGenericClassField = new GenericTestClass<>();
        myGenericClassField.field1 = "value1";
        myGenericClassField.field2 = 3;

        assertEquals("{\"field1\":\"value1\",\"field2\":3}", jsonb.toJson(myGenericClassField));

        final MyCyclicGenericClass<CyclicSubClass> myCyclicGenericClass = new MyCyclicGenericClass<>();
        final CyclicSubClass cyclicSubClass = new CyclicSubClass();
        cyclicSubClass.subField = "subFieldValue";
        myCyclicGenericClass.field1 = cyclicSubClass;

        assertEquals("{\"field1\":{\"subField\":\"subFieldValue\"}}", jsonb.toJson(myCyclicGenericClass));

        List<Optional<String>> expected = Arrays.asList(Optional.empty(), Optional.ofNullable("first"), Optional.of("second"));
        String json = jsonb.toJson(expected);
        assertEquals("[null,\"first\",\"second\"]", json);

        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("name", "home")
                .add("city", "Prague")
                .build();

        assertEquals("{\"name\":\"home\",\"city\":\"Prague\"}", jsonb.toJson(jsonObject));

        final JsonArray jsonArray = factory.createArrayBuilder()
                .add(1)
                .add(2)
                .build();

        assertEquals("[1,2]", jsonb.toJson(jsonArray));

        assertEquals("true", jsonb.toJson(JsonValue.TRUE));
        assertEquals("10", jsonb.toJson(new JsonpLong(10)));
        assertEquals("\"hello\"", jsonb.toJson(new JsonpString("hello")));

        assertEquals("null", jsonb.toJson(OptionalInt.empty()));
        assertEquals("null", jsonb.toJson(OptionalLong.empty()));
        assertEquals("null", jsonb.toJson(OptionalDouble.empty()));
        assertEquals("10", jsonb.toJson(OptionalInt.of(10)));
        assertEquals("100", jsonb.toJson(OptionalLong.of(100L)));
        assertEquals("10.0", jsonb.toJson(OptionalDouble.of(10.0D)));

        assertEquals("null", jsonb.toJson(Optional.empty()));
        assertEquals("{\"id\":1,\"name\":\"Cust1\"}", jsonb.toJson(Optional.of(new Customer(1, "Cust1"))));

        OptionalInt[] arrayInt = {OptionalInt.of(1), OptionalInt.of(2), OptionalInt.empty()};
        assertEquals("[1,2,null]", jsonb.toJson(arrayInt));

        final Optional[] array = {Optional.of(new Customer(1, "Cust1")), Optional.of(new Customer(2, "Cust2")), Optional.empty()};
        assertEquals("[{\"id\":1,\"name\":\"Cust1\"},{\"id\":2,\"name\":\"Cust2\"},null]", jsonb.toJson(array));

        assertEquals("100", jsonb.toJson(BigDecimal.valueOf(100L)));
        assertEquals("100.1", jsonb.toJson(BigDecimal.valueOf(100.1D)));
        assertEquals("100", jsonb.toJson(BigInteger.valueOf(100)));

        assertEquals("\"http://www.oracle.com\"", jsonb.toJson(new URI("http://www.oracle.com")));
        assertEquals("\"http://www.oracle.com\"", jsonb.toJson(new URL("http://www.oracle.com")));
    }

    private static class InnerPojo {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class MyCyclicGenericClass<T extends MyCyclicGenericClass<? extends T>> {
        public T field1;

        public MyCyclicGenericClass() {
        }
    }

    static class CyclicSubClass extends MyCyclicGenericClass<CyclicSubClass> {
        public String subField;

        public CyclicSubClass() {
        }
    }

    private static class Customer {
        private int id;
        private String name;

        public Customer() {
        }

        public Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
