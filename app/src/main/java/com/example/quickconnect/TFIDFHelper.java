//package com.example.quickconnect;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//
//public class TFIDFHelper {
//
//    private static final String TAG = "TFIDFHelper";
//    private static ByteBuffer modelBuffer;
//
//    // Load the model file from assets
//    public static ByteBuffer loadModelFile(Context context, String filename) {
//        AssetManager assetManager = context.getAssets();
//        try {
//            InputStream is = assetManager.open(filename);
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            return ByteBuffer.wrap(buffer);
//        } catch (IOException e) {
//            Log.e(TAG, "Error loading model file from assets: " + e.getMessage());
//            return null;
//        }
//    }
//    // Get the model buffer
//    public static ByteBuffer getModelBuffer() {
//        return modelBuffer;
//    }
//
//    // Load the TF-IDF vectorizer from assets
//    public static TfidfVectorizer loadVectorizerFromAssets(Context context, String filename) {
//        AssetManager assetManager = context.getAssets();
//        try {
//            InputStream is = assetManager.open(filename);
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder jsonStringBuilder = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonStringBuilder.append(line);
//            }
//            br.close();
//
//            // Deserialize using JSON
//            Gson gson = new Gson();
//            TfidfVectorizer vectorizer = gson.fromJson(jsonStringBuilder.toString(), TfidfVectorizer.class);
//
//            return vectorizer;
//        } catch (IOException e) {
//            Log.e(TAG, "Error loading TF-IDF vectorizer from assets: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//
//    // Preprocess input using the loaded model file
//    public static float[] preprocessInput(ByteBuffer modelBuffer, String input) {
//        // Convert input text to numerical features (replace with your actual conversion logic)
//        float[] features = convertInputToFeatures(input);
//
//        // Copy features to the model's input tensor buffer
//        ByteBuffer inputBuffer = getInputTensorBuffer();
//        inputBuffer.rewind();
//        for (float feature : features) {
//            inputBuffer.putFloat(feature);
//        }
//
//        return features;
//    }
//
//    // Replace this method with your actual conversion logic
//    private static float[] convertInputToFeatures(String input) {
//        // Placeholder implementation, replace with your logic
//        // Ensure that the output length matches the expected length (824)
//        float[] features = new float[824];
//        // Your conversion logic here
//
//        return features;
//    }
//
//    // Get the input tensor buffer from the loaded model file
//    private static ByteBuffer getInputTensorBuffer() {
//        // Replace "input" with the actual name of your model's input tensor
//        return modelBuffer.asReadOnlyBuffer().order(ByteOrder.nativeOrder());
//    }
//
//    // Post-process the model output
//    public static float postprocessOutput(ByteBuffer modelBuffer) {
//        // Replace "output" with the actual name of your model's output tensor
//        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 1}, DataType.FLOAT32);
//        outputBuffer.loadBuffer(modelBuffer);
//
//        // Access the value from the output buffer
//        return outputBuffer.getFloatValue(0);
//    }
//}
