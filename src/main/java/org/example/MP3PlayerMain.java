package org.example;

import org.example.Controllers.Controller;
import org.example.Controllers.ListController;
import org.example.Model.DatabaseModel;
import org.example.Model.ShazamModel;
import org.example.View.SwingView;

public class MP3PlayerMain {
    public static void main(String[] args) {
        // Initialize the model, view, and controller
        DatabaseModel model = new DatabaseModel();
        SwingView view = new SwingView();
        ShazamModel shazam = new ShazamModel();
        Controller controller = new Controller(model, shazam , view);
        ListController listController = new ListController(model, view);

        // Run the application
        controller.runApplication();
        listController.updatePlaylistView();

    }
}