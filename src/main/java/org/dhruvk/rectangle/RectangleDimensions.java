package org.dhruvk.rectangle;

import java.util.Objects;

class RectangleDimensions {
    public final int length;
    public final int breath;

    public int getLength() {
        return length;
    }

    public int getBreath() {
        return breath;
    }

    public RectangleDimensions(int length, int breath) {
        this.length = length;
        this.breath = breath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RectangleDimensions that = (RectangleDimensions) o;
        return length == that.length &&
                breath == that.breath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, breath);
    }
}
