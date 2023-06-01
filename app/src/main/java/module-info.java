module bellview {
	exports dwc.bellview.file;
	exports dwc.bellview.transform;
	exports dwc.bellview;
	exports dwc.bellview.model;
	
	opens dwc.bellview to javafx.fxml;

	requires commons.math3;
	//requires itextpdf;
	requires pdfa;
	requires commons;
	requires layout;
	requires kernel;
	requires io;
	requires java.desktop;
	requires java.prefs;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.swing;
	requires transitive org.apache.poi.ooxml;
	requires transitive org.apache.poi.poi;
	requires org.slf4j;
	requires ch.qos.logback.core;
	requires ch.qos.logback.classic;
}