package com.gregashby.challenge.json;

import static org.junit.Assert.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonTransformerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMapTransformation() throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "true");
		map.put("accountIdentifier", String.valueOf(123));
		String json = new JsonTransformer().render(map);
		assertEquals("{\"success\":\"true\",\"accountIdentifier\":\"123\"}", json);
		
	}

}
