package prv.k.reportgen.domain;

public class Report {
	private String name;
	private String query;
	private String format;
	private String filename;

	private ReportDefinition definition;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ReportDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ReportDefinition definition) {
		this.definition = definition;
	}

	public boolean hasColumnDefinition() {
		if (definition == null) {
			return false;
		}
		if (definition.getColumns() != null) {
			return definition.getColumns().size() > 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Report [name=" + name + ", query=" + query + ", format="
				+ format + ", filename=" + filename + ", definition="
				+ definition + "]";
	}
}
