package gameLogic.gameComponents;

import gameLogic.SolidBody;
import gameLogic.geometryShapes.Triangle;

import java.util.Arrays;


public class TriangleField extends SolidBody {
    private Triangle triangle;
    private Boolean isNeutral;
    private Boolean isLoser;

    double getHeight() {
        return triangle.getHeight();
    }

    double getHalfWidth() {
        return triangle.getHalfWidth();
    }

    double[][] getPointArray() {
        final double[][] localPoints = triangle.getPointArray();
        return (double[][]) Arrays.stream(localPoints).map(this::toGlobals).toArray();
    }

    double[] getBottomNorm() {
        return this.toGlobalsWithoutOffset(new double[]{0, 1});
    }

    boolean isNeutral() {
        return isNeutral;
    }

    boolean isLoser() {
        return isLoser;
    }

    void setLoser() {
        isLoser = true;
    }

    boolean containsGlobalPoint(double[] point) {
        return triangle.containsPoint(toLocals(point));
    }

    boolean containsLocalPoint(double[] point) {
        return triangle.containsPoint(point);
    }

    boolean reachesBottomLevel(Ball ball) {
        final double[] localBottomPosition = toLocals(ball.getOrigin());
        final double ballRadius = ball.getRadius();

        return triangle.getBottomDistance(localBottomPosition) < ballRadius;
    }

    double getWidthOnDistance(double bottomDistance) {
        return triangle.getWidthOnDistance(bottomDistance);
    }

    double getWidthRelativeDistance(double relativeDistance) {
        return triangle.getWidthOnDistance(relativeDistance * triangle.getHeight());
    }

}

/*
class TriangleField extends SolidBody {
    constructor(height, sectorAngle, isNeutral) {
        super();
        this._triangle = new shapes.Triangle(height, sectorAngle);
        this._isNeutral = isNeutral;
        this._isLoser = false;
    }
}
*/
