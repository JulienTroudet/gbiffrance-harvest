package models.harvest.ipt;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
 
public class UsageExample {
 
 public static void main(String[] args) throws IOException, UnsupportedArchiveException {
  // opens csv files with headers or dwc-a direcotries with a meta.xml descriptor
  Archive arch = ArchiveFactory.openArchive(new File("/home/michael/workspace/simple-harvest/examples/dwca"));
 
  // does scientific name exist?
  if (!arch.getCore().hasTerm(DwcTerm.scientificName)){
   System.out.println("This application requires dwc-a with scientific names");
   System.exit(1);
  }
 
  // loop over core darwin core records
  Iterator<StarRecord> iter = arch.iterator();
  @SuppressWarnings("unused")
StarRecord dwc;
  while(iter.hasNext()){
   dwc = iter.next();
   //System.out.println(dwc);
  }
 
  // loop over star records. i.e. core with all linked extension records
  for (StarRecord rec : arch){
   // print core ID + scientific name
   System.out.println(rec.id()+" - "+rec.value(DwcTerm.scientificName) + "-" + rec.value(DwcTerm.kingdom));
   if (rec.dataFile().hasTerm(DwcTerm.decimalLongitude) && rec.dataFile().hasTerm(DwcTerm.decimalLatitude)){
     System.out.println("Georeferenced: " + rec.value(DwcTerm.decimalLongitude)+","+rec.value(DwcTerm.decimalLatitude));;
    }
     
   }
  }
}
