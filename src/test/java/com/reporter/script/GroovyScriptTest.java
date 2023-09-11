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

    public static final String expected = "<!doctype html><html><head><meta charset=\"UTF-8\"><title>Test document" +
        "</title></head><body><h1>Title 1</h1><p style=\"font-family:" +
        "courierNew,monospace;font-size:10pt;font-weight:bold\">paragraph 1</p><table><tr><th style=" +
        "\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">column1</th><th style=" +
        "\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">column2 (столбец2)</th></tr><tr>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">1,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">2,000</td></tr>" +
        "<tr><td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">3,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">4,000</td></tr>" +
        "<tr><td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">5,000</td>" +
        "<td style=\"font-family:arial,monospace;font-size:14pt;font-weight:bold\">6,000</td></tr>" +
        "</table><h1 style=\"font-size:20pt\">Test document v.1</h1><hr " +
        "style=\"border-bottom:1px solid #008080;border-collapse:collapse\"><h1 style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 1</h1>" +
        "<h2 style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Chapter 1.1</h2><h3 style=\"font-family:" +
        "courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 1.1.1</h3><p style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">This is an " +
        "example of text in paragraph</p><table><tr><th style=\"border-bottom:double #000000;border-collapse:" +
        "collapse;border-left:double #000000;border-right:double #000000;border-top:double #000000;" +
        "font-family:arial,monospace;font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px" +
        "\">Column 1</th><th style=\"border-bottom:double #000000;border-collapse:collapse;border-left:" +
        "double #000000;border-right:double #000000;border-top:double #000000;font-family:arial," +
        "monospace;font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 2</th>" +
        "</tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">" +
        "Cell 1.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">" +
        "Cell 1.2</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Cell 2.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Cell 2.2</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Cell 3.1</td><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">Cell 3.2</td></tr><tr><td style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Cell 4.1</td><td style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Cell 4.2</td></tr></table><h1 style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Chapter 2</h1><h2 style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">Chapter 2.1</h2><h3 style=\"font-family:courierNew,monospace;" +
        "font-size:10pt;font-weight:bold\">Chapter 2.1.1</h3><p style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">This is an example of text in paragraph" +
        " 2</p><table><tr><th style=\"border-bottom:double #000000;border-collapse:collapse;border-left:double " +
        "#000000;border-right:double #000000;border-top:double #000000;font-family:arial,monospace;" +
        "font-size:14pt;font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 1</th><th " +
        "style=\"border-bottom:double #000000;border-collapse:collapse;border-left:double #000000;border-right:" +
        "double #000000;border-top:double #000000;font-family:arial,monospace;font-size:14pt;" +
        "font-weight:bold;height:15px;transform:rotate(10deg);width:20px\">Column 2</th></tr><tr><td style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 1.1</td><td style=" +
        "\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 1.2</td></tr><tr>" +
        "<td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 2.1</td>" +
        "<td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">Cell 2.2</td>" +
        "</tr></table><h1>Title 1</h1><p>paragraph 1</p><h2>shifted heading</h2><table><tr><th>столбец1</th><th>" +
        "column2</th></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">1,000</td><td style=\"font-family:courierNew,monospace;font-size:10pt;font-weight:" +
        "bold\">2,000</td></tr><tr><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">3,000</td><td style=\"font-family:courierNew,monospace;font-size:10pt;" +
        "font-weight:bold\">4 and some escape characters (символы) %;;;;;\\/</td></tr><tr><td style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">5,000</td><td style=\"" +
        "font-family:courierNew,monospace;font-size:10pt;font-weight:bold\">6,000</td></tr></table></body></html>";

    @Test
    public void testGroovyScriptCallWithStyleConditions() throws Throwable {
        final Binding binding = new Binding();

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("script/");
        final List<URL> urlList = Collections.singletonList(url);

        final GroovyScriptEngine engine = new GroovyScriptEngine(urlList.toArray(new URL[0]));
        final Object[] res = (Object[]) engine.run("htmlDocument.groovy", binding);

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
        final Object[] res = (Object[]) engine.run("bigDocument.groovy", binding);

        final HtmlFormatter htmlFormatter = HtmlFormatter.create().setStyleService((StyleService) res[2]);

        try (DocumentHolder documentHolder = htmlFormatter.handle((Document) res[0])) {
            final String text = FileUtils.readFileToString(documentHolder.getResource().getFile(), StandardCharsets.UTF_8);
            final String styleCode = "_" + Integer.toHexString(Objects.hashCode(res[1]));
        }
    }
}
