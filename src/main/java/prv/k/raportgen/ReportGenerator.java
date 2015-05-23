package prv.k.raportgen;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
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

import prv.k.raportgen.domain.Report;

public class ReportGenerator {

	final static Logger LOG = LoggerFactory.getLogger(ReportGenerator.class);
	private Report report;

	public ReportGenerator(Report report) {
		this.report = report;
	}

	public void execute(Connection conn) {
		try {
			JasperReportBuilder rapbuilder = genReport(conn);
			rapbuilder = configureColumns(rapbuilder, conn);
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
			}
			}
		} catch (Exception e) {
			LOG.error("Report generating error", e);
		}
	}

	protected JasperReportBuilder genReport(Connection connection) {
		final JasperReportBuilder reportBuilder = DynamicReports.report();
		final StyleBuilder baseStyle = stl.style();
		baseStyle.setFontName("FreeSans");
		baseStyle.setPadding(5);
		reportBuilder.setTextStyle(baseStyle);

		StyleBuilder boldStyle = stl.style(baseStyle).bold();
		StyleBuilder boldCenteredStyle = stl.style(boldStyle)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
				.setBorder(stl.pen1Point())
				.setBackgroundColor(Color.LIGHT_GRAY);

		return reportBuilder.setDataSource(report.getQuery(), connection)
				.title(cmp.text("Users").setStyle(boldCenteredStyle))
				.pageFooter(cmp.pageXofY())
				.setColumnTitleStyle(columnTitleStyle)
				.highlightDetailEvenRows();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JasperReportBuilder configureColumns(
			JasperReportBuilder builder, Connection connection) {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = connection.createStatement();
			stmt.setMaxRows(0);
			rset = stmt.executeQuery(report.getQuery());

			ResultSetMetaData meta = rset.getMetaData();
			for (int col = 1; col <= meta.getColumnCount(); col++) {
				String colName = meta.getColumnName(col);
				String colLabel = meta.getColumnLabel(col);
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
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return builder;
	}
}
