module javafxApp {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.bouncycastle.provider;
	requires org.apache.commons.io;

	opens javafxApp to javafx.fxml;

	exports javafxApp;

	opens javafxApp.controller to javafx.fxml;

	exports javafxApp.controller;

	opens javafxApp.controller.button to javafx.fxml;

	exports javafxApp.controller.button;

}