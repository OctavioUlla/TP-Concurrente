package Main;

class Rational {
    long numerator;
    long denominator;

    public Rational(long numerator, long denominator) {
        long gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;

        // Ensure the sign of the rational number is in the numerator
        if (this.denominator < 0) {
            this.numerator = -this.numerator;
            this.denominator = -this.denominator;
        }
    }

    private long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public Rational add(Rational other) {
        long newNumerator = this.numerator * other.denominator + this.denominator * other.numerator;
        long newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator);
    }

    public Rational negate() {
        return new Rational(-this.numerator, this.denominator);
    }

    public Rational multiply(Rational other) {
        long newNumerator = this.numerator * other.numerator;
        long newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }
}