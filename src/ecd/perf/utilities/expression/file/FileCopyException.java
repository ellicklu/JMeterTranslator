package ecd.perf.utilities.expression.file;

import java.io.IOException;

/**
 * Represents a failure during a FileUtility file copy operation.
 * 
 * Like a UtilitiesException classes, this class is translated at the OLT server
 * based on the locale of the user's browser.
 */
public class FileCopyException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private FileCopyException(String message, Object[] parts, Throwable cause)
	{
		super(message + ((parts != null && parts.length > 0) ? "\t path:"+parts[0] : ""), cause);
	}
	
	public static FileCopyException createCannotReadException(String filePath) {
		return new FileCopyException("FILE_COPY_ERROR_CANNOT_READ", new Object[] {filePath}, null); //$NON-NLS-1$
	}
	public static FileCopyException createCannotWriteException(String filePath) {
		return new FileCopyException("FILE_COPY_ERROR_CANNOT_WRITE", new Object[] {filePath}, null); //$NON-NLS-1$
	}
	public static FileCopyException createDestinationExistsException(String filePath) {
		return new FileCopyException("FILE_COPY_ERROR_DESTINATION_EXISTS", new Object[] {filePath}, null); //$NON-NLS-1$
	}
	public static FileCopyException createPathIsNotAFileException(String filePath) {
		return new FileCopyException("FILE_COPY_ERROR_PATH_IS_NOT_A_FILE", new Object[] {filePath}, null); //$NON-NLS-1$
	}
	public static FileCopyException createFileNotFoundException(String filePath) {
		return new FileCopyException("FILE_COPY_ERROR_FILE_NOT_FOUND", new Object[] {filePath}, null); //$NON-NLS-1$
	}
	public static FileCopyException createFailedToCopyException(String sourceName, String destName, IOException rootCause) {
		return new FileCopyException("FILE_COPY_ERROR", new Object[] {sourceName, destName}, rootCause); //$NON-NLS-1$
	}
	public static FileCopyException createFailedToCreateSubfolderException(String subfolder) {
		return new FileCopyException("FILE_COPY_ERROR_FAILED_TO_CREATE_SUBFOLDER", new Object[] {subfolder}, null); //$NON-NLS-1$
	}
	public static FileCopyException createCopyFolderToSubfolderException(String folder, String subfolder) {
		return new FileCopyException("FILE_COPY_ERROR_FAILED_FOLDER_COPY_TO_SUBFOLDER", new Object[] {folder, subfolder}, null); //$NON-NLS-1$
	}
	
	public static FileCopyException createGeneralException(String message, Throwable cause){
		return new FileCopyException(message, new Object[0], cause);
	}
	
}
