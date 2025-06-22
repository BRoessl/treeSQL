package io.broessl.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestWithJsonData {

	protected static final ObjectMapper OM = new ObjectMapper();

	protected static final String PATTERN_DATA_TREE = """
			{
			  "X": {"A": {"X": "xax"}},
			  "Y": {"Y": {"A": "yya"}},
			  "C": {"C": {"C": "ccc"}}
			}
						""";

	protected static final String SIMPLE_DATA_TREE = """
			{
			  "foo": ["bar", "baz"],
			  "highly": {
			    "nested": {
			    "objects": true
			    }
			  }
			}
						""";

	protected static final String SIMPLE_ARRAY = """
			[
			    {
			        "A": 1
			    },
			    {
			        "B": 2
			    },
			    {
			        "C": 3
			    }
			]
			""";

	protected static final String TEN_INTEGERS_ARRAY = """
			[0,1,2,3,4,5,6,7,8,9]
			""";

	public static JsonNode testDataSimpleDataTree() {
		return readSafeConstantValue(SIMPLE_DATA_TREE);
	}

	public static JsonNode testDataSimpleDataArray() {
		return readSafeConstantValue(SIMPLE_ARRAY);
	}

	public static JsonNode testDataArrayWithTenIntegers() {
		return readSafeConstantValue(TEN_INTEGERS_ARRAY);
	}

	public static JsonNode testDataPattern() {
		return readSafeConstantValue(PATTERN_DATA_TREE);
	}

	private static JsonNode readSafeConstantValue(String value) {
		try {
			return OM.readTree(value);
		} catch (Exception e) {
			// static data
		}
		throw new IllegalStateException("No valid JSON: " + value);
	}

}
