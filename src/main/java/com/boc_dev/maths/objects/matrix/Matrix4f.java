package com.boc_dev.maths.objects.matrix;

import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.Arrays;

public class Matrix4f {

	public static final int SIZE = 4;
	private final float[] elements;

	public static Matrix4f Identity = new Matrix4f(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f
	);

	public static Matrix4f Translation(Vec3f vec3f) {
		return new Matrix4f(
				1.0f, 0.0f, 0.0f, vec3f.getX(),
				0.0f, 1.0f, 0.0f, vec3f.getY(),
				0.0f, 0.0f, 1.0f, vec3f.getZ(),
				0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static Matrix4f Rotation(float angle, Vec3f axis) {
		float[] newElems = new float[16];
		newElems[15] = 1;

		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float C = 1 - cos;

		newElems[0 * SIZE + 0] = cos + axis.getX() * axis.getX() * C;
		newElems[1 * SIZE + 0] = axis.getX() * axis.getY() * C - axis.getZ() * sin;
		newElems[2 * SIZE + 0] = axis.getX() * axis.getZ() * C + axis.getY() * sin;
		newElems[0 * SIZE + 1] = axis.getY() * axis.getX() * C + axis.getZ() * sin;
		newElems[1 * SIZE + 1] = cos + axis.getY() * axis.getY() * C;
		newElems[2 * SIZE + 1] = axis.getY() * axis.getZ() * C - axis.getX() * sin;
		newElems[0 * SIZE + 2] = axis.getZ() * axis.getX() * C - axis.getY() * sin;
		newElems[1 * SIZE + 2] = axis.getZ() * axis.getY() * C + axis.getX() * sin;
		newElems[2 * SIZE + 2] = cos + axis.getZ() * axis.getZ() * C;

		return new Matrix4f(newElems);
	}

	public static Matrix4f Scale(Vec3f scale) {
		return new Matrix4f(
				scale.getX(), 0.0f, 0.0f, 0.0f,
				0.0f, scale.getY(), 0.0f, 0.0f,
				0.0f, 0.0f, scale.getZ(), 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		);
	}

    public static Matrix4f InverseScale(Vec3f scale) {
        return new Matrix4f(
                1.0f/scale.getX(), 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f/scale.getY(), 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f/scale.getZ(), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        );
    }

	public Matrix4f add(Matrix4f matrix) {

		float[] newElements = new float[16];

		for (int i = 0; i < this.elements.length; i++) {
			newElements[i] = this.elements[i] + matrix.getValues()[i];
		}

		return new Matrix4f(newElements);
	}

	public Matrix4f add(Vec3f vec3f) {
		return new Matrix4f(
				elements[0] + vec3f.getX(), elements[1], elements[2], elements[3],
				elements[4], elements[5] + vec3f.getY(), elements[6], elements[7],
				elements[8], elements[9], elements[10] + vec3f.getZ(), elements[11],
				elements[12], elements[13], elements[14], elements[15]
		);
	}

	public Matrix4f multiply(Matrix4f matrix4d) {

		float[] newElements = new float[16];

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				newElements[j * SIZE + i] =
						this.elements[i + SIZE * 0] * matrix4d.getValues()[0 + j * SIZE] +
								this.elements[i + SIZE * 1] * matrix4d.getValues()[1 + j * SIZE] +
								this.elements[i + SIZE * 2] * matrix4d.getValues()[2 + j * SIZE] +
								this.elements[i + SIZE * 3] * matrix4d.getValues()[3 + j * SIZE];
			}
		}

		return new Matrix4f(newElements);

	}

	public Vec3f multiply(Vec3f vec) {
		return new Vec3f(
				(vec.getX() * this.elements[0]) + (vec.getY() * this.elements[1]) + (vec.getZ() * this.elements[2]) + this.elements[3],
				(vec.getX() * this.elements[4]) + (vec.getY() * this.elements[5]) + (vec.getZ() * this.elements[6]) + this.elements[7],
				(vec.getX() * this.elements[8]) + (vec.getY() * this.elements[9]) + (vec.getZ() * this.elements[10] + this.elements[11])
		);
	}

	public Matrix4f(float... elements) {
		assert elements.length == 16;
		this.elements = elements;
	}

	public float get(int x, int y) {
		return elements[y * SIZE + x];
	}

	public float[] getValues() {
		return elements;
	}

