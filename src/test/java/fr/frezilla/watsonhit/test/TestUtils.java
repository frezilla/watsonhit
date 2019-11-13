package fr.frezilla.watsonhit.test;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class TestUtils {
    
    public static double floor(float f) {
        return (int) (f * 1000) / 1000.0;
    }
}
