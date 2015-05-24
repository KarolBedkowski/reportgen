package prv.k.reportgen;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prv.k.reportgen.domain.Column;
import prv.k.reportgen.domain.Report;
import prv.k.reportgen.domain.ReportDefinition;

/**
 * Report generation methods.
 * 
 * @author k
 *
 */
public class ReportGenerator {

	private final static Logger LOG = LoggerFactory
			.getLogger(ReportGenerator.class);

	transient private final Report report;

	public ReportGenerator(final Report report) {
		this.report = report;
	}

	public void execute(final Connection conn) {
		try {
			JasperReportBuilder rapbuilder = genReport(conn);
			if (report.hasColumnDefinition()) {
				rapbuilder = configureColumnsFromConf(rapbuilder);
			} else {
				// guess columns
				rapbuilder = configureColumns(rapbuilder, conn);
			}
			String dstFile = report.getFilename();
			switch (report.getFormat()) {
			case "html": {
				rapbuilder.toHtml(new FileOutputStream(dstFile));
				break;
			}
			case "pdf": {
				rapbuilder.toPdf(new FileOutputStream(dstFile));
				break;
			}
			case "csv": {
				rapbuilder.toCsv(new FileOutputStream(dstFile));
				break;
			}
			case "xlsx": {
				rapbuilder.toXlsx(new FileOutputStream(dstFile));
				break;
			}
			default: {
				LOG.error("Invalid dst format {} for {}", report.getFormat(),
						report.getName());
				break;
			}
			}
		} catch (Exception e) {
			LOG.error("Report generating error", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JasperReportBuilder configureColumnsFromConf(
			JasperReportBuilder builder) {
		ReportDefinition definition = report.getDefinition();
		for (Column column : definition.getColumns()) {
			DRIDataType type;
			switch (column.getType() != null ? column.getType() : "") {
			case "integer":
			case "decimal":
			case "numeric":
				type = DataTypes.integerType();
				break;
			case "double":
				type = DataTypes.doubleType();
				break;
			case "float":
				type = DataTypes.doubleType();
				break;
			case "date":
				type = DataTypes.dateType();
				break;
			case "time":
				type = DataTypes.timeHourToSecondType();
				break;
			case "timestamp":
				type = DataTypes.dateYearToSecondType();
				break;
			default:
				type = DataTypes.stringType();
				break;
			}
			builder.addColumn(Columns.column(column.getLabel(),
					column.getName(), type));
		}
		return builder;
	}

	protected JasperReportBuilder genReport(final Connection connection) {
		final JasperReportBuilder reportBuilder = DynamicReports.report();
		final StyleBuilder baseStyle = stl.style();
		baseStyle.setFontName("FreeSans");
		baseStyle.setPadding(5);
		reportBuilder.setTextStyle(baseStyle);

		final StyleBuilder boldStyle = stl.style(baseStyle).bold();
		final StyleBuilder boldCenteredStyle = stl.style(boldStyle)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		final StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
				.setBorder(stl.penThin());
		// .setBackgroundColor(Color.LIGHT_GRAY);
		final StyleBuilder textStyle = stl.style(baseStyle).setBorder(
				stl.penThin());

		return reportBuilder.setDataSource(report.getQuery(), connection)
				.title(cmp.text(report.getName()).setStyle(boldCenteredStyle))
				// .pageFooter(cmp.pageXofY())
				.setColumnTitleStyle(columnTitleStyle).setTextStyle(textStyle);
		// .highlightDetailEvenRows();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JasperReportBuilder configureColumns(JasperReportBuilder builder,
			Connection connection) {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = connection.createStatement();
			stmt.setMaxRows(0);
			rset = stmt.executeQuery(report.getQuery());

			final ResultSetMetaData meta = rset.getMetaData();
			for (int col = 1; col <= meta.getColumnCount(); col++) {
				final String colName = meta.getColumnName(col);
				final String colLabel = meta.getColumnLabel(col);
				DRIDataType type;
				switch (meta.getColumnType(col)) {
				case Types.INTEGER:
				case Types.DECIMAL:
				case Types.NUMERIC:
					type = DataTypes.integerType();
					break;
				case Types.DOUBLE:
					type = DataTypes.doubleType();
					break;
				case Types.FLOAT:
					type = DataTypes.doubleType();
					break;
				case Types.DATE:
					type = DataTypes.dateType();
					break;
				case Types.TIME:
					type = DataTypes.timeHourToSecondType();
					break;
				case Types.TIMESTAMP:
					type = DataTypes.dateYearToSecondType();
					break;
				default:
					type = DataTypes.stringType();
					break;
				}
				builder.addColumn(Columns.column(colLabel, colName, type));
			}

		} catch (SQLException err) {
			LOG.error("Guess column error", err);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					LOG.debug("Rset close error", e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LOG.debug("Stmt close error", e);
				}
			}
		}
		return builder;
	}
}
