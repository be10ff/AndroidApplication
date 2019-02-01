package ru.tcgeo.application.views.fragment;

public class OpenSDFileFragment extends ReOpenFileFragment {

    //	https://stackoverflow.com/questions/40068984/universal-way-to-write-to-external-sd-card-on-android
    public OpenSDFileFragment() {
        super();
        m_root = "/storage";
    }

    @Override
    protected void setDir() {
        getDir(m_root);
    }

}
