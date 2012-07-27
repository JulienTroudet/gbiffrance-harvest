package models.harvest.ipt.eml;

import java.io.File;
import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;



public class EmlParser {

	private Document document;
	private Element racine;
	private EmlData data;
	private GbifRacine gbif;
	private SAXBuilder sxb;

	
	public EmlParser (File file) throws JDOMException
	{
			
		
		//sxb.setFeature("http://apache.org/xml/features/validation/schema", true);
		//sxb.setFeature("http://rs.gbif.org/schema/eml-gbif-profile/dev/eml-gbif-profile.xsd", true);

		try {

			sxb = new SAXBuilder();
			document = sxb.build(file);

			racine = document.getRootElement();

			if(racine.getChild("dataset")!=null){
				Element datasetElement = racine.getChild("dataset");
				data = new EmlData();
				data = data.parse(datasetElement);		
				//System.out.println (data);
			}

			if(racine.getChild("additionalMetadata")!=null){
				if(racine.getChild("additionalMetadata").getChild("metadata")!=null){
					if(racine.getChild("additionalMetadata").getChild("metadata").getChild("gbif")!=null){
						Element datasetElement = racine.getChild("additionalMetadata").getChild("metadata").getChild("gbif");
						gbif = new GbifRacine();
						gbif = gbif.parse(datasetElement, data);	
						data.setGbifRacine(gbif);
						//System.out.println (gbif);
					}
				}
			}


		}catch (IOException e) {
			e.printStackTrace();
		}
	}


	public EmlData getData() {
		return data;
	}


	public GbifRacine getGbif() {
		return gbif;
	}
}
