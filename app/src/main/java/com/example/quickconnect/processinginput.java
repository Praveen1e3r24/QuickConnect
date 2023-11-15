//package com.example.quickconnect;
//
//import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
//
//import android.util.Log;
//import android.widget.MultiAutoCompleteTextView;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.Arrays;
//import java.util.List;
//
//public class processinginput {
//
//    // ... Other existing code
//
//    // Assuming you have a saved tokenizer
//    private MultiAutoCompleteTextView.Tokenizer tokenizer;
//
//    // ... Other existing code
//
//    // Load the tokenizer in your code
//    private void loadTokenizer() {
//        // Load the tokenizer from a file or another source
//        // Replace 'tokenizerFile' with the actual file or source
//        try {
//            FileInputStream fileInputStream = new FileInputStream(tokenizerFile);
//            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            tokenizer = (MultiAutoCompleteTextView.Tokenizer) objectInputStream.readObject();
//            objectInputStream.close();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Use the tokenizer to get word indices
//    private int getWordIndex(String word) {
//        if (tokenizer != null) {
//            List<String> wordsList = Arrays.asList(word);
//            List<List<String>> sequences = tokenizer.textsToSequences(wordsList);
//            if (!sequences.isEmpty()) {
//                List<Integer> sequence = sequences.get(0);
//                if (!sequence.isEmpty()) {
//                    return sequence.get(0); // Assuming you want the index of the first word
//                }
//            }
//        }
//        return 0; // Default index if not found
//    }
//
//    // Process the user input
//    private void processUserInput(String userInput) {
//        // Preprocess the user input
//        String cleanedInput = preprocessInput(userInput);
//
//        // Tokenize and pad the user input
//        List<String> inputList = Arrays.asList(cleanedInput);
//        List<List<String>> sequences = tokenizer.textsToSequences(inputList);
//        if (!sequences.isEmpty()) {
//            List<Integer> inputSequence = sequences.get(0);
//            int[] paddedInput = padSequence(inputSequence, 100); // Adjust maxlen if needed
//
//            // Make predictions
//            float predictedProbability = predictSeverity(paddedInput);
//
//            // Handle the result
//            handleModelOutput(predictedProbability);
//        }
//    }
//
//    // Additional preprocessing logic
//    private String preprocessInput(String text) {
//        // Implement your preprocessing logic here
//        // ...
//
//        return text;
//    }
//
//    // Additional padding logic
//    private int[] padSequence(List<Integer> sequence, int maxlen) {
//        // Implement your padding logic here
//        // ...
//
//        return new int[maxlen]; // Placeholder, replace with actual padding
//    }
//
//    // Additional prediction logic
//    private float predictSeverity(int[] paddedInput) {
//        // Implement your prediction logic here
//        // ...
//
//        return 0.0f; // Placeholder, replace with actual prediction
//    }
//
//    // Handle the model output
//    private void handleModelOutput(float predictedProbability) {
//        // Implement your logic to handle the model output
//        // ...
//
//        Log.d(TAG, "Predicted Probability: " + predictedProbability);
//    }
//
//    // ... Other existing code
//}