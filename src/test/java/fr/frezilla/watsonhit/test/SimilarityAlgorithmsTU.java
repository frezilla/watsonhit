package fr.frezilla.watsonhit.test;

import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithm;
import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithms;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SimilarityAlgorithmsTU {
    
    @Test
    public void testJaccard() {
        SimilarityAlgorithm algo = SimilarityAlgorithms.JACCARD.getAlgorithm();
        
        Assertions.assertEquals(0.222, TestUtils.floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.125, TestUtils.floor(algo.getHitRate("DWAYNE", "DUANE")));
        Assertions.assertEquals(0.25, TestUtils.floor(algo.getHitRate("MARTHA", "MARHTA")));
        Assertions.assertEquals(0.7, TestUtils.floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
    }
        
    @Test
    public void testJaro() {
        SimilarityAlgorithm algo = SimilarityAlgorithms.JARO.getAlgorithm();
        
        Assertions.assertEquals(0.766, TestUtils.floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.822, TestUtils.floor(algo.getHitRate("DWAYNE", "DUANE")));
        Assertions.assertEquals(0.896, TestUtils.floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
        Assertions.assertEquals(0.944, TestUtils.floor(algo.getHitRate("MARTHA", "MARHTA")));        
    }
    
    @Test
    public void testJaroWinkler() {
        SimilarityAlgorithm algo = SimilarityAlgorithms.JARO_WINKLER.getAlgorithm();
        
        Assertions.assertEquals(0.813, TestUtils.floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.84, TestUtils.floor(algo.getHitRate("DWAYNE", "DUANE")));
        Assertions.assertEquals(0.961, TestUtils.floor(algo.getHitRate("MARTHA", "MARHTA")));
        Assertions.assertEquals(0.896, TestUtils.floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
    }
    
    @Test
    public void testLevenshtein() {
        SimilarityAlgorithm algo = SimilarityAlgorithms.LEVENSTEIN.getAlgorithm();
        
        Assertions.assertEquals(0.5, TestUtils.floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.666, TestUtils.floor(algo.getHitRate("DWAYNE", "DUANE")));
        Assertions.assertEquals(0.666, TestUtils.floor(algo.getHitRate("MARTHA", "MARHTA")));
        Assertions.assertEquals(0.8, TestUtils.floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
    }
    
}