	public double[] getValuesD() {
		return new double[]{
				elements[0],
				elements[1],
				elements[2],
				elements[3],
				elements[4],
				elements[5],
				elements[6],
				elements[7],
				elements[8],
				elements[9],
				elements[10],
				elements[11],
				elements[12],
				elements[13],
				elements[14],
				elements[15]
		};
	}

	public static Matrix4f Transform(Vec3f pos, Matrix4f rot, Vec3f scale) {
		Matrix4f translation = Translation(pos);
		Matrix4f scaleMatrix = Scale(scale);
		return scaleMatrix.multiply(rot).multiply(translation);
	}

	public static Matrix4f InverseTransformation(Vec3f pos, Matrix4f rot, Vec3f scale) {
		Matrix4f translation = Translation(pos.neg());
		Matrix4f scaleMatrix = InverseScale(scale);

		return translation.multiply(rot.transpose()).multiply(scaleMatrix);
	}

    public static Matrix4f InverseTransformation(Vec3f pos, QuaternionF rot, Vec3f scale) {
        Matrix4f translation = Translation(pos.neg());
        Matrix4f scaleMatrix = InverseScale(scale);

        return translation.multiply(rot.inverse().toMatrix()).multiply(scaleMatrix);
    }

	public static Matrix4f PerspectiveProjection(float aspect, float fov, float near, float far) {

		float tanHalfFov = (float) Math.tan(fov / 2.0);
		float farNearDel = far - near;

		return new Matrix4f(
				1.0f / (aspect * tanHalfFov), 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f / tanHalfFov, 0.0f, 0.0f,
				0.0f, 0.0f, - (far + near)/farNearDel, - (2 * far * near) / farNearDel,
				0.0f, 0.0f, -1.0f, 0.0f
		);

	}

	public static Matrix4f OrthographicProjection(float width, float height, float near, float far) {

		float right = width;
		float top = -height;
		float left = -width;
		float bottom = height;

		return new Matrix4f(
				2 / (right - left), 0.0f, 0.0f, - (right + left) / (right - left),
				0.0f, 2 / (top - bottom), 0.0f, - (top + bottom) / (top - bottom),
				0.0f, 0.0f, -2 / (far - near), - (far + near) / (far - near),
				0.0f, 0.0f, 0.0f, 1
		);

	}

	public Matrix4f updatePerspectiveProjection(float aspect, float fov) {

		float tanHalfFov = (float) Math.tan(fov / 2.0);

		return new Matrix4f(
				1.0f / (aspect * tanHalfFov), 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f / tanHalfFov, 0.0f, 0.0f,
				0.0f, 0.0f, this.get(2, 2), this.get(3, 2),
				0.0f, 0.0f, -1.0f, 0.0f
		);

	}

	public Vec3f rotate(Vec3f vec) {
		return new Vec3f(
				(vec.getX() * this.elements[0]) + (vec.getY() * this.elements[1]) + (vec.getZ() * this.elements[2]),
				(vec.getX() * this.elements[4]) + (vec.getY() * this.elements[5]) + (vec.getZ() * this.elements[6]),
				(vec.getX() * this.elements[8]) + (vec.getY() * this.elements[9]) + (vec.getZ() * this.elements[10])
		);
	}

	public static Matrix4f View(Vec3f pos, Vec3f rot) {

		Matrix4f translation = Translation(pos.neg());
		Matrix4f rotationX = Rotation(rot.getX(), Vec3f.X);
		Matrix4f rotationY = Rotation(rot.getY(), Vec3f.Y);
		Matrix4f rotationZ = Rotation(rot.getZ(), Vec3f.Z);
		Matrix4f rotation = rotationZ.multiply(rotationY).multiply(rotationX);

		return translation.multiply(rotation);
	}

	public float trace() {
		return get(0, 0) + get(1, 1) + get(2, 2);
	}

