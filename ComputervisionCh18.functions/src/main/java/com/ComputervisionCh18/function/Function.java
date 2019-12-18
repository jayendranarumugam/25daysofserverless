package com.ComputervisionCh18.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.ComputervisionCh18.function.ImageResultModel;
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
    @FunctionName("BlobTrigger")
    @StorageAccount("AzureWebJobsStorage")
    public void blobTrigger(
            @BlobTrigger(name = "content", path = "ch18/{fileName}", dataType = "binary", connection = "AzureWebJobsStorage") byte[] content,
            @BindingName("fileName") String fileName, final ExecutionContext context) {

        final String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        final String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");

        context.getLogger().info("Java Blob trigger function processed a blob.\n Name: " + fileName + "\n Size: "
                + content.length + " Bytes");

        ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        AnalyzeLocalImage(compVisClient, content,context);        


    }

    public static void AnalyzeLocalImage(ComputerVisionClient compVisClient, byte[] imageByteArray,ExecutionContext context
            ) {
        /*
         * Analyze a local image:
         *
         * Set a string variable equal to the path of a local image. The image path
         * below is a relative path.
         */
        // </snippet_analyzelocal_refs>

        ImageResultModel imageResultModel = new ImageResultModel();
        // <snippet_analyzelocal_features>
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
        // featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);
        // </snippet_analyzelocal_features>

        context.getLogger().info("\nAnalyzing local image ...");

        try {
            // <snippet_analyzelocal_analyze>
            // Need a byte array for analyzing a local image.

            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImageInStream().withImage(imageByteArray)
                    .withVisualFeatures(featuresToExtractFromLocalImage).execute();

            // </snippet_analyzelocal_analyze>

            // <snippet_analyzelocal_captions>
            // // Display image captions and confidence values.
            // System.out.println("\nCaptions: ");
            // for (ImageCaption caption : analysis.description().captions()) {
            // System.out.printf("\'%s\' with confidence %f\n", caption.text(),
            // caption.confidence());
            // }
            // // </snippet_analyzelocal_captions>

            // // <snippet_analyzelocal_category>
            // // Display image category names and confidence values.
            // System.out.println("\nCategories: ");
            // for (Category category : analysis.categories()) {
            // System.out.printf("\'%s\' with confidence %f\n", category.name(),
            // category.score());
            // }
            // // </snippet_analyzelocal_category>

            // // <snippet_analyzelocal_tags>
            // // Display image tags and confidence values.
            context.getLogger().info("Tags are:");
            for (ImageTag tag : analysis.tags()) {
                imageResultModel.Tags.add(tag.name());
                context.getLogger().info(tag.name());
            }
            // </snippet_analyzelocal_tags>

            // <snippet_analyzelocal_faces>
            // // Display any faces found in the image and their location.
            // System.out.println("\nFaces: ");
            // for (FaceDescription face : analysis.faces()) {
            // System.out.printf("\'%s\' of age %d at location (%d, %d), (%d, %d)\n",
            // face.gender(), face.age(),
            // face.faceRectangle().left(), face.faceRectangle().top(),
            // face.faceRectangle().left() + face.faceRectangle().width(),
            // face.faceRectangle().top() + face.faceRectangle().height());
            // }
            // // </snippet_analyzelocal_faces>

            // // <snippet_analyzelocal_adult>
            // // Display whether any adult or racy content was detected and the confidence
            // // values.
            // System.out.println("\nAdult: ");
            // System.out.printf("Is adult content: %b with confidence %f\n",
            // analysis.adult().isAdultContent(),
            // analysis.adult().adultScore());
            // System.out.printf("Has racy content: %b with confidence %f\n",
            // analysis.adult().isRacyContent(),
            // analysis.adult().racyScore());
            // // </snippet_analyzelocal_adult>

            // // <snippet_analyzelocal_colors>
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
            // // </snippet_analyzelocal_colors>

            // // <snippet_analyzelocal_celebrities>
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
            // // </snippet_analyzelocal_celebrities>

            // // <snippet_analyzelocal_landmarks>
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
            // // </snippet_analyzelocal_landmarks>

            // // <snippet_imagetype>
            // // Display what type of clip art or line drawing the image is.
            // System.out.println("\nImage type:");
            // System.out.println("Clip art type: " + analysis.imageType().clipArtType());
            // System.out.println("Line drawing type: " +
            // analysis.imageType().lineDrawingType());
            // // </snippet_imagetype>
        }

        catch (Exception e) {
            System.out.println("Error Happen");
            imageResultModel.Message = e.getMessage();
            context.getLogger().info("Error Message "+ imageResultModel.Message);
            e.printStackTrace();
        }

        
    }
}
