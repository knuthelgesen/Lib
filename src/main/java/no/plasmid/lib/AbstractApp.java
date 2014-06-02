package no.plasmid.lib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Abstract application class.
 *
 */
public abstract class AbstractApp 
{

	/**
	 * Get the path where the native files should be stored.
	 * @return
	 */
	protected abstract String getCodeSourcePathString();
	
  /**
   * Load native libraries required by LWJGL based on platform the application is running on.
   * @param platform the platform the application is running on.
   */
  protected void loadNatives(SupportedPlatform platform) {
  	if (null == platform) {
  		throw new IllegalArgumentException("Platform can not be null");
  	}
  	
  	//Application running path
  	String path = getCodeSourcePathString();
  	
  	//Copy required libraries based on platform
  	switch (platform) {
		case LINUX:
	  	copyFileFromJar("/native/linux/libjinput-linux.so", path + "/native/libjinput-linux.so");
			copyFileFromJar("/native/linux/liblwjgl.so", path + "/native/liblwjgl.so");
			copyFileFromJar("/native/linux/libopenal.so", path + "/native/libopenal.so");  	
			break;
		case LINUX_64:
	  	copyFileFromJar("/native/linux/libjinput-linux64.so", path + "/native/libjinput-linux64.so");
			copyFileFromJar("/native/linux/liblwjgl64.so", path + "/native/liblwjgl64.so");
			copyFileFromJar("/native/linux/libopenal64.so", path + "/native/libopenal64.so");  	
			break;
		case MAC:
	  	copyFileFromJar("/native/macosx/libjinput-osx.jnilib", path + "/native/libjinput-osx.jnilib");
			copyFileFromJar("/native/macosx/liblwjgl.lnilib", path + "/native/liblwjgl.lnilib");
			copyFileFromJar("/native/macosx/openal.dylib", path + "/native/openal.dylib");  	
			break;
		case MAC_64:
	  	copyFileFromJar("/native/macosx/libjinput-osx.jnilib", path + "/native/libjinput-osx.jnilib");
			copyFileFromJar("/native/macosx/liblwjgl.lnilib", path + "/native/liblwjgl.lnilib");
			copyFileFromJar("/native/macosx/openal.dylib", path + "/native/openal.dylib");  	
			break;
		case WINDOWS:
	  	copyFileFromJar("/native/windows/jinput-raw.dll", path + "/native/jinput-raw.dll");
			copyFileFromJar("/native/windows/jinput-dx8.dll", path + "/native/jinput-dx8.dll");
			copyFileFromJar("/native/windows/lwjgl.dll", path + "/native/lwjgl.dll");
			copyFileFromJar("/native/windows/OpenAL32.dll", path + "/native/OpenAL32.dll");  	
			break;
		case WINDOWS_64:
	  	copyFileFromJar("/native/windows/jinput-raw_64.dll", path + "/native/jinput-raw_64.dll");
			copyFileFromJar("/native/windows/jinput-dx8_64.dll", path + "/native/jinput-dx8_64.dll");
			copyFileFromJar("/native/windows/lwjgl64.dll", path + "/native/lwjgl64.dll");
			copyFileFromJar("/native/windows/OpenAL64.dll", path + "/native/OpenAL64.dll");  	
			break;
		default:
  		throw new IllegalStateException("Error when opening native library");
  	}

  	//Set library path to the VM
		System.setProperty("java.library.path", path + "native/");
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

  /**
   * Copy a file from a .jar (use classloader) to a file on the file system.
   * @param source the name of the source file.
   * @param destination the name of the destination file.
   */
  protected void copyFileFromJar(String source, String destination) {
		InputStream is = null;
		FileOutputStream os = null;
		try {
	  	is = AbstractApp.class.getResourceAsStream(source);
	  	if (null == is) {
	  		throw new IllegalStateException("Error when opening native library");
	  	}
	  	os = new FileOutputStream(destination);
	  	
	  	byte[] byteBuffer = new byte[1024];
	  	int readBytes = -1;
	  	do {
	  		readBytes = is.read(byteBuffer);
	  		if (readBytes != -1) {
	  			os.write(byteBuffer, 0, readBytes);
	  		}
	  	} while (readBytes != -1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (null != os) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected enum SupportedPlatform {
  	LINUX, LINUX_64, MAC, MAC_64, WINDOWS, WINDOWS_64;
  	
  	public static SupportedPlatform getPlatformForOS() {
    	String OS_Arch = System.getProperty("os.arch").toLowerCase();
    	String OS_Name = System.getProperty("os.name").toLowerCase();
    	
    	SupportedPlatform rc = null;

    	if (OS_Name.indexOf("linux") >= 0) {
    		if (OS_Arch.indexOf("64") >= 0) {
    			rc = LINUX_64;
    		} else {
    			rc = LINUX;
    		}
    	}
    	if (OS_Name.indexOf("mac") >= 0) {
    		if (OS_Arch.indexOf("64") >= 0) {
    			rc = MAC_64;
    		} else {
    			rc = MAC;
    		}
    	}
    	if (OS_Name.indexOf("win") >= 0) {
    		if (OS_Arch.indexOf("64") >= 0) {
    			rc = WINDOWS_64;
    		} else {
    			rc = WINDOWS;
    		}
    	}
    	
    	if (null == rc) {
    		throw new IllegalStateException("Platform not supported: " + OS_Name + OS_Arch);
    	}
    	
    	return rc;
  	}
  	
  }
	
}
