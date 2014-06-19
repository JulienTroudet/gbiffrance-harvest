package models.harvest.inpn;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import play.Logger;

import com.vividsolutions.jts.geom.Geometry;

public class ShapeFile {

	public Map<String, Geometry> reader(File pFile) {
		Map<String, Geometry> lMapResult = null;

		try {
			DataStore lShapefileDataStore = new ShapefileDataStore(pFile
					.toURI().toURL());

			String lTypeName = lShapefileDataStore.getTypeNames()[0];

			FeatureSource<SimpleFeatureType, SimpleFeature> lFeatureSource = lShapefileDataStore
					.getFeatureSource(lTypeName);

			FeatureCollection<SimpleFeatureType, SimpleFeature> lFeatureCollection = null;

			lFeatureCollection = lFeatureSource.getFeatures();

			SimpleFeatureType lSimpleFeatureType = lFeatureSource.getSchema();

			Map<String, Integer> lMap = new HashMap<String, Integer>();
			for (int i = 0; i < lSimpleFeatureType.getAttributeCount(); i++) {
				AttributeDescriptor lAttributeDescriptor = lSimpleFeatureType
						.getDescriptor(i);
				lMap.put(lAttributeDescriptor.getName().getLocalPart(), i);

			}

			FeatureIterator<SimpleFeature> lFeatureIterator = lFeatureCollection
					.features();

			lMapResult = new HashMap<String, Geometry>();

			while (lFeatureIterator.hasNext()) {
				SimpleFeature lFeature = lFeatureIterator.next();

				lMapResult.put(
						(String) lFeature.getAttribute(lMap.get("identifian")),
						(Geometry) lFeature.getAttribute(lMap.get("the_geom")));
			}
			lFeatureIterator.close();

			return lMapResult;

		} catch (MalformedURLException e) {
			Logger.error("Shape : " + e.toString());
		} catch (IOException e) {
			Logger.error("Shape : " + e.toString());
		} catch (Exception e) {
			Logger.error("Shape : " + e.toString());
		}
		return lMapResult;
	}

	public String readPrj(File pFile) {

		DataStore lShapefileDataStore;
		CoordinateReferenceSystem crs = null;
		try {
			lShapefileDataStore = new ShapefileDataStore(pFile.toURI().toURL());

			String lTypeName = lShapefileDataStore.getTypeNames()[0];

			FeatureSource<SimpleFeatureType, SimpleFeature> lFeatureSource = lShapefileDataStore
					.getFeatureSource(lTypeName);

			SimpleFeatureType lSimpleFeatureType = lFeatureSource.getSchema();

			crs = lSimpleFeatureType.getGeometryDescriptor()
					.getCoordinateReferenceSystem();
		} catch (MalformedURLException e) {
			Logger.error("Shape : " + e.toString());
		} catch (IOException e) {
			Logger.error("Shape : " + e.toString());
		}

		return crs.getCoordinateSystem().getName().toString();

	}
}
