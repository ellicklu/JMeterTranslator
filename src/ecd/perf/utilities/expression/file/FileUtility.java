package ecd.perf.utilities.expression.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;
import java.util.Random;

/**
 * General purpose file copy functions.
 */
public class FileUtility
{

	private static final Random random = new Random();

	public static final String SELF_CONTAINED_MARKER = ".selfcontained";//$NON-NLS-1$

	public static String fileToString(File file) throws IOException
	{
		return fileToString(file, null);
	}

	/**
	 * Read out the content of a file into a string.
	 * 
	 * @param file
	 *            the file to read.
	 * @param encode
	 *            The encoding of the file to read.
	 * @return The contents of the file in string.
	 * @throws IOException
	 */
	public static String fileToString(File file, String encode) throws IOException
	{
		return streamToString(new FileInputStream(file), encode);
	}

	public static String fileToString(String name) throws IOException
	{
		return fileToString(name, null);
	}

	/**
	 * Read out the content of a file into a string.
	 * 
	 * @param name
	 *            The name of the file to read.
	 * @param encode
	 *            The encoding of the file to read.
	 * @return The contents of the file in string.
	 * @throws IOException
	 */
	public static String fileToString(String name, String encode) throws IOException
	{
		return streamToString(new FileInputStream(name), encode);
	}

	/**
	 * Read out the content of a file into a string.
	 * 
	 * @param inStream
	 *            input stream
	 * @return The contents of the file in string.
	 * @throws IOException
	 */
	public static String streamToString(InputStream inStream) throws IOException
	{
		return streamToString(inStream, null);
	}

	/**
	 * Read out the content of a file into a string.
	 * 
	 * @param inStream
	 *            input stream
	 * @param encode
	 *            The encoding of the file to read.
	 * @return The contents of the file in string.
	 * @throws IOException
	 */
	public static String streamToString(InputStream inStream, String encode) throws IOException
	{
		try {
			InputStreamReader reader = encode == null ? new InputStreamReader(inStream) : new InputStreamReader(inStream, encode);
			StringBuilder buffer = new StringBuilder();
			char[] charBuffer = new char[1024];
			int currentRead = -1;
			while ((currentRead = reader.read(charBuffer)) != -1) {
				buffer.append(charBuffer, 0, currentRead);
			}
			return buffer.toString();
		}
		finally {
			inStream.close();
		}
	}

	/**
	 * Write a string to a file, with overwrite default to true.
	 * 
	 * @param file
	 *            The file to write to.
	 * @param contents
	 *            The string content to write to a file.
	 * @throws IOException
	 */
	public static void writeStringToFile(File file, String contents) throws IOException
	{
		writeStringToFile(file, contents, true);
	}

