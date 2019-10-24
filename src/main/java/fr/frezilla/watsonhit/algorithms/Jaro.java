package fr.frezilla.watsonhit.algorithms;

import lombok.NonNull;

/**
 * https://fr.wikipedia.org/wiki/Distance_de_Jaro-Winkler
 * @author f.balme
 */
public class Jaro implements Similarity {

    @Override
    public double getHitRate(@NonNull String s1, @NonNull String s2) {
        final int s1Length = s1.length();
        final int s2Length = s2.length();
        
        double rate;
 
        if (s1Length == 0 && s2Length == 0) {
            rate = 0.0;
        } else {
            int matchDistance = (int) (((double) Math.max(s1Length, s2Length) / 2.0) - 1);

            boolean[] s1Matches = new boolean[s1Length];
            boolean[] s2Matches = new boolean[s2Length];

            int matches = 0;
            int transpositions = 0;

            for (int indexS1 = 0; indexS1 < s1Length; indexS1++) {
                int startSearchIndex = Math.max(0, indexS1 - matchDistance);
                int endSearchIndex = Math.min(indexS1 + matchDistance + 1, s2Length);

                for (int indexS2 = startSearchIndex; indexS2 < endSearchIndex; indexS2++) {
                    if (!s2Matches[indexS2] && s1.charAt(indexS1) == s2.charAt(indexS2)) {
                        s1Matches[indexS1] = true;
                        s2Matches[indexS2] = true;
                        matches++;
                        break;
                    }
                }
            }

            if (matches == 0) return 0;

            int k = 0;
            for (int i = 0; i < s1Length; i++) {
                if (!s1Matches[i]) continue;
                while (!s2Matches[k]) k++;
                if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                k++;
            }

            return (((double)matches / s1Length) +
                    ((double)matches / s2Length) +
                    (((double)matches - transpositions/2.0) / matches)) / 3.0;
        }
        return rate;
    }
}