	// from https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
	public QuaternionF toQuaternion() {

		float q0;
		float q1;
		float q2;
		float q3;

		// check that trace is bigger than 0
		float trace = trace();
		if (trace > 0) {
			// if it is, the matrix is a pure rotation and so you can use the simple conversion
			// this version is used to reduce numerical errors when dividing by really small numbers
			float traceAddition = (float) (Math.sqrt(trace + 1) * 2.0);
			q0 = traceAddition * 0.25f;
			float q04 = 4 * q0;
			q1 = get(2, 1) - get(1, 2) / q04;
			q2 = get(0, 2) - get(2, 0) / q04;
			q3 = get(1, 0) - get(0, 1) / q04;
		}
		// if the trace is less than or equal to 0, identify which major diagonal element has the greatest value
		else if ((get(0, 0) > get(1, 1) & get(0, 0) > get(2, 2))) {
			float S = (float) (Math.sqrt(1.0 + get(0, 0) - get(1, 1) - get(2, 2)) * 2); // S=4*qx
			q0 = (get(2, 1) - get(1, 2)) / S;
			q1 = 0.25f * S;
			q2 = (get(0, 1) + get(1, 0)) / S;
			q3 = (get(0, 2) + get(2, 0)) / S;
		} else if (get(1, 1) > get(2, 2)) {
			float S = (float) (Math.sqrt(1.0 + get(1, 1) - get(0, 0) - get(2, 2)) * 2); // S=4*qy
			q0 = (get(0, 2) - get(2, 0)) / S;
			q1 = (get(0, 1) + get(1, 0)) / S;
			q2 = 0.25f * S;
			q3 = (get(1, 2) + get(2, 1)) / S;
		} else {
			float S = (float) (Math.sqrt(1.0 + get(2, 2) - get(0, 0) - get(1, 1)) * 2); // S=4*qz
			q0 = (get(1, 0) - get(0, 1)) / S;
			q1 = (get(0, 2) + get(2, 0)) / S;
			q2 = (get(1, 2) + get(2, 1)) / S;
			q3 = 0.25f * S;
		}

		return new QuaternionF(q0, q1, q2, q3);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Matrix4f matrix4d = (Matrix4f) o;
		return Arrays.equals(elements, matrix4d.elements);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(elements);
	}

	public Matrix4f transpose() {

		float[] newElements = new float[16];

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				newElements[j * SIZE + i] = this.getValues()[i * SIZE + j];
			}
		}

		return new Matrix4f(newElements);
	}

	public Vec3f getXVec() {
		return new Vec3f(get(0,0), get(1,0), get(2,0));
	}

	public Vec3f getYVec() {
		return new Vec3f(get(0,1), get(1,1), get(2,1));
	}

	public Vec3f getZVec() {
		return new Vec3f(get(0,2), get(1,2), get(2,2));
	}

	public Matrix4d toMatrix4d() {

		double[] newElems = new double[elements.length];

		for (int i = 0; i < elements.length; i++) {
			newElems[i] = elements[i];
		}

		return new Matrix4d(newElems);
	}

	// only works for affine transformations
	public Matrix4f invert() {

		// get inverse of rotation part (1/transpose)
		float[] inverseRotation = new float[] {
				get(0, 0), get(0, 1), get(0, 2),
				get(1, 0), get(1, 1), get(1, 2),
				get(2, 0), get(2, 1), get(2, 2)
		};

		// get inverse of translation parts (-ve)
		float x = -get(3, 0);
		float y = -get(3, 1);
		float z = -get(3, 2);
		float[] translationInverse = new float[] {
				(x * inverseRotation[0]) + (y * inverseRotation[1]) + (z * inverseRotation[2]),
				(x * inverseRotation[3]) + (y * inverseRotation[4]) + (z * inverseRotation[5]),
				(x * inverseRotation[6]) + (y * inverseRotation[7]) + (z * inverseRotation[8])
		};

		float[] inverse = new float[] {
				inverseRotation[0], inverseRotation[1], inverseRotation[2], translationInverse[0],
				inverseRotation[3], inverseRotation[4], inverseRotation[5], translationInverse[1],
				inverseRotation[6], inverseRotation[7], inverseRotation[8], translationInverse[2],
				0, 0, 0, 1
		};

		return new Matrix4f(inverse);
	}

	private float invertDigit(float s) {
		if (s == 0) {
			return 0;
		} else {
			return 1/s;
		}
	}

	public Vec3f getTranslation() {
		return new Vec3f(
				get(3, 0),
				get(3, 1),
				get(3, 2)
		);
	}

	public float getTranslationX() {
		return get(3, 0);
	}

	public float getTranslationY() {
		return get(3, 1);
	}

	public float getTranslationZ() {
		return get(3, 2);
	}

	public Matrix4f scale(float s) {
		return new Matrix4f(
				get(0,0) * s, get(1,0) * s, get(2,0) * s, get(3,0) * s,
				get(0,1) * s, get(1,1) * s, get(2,1) * s, get(3,1) * s,
				get(0,2) * s, get(1,2) * s, get(2,2) * s, get(3,2) * s,
				get(0,3) * s, get(1,3) * s, get(2,3) * s, get(3,3) * s
		);
	}
}
