package com.reporter.utils;

import com.model.domain.style.constant.PictureFormat;
import com.model.utils.PictureUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;

import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PictureUtilsTest {

    static Stream<Arguments> testArguments() {
        return Stream.of(
            Arguments.of(PictureFormat.JPG),
            Arguments.of(PictureFormat.PNG),
            Arguments.of(PictureFormat.BMP),
            Arguments.of(PictureFormat.GIF),
            Arguments.of(PictureFormat.WMF),
            Arguments.of(PictureFormat.EMF),
            Arguments.of(PictureFormat.DIB),
            Arguments.of(PictureFormat.PICT)
        );
    }

    @ParameterizedTest(name = "{index}: {0} test")
    @MethodSource("testArguments")
    public void testGetFormatFromJPG(PictureFormat pictureFormat) throws Throwable {
        // Arrange
        final String resourceName = String.format("pic/pic.%s", pictureFormat.toString().toLowerCase());
        final URL url = getClass().getClassLoader().getResource(resourceName);
        Assertions.assertNotNull(url);
        final WritableResource resource = new PathResource(url.toURI());
        // Act
        final PictureFormat result = PictureUtils.getFormat(IOUtils.toByteArray(resource.getInputStream()));
        // Assert
        assertEquals(pictureFormat, result);
    }

    @Test
    public void testGetFormatWhenEmptyByteArrayThenReturnNull() {
        // Arrange
        final byte[] data = new byte[]{};

        // Act
        final PictureFormat result = PictureUtils.getFormat(data);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetFormatWhenByteArrayLengthLessThan4ThenReturnNull() {
        // Arrange
        final byte[] data = new byte[]{1, 2, 3};

        // Act
        final PictureFormat result = PictureUtils.getFormat(data);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetFormatWhenUnknownFormatThenReturnNull() {
        // Arrange
        final byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        // Act
        final PictureFormat result = PictureUtils.getFormat(data);

        // Assert
        assertNull(result);
    }
}