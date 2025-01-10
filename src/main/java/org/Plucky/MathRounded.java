package org.Plucky;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathRounded {

    public void readAandB() {
        BigDecimal a = new BigDecimal("46737.373618");
        BigDecimal b = a.setScale(3, RoundingMode.HALF_UP);

        BigDecimal c = new BigDecimal("1729282.838");
        BigDecimal d= c.setScale(2, RoundingMode.HALF_UP);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
    }
}
