package com.example.chatplugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;

@Service
//@State(additionalExportFile = StoragePathMacros.CACHE_FILE)
final class MySettings implements PersistentStateComponent<MySettings.State> {

    static class State {
        public String value;
    }

    private State myState = new State();

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }
}