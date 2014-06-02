package no.plasmid.lib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
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
	
	protected void compileShader(int shaderProgramId, String source, int type) {
		int shaderId = GL20.glCreateShader(type);
		if (0 == shaderId) {
			throw new IllegalStateException("Could not create shader!");
		}
		
		GL20.glShaderSource(shaderId, source);
		GL20.glCompileShader(shaderId);
		int compileStatus = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
		if (0 == compileStatus) {
			String error = GL20.glGetShaderInfoLog(shaderId, 2048);
			throw new IllegalStateException("Error when compiling shader: " + error);
		}
		
		GL20.glAttachShader(shaderProgramId, shaderId);
	}
	
	protected String loadTextFile(String fileName) throws FileNotFoundException {
		URL fileURL = AbstractRenderer.class.getResource(fileName);
		if (fileURL == null) {
			throw new FileNotFoundException("Could not find shader source file " + fileName);
		}
		
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		FileInputStream fis;
		Scanner scanner = null;
		try {
			fis = new FileInputStream(fileURL.getFile());
			scanner = new Scanner(fis);
			
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		
		return text.toString();
	}
	
}
