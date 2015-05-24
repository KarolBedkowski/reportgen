package prv.k.reportgen.domain;

public class Connection {

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

}
