package com.gregashby.challenge.db;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

public class DbConnectionTest {

	@Test
	public void test() throws Exception {
		String dbUrl = System.getenv("JDBC_DATABASE_URL");
	   
		try(Connection connection = DriverManager.getConnection(dbUrl)){
			assertTrue(true);
		};
	   
	    
	}

}
