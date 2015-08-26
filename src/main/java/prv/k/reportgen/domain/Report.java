package prv.k.reportgen.domain;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report {
	private static final Logger LOG = LoggerFactory.getLogger(Report.class);

	private String name;
	private String query;
	private String format;
	private String filename;
	private ReportDefinition definition;

	public String getName() {
		return formatString(name);
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
		String output = filename;
		if (output == null || output.isEmpty()) {
			output = name + "." + format;
		}
		return formatString(output);
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

	public boolean validate() {
		boolean result = true; 
		if (name == null || name.isEmpty()) {
			LOG.warn("Configuration: missing report name");
		}
		if (query == null || query.isEmpty()) {
			LOG.error("Configuration: report - missing query for report {}",
					name);
			result = false;
		}
		if (format == null || format.isEmpty()) {
			LOG.error("Configuration: report - missing format for report {}",
					name);
			result = false;
		} else {
			switch (format) {
			case "csv":
			case "pdf":
			case "xlsx":
			case "html":
				break;
			default:
				LOG.error(
						"Configuration: invalid format for report {}; accepted csv, pdf, xlsx, html",
						name);
				result = false;
			}
		}
		if (filename == null || filename.isEmpty()) {
			LOG.error(
					"Configuration: report - missing filename for report {}; using {}",
					name, getFilename());
		}
		if (definition != null) {
			result &= definition.validate("Report " + name);
		}
		return result;
	}

	private static String formatString(String inp) {
		Calendar cal = Calendar.getInstance();
		DateFormat fmtDate = SimpleDateFormat.getDateInstance();
		DateFormat fmtTime = SimpleDateFormat.getTimeInstance();
		inp = inp
				.replaceAll("%y", String.valueOf(cal.get(Calendar.YEAR)))
				.replaceAll("%m",
						String.format("%02d", cal.get(Calendar.MONTH)))
				.replaceAll("%d",
						String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
				.replaceAll("%H",
						String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)))
				.replaceAll("%M",
						String.format("%02d", cal.get(Calendar.MINUTE)))
				.replaceAll("%S",
						String.format("%02d", cal.get(Calendar.SECOND)))
				.replaceAll("%x", fmtDate.format(cal.getTime()))
				.replaceAll("%X", fmtTime.format(cal.getTime()));
		return inp;
	}
}
