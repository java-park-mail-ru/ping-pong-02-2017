package gameLogic.gameComponents;


import gameLogic.SolidBody;


public class Platform extends SolidBody {
    private double length;
    private double width;
    private boolean isActive;

    public Platform(double length, double width, boolean isActive) {
        this.length = length;
        this.width = width;
        this.isActive = isActive;
    }

    public Platform(double length, double width) {
        this.length = length;
        this.width = width;
        this.isActive = false;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setActive() {
        isActive = true;
    }

    public void setPassive() {
        isActive = false;
    }

    public double[][] getPointArray() {
        final double[][] localPoints = new double[][] {
                {getLeftBorder(), getLowerBorder()},
                {getRightBorder(), getLowerBorder()},
                {getRightBorder(), getUpperBorder()},
                {getLeftBorder(), getUpperBorder()}
        };

        final double[][] result = new double[localPoints.length][2];
        for (int i = 0; i != localPoints.length; ++i) {
            result[i] = toGlobals(localPoints[i]);
        }

        return result;
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

    private double getLeftBorder() {
        return -length / 2;
    }

    private double getRightBorder() {
        return length / 2;
    }

    private double getLowerBorder() {
        return -width / 2;
    }

    private double getUpperBorder() {
        return width / 2;
    }

}

/*
class Pllatform extends SolidBody {

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