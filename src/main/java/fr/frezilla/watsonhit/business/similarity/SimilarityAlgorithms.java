package fr.frezilla.watsonhit.business.similarity;

/**
 * Enumération des algorithmes de calcul de similarité entre deux chaines de
 * caractères.
 * <p>
 * L'algorithme est accessible via la méthode 
 * {@link SimilarityAlgorithms#getAlgorithm() }
 * <p>
 * Les algorithmes proposés sont :
 * <ul>
 *  <li>La distance de Jaro</li>
 *  <li>La distance de Jaro-Winkler</li>
 *  <li>La distance de Levenstein (dont le résultat est adaptée pour retourner 
 *      une valeur comprise entre 0 et 1)</li>
 * </ul>
 */
public enum SimilarityAlgorithms {
    JARO(new Jaro()),
    JARO_WINKLER(new JaroWinkler()),
    LEVENSTEIN(new Levenshtein());

    private final SimilarityAlgorithm algo;
    
    private SimilarityAlgorithms(SimilarityAlgorithm algo) {
        this.algo = algo;
    }
    
    public SimilarityAlgorithm getAlgorithm() {
        return algo;
    }
}
