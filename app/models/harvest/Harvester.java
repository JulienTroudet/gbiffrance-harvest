package models.harvest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import models.Dataset;
import models.Occurrence;
import models.harvest.biocase.ResponseToModelHandler;
import models.harvest.dataaccess.OccurrenceToDBHandler;
import models.harvest.util.TemplateUtils;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

@SuppressWarnings("deprecation")
public class Harvester {
	public String targetDirectory = "/tmp";
	public boolean withErrors = false;
	public String currentLower;
	public String currentUpper;
	public int currentStartAt;
	public String begin;
	public String end;
	public Dataset dataset;
	private OccurrenceToDBHandler databaseSync = new OccurrenceToDBHandler();

	boolean t;

	private static final play.Logger LOG = new play.Logger();

	// TODO: This does not need to be a multithreaded manager
	protected static ThreadSafeClientConnManager connectionManager;
	protected static HttpParams params = new BasicHttpParams();
	static {
		HttpConnectionParams.setConnectionTimeout(params, 600000);
		HttpConnectionParams.setSoTimeout(params, 600000);
		ConnManagerParams.setMaxTotalConnections(params, 10);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		connectionManager = new ThreadSafeClientConnManager(params,
				schemeRegistry);
	}
	private Map<String, String> templateParams = new HashMap<String, String>();
	private String templateLocation;
	private HttpClient httpClient = new DefaultHttpClient(connectionManager,
			params);
	// private ResponseToModelHandler modelFactory;
	private int maxResults;

	public Harvester() {
	}

	/**
	 * Creates the class
	 * 
	 * @throws Exception
	 */
	public Harvester(Dataset dataset, String targetDirectory, String begin,
			String end) {
		this.dataset = dataset;
		if (this.dataset.tempDirectory != null)
			this.targetDirectory = dataset.tempDirectory;
		else
			this.targetDirectory = targetDirectory + File.separator
					+ "resource-" + dataset.id + File.separator;
		File f = new File(this.targetDirectory);
		if (!f.exists()) {
			f.mkdirs();
		}
		f.setWritable(true, false);
		this.dataset.tempDirectory = this.targetDirectory;
		// if the dataset is from outside, we add a country filter to our search
		if (this.dataset.fromOutside)
			templateParams.put("country", "France");
		this.begin = begin;
		this.end = end;
		this.maxResults = (this.dataset.type.equals("biocase") ? 1000 : 200);
		// set up the template that will be used to issue biocase requests
		if (this.dataset.type.equals("biocase")) {
			templateLocation = "models/harvest/resources/template/biocase/search.vm";
			templateParams.put("destination", this.dataset.url);
			templateParams.put("hostAddress", "127.0.0.1");
			templateParams.put("contentNamespace",
					"http://www.tdwg.org/schemas/abcd/2.06");
			templateParams
					.put("subject",
							"/DataSets/DataSet/Units/Unit/Identifications/Identification/Result/TaxonIdentified/ScientificName/FullScientificNameString");
		} else if (this.dataset.type.equals("digir")) {
			templateLocation = "models/harvest/resources/template/digir/search.vm";
			templateParams.put("destination", this.dataset.url);
			templateParams.put("maxResults", Integer.toString(maxResults));
			templateParams.put("resource", dataset.name);
		} else if (this.dataset.type.equals("tapir")) {
			templateLocation = "models/harvest/resources/template/tapir/search.vm";
			templateParams.put("destination", this.dataset.url);
			templateParams.put("maxResults", Integer.toString(maxResults));
			templateParams.put("resource", dataset.name);
		} else
			LOG.error("Error in harvester selection");
		run(this.dataset);
	}

	/**
	 * Initiates a crawl
	 * 
	 * @throws Exception
	 */
	private void run(Dataset dataset) {
		try {
			if (this.begin != null) {
				partialHarvest();
			} else
				fullHarvest();
			LOG.info(this.dataset.name);
		} catch (Exception e) {
			LOG.error("Harvesting failed terminally", e);
			this.withErrors = true;
			if (e.getMessage().equals("Error in range"))
				LOG.error("Harvester stopped at " + this.currentLower);
			else
				e.printStackTrace();
		}
	}

