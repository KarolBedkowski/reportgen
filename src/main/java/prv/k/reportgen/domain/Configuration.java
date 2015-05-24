package prv.k.reportgen.domain;

import java.util.List;


public class Configuration {
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


}
