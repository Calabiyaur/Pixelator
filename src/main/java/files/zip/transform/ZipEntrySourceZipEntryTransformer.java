package main.java.files.zip.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import main.java.files.zip.ZipEntrySource;
import main.java.files.zip.commons.IOUtils;

public class ZipEntrySourceZipEntryTransformer implements ZipEntryTransformer {

  private final ZipEntrySource source;

  public ZipEntrySourceZipEntryTransformer(ZipEntrySource source) {
    this.source = source;
  }

  public void transform(InputStream in, ZipEntry zipEntry, ZipOutputStream out) throws IOException {
    addEntry(source, out);
  }

  /**
   * Adds a given ZIP entry to a ZIP file.
   * 
   * @param entry
   *          new ZIP entry.
   * @param out
   *          target ZIP stream.
   */
  static void addEntry(ZipEntrySource entry, ZipOutputStream out) throws IOException {
    out.putNextEntry(entry.getEntry());
    InputStream in = entry.getInputStream();
    if (in != null) {
      try {
        IOUtils.copy(in, out);
      }
      finally {
        IOUtils.closeQuietly(in);
      }
    }
    out.closeEntry();
  }

}
