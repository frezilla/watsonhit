package fr.frezilla.watsonhit.business.similarity;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

final class Levenshtein implements SimilarityAlgorithm {
    
    /**
     * Calcul la distance de Levenshtein entre deux chaînes de caractères 
     * courtes.
     * <p>
     * Cet algorithme ne s'occupe pas de déplacement, il ne détecte que la 
     * suppression ou l'insertion d'une lettre, ainsi que le remplacement d'une 
     * lettre par une autre.
     * 
     * Source {@link https://fr.wikipedia.org/wiki/Distance_de_Levenshtein}
     * 
     * @param s1
     * @param s2
     * @return 
     */
    public int calculate(@NonNull String s1, @NonNull String s2) {
        final int s1Length = s1.length();
        final int s2Lenght = s2.length();
        final int nbMatrixRows = s1Length + 1;
        final int nbMatrixLines = s2Lenght + 1;
        
        int matrix[][] = new int[nbMatrixRows][nbMatrixLines];
        
        for (int i = 0; i < nbMatrixRows; i++) {
            matrix[i][0] = i;
        }
        for (int i = 0; i < nbMatrixLines; i++) {
            matrix[0][i] = i;
        }
        
        for (int i = 1; i <= s1Length; i++) {
            for (int j = 1; j <= s2Lenght; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                
                matrix[i][j] = 
                        minimum(
                                matrix[i - 1][j] + 1,                           // Effacement du nouveau caractère de s1
                                matrix[i][j - 1] + 1,                           // Insertion dans s2 du nouveau caractère de s1
                                matrix[i - 1][j - 1] + cost                     // Substitution
                        );
            }
        }
        
        return matrix[s1Length][s2Lenght];
    }
    
    @Override
    public double getHitRate(@NonNull String s1, @NonNull String s2) {
        final int s1Length = s1.length();
        final int s2Length = s2.length();
        
        double hitRate;
        
        if (s1Length == 0 && s2Length == 0) {
            hitRate = 1.0;
        } else {
            int distance = calculate(s1, s2);
            if (s1Length < s2Length) {
                hitRate = 1.0 - ((double) distance/s2Length);
            } else {
                hitRate = 1.0 - ((double) distance/s1Length);
            }
        } 
        
        return hitRate;
    }
    
    private int minimum(@NonNull int...values) {
        final int valuesSize = values.length;
        
        List<Integer> list = new ArrayList<>(valuesSize);
        for (int i = 0; i < valuesSize; i++) {
            list.add(values[i]);
        }
        
        int minValue = Integer.MAX_VALUE;
        
        for (Integer value : list) {
            if (value != null && value < minValue) {
                minValue = value;
            }
        }
        
        return minValue;
    }
    
}
