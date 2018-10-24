package br.gov.serpro;

import java.io.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;

/*
	@see 
	http://mygeekjourney.com/programming-notes/how-to-merge-pdf-files-with-pdfbox-in-java/
	https://github.com/apache/pdfbox/blob/trunk/examples/src/main/java/org/apache/pdfbox/examples/pdmodel/EmbeddedFiles.java
*/

public class MergeFiles {
/*
 	============================================== tests 
 	1- directory that not exists OK 
 	2-    || 	 empty OK 
 	3-    || 	 contains some file that not is PDF format OBS
 */
	public static void main(String[] args) throws IOException, COSVisitorException {

		// use this to resolve java/lang/OutOfMemoryError exception
		int qtdMaxFile = 100;
		// OutOfMemoryError is thrown when the JVM can't allocate enough memory to complete the requested action

		// define path directory
		String dirPacth = "/home/06382025313/Documentos/tests/valid";
		File dirFile = new File(dirPacth);

		if (dirFile.isDirectory()) {
			Date startTime = new Date();
			System.out.println("Start time: " + startTime.toString());

			// walks the folder and merge the pdf according to last mod date
			File[] files = dirFile.listFiles();
			int count = files.length;

			if (count > 0) {
				// sort the files by last mod date in desc order
				Arrays.sort(files, new Comparator<File>() {
					public int compare(File f1, File f2) {
						return Long.compare(f2.lastModified(), f1.lastModified());
					}
				});

				// merged the qtd files = qtdMaxFile
				if (qtdMaxFile != 0 && qtdMaxFile < count) {
					count = qtdMaxFile;
				}

				// create a temp file for temp fle stream storage
				String tempFileName = (new Date()).getTime() + "_temp";
				File tempFile = new File("/home/06382025313/Documentos/tests" + tempFileName);

				// proceed to merge
				// this is the in-memory representation of the PDF document
				PDDocument desPDDoc = null;
				// this class will take a list of pdf documents and merge them, saving the result in a new document
				// this provive methods to merge two or more pdf documents in to a single pdf document 
				PDFMergerUtility pdfMerger = new PDFMergerUtility();

				try {
					boolean hasCloneFirstDoc = false;
					for (int i = 0; i < count; i++) {
						// walks each file summing with before and compare if sum is < count
						File file = files[i];
						PDDocument doc = null;
						try {
							if (hasCloneFirstDoc) {
								doc = PDDocument.load(file);
								// params destination, source
								pdfMerger.appendDocument(desPDDoc, doc);
		
						// mergeDocuments() -> merge the list of source documents, saving the result in the destination file
						// appendDocument() -> append all pages from source to destination
								
							} else {
								desPDDoc = PDDocument.load(file, new RandomAccessFile(tempFile, "rw"));
								hasCloneFirstDoc = true;
							}
						} catch (IOException ioe) {
							System.out.println("Invalid file detected: " + file.getName());
							ioe.printStackTrace();
						} finally {
							if (doc != null) {
								doc.close();
							}
						}
					}

					System.out.println("Merging and saving the file to its destination");
					desPDDoc.save("/home/06382025313/Documentos/tests/RESULT.pdf");
					System.out.println(count + " file merged");

					Date endTime = new Date();
					System.out.println("Process Completed: " + endTime);
					long timeTakenInSec = endTime.getTime() - startTime.getTime(); // calculate total time process
					System.out.println("Time: " + (timeTakenInSec / 1000) + " secs " + (timeTakenInSec % 1000) + " ms");

				} catch (IOException | COSVisitorException e) {
					// ApachePDFBox -> an exception that represents something gone wrong when visiting a PDF object
					e.printStackTrace(); // will encounter issues if it is more than maxQtd files defined
				} finally {
					try {
						if (desPDDoc != null) {
							desPDDoc.close();
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					tempFile.delete();
				}
			} else {
				System.out.println(count + " Files\nDirectory empty");
			}
		} else {
			System.out.println("[" + dirPacth + "] is not a directory or not exists");
		}
	}

}
