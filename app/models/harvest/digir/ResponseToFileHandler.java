package models.harvest.digir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import play.Play;
import play.PlayPlugin;

public class ResponseToFileHandler implements ResponseHandler<Void> {

	protected String file;

	public ResponseToFileHandler(String file) {
		this.file = file;
	}

	public Void handleResponse(HttpResponse response) throws IOException {
		InputStream contentStream = response.getEntity().getContent();
		try {
			// store the response
			GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(
					file));
			IOUtils.copy(contentStream, gos);
			gos.close();
			contentStream = new GZIPInputStream(new FileInputStream(file));

		} finally {
			contentStream.close();
		}

		return null;
	}
}
