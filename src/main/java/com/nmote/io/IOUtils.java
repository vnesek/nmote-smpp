/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO related utilites.
 */
public class IOUtils {

	/**
	 * Reads up to <code>len</code> bytes from <code>in</code> and writes them
	 * to <code>dest</code> starting with <code>off</code>. Returns number of
	 * bytes copied. If returned number is less than len then InputStream has
	 * returned end-of-file.
	 *
	 * @param in
	 * @param dest
	 * @param off
	 * @param len
	 * @return number of bytes copied
	 * @throws IOException
	 */
	public static int copyStreamToByteArray(InputStream in, byte[] dest, int off, int len) throws IOException {
		int r = 0;
		while (r < len) {
			int n = in.read(dest, off + r, len - r);
			if (n > 0) {
				r += n;
			} else if (n == -1) {
				break;
			} else {
				throw new IOException("Read 0 bytes from input stream");
			}
		}
		return r;
	}

	/**
	 * Loads loadable state from data.
	 *
	 * @param loadable
	 * @param data
	 * @throws IOException
	 */
	public static void load(Loadable loadable, byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		loadable.load(in);
	}

	/**
	 * Gets saved state of saveable.
	 *
	 * @param saveable
	 * @return instance state
	 * @throws IOException
	 */
	public static byte[] save(Saveable saveable) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		saveable.save(out);
		return out.toByteArray();
	}

	public static byte[] toByteArray(InputStream in, int startSize, int maxSize) throws IOException {
		if (in instanceof LengthLimitedInputStream) {
			int size = ((LengthLimitedInputStream) in).getBytesLeft();
			if (maxSize > size) {
				maxSize = size;
			}
		}

		if (startSize > maxSize) {
			startSize = maxSize;
		}

		// Allocate a buffer
		byte[] buffer = new byte[startSize];

		int pos = 0;
		for (;;) {
			// Copy stream into buffer
			int r = copyStreamToByteArray(in, buffer, pos, buffer.length - pos);

			// We've reached EOF
			if (r == 0) {
				break;
			}

			pos += r;

			// We've filled up a buffer
			if (pos == buffer.length) {
				// Calculate new buffer length
				int newLen = buffer.length * 2;

				// Don't use more than maxSize
				if (newLen > maxSize) {
					newLen = maxSize;
				}

				// Copy into a new buffer
				byte[] newBuffer = new byte[newLen];
				System.arraycopy(buffer, 0, newBuffer, 0, pos);
				buffer = newBuffer;
			}
		}

		// Copy to result arrray
		if (pos < buffer.length) {
			byte[] newBuffer = new byte[pos];
			System.arraycopy(buffer, 0, newBuffer, 0, pos);
			buffer = newBuffer;
		}

		return buffer;
	}
}