	/**
	 * Write a string to a file, with a param for overwriting.
	 * 
	 * @param name
	 *            The file to write to.
	 * @param contents
	 *            The string content to write to a file.
	 * @param bOverwrite
	 *            Whether to overwrite existing file.
	 * @throws IOException
	 */
	public static void writeStringToFile(File file, String contents, boolean bOverwrite) throws IOException
	{
		if (!bOverwrite && file.exists())
			return;

		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(contents);
		}
		finally {
			if (output != null) {
				try {
					output.close();
				}
				catch (IOException e) {
					// OK to ignore, because stream might already be closed in case of previous error
				}
			}
		}
	}

	/**
	 * Append a string to a file.
	 * 
	 * @param name
	 *            The file to write to.
	 * @param contents
	 *            The string content to write to a file.
	 * @throws IOException
	 */
	public static void appendStringToFile(File file, String contents) throws IOException
	{
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(contents);
		}
		finally {
			if (output != null) {
				try {
					output.close();
				}
				catch (IOException e) {
					// OK to ignore, because stream might already be closed in case of previous error
				}
			}
		}
	}

	/**
	 * Returns binary file contents
	 * 
	 * @param file
	 *            Local file containing bytes to read
	 */
	public static byte[] readBytesFromFile(File file) throws IOException
	{
		long fileLength = file.length() + 32;
		if (fileLength > Integer.MAX_VALUE) {
			throw new UnsupportedOperationException("unable to read the whole file since it's length + 32 bytes is " + fileLength
					+ ", which is greater than max int (" + Integer.MAX_VALUE + ")");
		}
		return readBytesFromStream(new FileInputStream(file), new ByteArrayOutputStream((int) fileLength));
	}

	/**
	 * Returns binary file contents
	 * 
	 * @param istream
	 *            Input stream containing bytes to read
	 */
	public static byte[] readBytesFromStream(InputStream istream) throws IOException
	{
		return readBytesFromStream(istream, new ByteArrayOutputStream());
	}

	private static byte[] readBytesFromStream(InputStream in, ByteArrayOutputStream out) throws IOException
	{
		try {
			try {
				copyStream(in, out);
				return out.toByteArray();
			}
			finally {
				out.close();
			}
		}
		finally {
			in.close();
		}
	}
	
	/**
	 * Returns the file extension
	 * 
	 * @param f
	 *            The file object to get the file extension of.
	 * @return Returns null if no file extension found.
	 */
	public static String getFileExtension(File f)
	{
		return getFileExtension(f, false);
	}
	
	public static String getFileExtension(File f, boolean removeUrlQuery)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
			
			if(removeUrlQuery)
			{
				i = ext.lastIndexOf("?");
				if(i>0 && i < ext.length() - 1)
					ext = ext.substring(0, i);
				else
				{
					//Check '&'
					i = ext.indexOf("&");
					if(i>0 && i<ext.length()-1)
						ext = ext.substring(0, i);
				}
			}
		}
		return ext;
	}

	/**
	 * Copy the contents of one input stream to another.
	 * 
	 * @param in
	 *            The input stream to copy from.
	 * @param out
	 *            The output stream to copy to.
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException
	{
		if (!(in instanceof BufferedInputStream || in instanceof ByteArrayInputStream)) {
			in = new BufferedInputStream(in);
		}
		if (!(out instanceof BufferedOutputStream || out instanceof ByteArrayOutputStream)) {
			out = new BufferedOutputStream(out);
		}
		byte[] buf = new byte[32768];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		out.flush();
	}

	/**
	 * Recursively delete a folder and all its subfolders and files
	 * 
	 * @param folder
	 *            Folder to delete.
	 */
	public static void deleteFolder(File folder)
	{
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				}
				file.delete();
			}
		}
		//Delete the itself also
		folder.delete();
	}

	/**
	 * If the destination exists, make sure it is a writeable file and ask before overwriting it. If the destination doesn't
	 * exist, make sure the directory exists and is writeable.
	 * 
	 * @param dest_name
	 * @param overwrite
	 * @throws FileCopyException
	 */
	public static File createDestinationFile(String dest_name, boolean overwrite) throws FileCopyException
	{
		return createDestinationFile(dest_name, overwrite, false);
	}

	public static File createDestinationFile(String dest_name, boolean overwrite, boolean temp) throws FileCopyException
	{
		File destination_file = new File(dest_name);
		if (destination_file.exists()) {
			if (destination_file.isFile()) {
				if (!destination_file.canWrite())
					throw FileCopyException.createCannotWriteException(dest_name);
				if (!overwrite)
					throw FileCopyException.createDestinationExistsException(dest_name);
			}
			else
				throw FileCopyException.createPathIsNotAFileException(dest_name);
		}
		else {
			File parentdir = parent(destination_file);
			if (!parentdir.exists()) {
				makeFolders(parentdir, temp);
			}
			if (!parentdir.exists()) {
				throw FileCopyException.createFileNotFoundException(dest_name);
			}
			if (!parentdir.canWrite()) {
				throw FileCopyException.createCannotWriteException(dest_name);
			}
			try {
				destination_file.createNewFile();
				if (temp)
					destination_file.deleteOnExit();
			}
			catch (IOException e) {
				// OK to ignore
			}
		}
		return destination_file;
	}

	/**
	 * File.getParent() can return null when the file is specified without a directory or is in the root directory. This method
	 * handles those cases.
	 */
	private static File parent(File f)
	{
		String dirname = f.getParent();
		if (dirname == null) {
			if (f.isAbsolute())
				return new File(File.separator);
			else
				return new File(System.getProperty("user.dir")); //$NON-NLS-1$
		}
		return new File(dirname);
	}

	public static void writeBytesToFile(byte[] bytes, File file) throws IOException
	{
		FileOutputStream outStream = null;
		DataOutputStream data = null;
		try {
			outStream = new FileOutputStream(file);
			data = new DataOutputStream(outStream);

			data.write(bytes);
			data.flush();
		}

		finally {
			data.close();
			outStream.close();
		}
	}

	public static boolean makeFolders(File dir, boolean temp)
	{
		if (!temp)
			return dir.mkdirs();

		if (dir.exists()) {
			return false;
		}
		if (dir.mkdir()) {
			dir.deleteOnExit();
			return true;
		}
		File canonFile = null;
		try {
			canonFile = dir.getCanonicalFile();
		}
		catch (IOException e) {
			return false;
		}

		File parent = canonFile.getParentFile();
		if (parent == null)
			return false;

		boolean b = makeFolders(parent, temp);
		if (!(parent.exists() || b))
			return false;

		if (canonFile.mkdir()) {
			canonFile.deleteOnExit();
			return true;
		}

		return false;
	}

	public static File createTempDir()
	{
		return createTempDir(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
	}

	public static File createTempDir(String tempBase)
	{
		long randomLong = Math.abs(random.nextLong()) + 1;

		File tempdir = new File(tempBase + File.separator + "tmpdir" + randomLong); //$NON-NLS-1$
		while (tempdir.exists()) {
			randomLong++;
			tempdir = new File(tempBase + File.separator + "tmpdir" + randomLong); //$NON-NLS-1$
		}
		if (!tempdir.mkdirs()) {
			return null;
		}
		tempdir.deleteOnExit();
		return tempdir;
	}

	public static void close(InputStream is)
	{
		try {
			is.close();
		}
		catch (IOException e) {
			System.out.println("warn: unable to close stream"); //$NON-NLS-1$
		}
	}

	public static void close(Reader reader)
	{
		try {
			reader.close();
		}
		catch (IOException e) {
			System.out.println("warn: unable to close reader"); //$NON-NLS-1$
		}
	}

	public static void close(Writer writer)
	{
		try {
			writer.close();
		}
		catch (IOException e) {
			System.out.println("warn: unable to close writer"); //$NON-NLS-1$
		}
	}

	public static void close(OutputStream os)
	{
		try {
			os.close();
		}
		catch (IOException e) {
			System.out.println("warn: unable to close stream"); //$NON-NLS-1$
		}
	}

	public static void close(Channel c)
	{
		try {
			c.close();
		}
		catch (IOException e) {
			System.out.println("warn: unable to close channel"); //$NON-NLS-1$
		}
	}

	
	/**
	 * Transfers data from input stream to the output stream without closing any stream.
	 * If the <code>flush</code> parameter is specified as true then the output stream
	 * is flushed at the end of the transfer.
	 * @param src The input stream from which data needs to be transferred.
	 * @param dst The output stream to which data needs to be transferred.
	 * @param flush A boolean specifying whether the output stream needs to be flushed at
	 * the end of not.
	 * @return The number of bytes transferred.
	 * @throws IOException
	 */
	public static long transferStream(InputStream src, OutputStream dst, boolean flush) throws IOException
	{
		long bytesTransferred = 0;
		int bytesRead = 0;
		byte byteBuff[] = new byte[4096];
		
		while((bytesRead = src.read(byteBuff)) != -1)
		{
			dst.write(byteBuff, 0, bytesRead);
			bytesTransferred+=bytesRead;
		}
		
		if(flush)
			dst.flush();
		
		return bytesTransferred;
	}
	
	
}
