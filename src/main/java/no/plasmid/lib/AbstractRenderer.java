package no.plasmid.lib;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

public abstract class AbstractRenderer {

	public Matrix4f calculateProjectionMatrix(int width, int height, float zNear, float zFar, float fov) {
		Matrix4f rc = new Matrix4f();
		
		float aspectRatio = (float)width / (float)height;
		float yScale = (float)(1.0f / Math.tan(Math.toRadians((fov / 2))));
		float xScale = yScale / aspectRatio;
		
		rc.m00 = xScale;
		rc.m11 = yScale;
		rc.m22 = -((zFar + zNear)/(zFar - zNear));
		rc.m23 = -1;
		rc.m32 = -((2 * zNear * zFar) / (zFar - zNear));
		rc.m33 = 0;
		
		return rc;
	}
	
	/**
	 * Check for OpenGL error, and throw exception if any are found
	 */
	protected void checkGL() {
		final int code = GL11.glGetError();
		if (code != 0) {
			final String errorString = GLU.gluErrorString(code);
			final String message = "OpenGL error (" + code + "): " + errorString;
			throw new IllegalStateException(message);
		}
	}
	
}
