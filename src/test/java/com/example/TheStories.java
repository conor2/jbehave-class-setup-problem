package com.example;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.context.Context;
import org.jbehave.core.context.ContextView;
import org.jbehave.core.context.JFrameContextView;
import org.jbehave.core.embedder.PropertyBasedEmbedderControls;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.ContextOutput;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ContextStepMonitor;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML_TEMPLATE;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML_TEMPLATE;

import java.util.List;
import java.util.Properties;

/**
 * Created by CONOR2 on 8/20/2015.
 */
public class TheStories extends JUnitStories
{
	private final CrossReference xref = new CrossReference();
	private Context context = new Context();
	private Format contextFormat = new ContextOutput(context);
	private ContextView contextView = new JFrameContextView().sized(640, 120);
	private ContextStepMonitor contextStepMonitor = new ContextStepMonitor(context, contextView, xref.getStepMonitor());


	public TheStories()
	{
		configuredEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(false)
							.doIgnoreFailureInView(true).doVerboseFailures(true).useThreads(2).useStoryTimeoutInSecs(60);
		configuredEmbedder().useEmbedderControls(new PropertyBasedEmbedderControls());
	}


	public Configuration configuration()
	{
		Properties viewResources = new Properties();
		viewResources.put("decorateNonHtml", "true");
		viewResources.put("reports", "ftl/jbehave-reports-with-totals.ftl");

		return new MostUsefulConfiguration()
				.useStoryReporterBuilder(
						new StoryReporterBuilder()
								.withDefaultFormats().withViewResources(viewResources)
								.withFormats(contextFormat, CONSOLE, TXT, HTML_TEMPLATE, XML_TEMPLATE).withFailureTrace(true)
								.withFailureTraceCompression(true).withCrossReference(xref))
				.useStepMonitor(contextStepMonitor);
	}


	@Override
	public InjectableStepsFactory stepsFactory()
	{
		return new InstanceStepsFactory(configuration(), new SomeNewFeatureSteps());
	}


	@Override
	protected List<String> storyPaths()
	{
		String filter = System.getProperty("story.filter", "**/*.story");
		return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), filter, "**/failing_before*.story");
	}
}
