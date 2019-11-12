package fr.frezilla.watsonhit.business.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
final class Jaccard implements SimilarityAlgorithm {
    
    private final static int NGRAMSLENGTH = 2;

    @Override
    public float getHitRate(@NonNull String s1, @NonNull String s2) {
        List<String> list1 = splitString(s1, NGRAMSLENGTH);
        List<String> list2 = splitString(s2, NGRAMSLENGTH);
        
        float coef;
        
        if (list1.isEmpty() && list2.isEmpty()) {
            coef = 1.0f;
        } else if (list1.isEmpty() || list2.isEmpty()) {
            coef = 0.0f;
        } else {
            int intersection = ListUtils.intersection(list1, list2).size();
            coef = intersection / (float) (list1.size() + list2.size() - intersection);
        }
        
        return 1 - coef;
    }

    private List<String> splitString(@NonNull String s, int ngrams) {
        Set<String> set = new TreeSet<>();
        
        for (int i = 0; i < s.length(); i++) {
            String subString = StringUtils.substring(s, i, i + ngrams);
            if (subString.length() == ngrams) {
                set.add(subString);
            } else {
                break;
            }
        }
        return new ArrayList<>(set);
    }
    
    
    
}
