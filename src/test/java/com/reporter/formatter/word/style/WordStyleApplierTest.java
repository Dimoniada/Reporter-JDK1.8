package com.reporter.formatter.word.style;

import com.model.domain.style.constant.PictureFormat;
import org.apache.poi.common.usermodel.PictureType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.model.formatter.word.style.WordStyleService.toWordPictureFormat;

public class WordStyleApplierTest {

    @Test
    public void testToWordPictureFormat() {
        Assertions.assertEquals(PictureType.EMF, toWordPictureFormat(PictureFormat.EMF));
        Assertions.assertEquals(PictureType.WMF, toWordPictureFormat(PictureFormat.WMF));
        Assertions.assertEquals(PictureType.PICT, toWordPictureFormat(PictureFormat.PICT));
        Assertions.assertEquals(PictureType.JPEG, toWordPictureFormat(PictureFormat.JPG));
        Assertions.assertEquals(PictureType.PNG, toWordPictureFormat(PictureFormat.PNG));
        Assertions.assertEquals(PictureType.DIB, toWordPictureFormat(PictureFormat.DIB));
    }
}
