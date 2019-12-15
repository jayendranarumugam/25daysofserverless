package com.computervisionCH15.functions;

import java.net.URL;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpTrigger-Java&code={your function key} 2. curl "{your
     * host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke
     * function deployed to Azure. More details:
     * https://aka.ms/functions_authorization_keys
     */
    @FunctionName("ImgAnalysis")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = {
            HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("imgurl");
        final String imgurl = request.getBody().orElse(query);
        String message = "";

        final String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        final String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");

       

        if (imgurl == null) {
            message="Please pass the imgurl in the query parameter";            
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(message).build();
        } else {
            try {
                if (imgurl != "") {
                    URL uRL = new URL(imgurl);
                    final ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey)
                    .withEndpoint(endpoint);
                    ImageResultModel imageResultModel= AnalyzeRemoteImage(compVisClient,uRL.toString(),context);
                    return request.createResponseBuilder(HttpStatus.OK).body(imageResultModel).build();

                } else {
                    message = "String Should not be Empty";
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(message).build();
                }
            }

            catch (Exception e) {
                message = "Not a Valid URL";
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(message).build();
            }

            
        }

    }

    public ImageResultModel AnalyzeRemoteImage(ComputerVisionClient compVisClient, String pathToRemoteImage,ExecutionContext context) {
        /*
         * Analyze an image from a URL:
         *
         * Set a string variable equal to the path of a remote image.
         */
        ImageResultModel ImageResultModel = new ImageResultModel();
        ImageResultModel.ImageURL=pathToRemoteImage;
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromRemoteImage = new ArrayList<>();
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.DESCRIPTION);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.TAGS);
        // featuresToExtractFromRemoteImage.add(VisualFeatureTypes.FACES);
        // featuresToExtractFromRemoteImage.add(VisualFeatureTypes.ADULT);
        // featuresToExtractFromRemoteImage.add(VisualFeatureTypes.COLOR);
        // featuresToExtractFromRemoteImage.add(VisualFeatureTypes.IMAGE_TYPE);

        context.getLogger().info("\n\nAnalyzing an image from a URL ...");

        try {
            // Call the Computer Vision service and tell it to analyze the loaded image.
            final ImageAnalysis analysis = compVisClient.computerVision().analyzeImage().withUrl(pathToRemoteImage)
                    .withVisualFeatures(featuresToExtractFromRemoteImage).execute();

            // Display image captions and confidence values.
            context.getLogger().info("\nCaptions: ");
            for (final ImageCaption caption : analysis.description().captions()) {
                ImageResultModel.Caption.add(caption.text());
            }

            // Display image category names and confidence values.
            context.getLogger().info("\nCategories: ");
            for (final Category category : analysis.categories()) {
                ImageResultModel.Category.add(category.name());
            }

            // Display image tags and confidence values.
            context.getLogger().info("\nTags: ");
            for (final ImageTag tag : analysis.tags()) {
                ImageResultModel.Tags.add(tag.name());
            }

            // Display any faces found in the image and their location.
            // System.out.println("\nFaces: ");
            // for (FaceDescription face : analysis.faces()) {
            // System.out.printf("\'%s\' of age %d at location (%d, %d), (%d, %d)\n",
            // face.gender(), face.age(),
            // face.faceRectangle().left(), face.faceRectangle().top(),
            // face.faceRectangle().left() + face.faceRectangle().width(),
            // face.faceRectangle().top() + face.faceRectangle().height());
            // }

            // // Display whether any adult or racy content was detected and the confidence
            // // values.
            // System.out.println("\nAdult: ");
            // System.out.printf("Is adult content: %b with confidence %f\n",
            // analysis.adult().isAdultContent(),
            // analysis.adult().adultScore());
            // System.out.printf("Has racy content: %b with confidence %f\n",
            // analysis.adult().isRacyContent(),
            // analysis.adult().racyScore());

            // // Display the image color scheme.
            // System.out.println("\nColor scheme: ");
            // System.out.println("Is black and white: " + analysis.color().isBWImg());
            // System.out.println("Accent color: " + analysis.color().accentColor());
            // System.out.println("Dominant background color: " +
            // analysis.color().dominantColorBackground());
            // System.out.println("Dominant foreground color: " +
            // analysis.color().dominantColorForeground());
            // System.out.println("Dominant colors: " + String.join(", ",
            // analysis.color().dominantColors()));

            // // Display any celebrities detected in the image and their locations.
            // System.out.println("\nCelebrities: ");
            // for (Category category : analysis.categories()) {
            // if (category.detail() != null && category.detail().celebrities() != null) {
            // for (CelebritiesModel celeb : category.detail().celebrities()) {
            // System.out.printf("\'%s\' with confidence %f at location (%d, %d), (%d,
            // %d)\n", celeb.name(),
            // celeb.confidence(), celeb.faceRectangle().left(),
            // celeb.faceRectangle().top(),
            // celeb.faceRectangle().left() + celeb.faceRectangle().width(),
            // celeb.faceRectangle().top() + celeb.faceRectangle().height());
            // }
            // }
            // }

            // // Display any landmarks detected in the image and their locations.
            // System.out.println("\nLandmarks: ");
            // for (Category category : analysis.categories()) {
            // if (category.detail() != null && category.detail().landmarks() != null) {
            // for (LandmarksModel landmark : category.detail().landmarks()) {
            // System.out.printf("\'%s\' with confidence %f\n", landmark.name(),
            // landmark.confidence());
            // }
            // }
            // }

            // // Display what type of clip art or line drawing the image is.
            // System.out.println("\nImage type:");
            // System.out.println("Clip art type: " + analysis.imageType().clipArtType());
            // System.out.println("Line drawing type: " +
            // analysis.imageType().lineDrawingType());

        }

        catch (final Exception e) {
            System.out.println("Error Happen");
            ImageResultModel.Message=e.getMessage();
            e.printStackTrace();
        }
        return ImageResultModel;
    }
}
