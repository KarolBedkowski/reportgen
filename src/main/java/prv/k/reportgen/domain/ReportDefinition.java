package prv.k.reportgen.domain;

import java.util.List;

public class ReportDefinition {
	private List<Column> columns;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "ReportDefinition [columns=" + columns + "]";
	}

}
