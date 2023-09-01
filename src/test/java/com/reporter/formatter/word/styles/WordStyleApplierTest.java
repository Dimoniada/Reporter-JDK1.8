package com.reporter.formatter.word.styles;

import com.model.domain.style.constant.PictureFormat;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.model.formatter.word.styles.WordStyleService.toWordPictureFormat;

public class WordStyleApplierTest {

    @Test
    public void testToWordPictureFormat() {
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_EMF, toWordPictureFormat(PictureFormat.EMF));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_WMF, toWordPictureFormat(PictureFormat.WMF));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_PICT, toWordPictureFormat(PictureFormat.PICT));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_JPEG, toWordPictureFormat(PictureFormat.JPEG));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_JPEG, toWordPictureFormat(PictureFormat.JPG));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_PNG, toWordPictureFormat(PictureFormat.PNG));
        Assertions.assertEquals(XWPFDocument.PICTURE_TYPE_DIB, toWordPictureFormat(PictureFormat.DIB));
    }
}
