package fr.frezilla.watsonhit.business.similarity;

public enum SimilarityAlgorithms {
    JARO(new Jaro());

    private final SimilarityAlgorithm algo;
    
    private SimilarityAlgorithms(SimilarityAlgorithm algo) {
        this.algo = algo;
    }
    
    public SimilarityAlgorithm getAlgorithm() {
        return algo;
    }
}
