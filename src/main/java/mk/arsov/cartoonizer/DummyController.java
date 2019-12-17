package mk.arsov.cartoonizer;

import mk.arsov.cartoonizer.config.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class DummyController {

    private final BlurConfiguration blurConfiguration;
    private final EdgeDetectionConfiguration edgeDetectionConfiguration;
    private final EdgeTangentFlowConfiguration edgeTangentFlowConfiguration;
    private final LineIntegralConvolutionConfiguration lineIntegralConvolutionConfiguration;
    private final RegionSmoothingConfiguration regionSmoothingConfiguration;

    @Inject
    public DummyController(final BlurConfiguration blurConfiguration, final EdgeDetectionConfiguration edgeDetectionConfiguration,
            final EdgeTangentFlowConfiguration edgeTangentFlowConfiguration, final LineIntegralConvolutionConfiguration lineIntegralConvolutionConfiguration,
            final RegionSmoothingConfiguration regionSmoothingConfiguration) {
        this.blurConfiguration = blurConfiguration;
        this.edgeDetectionConfiguration = edgeDetectionConfiguration;
        this.edgeTangentFlowConfiguration = edgeTangentFlowConfiguration;

        this.lineIntegralConvolutionConfiguration = lineIntegralConvolutionConfiguration;
        this.regionSmoothingConfiguration = regionSmoothingConfiguration;
    }

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }
}
