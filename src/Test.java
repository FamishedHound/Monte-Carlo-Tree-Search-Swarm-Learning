import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

        ArrayList<WeightedObservedPoint> points = new ArrayList<>();
        points.add(new WeightedObservedPoint(1,1,8));
        points.add(new WeightedObservedPoint(1,0,3));
        points.add(new WeightedObservedPoint(1,0,4));
        points.add(new WeightedObservedPoint(1,3,4));

        double newParameter=0;
        for(int i=0;i<4;i++){
            newParameter += i*fitter.fit(points)[i] * Math.pow(Math.abs(2),i-1);
        }
        System.out.println(newParameter);

        System.out.println(Arrays.toString(fitter.fit(points)));

    }
}
