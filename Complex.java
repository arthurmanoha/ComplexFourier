package complexfourier;

import static java.lang.Math.PI;

/**
 * Complex numbers
 *
 * @author arthu
 */
public class Complex {

    private double radius, argument;

    /**
     * Define a complex number in polar form (radius and argument)
     *
     * @param newRadius
     * @param newArgument
     */
    public Complex(double newRadius, double newArgument) {
        this.radius = newRadius;
        this.argument = newArgument % (2 * PI);
    }

    /**
     * Define a purely real number
     *
     * @param realPart
     */
    public Complex(double realPart) {
        this(realPart, 0);
    }

    /**
     * Define zero
     *
     */
    public Complex() {
        this(0, 0);
    }

    /**
     * Set the number's value in cartesian coordinates, not polar form
     *
     * @param x
     * @param y
     */
    public void setCartesianValue(double x, double y) {
        this.radius = Math.sqrt(x * x + y * y);
        this.argument = Math.atan2(y, x);
    }

    public double getRadius() {
        return this.radius;
    }

    public double getArgument() {
        return this.argument;
    }

    public void setRadius(double newR) {
        this.radius = newR;
    }

    public void setArgument(double newA) {
        this.argument = newA;
    }

    public double getX() {
        return radius * Math.cos(argument);
    }

    public double getY() {
        return radius * Math.sin(argument);
    }

    /**
     * Returns this*other
     *
     * @param other
     * @return this*other
     */
    public Complex mult(Complex other) {
        return new Complex(this.radius * other.radius, this.argument + other.argument);
    }

    /**
     * Scale a complex by a scalar
     *
     * @param val
     * @return
     */
    public Complex mult(double val) {
        return new Complex(this.radius * val, this.argument);
    }

    /**
     * Add a number to this, return the result.
     *
     * @param increment
     * @return
     */
    public Complex add(Complex increment) {
        double xSum = this.getX() + increment.getX();
        double ySum = this.getY() + increment.getY();
        Complex sum = new Complex();
        sum.setCartesianValue(xSum, ySum);
        return sum;
    }

    public String toString() {
        return "C{r = " + this.radius
                + ", arg = " + (argument / PI) + " * pi}";
    }
}
