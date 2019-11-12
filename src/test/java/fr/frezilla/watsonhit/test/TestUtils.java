package fr.frezilla.watsonhit.test;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class TestUtils {
    
    public static double floor(double d) {
        return (int) (d * 1000) / 1000.0;
    }
}
