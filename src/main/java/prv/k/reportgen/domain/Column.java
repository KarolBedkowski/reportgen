package prv.k.reportgen.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Column {
	private static final Logger LOG = LoggerFactory.getLogger(Column.class);

	private String name;
	private String label;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		if (label == null || label.isEmpty()) {
			return name;
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		if (type == null || type.isEmpty()) {
			return "text";
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", label=" + label + ", type=" + type
				+ "]";
	}

	public boolean validate(String parentName) {
		boolean result = true;
		if (name == null || name.isEmpty()) {
			LOG.error("Configuration: column - missing name for column in {}",
					parentName);
			result = false;
		}
		return result;
	}

}
