package fr.frezilla.watsonhit;

import fr.frezilla.watsonhit.algorithms.Jaro;
import fr.frezilla.watsonhit.algorithms.Levenshtein;

public class Launch {
    
    public static void main(String[] args) {
        Levenshtein l = new Levenshtein();
//        System.out.println(l.calculate("NICHE", "CHIENS"));
//        System.out.println(l.calculate("NICHE", "NICHES"));
//        System.out.println(l.calculate("NICHE.", "NICHES"));
//        System.out.println(l.calculate("NICHE", "NICHE"));
        
        System.out.println(l.getHitRate("Grunberg Agency", "Agency Grunberg"));
        System.out.println(l.getHitRate("", "rehcerche d'une tbalrette"));
//        System.out.println(l.getHitRate("rehcerche", "recherche"));
//        System.out.println(l.getHitRate("dnas", "dans"));
        

        Jaro j = new Jaro();
        System.out.println(j.getHitRate("MARTHA", "MARHTA"));
        System.out.println(j.getHitRate("DWAYNE", "DUANE"));
        System.out.println(j.getHitRate("DIXON", "DICKSONX"));
        
        System.out.println(j.getHitRate("AABABCAAAC", "ABAACBAAAC"));
        
           
    }
    
}

