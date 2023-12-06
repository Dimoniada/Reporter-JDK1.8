package script

import com.model.domain.Document
import com.model.domain.Heading
import com.model.domain.Paragraph
import com.model.domain.Title
import com.model.domain.style.Style
import com.model.domain.style.StyleCondition
import com.model.domain.style.TextStyle
import com.model.domain.style.constant.Color
import com.model.formatter.html.style.HtmlStyleService

final Style titleStyle = TextStyle
    .create()
    .setFontNameResource("Brush Script MT")
    .setItalic(true)
    .setBold(true)
    .setFontSize((short) 35)
    .setColor(Color.GREEN)
    .setCondition(StyleCondition.create(Title.class, o -> o instanceof Title));

final Style paragraphStyle = TextStyle
    .create()
    .setFontNameResource("Gill Sans")
    .setFontSize((short) 15)
    .setColor(Color.RED)
    .setCondition(StyleCondition.create(Paragraph.class, o -> o instanceof Paragraph));

final HtmlStyleService styleService = HtmlStyleService.create()
    .addStyles(titleStyle, paragraphStyle);
    styleService.setWriteStyleInplace(false);

final Document doc = Document.create().setLabel("doc1")
    .addParts(Heading.create(1).setText("testHeading"),
    Heading.create(2).setText("testHeading"),
    Title.create().setText("mainTitle"),
    Heading.create(3).setText("testHeading"),
    Paragraph.create().setText("testParagraph1"),
    Paragraph.create().setText("testParagraph2"),
    Paragraph.create().setText("testParagraph3"),
    Heading.create(2).setText("nextHeading"),
    Paragraph.create().setText("testParagraph4")
    );

final Map<String, Object> result = new HashMap<String, Object>();
    result.put("document", doc);
    result.put("styleService", styleService);

return result;
