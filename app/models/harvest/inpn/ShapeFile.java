package models.harvest.inpn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.datanucleus.jdo.spatial.JgeomSpatialHelper;
import org.datanucleus.store.mapped.mapping.jgeom.JGeometryMapping;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.header.ShapeFileHeader;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPointZShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PointShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;

import models.Occurrence;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.*;
import play.Logger;

public class ShapeFile {

	public void reader(File pFile, Occurrence pOccurrence) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(pFile);

			ShapeFileReader r = new ShapeFileReader(is);

			AbstractShape s;
			while ((s = r.next()) != null) {

				switch (s.getShapeType()) {
				case POINT:
					PointShape aPoint = (PointShape) s;
					pOccurrence.geometry = new JGeometry(aPoint.getX(),
							aPoint.getY(), 1);
					break;
				case MULTIPOINT_Z:
					MultiPointZShape aMultiPointZ = (MultiPointZShape) s;

					pOccurrence.geometry = new JGeometry(
							aMultiPointZ.getBoxMinX(),
							aMultiPointZ.getBoxMinY(),
							aMultiPointZ.getBoxMaxX(),
							aMultiPointZ.getBoxMaxY(), 1);

					break;
				}

			}

			is.close();
		} catch (FileNotFoundException e) {
			Logger.debug(e.toString(), "Shape");
		} catch (InvalidShapeFileException e) {
			Logger.debug(e.toString(), "Shape");
		} catch (IOException e) {
			Logger.debug(e.toString(), "Shape");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.debug(e.toString(), "Shape");
				}
			}
		}
	}
}
