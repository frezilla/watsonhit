package fr.frezilla.watsonhit.test;

import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithm;
import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithms;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SimilarityAlgorithmsTU {
        
    @Test
    public void testJaro() {
        SimilarityAlgorithm algo = SimilarityAlgorithms.JARO.getAlgorithm();
        
        Assertions.assertEquals(0.944, TestUtils.floor(algo.getHitRate("MARTHA", "MARHTA")));
        Assertions.assertEquals(0.766, TestUtils.floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.896, TestUtils.floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
        Assertions.assertEquals(0.822, TestUtils.floor(algo.getHitRate("DWAYNE", "DUANE")));
        
        algo.getHitRate("GRUNBERG AGENCY", "AGENCE GRUNBERG");
    }
    
}
