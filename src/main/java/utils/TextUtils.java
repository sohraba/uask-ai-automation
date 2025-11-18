package utils;

import java.io.IOException;
import java.text.Normalizer;
import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.TranslateException;

public class TextUtils {
	
	// ------------------- Keyword-based similarity -------------------

    /**
     * To checks if AI response matches the expected text based on keyword similarity.
     *
     * @param expected 
     * 		- Expected answer from JSON
     * @param actual   
     * 		- AI generated response
     * @param threshold 
     * 		- Similarity threshold (0.0 - 1.0)
     * @return boolean
     * 		- true if similarity >= threshold, false otherwise
     */
    public static boolean isResponseValid(String expected, String actual, double threshold) {
        if (expected == null || actual == null || expected.isEmpty() || actual.isEmpty()) {
            return false;
        }

        // Normalize text: lowercase, remove punctuation, normalize spaces
        expected = normalizeText(expected);
        actual = normalizeText(actual);

        String[] keywords = expected.split("\\s+");
        int matched = 0;

        for (String word : keywords) {
            if (!word.isBlank() && actual.contains(word)) {
                matched++;
            }
        }

        double similarity = (double) matched / keywords.length;
        Log.message("Keyword match similarity: " + similarity);

        return similarity >= threshold;
    }

    /**
     * To checks if AI response matches the expected text based on keyword similarity.
     *
     * @param expected 
     * 		- Expected answer from JSON
     * @param actual   
     * 		- AI generated response
     * @return double
     */
    public static double isResponseValid(String expected, String actual) {
        if (expected == null || actual == null || expected.isEmpty() || actual.isEmpty()) {
            return 0;
        }

        // Normalize text: lowercase, remove punctuation, normalize spaces
        expected = normalizeText(expected);
        actual = normalizeText(actual);

        String[] keywords = expected.split("\\s+");
        int matched = 0;

        for (String word : keywords) {
            if (!word.isBlank() && actual.contains(word)) {
                matched++;
            }
        }

        double similarity = (double) matched / keywords.length;
        Log.message("Keyword match similarity: " + similarity);

        return similarity;
    }
    
    /**
     * To normalize the text(lowercase, remove punctuation, normalize Arabic diacritics)
     * 
     * @param text
     * @return String
     */
    private static String normalizeText(String text) {
        text = text.toLowerCase().trim();

        // Remove all punctuation
        text = text.replaceAll("[\\p{Punct}]", " ");

        // Normalize spaces
        text = text.replaceAll("\\s+", " ");

        // Remove Arabic diacritics (fatha, damma, kasra, etc.)
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                         .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return text;
    }
    
	// ------------------- Semantic similarity using a pre-trained sentence embedding model -------------------
    
    /**
     * Computes the semantic similarity between two pieces of text using a pre-trained
     * sentence embedding model ("all-MiniLM-L6-v2") from the Sentence-Transformers family.
     * <p>
     * This method generates embeddings for each text input and calculates the cosine
     * similarity between the two vectors. The similarity score ranges from -1.0 to 1.0,
     * where 1.0 indicates identical semantics and -1.0 indicates completely opposite semantics.
     * </p>
     *
     * @param text1 
     * 		- the first input text for comparison
     * @param text2 
     * 		- the second input text for comparison
     * @return a double 
     * 		- the value representing the semantic similarity between text1 and text2
     * @throws IOException 
     * 		- if there is an error loading the model or reading model files
     * @throws ModelException 
     * 		- if there is an error related to the model's configuration or inference
     * @throws TranslateException 
     * 		- if an error occurs during the translation of text into embeddings
     *
     * @implNote This method uses DJL (Deep Java Library) with the PyTorch engine.
     *           The underlying model "sentence-transformers/all-MiniLM-L6-v2" generates
     *           embeddings suitable for semantic similarity, clustering, or semantic search tasks.
     *
     * @see #cosineSimilarity(float[], float[])
     */
    public static double getSemanticSimilarity(String text1, String text2)
            throws IOException, ModelException, TranslateException {

        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optApplication(Application.NLP.TEXT_EMBEDDING)
                .optEngine("PyTorch")  // optional, but ensures PyTorch backend
                .optModelName("sentence-transformers/all-MiniLM-L6-v2")
                .build();

        try (ZooModel<String, float[]> model = ModelZoo.loadModel(criteria);
             Predictor<String, float[]> predictor = model.newPredictor()) {

            float[] emb1 = predictor.predict(text1);
            float[] emb2 = predictor.predict(text2);

            return cosineSimilarity(emb1, emb2);
        }
    }



    /**
     * To computes the cosine similarity between two float vectors.
     * <p>
     * Cosine similarity is a measure of similarity between two non-zero vectors of an inner product space.
     * It is calculated as the dot product of the vectors divided by the product of their magnitudes.
     * </p>
     *
     * @param vec1 
     * 		- the first vector
     * @param vec2 
     * 		- the second vector
     * @return a double 
     * 		- it representing the cosine similarity between vec1 and vec2
     *
     */
    private static double cosineSimilarity(float[] vec1, float[] vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            norm1 += Math.pow(vec1[i], 2);
            norm2 += Math.pow(vec2[i], 2);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
