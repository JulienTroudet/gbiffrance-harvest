CREATE TABLE IF NOT EXISTS `dataset` (
  `dataset_id` int(2) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`dataset_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

--
-- Table content of `dataset`
--

INSERT INTO `dataset` (`name`, `url`, `type`) VALUES
('mola', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=mola', 'biocase'),
('crbip', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=crbip', 'biocase'),
('cal', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cal', 'biocase'),
('wal_fut', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=wal_fut', 'biocase'),
('mzs_ave', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=mzs_ave', 'biocase'),
('palbot', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=palbot', 'biocase'),
('guy', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=guy', 'biocase'),
('cirm', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cirm', 'biocase'),
('cbnfc', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cbnfc', 'biocase'),
('floraine', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=floraine', 'biocase'),
('cfbp', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cfbp', 'biocase'),
('mzs_po', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=crbip', 'biocase'),
('aublet', 'http://ceperou.cayenne.ird.fr/biocase/pywrapper.cgi?dsa=aublet', 'biocase'),
('arachne', 'http://dsibib.mnhn.fr/biocase/pywrapper.cgi?dsa=arachne', 'biocase'),
('coleoptere', 'http://dsibib.mnhn.fr/biocase/pywrapper.cgi?dsa=coleoptere', 'biocase'),
('invmar', 'http://dsibib.mnhn.fr/biocase/pywrapper.cgi?dsa=invmar', 'biocase'),
('mycobase', 'http://dsibib.mnhn.fr/biocase/pywrapper.cgi?dsa=mycobase', 'biocase'),
('reptamph', 'http://dsibib.mnhn.fr/biocase/pywrapper.cgi?dsa=reptamph', 'biocase'),
('cbnbp', 'http://dsimap.mnhn.fr/biocase/pywrapper.cgi?dsa=cbnbp', 'biocase'),
('inpn', 'http://dsimap.mnhn.fr/biocase/pywrapper.cgi?dsa=inpn', 'biocase'),
('ecoscope', 'http://vmirdgbif-proto.mpl.ird.fr:8080/ipt/resource.do?r=ecoscope_observation_database', 'ipt'),
('cnidariamzs', 'http://www.gbif.fr:8080/ipt/eml.do?r=cnidariamzs', 'ipt'),
('guadeloupe_insectes', 'http://www.gbif.fr:8080/ipt/eml.do?r=guadeloupe_insectes', 'ipt'),
('herbierdestrasbourg', 'http://www.gbif.fr:8080/ipt/eml.do?r=herbierdestrasbourg', 'ipt'),
('quadrige', 'http://www.gbif.fr:8080/ipt/eml.do?r=quadrige', 'ipt'),
('cel', 'http://www.tela-botanica.org/tapirlink/tapir.php/cel', 'tapir'),
('chorodep', 'http://www.tela-botanica.org/tapirlink/tapir.php/chorodep', 'tapir'),
('Gicim', 'http://dsibib.mnhn.fr/ici/digir', 'digir'),
('Hemipteres', 'http://dsibib.mnhn.fr/ici/digir', 'digir'),
('Ensiferes', 'http://dsibib.mnhn.fr/ici/digir', 'digir')

/*('legumino', 'http://www.tela-botanica.org/tapirlink/tapir.php/legumino', 'tapir'),
('pterido', 'http://www.tela-botanica.org/tapirlink/tapir.php/pterido', 'tapir'),
('biocean', 'http://www.ifremer.fr/digir/DiGIR.php', 'digir'),
('comarge', 'http://www.ifremer.fr/digir/DiGIR.php', 'digir')*/
