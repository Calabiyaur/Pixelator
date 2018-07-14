/**
 *    Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package main.java.files.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

public class ByteSource implements ZipEntrySource {

  private final String path;
  private final byte[] bytes;
  private final long time;
  private final int compressionMethod;
  private final long crc;

  public ByteSource(String path, byte[] bytes) {
    this(path, bytes, System.currentTimeMillis());
  }

  public ByteSource(String path, byte[] bytes, long time) {
    this(path, bytes, time, -1);
  }
  public ByteSource(String path, byte[] bytes, int compressionMethod) {
    this(path, bytes, System.currentTimeMillis(), compressionMethod);
  }

  public ByteSource(String path, byte[] bytes, long time, int compressionMethod) {
    this.path = path;
    this.bytes = (byte[])bytes.clone();
    this.time = time;
    this.compressionMethod = compressionMethod;
    if(compressionMethod != -1) {
      CRC32 crc32 = new CRC32();
      crc32.update(bytes);
      this.crc = crc32.getValue();
    } else {
      this.crc = -1;
    }
  }

  public String getPath() {
    return path;
  }

  public ZipEntry getEntry() {
    ZipEntry entry = new ZipEntry(path);
    if (bytes != null) {
      entry.setSize(bytes.length);
    }
    if(compressionMethod != -1) {
      entry.setMethod(compressionMethod);
    }
    if(crc != -1L) {
      entry.setCrc(crc);
    }
    entry.setTime(time);
    return entry;
  }

  public InputStream getInputStream() throws IOException {
    if (bytes == null) {
      return null;
    }
    else {
      return new ByteArrayInputStream(bytes);
    }
  }

  public String toString() {
    return "ByteSource[" + path + "]";
  }

}
