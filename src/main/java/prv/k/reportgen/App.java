package prv.k.reportgen;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.moandjiezana.toml.Toml;

import prv.k.reportgen.domain.Configuration;
import prv.k.reportgen.domain.Report;

/**
 * Hello world!
 *
 */
public class App {
	private final static Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(final String[] args) throws IOException {
		if (args.length == 0) {
			LOG.error("Missing argiment; Usage: <connfig.yml> [<connfig.yml>...]");
			return;
		}

		// load configuration
		final Configuration config = loadConfiguration(args);
		if (config == null) {
			return;
		}

		if (!config.validate()) {
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
				final ReportGenerator rgen = new ReportGenerator(report);
				rgen.execute(conn);
				LOG.info("Report {} DONE", report.getName());
			}
		} catch (SQLException err) {
			LOG.error("Generate raport error", err);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.debug("Close connection) error", e);
				}
			}
		}
		LOG.info("All done");
	}

	private static Configuration loadConfiguration(String[] filenames) {
		InputStream in;
		try {
			List<InputStream> instrList = new ArrayList<InputStream>();
			for (String fname : filenames) {
				LOG.debug("Loading configuration from {}", fname);
				instrList.add(Files.newInputStream(Paths.get(fname)));
			}
			in = new SequenceInputStream(Collections.enumeration(instrList));
		} catch (IOException e) {
			LOG.error("Load configuration error", e);
			return null;
		}
		/*
		Yaml yaml = new Yaml();
		return yaml.loadAs(in, Configuration.class);
		*/
		Configuration conf = new Toml().parse(in).to(Configuration.class);
		return conf;
	}

}