	/**
	 * Does the harvesting
	 * 
	 * @throws Exception
	 */
	private void fullHarvest() throws Exception {
		LOG.info("Starting harvesting");
		// get the things before A
		// if the dataset has never or has already been completely harvested
		if (!new File(this.dataset.tempDirectory + "0_null-AAA-0_.txt.gz")
				.exists()
				|| new File(this.dataset.tempDirectory + "zzz-null-0.txt.gz")
						.exists()) {
			LOG.info(("New harvesting process"));
			this.currentLower = null;
			this.currentUpper = "AAA";
			this.currentStartAt = 0;
			try {
				pageRange(null, "AAA", 0);
			} catch (Exception e) {
				LOG.error("Error in range [null-AAA]", e);
				this.withErrors = true;
				deleteTemporaryDirectory(this.dataset.tempDirectory,
						this.currentLower);
				deleteOccurrencesFrom(this.dataset, this.currentLower);
				throw new Exception("Error in range");
			}
			// loop on the name basis Aaa-Aaz
			for (char c1 = 'A'; c1 <= 'Z'; c1++) {
				for (char c2 = 'a'; c2 <= 'z'; c2++) {
					String lower = c1 + "" + c2 + "a";
					String upper = c1 + "" + c2 + "z";
					this.currentLower = lower;
					this.currentUpper = upper;
					this.currentStartAt = 0;
					try {
						pageRange(lower, upper, 0);
					} catch (Exception e) {
						LOG.error("Error in range [" + lower + "-" + upper
								+ "], message: " + e.getMessage()
								+ "\n cause: " + e.getCause());
						this.withErrors = true;
						deleteTemporaryDirectory(this.dataset.tempDirectory,
								this.currentLower);
						deleteOccurrencesFrom(this.dataset, this.currentLower);
						throw new Exception("Error in range");
					}
				}
			}
		}
		// else if the dataset has not been completely harvested the last time,
		// continue from the breaking point
		else {
			boolean incomplete = false;
			char incompleteC1 = 'A';
			char incompleteC2 = 'a';
			for (char c1 = 'A'; c1 <= 'Z'; c1++) {
				for (char c2 = 'a'; c2 <= 'z'; c2++) {
					String lower = c1 + "" + c2 + "a";
					String upper = c1 + "" + c2 + "z";
					if (!(new File(this.dataset.tempDirectory + lower + "-"
							+ upper + "-0.txt.gz").exists())
							&& new File(this.dataset.tempDirectory + lower
									+ "-" + upper + "-0_.txt.gz").exists()) {
						LOG.info(("Harvester has stopped at "
								+ this.dataset.tempDirectory + lower + "-"
								+ upper + "-0.txt.gz"));
						deleteTemporaryDirectory(this.dataset.tempDirectory,
								lower);
						deleteOccurrencesFrom(this.dataset, lower);
						incomplete = true;
						incompleteC1 = c1;
						incompleteC2 = c2;
						break;
					} else {
						for (int startAt = 0; new File(
								this.dataset.tempDirectory + lower + "-"
										+ upper + "-" + startAt + "_.txt.gz")
								.exists(); startAt = startAt + this.maxResults) {
							if (!(new File(this.dataset.tempDirectory + lower
									+ "-" + upper + "-" + startAt + ".txt.gz")
									.exists())
									&& new File(this.dataset.tempDirectory
											+ lower + "-" + upper + "-"
											+ startAt + "_.txt.gz").exists()) {
								LOG.info(("Harvester has previously stopped at "
										+ this.dataset.tempDirectory
										+ lower
										+ "-" + upper + "-0.txt.gz"));
								deleteTemporaryDirectory(
										this.dataset.tempDirectory, lower);
								deleteOccurrencesFrom(this.dataset, lower);
								incomplete = true;
								incompleteC1 = c1;
								incompleteC2 = c2;
								// break;
							}
							if (incomplete) {
								break;
							}
						}
					}
				}
				if (incomplete) {
					break;
				}
			}
			for (char c1 = incompleteC1; c1 <= 'Z'; c1++) {
				for (char c2 = incompleteC2; c2 <= 'z'; c2++) {
					String lower = c1 + "" + c2 + "a";
					String upper = c1 + "" + c2 + "z";
					this.currentLower = lower;
					this.currentUpper = upper;
					this.currentStartAt = 0;
					try {
						pageRange(lower, upper, 0);
					} catch (Exception e) {
						LOG.error("Error in range [" + lower + "-" + upper
								+ "], message: " + e.getMessage()
								+ "\n cause: " + e.getCause());
						this.withErrors = true;
						deleteTemporaryDirectory(this.dataset.tempDirectory,
								this.currentLower);
						deleteOccurrencesFrom(this.dataset, this.currentLower);
						throw new Exception("Error in range");
					}
					if (c2 == 'z')
						incompleteC2 = 'a';
				}
			}
		}// else
			// get the things after z
		this.currentLower = "zzz";
		this.currentUpper = null;
		this.currentStartAt = 0;
		try {
			pageRange("zzz", null, 0);
		} catch (Exception e) {
			LOG.error("Error in range [zzz-AAA]", e);
			this.withErrors = true;
			deleteTemporaryDirectory(this.dataset.tempDirectory,
					this.currentLower);
			deleteOccurrencesFrom(this.dataset, this.currentLower);
			throw new Exception("Error in range");
		}
		LOG.info("Finished harvesting");
	}

