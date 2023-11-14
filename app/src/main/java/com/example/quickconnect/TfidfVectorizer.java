//package com.example.quickconnect;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class TfidfVectorizer {
//    private List<String> vocabulary;
//    private List<List<Double>> tfidfMatrix;
//
//    public TfidfVectorizer(List<String> documents) {
//        this.vocabulary = buildVocabulary(documents);
//        this.tfidfMatrix = calculateTfidfMatrix(documents);
//    }
//
//    private List<String> buildVocabulary(List<String> documents) {
//        List<String> vocabulary = new ArrayList<>();
//        for (String document : documents) {
//            String[] words = document.toLowerCase().split("\\s+");
//            vocabulary.addAll(Arrays.asList(words));
//        }
//        return vocabulary;
//    }
//
//    private List<List<Double>> calculateTfidfMatrix(List<String> documents) {
//        List<List<Double>> tfidfMatrix = new ArrayList<>();
//        for (String document : documents) {
//            String[] words = document.toLowerCase().split("\\s+");
//            List<Double> tfidfVector = new ArrayList<>();
//            for (String term : vocabulary) {
//                double tf = calculateTermFrequency(words, term);
//                double idf = calculateInverseDocumentFrequency(documents, term);
//                double tfidf = tf * idf;
//                tfidfVector.add(tfidf);
//            }
//            tfidfMatrix.add(tfidfVector);
//        }
//        return tfidfMatrix;
//    }
//
//    private double calculateTermFrequency(String[] words, String term) {
//        long termCount = Arrays.stream(words).filter(word -> word.equals(term)).count();
//        return (double) termCount / words.length;
//    }
//
//    private double calculateInverseDocumentFrequency(List<String> documents, String term) {
//        long documentCount = documents.stream().filter(document -> Arrays.asList(document.split("\\s+")).contains(term)).count();
//        double idf = Math.log((double) documents.size() / (1 + documentCount));
//        return idf;
//    }
//
//    public List<String> getVocabulary() {
//        return vocabulary;
//    }
//
//    public List<List<Double>> getTfidfMatrix() {
//        return tfidfMatrix;
//    }
//
//    // Example usage
//    public static void main(String[] args) {
//        List<String> documents = Arrays.asList(
//                "This is the first document.",
//                "This document is the second document.",
//                "And this is the third one.",
//                "Is this the first document?"
//        );
//
//        TfidfVectorizer vectorizer = new TfidfVectorizer(documents);
//
//        // Get the vocabulary
//        List<String> vocabulary = vectorizer.getVocabulary();
//        System.out.println("Vocabulary: " + vocabulary);
//
//        // Get the TF-IDF matrix
//        List<List<Double>> tfidfMatrix = vectorizer.getTfidfMatrix();
//        System.out.println("TF-IDF Matrix: " + tfidfMatrix);
//    }
//}
