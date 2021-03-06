package com.boc_dev.maths.objects.vector;

import com.boc_dev.maths.objects.matrix.Matrix4d;

import java.util.Objects;

public class Vec3d implements Vecd {

	public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
	public static final Vec3d X = new Vec3d(1.0, 0.0, 0.0);
	public static final Vec3d Y = new Vec3d(0.0, 1.0, 0.0);
	public static final Vec3d Z = new Vec3d(0.0, 0.0, 1.0);
	public static final Vec3d ONE = new Vec3d(1.0, 1.0, 1.0);

	private final double x;
	private final double y;
	private final double z;

	public Vec3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Create a restricted vector
	public Vec3d(Vec3d momentumUnrestricted, double limit) {
		x = Math.min(momentumUnrestricted.getX(), limit);
		y = Math.min(momentumUnrestricted.getY(), limit);
		z = Math.min(momentumUnrestricted.getZ(), limit);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Vec3d add(Vecd vec) {
		assert vec instanceof Vec3d;
		Vec3d vec3d = (Vec3d) vec;
		return new Vec3d(
				this.x + vec3d.x,
				this.y + vec3d.y,
				this.z + vec3d.z);
	}

	public Vec3d subtract(Vecd vec) {
		assert vec instanceof Vec3d;
		Vec3d vec3d = (Vec3d) vec;
		return new Vec3d(
				this.x - vec3d.x,
				this.y - vec3d.y,
				this.z - vec3d.z);
	}

	public Vec3d scale(double s) {
		return new Vec3d(
				this.x * s,
				this.y * s,
				this.z * s);
	}

	public double dot(Vecd vec) {
		assert vec instanceof Vec3d;
		Vec3d vec3d = (Vec3d) vec;
		return
				this.x * vec3d.getX() +
				this.y * vec3d.getY() +
				this.z * vec3d.getZ();
	}

	public double length2() {
		return
				(this.x * this.x) +
				(this.y * this.y) +
				(this.z * this.z);
	}

	public double length() {
		return Math.sqrt(length2());
	}

	public Vec3d normalise() {
		if (this.length() == 0.0 ) {
			return Vec3d.ZERO;
		}
		return this.scale(1/this.length());
	}

	public double[] getValues() {
		return new double[] {x, y, z};
	}

	public Matrix4d outerProduct(Vecd vec3d) {

		double[] elements = new double[16];

		for (int thisVecIndex = 0; thisVecIndex < this.getValues().length; thisVecIndex++) {
			for (int otherVecIndex = 0; otherVecIndex < vec3d.getValues().length; otherVecIndex++) {

				elements[thisVecIndex * 4 + otherVecIndex] = this.getValues()[thisVecIndex] * vec3d.getValues()[otherVecIndex];

			}
		}

		return new Matrix4d(elements);
	}

	public Vec3d cross(Vecd vec) {
		assert vec instanceof Vec3d;
		Vec3d vec3d = (Vec3d) vec;
		return new Vec3d(
				this.y * vec3d.z - this.z * vec3d.y,
				this.z * vec3d.x - this.x * vec3d.z,
				this.x * vec3d.y - this.y * vec3d.x
		);
	}

	public Vec3d neg() {
		return new Vec3d(
				-this.x,
				-this.y,
				-this.z
		);
	}

	// used for integration
	public Matrix4d star() {
		return new Matrix4d(
				0.0, -z, y, 0.0,
				z, 0.0, -x, 0.0,
				-y, x, 0.0, 0.0,
				0.0, 0.0, 0.0, 1.0
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vec3d vec3d = (Vec3d) o;
		return Double.compare(vec3d.x, x) == 0 &&
				Double.compare(vec3d.y, y) == 0 &&
				Double.compare(vec3d.z, z) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}

	public Vec3d multiply(Matrix4d m) {
		return new Vec3d(
				this.dot(m.getXVec()),
				this.dot(m.getYVec()),
				this.dot(m.getZVec())
		);
	}

	// element wise multiplication
	public Vec3d multiply(Vecd vec) {
		assert vec instanceof Vec3d;
		Vec3d vec3d = (Vec3d) vec;
		return new Vec3d(
				x * vec3d.getX(),
				y * vec3d.getY(),
				z * vec3d.getZ()
		);
	}

	@Override
	public double get(int i) {
		switch (i) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				throw new RuntimeException(i + " is out of bounds for current vector");
		}
	}

    @Override
    public Vecf toVecf() {
        return new Vec3f((float) x, (float) y, (float) z);
    }

    public Vec3f toVec3f() {
		return new Vec3f((float) x, (float) y, (float) z);
    }

    public static Vec3d Min(Vec3d a, Vec3d b) {
		return new Vec3d(
				Math.min(a.getX(), b.getX()),
				Math.min(a.getY(), b.getY()),
				Math.min(a.getZ(), b.getZ())
		);
	}

	public static Vec3d Max(Vec3d a, Vec3d b) {
		return new Vec3d(
				Math.max(a.getX(), b.getX()),
				Math.max(a.getY(), b.getY()),
				Math.max(a.getZ(), b.getZ())
		);
	}

	public float[] getValuesF() {
		return new float[] {(float) x, (float) y, (float) z};
	}

    public Vec3d reflectionOverPlane(Vec4d plane) {

        // find point on plane
        // find normal
        Vec3d n = new Vec3d(plane.getX(), plane.getY(), plane.getZ());

        // point on above type defined plane is the normal time s by the scale away from 0
        Vec3d pointOnPlane = n.scale(plane.getS());

        // find Point we want to reflect - point on plane
        Vec3d D = this.subtract(pointOnPlane);

        // Find component of D that is normal to plane
        Vec3d Dn = n.scale(D.dot(n));

        // find D reflected
        Vec3d Dref = D.subtract(Dn.scale(2));

        // now add in point on quad to find the reflected point
        return pointOnPlane.add(Dref);

    }

    public Vecd lerp(Vecd vecb, double percent) {
        return (this.scale(1 - percent)).add(vecb.scale(percent));
    }

}
