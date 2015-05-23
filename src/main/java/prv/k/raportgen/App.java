package prv.k.raportgen;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import prv.k.raportgen.domain.Configuration;
import prv.k.raportgen.domain.Report;

/**
 * Hello world!
 *
 */
public class App {
	final static Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: <connfig.yml>");
			return;
		}

		// laod configuration
		Configuration config = loadConfiguration(args[0]);
		if (config == null) {
			return;
		}

		LOG.debug("Loading db driver {}", config.getConnection().getDriver());

		// load db driver
		try {
			Class.forName(config.getConnection().getDriver());
		} catch (ClassNotFoundException e) {
			LOG.error("Load driver error", e);
			return;
		}

		LOG.debug("Connectiong to {}", config.getConnection().getUrl());
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(config.getConnection().getUrl(),
					config.getConnection().getUser(), config.getConnection()
							.getPassword());

			// generate
			for (Report report : config.getReports()) {
				LOG.info("Generating report {}", report.getName());
				ReportGenerator rgen = new ReportGenerator(report);
				rgen.execute(conn);
				LOG.info("Report {} DONE", report.getName());
			}
		} catch (SQLException err) {
			System.out.println(err.toString());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		LOG.info("All done");
	}

	private static Configuration loadConfiguration(String filename) {
		LOG.debug("Loading configuration from {}", filename);
		InputStream in;
		try {
			in = Files.newInputStream(Paths.get(filename));
		} catch (IOException e) {
			LOG.error("Load configuration error", e);
			return null;
		}
		Yaml yaml = new Yaml();
		return yaml.loadAs(in, Configuration.class);
	}
}
