package nl.topicus.onderwijs.dashboard.web.components.table;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

public class StackedTablesPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private RepeatingView tables;

	public StackedTablesPanel(String id) {
		super(id);

		tables = new RepeatingView("tables");
		add(tables);
	}

	public String nextTableId() {
		return tables.newChildId();
	}

	public void addTable(Panel table) {
		tables.add(table);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(WidgetJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						StackedTablesPanel.class,
						"jquery.ui.dashboardstackedtables.js")));
		response.render(OnDomReadyHeaderItem.forScript(statement().render()));
	}

	private JsStatement statement() {
		Options options = new Options();
		options.put("secondsBetweenSwitch", WicketApplication.get()
				.isDevelopment() ? 30 : 60);
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardStackedTables",
				options.getJavaScriptOptions());
	}
}
