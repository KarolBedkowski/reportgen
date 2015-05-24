package prv.k.reportgen.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

	private String url;
	private String user;
	private String password;
	private String driver;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "Connection [url=" + url + ", user=" + user + ", password="
				+ password + ", driver=" + driver + "]";
	}

	public boolean validate() {
		boolean result = true;
		if (url == null || url.isEmpty()) {
			LOG.error("Configuration: missing connection -> url");
			result = false;
		}
		if (user == null || user.isEmpty()) {
			LOG.error("Configuration: missing connection -> user");
			result = false;
		}
		if (password == null || password.isEmpty()) {
			LOG.error("Configuration: missing connection -> password");
			result = false;
		}
		if (driver == null || driver.isEmpty()) {
			LOG.error("Configuration: missing connection -> driver");
			result = false;
		}
		return result;
	}
}
