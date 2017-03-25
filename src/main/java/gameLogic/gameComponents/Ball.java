package gameLogic.gameComponents;


import gameLogic.SolidBody;
import gameLogic.geometryShapes.Circle;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Ball extends SolidBody {
    private Circle circle;

    public Ball(double radius) {
        super();

        circle = new Circle(radius);
    }

    public double getRadius() {
        return this.circle.getRadius();
    }

    void bounce(double[] inputNormVec) {
        final RealVector normVec = new ArrayRealVector(inputNormVec);
        final RealVector normVec0 = normVec.mapDivide(normVec.getNorm());

        final RealMatrix normMatrix = MatrixUtils.createRealMatrix(
                new double[][]{
                        {normVec0.getEntry(0) * normVec0.getEntry(0), normVec0.getEntry(0), normVec0.getEntry(1)},
                        {normVec0.getEntry(0) * normVec0.getEntry(1), normVec0.getEntry(1), normVec0.getEntry(1)}
                }
        );

        final RealMatrix identity = MatrixUtils.createRealIdentityMatrix(2);
        final RealMatrix transformationMatrix = identity.subtract(normMatrix.scalarMultiply(2));

        final double[] newVelocity = transformationMatrix.operate(getVelocity());
        for (int i = 0; i != newVelocity.length; ++i) {
            newVelocity[i] *= -1;       // TODO проверить. Возможно, в оригинале эта мера вызвана левой системой координат.
        }

        setVelocity(newVelocity);
    }
}
