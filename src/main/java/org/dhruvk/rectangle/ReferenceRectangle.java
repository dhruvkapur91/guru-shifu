package org.dhruvk.rectangle;

import java.util.Objects;

class ReferenceRectangle {
    public final int length;
    public final int breath;

    public int getLength() {
        return length;
    }

    public int getBreath() {
        return breath;
    }

    public ReferenceRectangle(int length, int breath) {
        this.length = length;
        this.breath = breath;
    }

    public int area() {
        return length * breath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceRectangle that = (ReferenceRectangle) o;
        return length == that.length &&
                breath == that.breath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, breath);
    }
}
