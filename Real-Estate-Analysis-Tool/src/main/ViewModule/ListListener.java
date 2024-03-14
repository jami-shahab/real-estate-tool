package main.ViewModule;

import main.Controller.TimeSeries;

public interface ListListener {
    void onListChanged(TimeSeries timeSeries);
}
