package fr.frezilla.watsonhit.test;

import fr.frezilla.watsonhit.algorithms.Jaro;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class JaroTest extends AbstractTest {
    
    public JaroTest() {
        algo = new Jaro();
    }
    
    @Test
    public void getHitRate() {
        Assertions.assertEquals(0.944, floor(algo.getHitRate("MARTHA", "MARHTA")));
        Assertions.assertEquals(0.766, floor(algo.getHitRate("DIXON", "DICKSONX")));
        Assertions.assertEquals(0.896, floor(algo.getHitRate("JELLYFISH", "SMELLYFISH")));
        Assertions.assertEquals(0.822, floor(algo.getHitRate("DWAYNE", "DUANE")));
    }
    
   
}
