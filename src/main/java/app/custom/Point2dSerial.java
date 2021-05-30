package app.custom;

import javafx.geometry.Point2D;

import java.io.Serializable;

public class Point2dSerial extends Point2D implements Serializable {

    public Point2dSerial(double v, double v1) {
        super(v, v1);
    }
}
