package dk.jyskit.waf.utils.images;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

import lombok.extern.slf4j.Slf4j;
import dk.jyskit.waf.application.utils.exceptions.SystemException;

@Slf4j
public class ImageUtils {
	/**
	 * Constructs a byte array and fills it with data that is read from the
	 * specified resource.
	 * @param baseClass class defining root of path
	 * @param filename the path to the resource
	 * @return the specified resource as a byte array
	 * @throws java.io.IOException if the resource cannot be read, or the
	 *   bytes cannot be written, or the streams cannot be closed
	 */
	public static SerialBlob getImageBlob(Class baseClass, String filename) throws IOException {
		return getImageBlobFromInputStream(baseClass.getResourceAsStream(filename));
	}
	
	/**
	 * Constructs a byte array and fills it with data that is read from the
	 * specified resource.
	 * @param fileName the path to the resource
	 * @return the specified resource as a byte array
	 * @throws java.io.IOException if the resource cannot be read, or the
	 *   bytes cannot be written, or the streams cannot be closed
	 */
	public static SerialBlob getImageBlob(String fileName) throws IOException {
	    try {
			return getImageBlobFromInputStream(new FileInputStream(fileName));
		} catch (IOException e) {
			throw new IOException(e);
		}
	}
	
	public static SerialBlob getImageBlobFromInputStream(InputStream inputStream) throws IOException {
	    try {
			return new SerialBlob(getDataFromInputStream(inputStream));
		} catch (SQLException e) {
			throw new IOException(e);
		} finally {
			inputStream.close();
		}
	}
	
	public static byte[] getDataFromInputStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = null;
	    try {
			outputStream = new ByteArrayOutputStream(1024);
			byte[] bytes = new byte[4096];
 
			// Read bytes from the input stream in bytes.length-sized chunks and write
			// them into the output stream
			int readBytes;
			while ((readBytes = inputStream.read(bytes)) > 0) {
			    outputStream.write(bytes, 0, readBytes);
			}
 
			// Convert the contents of the output stream into a byte array
			return outputStream.toByteArray();
		} finally {
			// Close the streams
			inputStream.close();
			outputStream.close();
		}
	}
	
	public static byte[] bufferedImageToByteArray(BufferedImage image) {
		try {
			byte[] imageInByte;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException e) {
			log.error("Could not convert image", e);
			throw new SystemException(e);
		}
	}
	
	public static BufferedImage byteArrayToBufferedImage(byte[] bytes) {
		try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
			log.error("Could not convert image", e);
			throw new SystemException(e);
        }
	}
}
