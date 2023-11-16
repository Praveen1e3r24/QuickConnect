//package com.example.quickconnect;
//
//import android.content.Context;
//import android.content.res.AssetFileDescriptor;
//import android.content.res.AssetManager;
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.tensorflow.lite.Interpreter;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//public class Classifier {
//
//    private static final String TAG = "Classifier";
//    private Map<String, Integer> vocabData;
//    private int maxlen;
//    private Context context;
//    private Interpreter interpreter;  // Added instance variable
//
//    // Updated constructor to accept Context parameter
//    public Classifier(Context context) {
//        this.context = context;
//        loadModelAndData();  // Load model during initialization
//    }
//
//    // Updated constructor to accept Context parameter
//    private void loadModelAndData() {
//        loadVocabulary();
//        try {
//            interpreter = new Interpreter(loadModelFile());
//        } catch (IOException e) {
//            Log.e(TAG, "Error loading model", e);
//        }
//    }
//
//    private void loadVocabulary() {
//        try {
//            String json = loadJSONFromAsset("word_dict.json");
//            JSONObject jsonObject = new JSONObject(json);
//            vocabData = new HashMap<>();
//
//            Iterator<String> keys = jsonObject.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                int value = jsonObject.getInt(key);
//                vocabData.put(key, value);
//            }
//
//            maxlen = jsonObject.getInt("maxlen");
//
//        } catch (IOException | JSONException e) {
//            Log.e(TAG, "Error loading vocabulary", e);
//        }
//    }
//
//    // Updated method to accept Context parameter
//    private String loadJSONFromAsset(String filename) throws IOException {
//        AssetManager assetManager = context.getAssets();
//        InputStream inputStream = assetManager.open(filename);
//        int size = inputStream.available();
//        byte[] buffer = new byte[size];
//        inputStream.read(buffer);
//        inputStream.close();
//        return new String(buffer, "UTF-8");
//    }
//
//    // Updated method to accept Context parameter
//    private MappedByteBuffer loadModelFile() throws IOException {
//        String MODEL_ASSETS_PATH = "model.tflite";
//        AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(MODEL_ASSETS_PATH);
//        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = fileInputStream.getChannel();
//        long startoffset = assetFileDescriptor.getStartOffset();
//        long declaredLength = assetFileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength);
//    }
//
//    // Updated method to accept float[] inputs
//    public float[] classifySequence(float[] inputs) {
//        try {
//            float[] outputs = new float[2];  // Adjust the size based on your model's output
//            interpreter.run(inputs, outputs);
//            return outputs;
//        } catch (Exception e) {
//            Log.e(TAG, "Error during inference", e);
//            return null;
//        }
//    }
//
//    // New method to preprocess user input and make predictions
//    public String predict(String userInput) {
//        // Preprocess the input
//        String cleanedInput = cleanseText(userInput);
//        List<Integer> inputSequence = tokenize(cleanedInput);
//        float[] paddedInput = padInputSequence(inputSequence);
//
//        // Perform inference
//        float[] outputs = classifySequence(paddedInput);
//
//        // Get predicted label
//        return getPredictionLabel(outputs);
//    }
//
//    private String cleanseText(String text) {
//        // Placeholder implementation (replace with actual logic)
//        // Your model should internally handle the conversion of raw text to numerical values
//        // If your model expects preprocessed input, you might need to adjust this method accordingly
//
//        // This is just an example; adjust the cleaning logic based on your requirements
//        text = text.toLowerCase();
//        text = text.replaceAll("</?.*?>", " <> ");
//        text = text.replaceAll("(\\d|\\W|_)+", " ");
//
//        return text;
//    }
//
//    // Placeholder implementation, replace with actual logic based on your requirements
//    private List<Integer> tokenize(String cleanedInput) {
//        // Implement the logic to tokenize the input text
//        // Return a List<Integer> representing the tokenized sequence
//        return null;
//    }
//
//    // Placeholder implementation, replace with actual logic based on your requirements
//    private float[] padInputSequence(List<Integer> inputSequence) {
//        // Implement the logic to pad the input sequence to the required length
//        // You might need to adjust maxlen based on your model
//        return null;
//    }
//
//    // Placeholder implementation, replace with actual logic based on your requirements
//    private String getPredictionLabel(float[] outputArray) {
//        // Implement the logic to get the predicted label from the output array
//        return null;
//    }
//}