	private void partialHarvest() throws Exception {
		LOG.info("Starting harvesting from " + begin);
		deleteTemporaryDirectory(this.dataset.tempDirectory, this.begin);
		deleteOccurrencesFrom(this.dataset, this.begin);
		for (char c1 = this.begin.charAt(0); c1 <= 'Z'; c1++) {
			for (char c2 = this.begin.charAt(1); c2 <= 'z'; c2++) {
				String lower = c1 + "" + c2 + "a";
				String upper = c1 + "" + c2 + "z";
				this.currentLower = lower;
				this.currentUpper = upper;
				this.currentStartAt = 0;
				try {
					pageRange(lower, upper, 0);
				} catch (Exception e) {
					LOG.error("Error in range [" + lower + "-" + upper
							+ "], message: " + e.getMessage() + "\n cause: "
							+ e.getCause());
					this.withErrors = true;
					deleteTemporaryDirectory(this.dataset.tempDirectory,
							this.currentLower);
					deleteOccurrencesFrom(this.dataset, this.currentLower);
					throw new Exception("Error in range");
				}
				if (c2 == 'z')
					this.begin = this.begin.charAt(0) + "" + 'a';
			}
		}
		LOG.info("Finished harvesting");
	}

	/**
	 * Issues a call to get a page of results
	 */
	public void pageRange(String lower, String upper, int startAt)
			throws Exception {
		templateParams.put("lower", lower);
		templateParams.put("upper", upper);
		templateParams.put("startAt", Integer.toString(startAt));
		LOG.info("Starting lower[" + lower + "] upper[" + upper + "] start["
				+ startAt + "]");
		String query = TemplateUtils.getAndMerge(templateLocation,
				templateParams);
		String request = buildURL(this.dataset.url, "request", query);
		String requestFile = targetDirectory
				+ ((lower == null) ? "0_" + lower : lower) + "-" + upper + "-"
				+ startAt + "_.txt.gz";
		String responseFile = targetDirectory
				+ ((lower == null) ? "0_" + lower : lower) + "-" + upper + "-"
				+ startAt + ".txt.gz";
		LOG.info(requestFile);
		// store the request
		GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(
				requestFile));
		IOUtils.write(query, gos);
		gos.close();
		File f = new File(requestFile);
		f.setWritable(true, false);
		f.setExecutable(true, false);
		// issue request and store response
		HttpGet httpget = new HttpGet(request);
		LOG.info("Initiating Request[" + requestFile + "] for Range[" + lower
				+ "-" + upper + "] starting at[" + startAt + "]");
		if (this.dataset.type.equals("biocase")) {
			models.harvest.biocase.ResponseToFileHandler responseToFile = new models.harvest.biocase.ResponseToFileHandler(
					responseFile);
			httpClient.execute(httpget, responseToFile);
		} else if (this.dataset.type.equals("digir")) {
			models.harvest.digir.ResponseToFileHandler responseToFile = new models.harvest.digir.ResponseToFileHandler(
					responseFile);
			httpClient.execute(httpget, responseToFile);
		} else if (this.dataset.type.equals("tapir")) {
			models.harvest.tapir.ResponseToFileHandler responseToFile = new models.harvest.tapir.ResponseToFileHandler(
					responseFile);
			httpClient.execute(httpget, responseToFile);
		}
		LOG.info("Range[" + lower + "-" + upper + "] starting at[" + startAt
				+ "] returned response[" + responseFile + "]");

