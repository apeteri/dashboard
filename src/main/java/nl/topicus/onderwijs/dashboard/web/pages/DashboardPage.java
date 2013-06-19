package nl.topicus.onderwijs.dashboard.web.pages;

import java.util.ArrayList;

import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datasources.Issues;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.keys.Location;
import nl.topicus.onderwijs.dashboard.keys.Misc;
import nl.topicus.onderwijs.dashboard.keys.Summary;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.PlotSourcesService;
import nl.topicus.onderwijs.dashboard.web.DashboardMode;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.alerts.AlertsPanel;
import nl.topicus.onderwijs.dashboard.web.components.bargraph.BarGraphPanel;
import nl.topicus.onderwijs.dashboard.web.components.events.EventsPanel;
import nl.topicus.onderwijs.dashboard.web.components.plot.PlotPanel;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;
import nl.topicus.onderwijs.dashboard.web.components.table.StackedTablesPanel;
import nl.topicus.onderwijs.dashboard.web.components.table.TablePanel;
import nl.topicus.onderwijs.dashboard.web.components.twitter.TwitterPanel;
import nl.topicus.onderwijs.dashboard.web.components.weather.WeatherPanel;
import nl.topicus.onderwijs.dashboard.web.resources.ResourceLocator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;

public class DashboardPage extends WebPage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private PlotSourcesService plotSources;

	public DashboardPage(final PageParameters parameters) {
		AjaxLink<Void> liveToRandomModeSwitch = new AjaxLink<Void>("live") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				WicketApplication.get().switchMode();
				target.add(this);
			}

			@Override
			public boolean isVisible() {
				return getApplication().usesDevelopmentConfig();
			}
		};
		add(liveToRandomModeSwitch);
		liveToRandomModeSwitch.add(new Label("label",
				new PropertyModel<String>(this, "mode")));
		ArrayList<Class<? extends DataSource<? extends Number>>> datasources = new ArrayList<Class<? extends DataSource<? extends Number>>>();
		datasources.add(NumberOfUsers.class);
		datasources.add(AverageRequestTime.class);
		datasources.add(RequestsPerMinute.class);
		add(new BarGraphPanel("bargraph",
				new ListModel<Class<? extends DataSource<? extends Number>>>(
						datasources)));
		add(new StatusTablePanel("table"));
		add(new TablePanel("ns", Trains.class, WicketApplication.get()
				.getRepository().getKeys(Location.class).get(0), true));

		StackedTablesPanel tablestack = new StackedTablesPanel("tablestack");
		tablestack.addTable(new TablePanel(tablestack.nextTableId(),
				Commits.class, Summary.get(), false));
		tablestack.addTable(new TablePanel(tablestack.nextTableId(),
				Issues.class, Summary.get(), false));
		add(tablestack);

		add(new WeatherPanel("weather", WicketApplication.get().getRepository()
				.getKeys(Location.class).get(0)));
		StackedTablesPanel plotstack = new StackedTablesPanel("plotstack");
		for (int index = 0; index < plotSources.getPlotSources().size(); index++) {
			plotstack.addTable(new PlotPanel(tablestack.nextTableId(), index));
		}
		add(plotstack);
		add(new EventsPanel("events", Events.class, Summary.get()));
		add(new AlertsPanel("alerts"));
		add(new TwitterPanel("twitter", WicketApplication.get().getRepository()
				.getKeys(Misc.class).get(0)));
	}

	public DashboardMode getMode() {
		return WicketApplication.get().getMode();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						ResourceLocator.class, "jquery.timers-1.1.3.js")));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						DashboardPage.class, "jquery.dashboardclock.js")));
		response.render(OnDomReadyHeaderItem.forScript(statement().render()));
	}

	private JsStatement statement() {
		return new JsQuery(this).$().chain(
				"dashboardClock",
				"'resources/application/starttime'",
				Boolean.toString(WicketApplication.get()
						.isContextMenuDisabled()));
	}
}
