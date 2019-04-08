package controllers;

import models.MainWindowModel;

public class MainWindow implements ModelBindable<MainWindowModel> {
    private MainWindowModel service;

    @Override
    public void setService(MainWindowModel service) {
        this.service = service;
    }
}
