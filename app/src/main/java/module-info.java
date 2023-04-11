module bellview {
	exports dwc.bellview.file;
	exports dwc.bellview.transform;
	exports dwc.bellview;
	exports dwc.bellview.export;
	exports dwc.bellview.model;
	
	opens dwc.bellview to javafx.fxml;

	requires commons.math3;
	requires transitive itextpdf;
	requires java.desktop;
	requires java.prefs;
	requires transitive javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.swing;
	requires transitive org.apache.poi.ooxml;
	requires transitive org.apache.poi.poi;
	requires org.slf4j;
	requires ch.qos.logback.core;
	//requires java.xml.crypto;
    //requires java.management;
    requires java.naming;
    requires java.logging;
    //requires java.scripting;
    //requires java.sql;
    //requires java.rmi;
    //requires java.xml;
    //requires java.compiler;
    
}