		// now process the response to build the records
		GZIPInputStream contentStream = null;
		List<Occurrence> occurrences = new ArrayList<Occurrence>();
		try {
			contentStream = new GZIPInputStream(new FileInputStream(
					responseFile));
			if (this.dataset.type.equals("biocase")) {
				ResponseToModelHandler modelFactory = new models.harvest.biocase.ResponseToModelHandler();
				occurrences = modelFactory.handleResponse(contentStream);
			} else if (this.dataset.type.equals("digir")) {
				models.harvest.digir.ResponseToModelHandler modelFactory = new models.harvest.digir.ResponseToModelHandler();
				occurrences = modelFactory.handleResponse(contentStream);
			} else if (this.dataset.type.equals("tapir")) {
				models.harvest.tapir.ResponseToModelHandler modelFactory = new models.harvest.tapir.ResponseToModelHandler();
				occurrences = modelFactory.handleResponse(contentStream);
			}
		} finally {
			IOUtils.closeQuietly(contentStream);
			f = new File(responseFile);
			f.setWritable(true, false);
			f.setExecutable(true, false);
		}
		// now synchronise the results to the database
		LOG.info("Number of results: " + occurrences.size());
		for (Occurrence o : occurrences) {
			o.dataset = this.dataset;
		}
		try {
			databaseSync.synchronize(occurrences);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("Finished lower[" + lower + "] upper[" + upper + "] start["
				+ startAt + "]");
		if (occurrences.size() >= maxResults) {
			occurrences = null; // make eligible for garbage collection
			this.currentStartAt = startAt + maxResults;
			pageRange(lower, upper, startAt + maxResults);
		}
	}

	public String buildURL(String url, String parameterKey, String content)
			throws UnsupportedEncodingException {
		if (content != null && content.length() > 0) {
			if (url.contains("?")) {
				url = url + "&" + parameterKey + "="
						+ URLEncoder.encode(content, "UTF-8");
			} else {
				url = url + "?" + parameterKey + "="
						+ URLEncoder.encode(content, "UTF-8");
			}
		}
		return url;
	}

	public static void deleteTemporaryDirectory(String directory, String lower) {
		if (directory == null)
			LOG.info("No temp directory, do nothing");
		else {
			File path = new File(directory);
			if (path.exists()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; ++i) {
					if (files[i].isDirectory()) {
						deleteTemporaryDirectory(files[i].getAbsolutePath(),
								lower);
					} else if (lower != null) {
						if ((files[i].toString().split(directory)[1].split("-")[0])
								.compareTo(lower) >= 0
								&& !(files[i].toString().substring(
										files[i].toString().length() - 10,
										files[i].toString().length())
										.equals("-0_.txt.gz"))) {
							files[i].delete();
						}
					} else
						files[i].delete();
				}
			} else
				LOG.info("No temp directory associated to the resource");
			if (path.delete()) {
				LOG.info("Temp directory deleted");
			}
		}
	}

	public void deleteOccurrencesFrom(Dataset dataset, String lower) {
		databaseSync.synchronizeDelete(dataset, lower);
		LOG.info("Occurrences upper than " + lower
				+ " are deleted from the database");
	}
}
