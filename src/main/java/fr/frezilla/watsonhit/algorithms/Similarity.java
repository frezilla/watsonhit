package fr.frezilla.watsonhit.algorithms;

public interface Similarity {
    
    /**
     * Calcule le taux de similarité entre deux chaines de caractères.
     * <p>
     * La valeur retournée est comprise entre 0.0 et 1.0; une valeur proche de 
     * 0.0 indique une différence importante et une valeur proche de 1.0 indique
     * une similarité proche.
     * 
     * @param s1 1ère chaine de caractères à traiter
     * @param s2 2ème chaine de caractères à traiter
     * @return Taux de similarité
     */
    double getHitRate(String s1, String s2);
    
}
