module javafxApp {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.bouncycastle.provider;
	requires org.apache.commons.io;
	requires javafx.media;
	requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;

	opens javafxApp to javafx.fxml;

	exports javafxApp;

	opens javafxApp.controller to javafx.fxml;

	exports javafxApp.controller;

	opens javafxApp.controller.button to javafx.fxml;

	exports javafxApp.controller.button;

}