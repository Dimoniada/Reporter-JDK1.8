package com.reporter.script;

import com.google.common.base.Objects;
import com.model.domain.Document;
import com.model.domain.style.StyleService;
import com.model.formatter.DocumentHolder;
import com.model.formatter.html.HtmlFormatter;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class GroovyScriptTest {

    @Test
    public void testGroovyScriptCallWithStyleConditions() throws Throwable {
        final Binding binding = new Binding();

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("script/");
        final List<URL> urlList = Collections.singletonList(url);

        final GroovyScriptEngine engine = new GroovyScriptEngine(urlList.toArray(new URL[0]));
        final Object[] res = (Object[]) engine.run("HtmlDocument.groovy", binding);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService((StyleService) res[1]);
        try (DocumentHolder documentHolder = htmlFormatter.handle((Document) res[0])) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(text,
                "{color:#00FF00;" +
                    "font-family:Brush Script MT,monospace;" +
                    "font-size:35pt;" +
                    "font-style:italic;" +
                    "font-weight:bold}"));
            Assertions.assertEquals(1, StringUtils.countOccurrencesOf(text,
                "{color:#FF0000;" +
                    "font-family:Gill Sans,monospace;" +
                    "font-size:15pt}"));
        }
    }

    @Test
    public void testBigDocument() throws Throwable {
        final Binding binding = new Binding();

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("script/");
        final List<URL> urlList = Collections.singletonList(url);

        final GroovyScriptEngine engine = new GroovyScriptEngine(urlList.toArray(new URL[0]));
        final Object[] res = (Object[]) engine.run("HtmlDocumentBase.groovy", binding);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService((StyleService) res[2]);

        try (DocumentHolder documentHolder = htmlFormatter.handle((Document) res[0])) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            final String styleCode = "_" + Integer.toHexString(Objects.hashCode(res[1]));
        }
    }
}
