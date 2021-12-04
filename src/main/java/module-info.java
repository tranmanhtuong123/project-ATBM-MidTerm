module javafxApp {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.bouncycastle.provider;
	requires org.apache.commons.io;
	requires javafx.media;
	requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;

	opens javafx to javafx.fxml;

	exports javafx;

	opens javafx.controller to javafx.fxml;

	exports javafx.controller;

	opens javafx.controller.button to javafx.fxml;

	exports javafx.controller.button;

}