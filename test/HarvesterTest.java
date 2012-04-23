import models.DataPublisher;
import models.Dataset;
import models.harvest.Harvester;

import org.junit.Test;

import play.test.FunctionalTest;


public class HarvesterTest extends FunctionalTest{

	@Test
	public void pageRangeTest() throws Exception{
		Dataset dataset = Dataset.findById((long) 33);
		models.harvest.Harvester harvester = new models.harvest.Harvester(dataset, "/tmp", null, null );
		harvester.pageRange("Aga", "Agz", 17000);
	}
	
	@Test
	public void falseDatasetTest() throws Exception{
		DataPublisher dataPublisher = new DataPublisher("test", "test", "test", "test");
		Dataset dataset = new Dataset("test", "test", "test", dataPublisher);
		@SuppressWarnings("unused")
		models.harvest.Harvester harvester = new models.harvest.Harvester(dataset, "/tmp", null, null );
	}
	
}
