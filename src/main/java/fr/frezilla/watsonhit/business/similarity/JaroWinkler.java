package fr.frezilla.watsonhit.business.similarity;

import lombok.NonNull;

/**
 * Mesure la similarité de deux chaines de caractères en appliquant la méthode
 * du calcul de la distance de Jaro-Winkler.
 * <p>
 * Le résultat est normalisé de façon à avoir une mesure entre 0 (chaines
 * différentes) et 1 (chaines égales).
 * <p>
 * Le coefficient qui permet de favoriser les chaînes avec un préfixe commun est
 * fixé à 0.1 comme proposé par Winkler.
 * <p>
 * Source {@linkplain https://fr.wikipedia.org/wiki/Distance_de_Jaro-Winkler
 */
final class JaroWinkler implements SimilarityAlgorithm {

    private final Jaro algo;

    /**
     * Constructeur
     */
    public JaroWinkler() {
        algo = new Jaro();
    }

    @Override
    public double getHitRate(@NonNull String s1, @NonNull String s2) {
        double dj = algo.getHitRate(s1, s2);
        double p = 0.1;

        double l = 0;
        int scanLength = Math.min(Math.min(s1.length(), s2.length()), 4);
        for (int i = 0; i < scanLength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                l++;
            } else {
                break;
            }
        }

        return dj + (l * p * (1 - dj));
    }

}
