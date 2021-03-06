module javafxApp {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;
	requires org.bouncycastle.provider;
	requires org.apache.commons.io;
	requires org.kordamp.ikonli.core;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.ikonli.fontawesome;
    requires javafx.graphics;

    opens view to javafx.fxml;

	exports view;

	opens view.controller to javafx.fxml;

	exports view.controller;

	opens view.controller.button to javafx.fxml;

	exports view.controller.button;

}