package prv.k.reportgen.domain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static final Logger LOG = LoggerFactory
			.getLogger(Configuration.class);

	private Connection connection;
	private List<Report> reports;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	@Override
	public String toString() {
		return "Configuration [connection=" + connection + "]";
	}

	public boolean validate() {
		boolean result = true;
		if (connection == null) {
			LOG.error("Configuration: missing connection");
			result = false;
		} else {
			result &= connection.validate();
		}
		if (reports == null || reports.size() == 0) {
			LOG.error("Configuration: missing reports");
			result = false;
		} else {
			for (Report report : reports) {
				result &= report.validate();
			}
		}
		return result;
	}
}
