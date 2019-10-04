package pazone.ashot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pazone.ashot.comparison.*;
import pazone.ashot.coordinates.Coords;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static pazone.ashot.util.TestImageUtils.*;

/**
 * @author <a href="pazone@yandex-team.ru">Pavel Zorin</a>
 */
class DifferTestWithTolerance {

    private static final BufferedImage IMAGE_B_BIG = loadImage("img/B_b.png");
    private static final String IMAGE_IGNORED_TEMPLATE = "img/ignore_color_template.png";
    private static final String IMAGE_IGNORED_PASS = "img/ignore_color_pass.png";
    private static final String IMAGE_IGNORED_FAIL = "img/ignore_color_fail.png";

    private static Stream<DiffMarkupPolicy> data() {
        return Stream.of(new PointsMarkupPolicy(), new ImageMarkupPolicy());
    }

    private ImageDiffer createImageDiffer(DiffMarkupPolicy diffMarkupPolicy) {
        return new ImageDiffer()
                .withColorDistortion(10)
                .withDiffMarkupPolicy(diffMarkupPolicy.withDiffColor(Color.RED));
    }

    @MethodSource("data")
    @Test
    void testIgnoredCoordsSame() throws IOException {
        Screenshot a =new Screenshot(IMAGE_A_TOLERANCE);
        Screenshot b = new Screenshot(IMAGE_B_TOLERANCE);
        ImageDiff diff = new ImageDiffer().makeDiff(a, b);
        File newFile =  new File("src/test/resources/img/diff_result.png");
        ImageIO.write(diff.getMarkedImage(), "png", newFile);
        assertTrue(diff.hasDiff());
        //assertImageEquals(diff.getMarkedImage(), "img/expected/ignore_coords_same.png");
    }

    private Screenshot createScreenshotWithIgnoredAreas(BufferedImage image, Coords ignoredArea) {
        Screenshot screenshot = new Screenshot(image);
        screenshot.setIgnoredAreas(Collections.singleton(ignoredArea));
        return screenshot;
    }

    static Stream<Arguments> dataWithIgnoredColorDiff() {
        return Stream.of(
                Arguments.of(Color.MAGENTA, IMAGE_IGNORED_PASS, false),
                Arguments.of(Color.MAGENTA, IMAGE_IGNORED_FAIL, true),
                Arguments.of(Color.RED,     IMAGE_IGNORED_PASS, true)
        );
    }

}
