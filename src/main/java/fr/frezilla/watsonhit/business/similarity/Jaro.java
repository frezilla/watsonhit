package fr.frezilla.watsonhit.business.similarity;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Mesure la similarité de deux chaines de caractères en appliquant la méthode
 * du calcul de la distance de Jaro.
 * <p>
 * Le résultat est normalisé de façon à avoir une mesure entre 0 (chaines 
 * différentes) et 1 (chaines égales).
 * 
 * Source {@link https://fr.wikipedia.org/wiki/Distance_de_Jaro-Winkler
 */
final class Jaro implements SimilarityAlgorithm {
    
    /**
     * Calcule la distance de Jaro.
     * 
     * @param s1
     * @param s2
     * @return Distance entre 0 et 1
     */
    private double compute(@NonNull String s1, @NonNull String s2) {
        final int s1Length = s1.length();
        final int s2Length = s2.length();
        
        int matchDistance = (int) (((double) Math.max(s1Length, s2Length) / 2.0) - 1);

        boolean[] s1Matches = new boolean[s1Length];
        boolean[] s2Matches = new boolean[s2Length];

        int matches = 0;
        int transpositions = 0;

        for (int index = 0; index < s1Length; index++) {
            List<Integer> indexes = 
                    matches(
                            s1.charAt(index), 
                            index, 
                            s2, 
                            matchDistance
                    );
        
            if (!indexes.isEmpty()) {
                for (Integer indexMatch : indexes) {
                    if (!s2Matches[indexMatch]) {
                        s1Matches[index] = true;
                        s2Matches[indexMatch] = true;
                        matches++;
                        break;
                    }
                }
            }
        }

        double rate;
        if (matches == 0) {
            rate = 0.0;
        } else {
            int k = 0;
            
            for (int i = 0; i < s1Length; i++) {
                if (s1Matches[i]) {
                    while (!s2Matches[k]) k++;
                    if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                    k++;
                }
            }
            
            double m = (double) matches;
            double t = transpositions / 2.0;
            
            rate = ((m / s1Length) + (m / s2Length) + ((m - t) / m)) / 3.0;
        }
        return rate;
    }

    @Override
    public double getHitRate(@NonNull String s1, @NonNull String s2) {
        final int s1Length = s1.length();
        final int s2Length = s2.length();
        
        double rate;
 
        if (s1Length == 0 && s2Length == 0) {
            rate = 0.0;
        } else {
            rate = compute(s1, s2);
        }
        return rate;
    }
    
    /**
     * Retourne la liste des positions des caractères d'une chaine (paramètre s)
     * égaux à un caractère (paramètre c) qui respecte la contrainte de distance 
     * (paramètre matchDistance)
     * <p>
     * La recherche est effectuée dans la plage MAX[0, index - matchDistance] ->
     * MIN[index + matchDistance + 1, longueur chaine]
     * 
     * @param c Caractère à rechercher
     * @param index Index d'origine de la recherche
     * @param s Chaine dans laquelle on recherche le caractère
     * @param matchDistance Distance maximale de recherche
     * @return Liste des positions ou liste vide si aucune correspondance
     */
    private List<Integer> matches(char c, int index, @NonNull String s, int matchDistance) {
        final int sLength = s.length();
        
        final int startSearchIndex = Math.max(0, index - matchDistance);
        final int endSearchIndex = Math.min(index + matchDistance + 1, sLength);
        
        List<Integer> list = new ArrayList<>();
        for (int i = startSearchIndex; i < endSearchIndex; i++) {
            if (s.charAt(i) == c) {
                list.add(i);
            }
        }
        
        return list;
    }
}
