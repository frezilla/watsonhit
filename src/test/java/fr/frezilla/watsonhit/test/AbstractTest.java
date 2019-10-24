package fr.frezilla.watsonhit.test;

import fr.frezilla.watsonhit.algorithms.Similarity;

public abstract class AbstractTest {
    
    protected Similarity algo;
    
    protected final double floor(double d) {
        return (int) (d * 1000) / 1000.0;
    }
}
