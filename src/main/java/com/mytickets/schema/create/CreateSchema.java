package com.mytickets.schema.create;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.Persistence;

import org.hibernate.jpa.AvailableSettings;

public class CreateSchema {
	public static void main(String[] args) throws IOException {
		execute("myTicketsInMem", "target/jpa/sql/create-schema.sql");
	}

	public static void execute(String persistenceUnitName, String destination) {
		System.out.println("Generating DDL create script to : " + destination);

		final Properties persistenceProperties = new Properties();

		persistenceProperties.setProperty(org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO, "");
		persistenceProperties.setProperty(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "none");

		persistenceProperties.setProperty(AvailableSettings.SCHEMA_GEN_SCRIPTS_ACTION, "create");
		persistenceProperties.setProperty(AvailableSettings.SCHEMA_GEN_CREATE_SOURCE, "metadata");
		persistenceProperties.setProperty(AvailableSettings.SCHEMA_GEN_SCRIPTS_CREATE_TARGET, destination);

		Persistence.generateSchema(persistenceUnitName, persistenceProperties);
	}
}
