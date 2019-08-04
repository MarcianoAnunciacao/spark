package com.jobreadyprogrammer.spark;

import static org.apache.spark.sql.functions.concat;
import static org.apache.spark.sql.functions.lit;

import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

public class Application {
	
	public static void main(String args[]) throws InterruptedException {
		
		// Create a session
		SparkSession spark = new SparkSession.Builder()
				.appName("CSV to DB")
				.master("local")
				.getOrCreate();

		// get data(Immutable Structure)
		Dataset<Row> df = spark.read().format("csv")
			.option("header", true)
			.load("src/main/resources/name_and_comments.txt");
		
		//df.show(3);
		
		// Transformation
		df = df.withColumn("full_name", 
				concat(df.col("last_name"), lit(", "), df.col("first_name")))
				.filter(df.col("comment").rlike("\\d+"));

		df.show();

		// Write to destination
		String dbConnectionUrl = "????"; // <<- You need to create this database
		Properties prop = new Properties();
	    prop.setProperty("driver", "org.postgresql.Driver");
	    prop.setProperty("user", "???");
	    prop.setProperty("password", "???"); // <- The password you used while installing Postgres



	    df.write()
	    	.mode(SaveMode.Overwrite)
	    	.jdbc(dbConnectionUrl, "project1", prop);

	    System.out.println("SELECT ->>>");
	   spark.read().jdbc(dbConnectionUrl, "(SELECT * FROM project1) as q1", prop);
	}
}