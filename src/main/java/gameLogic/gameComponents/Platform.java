package gameLogic.gameComponents;


import gameLogic.SolidBody;

import java.util.Arrays;

public class Platform extends SolidBody {
    private double length;
    private double width;
    private boolean isActive;

    Platform(double length, double width, boolean isActive) {
        this.length = length;
        this.width = width;
        this.isActive = isActive;
    }

    Platform(double length, double width) {
        this.length = length;
        this.width = width;
        this.isActive = false;
    }

    void setActive() {
        isActive = true;
    }

    void setPassive() {
        isActive = false;
    }

    double getLeftBorder() {
        return -length / 2;
    }

    double getRightBorder() {
        return length / 2;
    }

    double getLowerBorder() {
        return -width / 2;
    }

    double getUpperBorder() {
        return width / 2;
    }

    double[][] getPointArray() {
        final double[][] localPoints = new double[][] {
                {getLeftBorder(), getLowerBorder()},
                {getRightBorder(), getLowerBorder()},
                {getRightBorder(), getUpperBorder()},
                {getLeftBorder(), getUpperBorder()}
        };

        return (double[][]) Arrays.stream(localPoints).map(this::toGlobals).toArray();
        /*
        final double[][] result = new double[localPoints.length][2];
        for (int i = 0; i != localPoints.length; ++i) {
            result[i] = toGlobals(result[i]);
        }

        return result;
        */
    }

    public double[] getNorm() {
        return toGlobalsWithoutOffset(new double[]{0, 1});
    }

    public boolean inBounceZone(Ball ball) {
        final double[] localBallPosition = toLocals(ball.getOrigin());
        final double[] checkPoint = new double[]{
                localBallPosition[0], localBallPosition[1] - ball.getRadius()
        };

        if (getLeftBorder() <= checkPoint[0]) if (checkPoint[0] <= getRightBorder())
            if (getLowerBorder() <= checkPoint[1]) if (checkPoint[1] <= getUpperBorder()) return true;
        return false;
    }

}

/*
class Pllatform extends SolidBody {
    constructor(length, width, isActive) {
        super();
        this._length = length;
        this._width = width;
        this._isActive = isActive || false;

        this._optionalPositioningInfo = null;   // extra info necessary to make positioning inside another object
    }

    static platformFromTriangleField(triangleField, _relativeDistance, _relativeLength, _width) {
        let relativeDistance = _relativeDistance || DEFAULT_RELATIVE_DISTANCE;
        let relativeLength = _relativeLength || DEFAULT_RELATIVE_LENGTH;
        let width = _width || DEFAULT_WIDTH;

        let position = triangleField.toGlobals([0, triangleField.height * (relativeDistance - 1)]); // using such
        // coordinates because triangleField coordinate system origin is in the topmost corner.
        let rotation = triangleField.rotation;
        let totalLength = triangleField.getWidthOnRelativeDistance(relativeDistance);
        let platformLength = totalLength * relativeLength;

        let platform = new Platform(platformLength, width);
        platform.moveTo(position);
        platform.rotateTo(rotation);

        platform.optionalPositioningInfo = {
                "originalPosition": platform.position.slice(),
                "maxOffset": totalLength * (1 - relativeLength) / 2
        };

        return platform;
    }




    get optionalPositioningInfo() {
        return this._optionalPositioningInfo;
    }

    set optionalPositioningInfo(optionalInfo) {
        this._optionalPositioningInfo = optionalInfo;
    }

}
*/