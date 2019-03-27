package com.example.qrallye;

import android.net.Uri;

public interface FragmentCallback {

    void onFragmentInteraction(Uri uri);

    void onLocationClick(int location);

}
